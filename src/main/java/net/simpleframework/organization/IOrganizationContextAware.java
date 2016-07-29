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

	static final IDepartmentService _deptService = orgContext.getDepartmentService();

	static final IAccountService _accountService = orgContext.getAccountService();
	static final IUserService _userService = orgContext.getUserService();

	static final IRoleService _roleService = orgContext.getRoleService();
	static final IRoleChartService _rolecService = orgContext.getRoleChartService();
	static final IRoleMemberService _rolemService = orgContext.getRoleMemberService();

	static final IAccountStatService _accountStatService = orgContext.getAccountStatService();
}
