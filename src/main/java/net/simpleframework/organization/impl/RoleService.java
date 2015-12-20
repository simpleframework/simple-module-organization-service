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
import net.simpleframework.common.object.ObjectUtils;
import net.simpleframework.ctx.permission.LoginUser;
import net.simpleframework.ctx.permission.PermissionConst;
import net.simpleframework.ctx.script.IScriptEval;
import net.simpleframework.ctx.script.ScriptEvalFactory;
import net.simpleframework.organization.Account;
import net.simpleframework.organization.Department;
import net.simpleframework.organization.IRoleService;
import net.simpleframework.organization.OrganizationException;
import net.simpleframework.organization.Role;
import net.simpleframework.organization.Role.ERoleMark;
import net.simpleframework.organization.Role.ERoleType;
import net.simpleframework.organization.RoleChart;
import net.simpleframework.organization.RoleMember;
import net.simpleframework.organization.RoleMember.ERoleMemberType;
import net.simpleframework.organization.User;
import net.simpleframework.organization.role.IRoleHandler;
import net.simpleframework.organization.role.RolenameW;
import net.simpleframework.organization.role.RolenameW.RoleW;

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
		return role == null ? null : _rolecService.getBean(role.getRoleChartId());
	}

	@Override
	public String toUniqueName(final Role role) {
		final RoleChart chart = getRoleChart(role);
		final Department org = _deptService.getBean(chart.getOrgId());
		return RolenameW.toUniqueRolename(org != null ? org.getName() : null, chart.getName(),
				role.getName());
	}

	@Override
	public Role getRoleByName(final String name) {
		final String[] arr = RolenameW.split(name);
		if (arr.length == 3) {
			return getRoleByName(
					_rolecService.getRoleChartByName(_deptService.getDepartmentByName(arr[0]), arr[1]),
					arr[2]);
		} else if (arr.length == 2) {
			return getRoleByName(_rolecService.getRoleChartByName(arr[0]), arr[1]);
		} else {
			throw OrganizationException.of($m("RoleService.2"));
		}
	}

	@Override
	public Role getRoleByName(final RoleChart roleChart, final String name) {
		if (roleChart == null) {
			return null;
		}
		Role r = getBean("rolechartid=? and name=?", roleChart.getId(), name);
		RoleW w;
		if (r == null && (w = RolenameW.getBuiltInRole(name)) != null) {
			r = createRole(w);
			r.setRoleChartId(roleChart.getId());
			insert(r);
		}
		return r;
	}

	private Role createRole(final RoleW w) {
		final Role r = createBean();
		r.setName(w.getName());
		r.setText(w.getText());
		r.setDescription(w.getDescription());
		r.setRoleType(w.getRoleType());
		r.setRoleMark(ERoleMark.builtIn);
		return r;
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
		if (role != null && role.getOrgId() != null) {
			// 此处不能直接传递variables
			if (_isMember(user, getRoleByName(PermissionConst.ROLE_DOMAIN_MANAGER), new KVMap())) {
				return true;
			}
		}
		return _isMember(user, role, variables);
	}

	private boolean _isMember(final User user, final Role role, final Map<String, Object> variables) {
		if (user == null || role == null) {
			return false;
		}
		if (role != null && PermissionConst.ROLE_ANONYMOUS.equals(toUniqueName(role))) {
			return true;
		}
		final ERoleType jt = role.getRoleType();
		if (jt == ERoleType.normal) {
			if (_rolemService.getBean("roleid=? and membertype=? and memberid=?", role.getId(),
					ERoleMemberType.user, user.getId()) != null) {
				variables.put(PermissionConst.VAR_ROLEID, role.getId());
				return true;
			} else {
				final IDataQuery<RoleMember> dq = _rolemService.query("roleid=? and membertype=?",
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
				final User user2 = _userService.getBean(rr.substring(1));
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
		if (user != null && (account = _userService.getAccount(user.getId())) != null
				&& account.isAdmin()) {
			return true;
		}
		return isMember(user, PermissionConst.ROLE_MANAGER, variables);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterator<User> users(final Role role, final ID deptId, final Map<String, Object> variables) {
		final ERoleType jt = role.getRoleType();
		if (jt == ERoleType.normal) {
			final IDataQuery<RoleMember> dq = _rolemService.queryRoleMembers(role, null);
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
							user = _userService.getBean(memberId);
							if (user != null && (deptId == null || deptId.equals(user.getDepartmentId()))) {
								variables.put(PermissionConst.VAR_ROLEID, role.getId());
								variables.put(PermissionConst.VAR_DEPTID, jm.getDeptId());
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
	public Iterator<RoleM> roles(final User user) {
		return roles(user, new KVMap());
	}

	@Override
	public Iterator<RoleM> roles(final User user, final Map<String, Object> variables) {
		final IDataQuery<RoleMember> dq = _rolemService.query("memberType=? and memberId=?",
				ERoleMemberType.user, user.getId());
		// 是否仅显示用户识别的角色
		final boolean _userRole = Convert.toBool(variables.get("userRole"), false);
		// 是否分析规则角色
		final boolean _ruleRole = Convert.toBool(variables.get("ruleRole"), false);
		// 显示同一机构的角色
		final boolean _inOrg = Convert.toBool(variables.get("inOrg"), false);
		return new AbstractIterator<RoleM>() {
			@Override
			public boolean hasNext() {
				RoleMember jm;
				while ((jm = dq.next()) != null) {
					final Role role = getBean(jm.getRoleId());
					if (role != null && (!_userRole || role.isUserRole())
							&& (!_inOrg || ObjectUtils.objectEquals(role.getOrgId(), user.getOrgId()))) {
						return r(new RoleM(role, jm));
					}
				}
				if (_ruleRole) {
					// 调用此处，会造成大量计算
					if (qd2 == null) {
						qd2 = query("roletype=? or roletype=?", ERoleType.handle, ERoleType.script);
					}
					Role r;
					while ((r = qd2.next()) != null) {
						if ((!_userRole || r.isUserRole())
								&& (!_inOrg || ObjectUtils.objectEquals(r.getOrgId(), user.getOrgId()))
								&& isMember(user, r, variables)) {
							return r(new RoleM(r));
						}
					}
				}
				return r(null);
			}

			private boolean r(final RoleM _rolem) {
				return (rolem = _rolem) != null;
			}

			@Override
			public RoleM next() {
				return rolem;
			}

			private RoleM rolem = null;
			private IDataQuery<Role> qd2 = null;
		};
	}

	@Override
	public Role getPrimaryRole(final User user) {
		Role r = null;
		final RoleMember rm = _rolemService.getBean("memberType=? and memberId=? and primaryRole=?",
				ERoleMemberType.user, user.getId(), true);
		if (rm != null) {
			r = getBean(rm.getRoleId());
		} else {
			final Iterator<RoleM> it = roles(user, new KVMap());
			if (it.hasNext()) {
				r = it.next().role;
			}
		}
		if (r == null) {
			r = getRoleByName(PermissionConst.ROLE_ALL_ACCOUNT);
		}
		return r;
	}

	@Override
	public void onInit() throws Exception {
		super.onInit();

		addListener(new DbEntityAdapterEx<Role>() {

			@Override
			public void onBeforeDelete(final IDbEntityManager<Role> manager,
					final IParamsValue paramsValue) throws Exception {
				super.onBeforeDelete(manager, paramsValue);

				for (final Role role : coll(manager, paramsValue)) {
					// 含有孩子
					if (queryChildren(role).getCount() > 0) {
						throw OrganizationException.of($m("RoleService.1"));
					}
					// 内置角色
					if (!LoginUser.isAdmin() && role.getRoleMark() == ERoleMark.builtIn) {
						throw OrganizationException.of($m("RoleService.0"));
					}
					if (_rolemService.queryRoleMembers(role, null).getCount() > 0) {
						throw OrganizationException.of($m("RoleService.3"));
					}
					// 删除成员
					_rolemService.deleteWith("membertype=? and memberid=?", ERoleMemberType.role,
							role.getId());

					// 统计
					doUpdateRoles(role, -1);
				}
			}

			@Override
			public void onAfterInsert(final IDbEntityManager<Role> manager, final Role[] beans)
					throws Exception {
				super.onAfterInsert(manager, beans);
				for (final Role role : beans) {
					doUpdateRoles(role, 0);
				}
			}
		});
	}

	private void doUpdateRoles(final Role role, final int delta) {
		final RoleChart chart = _rolecService.getBean(role.getRoleChartId());
		chart.setRoles(count("rolechartid=?", chart.getId()) + delta);
		_rolecService.update(new String[] { "roles" }, chart);
	}
}
