package net.simpleframework.organization.impl;

import static net.simpleframework.common.I18n.$m;
import static net.simpleframework.ctx.permission.IPermissionConst.ROLECHART_SYSTEM;
import static net.simpleframework.ctx.permission.IPermissionConst.ROLE_ALL_ACCOUNT;
import static net.simpleframework.ctx.permission.IPermissionConst.ROLE_ANONYMOUS;
import static net.simpleframework.ctx.permission.IPermissionConst.ROLE_LOCK_ACCOUNT;
import static net.simpleframework.ctx.permission.IPermissionConst.ROLE_MANAGER;

import java.util.HashMap;
import java.util.Map;

import net.simpleframework.ado.db.DbEntityTable;
import net.simpleframework.ctx.IApplicationContext;
import net.simpleframework.ctx.IModuleRef;
import net.simpleframework.ctx.Module;
import net.simpleframework.ctx.permission.IPermissionConst;
import net.simpleframework.ctx.permission.PermissionRole;
import net.simpleframework.ctx.service.ado.db.AbstractDbModuleContext;
import net.simpleframework.organization.ERoleChartMark;
import net.simpleframework.organization.ERoleMark;
import net.simpleframework.organization.ERoleType;
import net.simpleframework.organization.IAccountService;
import net.simpleframework.organization.IDepartmentService;
import net.simpleframework.organization.IOrganizationContext;
import net.simpleframework.organization.IRole;
import net.simpleframework.organization.IRoleChart;
import net.simpleframework.organization.IRoleChartService;
import net.simpleframework.organization.IRoleHandler;
import net.simpleframework.organization.IRoleMemberService;
import net.simpleframework.organization.IRoleService;
import net.simpleframework.organization.IUserService;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class OrganizationContext extends AbstractDbModuleContext implements IOrganizationContext {

	@Override
	public void onInit(final IApplicationContext application) throws Exception {
		super.onInit(application);

		// 创建缺省视图及角色
		final IRoleChartService rcService = getRoleChartService();
		IRoleChart roleChart = rcService.getRoleChartByName(ROLECHART_SYSTEM);
		if (roleChart == null) {
			roleChart = rcService.createBean();
			roleChart.setName(ROLECHART_SYSTEM);
			roleChart.setText($m("RoleChartService.0"));
			roleChart.setChartMark(ERoleChartMark.builtIn);
			roleChart.setDescription($m("RoleChartService.1"));
			rcService.insert(roleChart);
		}

		final IRoleService rService = getRoleService();
		for (final String r : new String[] { ROLE_ALL_ACCOUNT, ROLE_LOCK_ACCOUNT, ROLE_ANONYMOUS,
				ROLE_MANAGER }) {
			final String name = PermissionRole.split(r)[1];
			IRole role = rService.getRoleByName(roleChart, name);
			if (role == null) {
				role = rService.createBean();
				role.setName(name);
				role.setText($m("RoleChartService." + name));
				role.setRoleChartId(roleChart.getId());
				role.setRoleMark(ERoleMark.builtIn);
				if (ROLE_MANAGER.equals(r)) {
					role.setRoleType(ERoleType.normal);
				} else {
					role.setRoleType(ERoleType.handle);
				}
				rService.insert(role);
			}
		}

		// 注册缺省的规则角色
		registRoleHandler(ROLE_ALL_ACCOUNT, BuiltInRole.All.class);
		registRoleHandler(ROLE_LOCK_ACCOUNT, BuiltInRole.Lock.class);
		registRoleHandler(ROLE_ANONYMOUS, BuiltInRole.Anonymous.class);
	}

	@Override
	protected DbEntityTable[] getEntityTables() {
		return new DbEntityTable[] { Account.TBL, User.TBL, UserLob.TBL, Department.TBL, Role.TBL,
				RoleChart.TBL, RoleMember.TBL };
	}

	@Override
	public IRoleChart getSystemChart() {
		return getRoleChartService().getRoleChartByName(ROLECHART_SYSTEM);
	}

	@Override
	public String getManagerRole() {
		return IPermissionConst.ROLE_MANAGER;
	}

	@Override
	protected Module createModule() {
		return new Module().setName(MODULE_NAME).setText($m("OrganizationContext.0")).setOrder(11);
	}

	@Override
	public IAccountService getAccountService() {
		return singleton(AccountService.class);
	}

	@Override
	public IUserService getUserService() {
		return singleton(UserService.class);
	}

	@Override
	public IDepartmentService getDepartmentService() {
		return singleton(DepartmentService.class);
	}

	@Override
	public IRoleService getRoleService() {
		return singleton(RoleService.class);
	}

	@Override
	public IRoleMemberService getRoleMemberService() {
		return singleton(RoleMemberService.class);
	}

	@Override
	public IRoleChartService getRoleChartService() {
		return singleton(RoleChartService.class);
	}

	@Override
	public IModuleRef getMessageRef() {
		return getRef("net.simpleframework.organization.OrganizationMessageRef");
	}

	static Map<String, Class<? extends IRoleHandler>> rHandleRegistry;
	static {
		rHandleRegistry = new HashMap<String, Class<? extends IRoleHandler>>();
	}

	public static void registRoleHandler(final String role,
			final Class<? extends IRoleHandler> rHandleClass) {
		rHandleRegistry.put(role, rHandleClass);
	}
}
