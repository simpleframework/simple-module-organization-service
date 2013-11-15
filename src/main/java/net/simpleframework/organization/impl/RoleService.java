package net.simpleframework.organization.impl;

import static net.simpleframework.common.I18n.$m;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;

import net.simpleframework.ado.IParamsValue;
import net.simpleframework.ado.db.IDbEntityManager;
import net.simpleframework.ado.db.common.EntityInterceptor;
import net.simpleframework.ado.query.DataQueryUtils;
import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.ID;
import net.simpleframework.common.StringUtils;
import net.simpleframework.ctx.permission.IPermissionConst;
import net.simpleframework.ctx.permission.PermissionRole;
import net.simpleframework.ctx.script.IScriptEval;
import net.simpleframework.ctx.script.ScriptEvalFactory;
import net.simpleframework.organization.ERoleMark;
import net.simpleframework.organization.ERoleMemberType;
import net.simpleframework.organization.ERoleType;
import net.simpleframework.organization.IAccount;
import net.simpleframework.organization.IRole;
import net.simpleframework.organization.IRoleChart;
import net.simpleframework.organization.IRoleHandler;
import net.simpleframework.organization.IRoleMember;
import net.simpleframework.organization.IRoleService;
import net.simpleframework.organization.IUser;
import net.simpleframework.organization.OrganizationException;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
@EntityInterceptor(listenerTypes = { "net.simpleframework.module.log.EntityDeleteLogAdapter" })
public class RoleService extends AbstractOrganizationService<IRole, Role> implements IRoleService {

	@Override
	public IRoleChart getRoleChart(final IRole role) {
		return role == null ? null : getRoleChartService().getBean(role.getRoleChartId());
	}

	@Override
	public String toUniqueName(final IRole role) {
		return PermissionRole.toUniqueRolename(getRoleChart(role).getName(), role.getName());
	}

	@Override
	public IRole getRoleByName(final String name) {
		final String[] arr = PermissionRole.split(name);
		if (arr == null || arr.length != 2) {
			throw OrganizationException.of($m("RoleService.2"));
		}
		return getRoleByName(getRoleChartService().getRoleChartByName(arr[0]), arr[1]);
	}

	@Override
	public IRole getRoleByName(final IRoleChart roleChart, final String name) {
		if (roleChart == null) {
			return null;
		}
		return getBean("rolechartid=? and name=?", roleChart.getId(), name);
	}

	@Override
	public IDataQuery<IRole> queryRoot(final IRoleChart roleChart) {
		if (roleChart == null) {
			return DataQueryUtils.nullQuery();
		}
		return query("rolechartid=? and parentid is null", roleChart.getId());
	}

	@Override
	public IDataQuery<IRole> queryRoles(final IRoleChart roleChart) {
		if (roleChart == null) {
			return DataQueryUtils.nullQuery();
		}
		return query("rolechartid=?", roleChart.getId());
	}

	@Override
	public IRoleHandler getRoleHandler(final IRole role) {
		final Class<?> rHandleClass = OrganizationContext.rHandleRegistry.get(toUniqueName(role));
		if (rHandleClass != null) {
			return (IRoleHandler) singleton(rHandleClass);
		}
		final String sHandleClass = role.getRuleHandler();
		if (StringUtils.hasText(sHandleClass)) {
			return (IRoleHandler) singleton(sHandleClass);
		}
		return null;
	}

	@Override
	public boolean isMember(final IUser user, final IRole role, final Map<String, Object> variables) {
		if (isManager(user, variables)) {
			return true;
		}
		return _isMember(user, role, variables);
	}

	private boolean _isMember(final IUser user, final IRole role, final Map<String, Object> variables) {
		if (user == null || role == null) {
			return false;
		}
		if (role != null && IPermissionConst.ROLE_ANONYMOUS.equals(toUniqueName(role))) {
			return true;
		}
		final ERoleType jt = role.getRoleType();
		if (jt == ERoleType.normal) {
			final IDataQuery<? extends IRoleMember> dq = members(role);
			IRoleMember jm;
			while ((jm = dq.next()) != null) {
				final ID memberId = jm.getMemberId();
				if (jm.getMemberType() == ERoleMemberType.user) {
					if (user.getId().equals(memberId)) {
						return true;
					}
				} else {
					if (_isMember(user, getBean(memberId), variables)) {
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
	public boolean isMember(final IUser user, final String roleRule,
			final Map<String, Object> variables) {
		if (!StringUtils.hasText(roleRule)) {
			return false;
		}
		for (final String rr : StringUtils.split(roleRule)) {
			if (rr.startsWith("#")) { // #开头则认为是用户名
				final IUser user2 = getUserService().getBean(rr.substring(1));
				if (user2 != null && user2.getId().equals(user.getId())) {
					return true;
				}
			} else {
				if (_isMember(user, getBean(rr), variables)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean isManager(final IUser user, final Map<String, Object> variables) {
		IAccount account;
		if (user != null && (account = getUserService().getAccount(user.getId())) != null
				&& account.isAdmin()) {
			return true;
		}
		return isMember(user, IPermissionConst.ROLE_MANAGER, variables);
	}

	@Override
	public Collection<? extends IUser> users(final IRole role, final Map<String, Object> variables) {
		final LinkedHashSet<IUser> users = new LinkedHashSet<IUser>();
		if (role != null) {
			final ERoleType jt = role.getRoleType();
			if (jt == ERoleType.normal) {
				final IDataQuery<? extends IRoleMember> dq = members(role);
				IRoleMember jm;
				while ((jm = dq.next()) != null) {
					final ID memberId = jm.getMemberId();
					if (jm.getMemberType() == ERoleMemberType.user) {
						final IUser user = getUserService().getBean(memberId);
						if (user != null) {
							users.add(user);
						}
					} else {
						users.addAll(users(getBean(memberId), variables));
					}
				}
			} else if (jt == ERoleType.handle) {
				final IRoleHandler rHandler = getRoleHandler(role);
				if (rHandler != null) {
					users.addAll(rHandler.members(variables));
				}
			}
		}
		return users;
	}

	@Override
	public IDataQuery<? extends IRoleMember> members(final IRole role) {
		return getRoleMemberService().queryMembers(role);
	}

	@Override
	public void onInit() throws Exception {
		addListener(new DbEntityAdapterEx() {

			@Override
			public void onBeforeDelete(final IDbEntityManager<?> service,
					final IParamsValue paramsValue) {
				super.onBeforeDelete(service, paramsValue);

				for (final IRole role : coll(paramsValue)) {
					// 含有孩子
					if (queryChildren(role).getCount() > 0) {
						throw OrganizationException.of($m("RoleService.1"));
					}
					// 内置角色
					if (role.getRoleMark() == ERoleMark.builtIn) {
						throw OrganizationException.of($m("RoleService.0"));
					}

					final ID rid = role.getId();
					getRoleMemberService().deleteWith("roleId=? or (membertype=? and memberid=?)", rid,
							ERoleMemberType.role, rid);
				}
			}

			@Override
			public void onBeforeUpdate(final IDbEntityManager<?> manager, final String[] columns,
					final Object[] beans) {
				super.onBeforeUpdate(manager, columns, beans);
				for (final Object o : beans) {
					assertParentId((IRole) o);
				}
			}
		});
	}
}
