package net.simpleframework.organization.impl;

import net.simpleframework.ctx.service.ado.db.AbstractDbBeanService;
import net.simpleframework.organization.IOrganizationContextAware;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class AbstractOrganizationService<T> extends AbstractDbBeanService<T> implements
		IOrganizationContextAware {

	protected AccountService getAccountService() {
		return (AccountService) orgContext.getAccountService();
	}

	protected UserService getUserService() {
		return (UserService) orgContext.getUserService();
	}

	protected DepartmentService getDepartmentService() {
		return (DepartmentService) orgContext.getDepartmentService();
	}

	protected RoleChartService getRoleChartService() {
		return (RoleChartService) orgContext.getRoleChartService();
	}

	protected RoleService getRoleService() {
		return (RoleService) orgContext.getRoleService();
	}

	protected RoleMemberService getRoleMemberService() {
		return (RoleMemberService) orgContext.getRoleMemberService();
	}
}
