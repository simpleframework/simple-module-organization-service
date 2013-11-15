package net.simpleframework.organization.impl;

import net.simpleframework.ctx.service.ado.db.AbstractDbBeanService;
import net.simpleframework.organization.IOrganizationContextAware;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class AbstractOrganizationService<T, M extends T> extends AbstractDbBeanService<T>
		implements IOrganizationContextAware {

	protected AccountService getAccountService() {
		return (AccountService) context.getAccountService();
	}

	protected UserService getUserService() {
		return (UserService) context.getUserService();
	}

	protected DepartmentService getDepartmentService() {
		return (DepartmentService) context.getDepartmentService();
	}

	protected RoleChartService getRoleChartService() {
		return (RoleChartService) context.getRoleChartService();
	}

	protected RoleService getRoleService() {
		return (RoleService) context.getRoleService();
	}

	protected RoleMemberService getRoleMemberService() {
		return (RoleMemberService) context.getRoleMemberService();
	}
}
