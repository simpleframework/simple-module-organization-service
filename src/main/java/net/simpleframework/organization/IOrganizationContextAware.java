package net.simpleframework.organization;

import net.simpleframework.ctx.IModuleContextAware;
import net.simpleframework.ctx.ModuleContextFactory;
import net.simpleframework.organization.impl.AccountService;
import net.simpleframework.organization.impl.DepartmentService;
import net.simpleframework.organization.impl.RoleChartService;
import net.simpleframework.organization.impl.RoleMemberService;
import net.simpleframework.organization.impl.RoleService;
import net.simpleframework.organization.impl.UserService;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public interface IOrganizationContextAware extends IModuleContextAware {

	static final IOrganizationContext orgContext = ModuleContextFactory
			.get(IOrganizationContext.class);

	static AccountService aService = (AccountService) orgContext.getAccountService();
	static UserService uService = (UserService) orgContext.getUserService();
	static DepartmentService dService = (DepartmentService) orgContext.getDepartmentService();

	static RoleChartService rcService = (RoleChartService) orgContext.getRoleChartService();
	static RoleService rService = (RoleService) orgContext.getRoleService();

	static RoleMemberService rmService = (RoleMemberService) orgContext.getRoleMemberService();
}
