package net.simpleframework.organization.impl;

import net.simpleframework.organization.IOrganizationContextAware;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public interface IOrganizationServiceAware extends IOrganizationContextAware {

	static AccountService aService = (AccountService) orgContext.getAccountService();
	static UserService uService = (UserService) orgContext.getUserService();
	static DepartmentService dService = (DepartmentService) orgContext.getDepartmentService();

	static RoleChartService rcService = (RoleChartService) orgContext.getRoleChartService();
	static RoleService rService = (RoleService) orgContext.getRoleService();

	static RoleMemberService rmService = (RoleMemberService) orgContext.getRoleMemberService();
}
