package net.simpleframework.organization.impl;

import static net.simpleframework.common.I18n.$m;

import java.util.Iterator;
import java.util.Map;

import net.simpleframework.ado.IParamsValue;
import net.simpleframework.ado.db.IDbEntityManager;
import net.simpleframework.ado.db.common.EntityInterceptor;
import net.simpleframework.ado.query.DataQueryUtils;
import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.Convert;
import net.simpleframework.common.ID;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.CollectionUtils;
import net.simpleframework.common.coll.CollectionUtils.AbstractIterator;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.ctx.permission.IPermissionConst;
import net.simpleframework.ctx.permission.IPermissionHandler;
import net.simpleframework.ctx.permission.PermissionRole;
import net.simpleframework.ctx.script.IScriptEval;
import net.simpleframework.ctx.script.ScriptEvalFactory;
import net.simpleframework.organization.Account;
import net.simpleframework.organization.ERoleMark;
import net.simpleframework.organization.ERoleMemberType;
import net.simpleframework.organization.ERoleType;
import net.simpleframework.organization.IRoleHandler;
import net.simpleframework.organization.IRoleService;
import net.simpleframework.organization.OrganizationException;
import net.simpleframework.organization.Role;
import net.simpleframework.organization.RoleChart;
import net.simpleframework.organization.RoleMember;
import net.simpleframework.organization.User;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
@EntityInterceptor(listenerTypes = { "net.simpleframework.module.log.EntityDeleteLogAdapter" })
public class RoleService extends AbstractOrganizationService<Role> implements IRoleService {

	@Override
	public RoleChart getRoleChart(final Role role) {
		return role == null ? null : rcService.getBean(role.getRoleChartId());
	}

	@Override
	public String toUniqueName(final Role role) {
		return PermissionRole.toUniqueRolename(getRoleChart(role).getName(), role.getName());
	}

	@Override
	public Role getRoleByName(final String name) {
		final String[] arr = PermissionRole.split(name);
		if (arr == null || arr.length != 2) {
			throw OrganizationException.of($m("RoleService.2"));
		}
		return getRoleByName(rcService.getRoleChartByName(arr[0]), arr[1]);
	}

	@Override
	public Role getRoleByName(final RoleChart roleChart, final String name) {
		if (roleChart == null) {
			return null;
		}
		return getBean("rolechartid=? and name=?", roleChart.getId(), name);
	}

	@Override
	public IDataQuery<Role> queryRoot(final RoleChart roleChart) {
		if (roleChart == null) {
			return DataQueryUtils.nullQuery();
		}
		return query("rolechartid=? and parentid is null", roleChart.getId());
	}

	@Override
	public IDataQuery<Role> queryRoles(final RoleChart roleChart) {
		if (roleChart == null) {
			return DataQueryUtils.nullQuery();
		}
		return query("rolechartid=?", roleChart.getId());
	}

	@Override
	public IRoleHandler getRoleHandler(final Role role) {
		final Class<?> rHandlerClass = OrganizationContext.rHandleRegistry.get(toUniqueName(role));
		if (rHandlerClass != null) {
			return (IRoleHandler) singleton(rHandlerClass);
		}
		final String sHandlerClass = role.getRuleHandler();
		if (StringUtils.hasText(sHandlerClass)) {
			return (IRoleHandler) singleton(sHandlerClass);
		}
		return null;
	}

	@Override
	public boolean isMember(final User user, final Role role, final Map<String, Object> variables) {
		if (isManager(user, variables)) {
			return true;
		}
		return _isMember(user, role, variables);
	}

	private boolean _isMember(final User user, final Role role, final Map<String, Object> variables) {
		if (user == null || role == null) {
			return false;
		}
		if (role != null && IPermissionConst.ROLE_ANONYMOUS.equals(toUniqueName(role))) {
			return true;
		}
		final ERoleType jt = role.getRoleType();
		if (jt == ERoleType.normal) {
			if (rmService.getBean("roleid=? and membertype=? and memberid=?", role.getId(),
					ERoleMemberType.user, user.getId()) != null) {
				variables.put(IPermissionHandler.CTX_ROLEID, role.getId());
				return true;
			} else {
				final IDataQuery<RoleMember> dq = rmService.query("roleid=? and membertype=?",
						role.getId(), ERoleMemberType.role);
				RoleMember jm;
				while ((jm = dq.next()) != null) {
					if (_isMember(user, getBean(jm.getMemberId()), variables)) {
						return true;
					}
				}
			}
		} else if (jt == ERoleType.handle) {
			final IRoleHandler rHandler = getRoleHandler(role);
			if (rHandler != null) {
				return rHandler.isMember(user, variables);
			}
		} else if (jt == ERoleType.script) {
			variables.put("role", role);
			variables.put("user", user);
			final IScriptEval scriptEval = ScriptEvalFactory.createDefaultScriptEval(variables);
			final Object o = scriptEval.eval(role.getRuleScript());
			if (o instanceof Boolean) {
				return (Boolean) o;
			}
		}
		return false;
	}

	@Override
	public boolean isMember(final User user, final String roleRule,
			final Map<String, Object> variables) {
		if (!StringUtils.hasText(roleRule)) {
			return false;
		}
		for (final String rr : StringUtils.split(roleRule, ";")) {
			if (rr.startsWith("#")) { // #开头则认为是用户名
				final User user2 = uService.getBean(rr.substring(1));
				if (user2 != null && user2.equals(user)) {
					return true;
				}
			} else {
				if (_isMember(user, getRoleByName(rr), variables)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean isManager(final User user, final Map<String, Object> variables) {
		Account account;
		if (user != null && (account = uService.getAccount(user.getId())) != null
				&& account.isAdmin()) {
			return true;
		}
		return isMember(user, IPermissionConst.ROLE_MANAGER, variables);
	}

	@Override
	public IDataQuery<RoleMember> members(final Role role) {
		return rmService.queryMembers(role);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterator<User> users(final Role role, final ID deptId, final Map<String, Object> variables) {
		final ERoleType jt = role.getRoleType();
		if (jt == ERoleType.normal) {
			final IDataQuery<RoleMember> dq = members(role);
			return new AbstractIterator<User>() {
				@Override
				public boolean hasNext() {
					if (nest != null && nest.hasNext()) {
						user = nest.next();
						return true;
					}
					RoleMember jm;
					while ((jm = dq.next()) != null) {
						final ID memberId = jm.getMemberId();
						if (jm.getMemberType() == ERoleMemberType.user) {
							user = uService.getBean(memberId);
							if (user != null && (deptId == null || deptId.equals(user.getDepartmentId()))) {
								variables.put(IPermissionHandler.CTX_ROLEID, role.getId());
								variables.put(IPermissionHandler.CTX_DEPTID, jm.getDeptId());
								return true;
							}
						} else {
							if ((nest = users(getBean(memberId), deptId, variables)).hasNext()) {
								user = nest.next();
								return true;
							}
						}
					}
					return false;
				}

				@Override
				public User next() {
					return user;
				}

				User user = null;

				Iterator<User> nest = null;
			};
		} else if (jt == ERoleType.handle) {
			final IRoleHandler rHandler = getRoleHandler(role);
			if (rHandler != null) {
				return rHandler.members(variables);
			}
		}
		return CollectionUtils.EMPTY_ITERATOR;
	}

	@Override
	public Iterator<Role> roles(final User user) {
		return roles(user, new KVMap());
	}

	@Override
	public Iterator<Role> roles(final User user, final Map<String, Object> variables) {
		final IDataQuery<RoleMember> dq = rmService.query("memberType=? and memberId=?",
				ERoleMemberType.user, user.getId());
		return new AbstractIterator<Role>() {
			@Override
			public boolean hasNext() {
				RoleMember jm;
				while ((jm = dq.next()) != null) {
					role = getBean(jm.getRoleId());
					final boolean userRole = Convert.toBool(variables.get("userRole"), false);
					if (role != null && (!userRole || role.isUserRole())) {
						return true;
					}
				}
				if (Convert.toBool(variables.get("ruleRole"), false)) {
					if (qd2 == null) {
						qd2 = query("roleType=? or roleType=?", ERoleType.handle, ERoleType.script);
					}
					Role r;
					while ((r = qd2.next()) != null) {
						if (isMember(user, r, variables)) {
							role = r;
							return true;
						}
					}
				}
				return false;
			}

			@Override
			public Role next() {
				return role;
			}

			Role role = null;

			IDataQuery<Role> qd2 = null;
		};
	}

	@Override
	public Role getPrimaryRole(final User user) {
		Role r = null;
		final RoleMember rm = rmService.getBean("memberType=? and memberId=? and primaryRole=?",
				ERoleMemberType.user, user.getId(), true);
		if (rm != null) {
			r = getBean(rm.getRoleId());
		} else {
			final Iterator<Role> it = roles(user, new KVMap());
			if (it.hasNext()) {
				r = it.next();
			}
		}
		if (r == null) {
			r = getRoleByName(IPermissionConst.ROLE_ALL_ACCOUNT);
		}
		return r;
	}

	@Override
	public void onInit() throws Exception {
		super.onInit();

		addListener(new DbEntityAdapterEx() {

			@Override
			public void onBeforeDelete(final IDbEntityManager<?> service,
					final IParamsValue paramsValue) {
				super.onBeforeDelete(service, paramsValue);

				for (final Role role : coll(paramsValue)) {
					// 含有孩子
					if (queryChildren(role).getCount() > 0) {
						throw OrganizationException.of($m("RoleService.1"));
					}
					// 内置角色
					if (role.getRoleMark() == ERoleMark.builtIn) {
						throw OrganizationException.of($m("RoleService.0"));
					}

					final ID rid = role.getId();
					rmService.deleteWith("roleId=? or (membertype=? and memberid=?)", rid,
							ERoleMemberType.role, rid);
				}
			}
		});
	}
}
