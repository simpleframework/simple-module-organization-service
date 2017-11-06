package net.simpleframework.organization.impl;

import static net.simpleframework.common.I18n.$m;

import java.util.HashMap;
import java.util.Map;

import net.simpleframework.ado.ColumnData;
import net.simpleframework.ado.db.DbEntityTable;
import net.simpleframework.ctx.AbstractADOModuleContext;
import net.simpleframework.ctx.IApplicationContext;
import net.simpleframework.ctx.IModuleRef;
import net.simpleframework.ctx.Module;
import net.simpleframework.ctx.ModuleRefUtils;
import net.simpleframework.ctx.permission.PermissionConst;
import net.simpleframework.organization.IAccountSSOService;
import net.simpleframework.organization.IAccountService;
import net.simpleframework.organization.IAccountStatService;
import net.simpleframework.organization.IDepartmentService;
import net.simpleframework.organization.IOrganizationContext;
import net.simpleframework.organization.IRoleChartService;
import net.simpleframework.organization.IRoleMemberService;
import net.simpleframework.organization.IRoleService;
import net.simpleframework.organization.IUserService;
import net.simpleframework.organization.bean.Account;
import net.simpleframework.organization.bean.AccountSSO;
import net.simpleframework.organization.bean.AccountStat;
import net.simpleframework.organization.bean.Department;
import net.simpleframework.organization.bean.Role;
import net.simpleframework.organization.bean.RoleChart;
import net.simpleframework.organization.bean.RoleMember;
import net.simpleframework.organization.bean.User;
import net.simpleframework.organization.bean.UserLob;
import net.simpleframework.organization.role.BuiltInRole;
import net.simpleframework.organization.role.IRoleHandler;
import net.simpleframework.organization.role.RolenameW;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class OrganizationContext extends AbstractADOModuleContext
		implements IOrganizationContext {

	@Override
	public void onInit(final IApplicationContext application) throws Exception {
		super.onInit(application);

		// 注册缺省的规则角色
		registRoleHandler(PermissionConst.ROLE_ALL_ACCOUNT, BuiltInRole.All.class);
		registRoleHandler(PermissionConst.ROLE_LOCK_ACCOUNT, BuiltInRole.Lock.class);
		registRoleHandler(PermissionConst.ROLE_ANONYMOUS, BuiltInRole.Anonymous.class);
		registRoleHandler(PermissionConst.ROLE_INDEPT, BuiltInRole.InDept.class);
		registRoleHandler(PermissionConst.ROLE_INORG, BuiltInRole.InOrg.class);
	}

	@Override
	protected DbEntityTable[] createEntityTables() {
		return new DbEntityTable[] { new DbEntityTable(Account.class, "sf_organization_account"),
				new DbEntityTable(AccountStat.class, "sf_organization_accountstat"),
				new DbEntityTable(AccountSSO.class, "sf_organization_account_sso"),

				new DbEntityTable(User.class, "sf_organization_user"),
				new DbEntityTable(UserLob.class, "sf_organization_user_lob").setNoCache(true),

				new DbEntityTable(Department.class, "sf_organization_department"),

				new DbEntityTable(Role.class, "sf_organization_role")
						.setDefaultOrder(ColumnData.ASC("oorder")),
				new DbEntityTable(RoleChart.class, "sf_organization_rolechart")
						.setDefaultOrder(ColumnData.ASC("oorder")),
				new DbEntityTable(RoleMember.class, "sf_organization_rolemember") };
	}

	@Override
	public RoleChart getSystemChart() {
		return getRoleChartService().getRoleChartByName(RolenameW.ROLECHART_SYSTEM);
	}

	@Override
	protected Module createModule() {
		return super.createModule().setRole(PermissionConst.ROLE_MANAGER)
				.setManagerRole(PermissionConst.ROLE_MANAGER).setName(MODULE_NAME)
				.setText($m("OrganizationContext.0")).setOrder(11);
	}

	@Override
	public IAccountService getAccountService() {
		return singleton(AccountService.class);
	}

	@Override
	public IAccountSSOService getAccountSSOService() {
		return singleton(AccountSSOService.class);
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
	public IAccountStatService getAccountStatService() {
		return singleton(AccountStatService.class);
	}

	@Override
	public IModuleRef getMessageRef() {
		return ModuleRefUtils.getRef("net.simpleframework.organization.OrganizationMessageRef");
	}

	static Map<String, Class<? extends IRoleHandler>> rHandleRegistry;
	static {
		rHandleRegistry = new HashMap<>();
	}

	public static void registRoleHandler(final String role,
			final Class<? extends IRoleHandler> rHandlerClass) {
		rHandleRegistry.put(role, rHandlerClass);
	}
}
