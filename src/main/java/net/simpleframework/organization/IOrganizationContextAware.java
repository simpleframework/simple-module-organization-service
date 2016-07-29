package net.simpleframework.organization;

import net.simpleframework.ctx.IModuleContextAware;
import net.simpleframework.ctx.ModuleContextFactory;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public interface IOrganizationContextAware extends IModuleContextAware {

	static final IOrganizationContext orgContext = ModuleContextFactory
			.get(IOrganizationContext.class);

	/* 帐号服务 */
	static final IAccountService _accountService = orgContext.getAccountService();
	/* 帐号单点服务 */
	static final IAccountSSOService _accountSSOService = orgContext.getAccountSSOService();
	/* 帐号统计服务 */
	static final IAccountStatService _accountStatService = orgContext.getAccountStatService();

	/* 用户服务 */
	static final IUserService _userService = orgContext.getUserService();
	/* 用户部门服务 */
	static final IDepartmentService _deptService = orgContext.getDepartmentService();

	/* 角色服务 */
	static final IRoleService _roleService = orgContext.getRoleService();
	/* 角色视图服务 */
	static final IRoleChartService _rolecService = orgContext.getRoleChartService();
	/* 角色成员服务 */
	static final IRoleMemberService _rolemService = orgContext.getRoleMemberService();
}
