package net.simpleframework.organization.impl;

import net.simpleframework.organization.IOrganizationContextAware;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public interface IOrganizationServiceImplAware extends IOrganizationContextAware {

	static UserService userService = (UserService) orgContext.getUserService();

	static AccountService accountService = (AccountService) orgContext.getAccountService();

	static DepartmentService deptService = (DepartmentService) orgContext.getDepartmentService();

	static RoleService roleService = (RoleService) orgContext.getRoleService();
	static RoleChartService rolecService = (RoleChartService) orgContext.getRoleChartService();

	static RoleMemberService rolemService = (RoleMemberService) orgContext.getRoleMemberService();

	static AccountStatService accountStatService = (AccountStatService) orgContext
			.getAccountStatService();
}
