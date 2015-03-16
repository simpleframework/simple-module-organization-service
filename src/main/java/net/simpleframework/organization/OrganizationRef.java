package net.simpleframework.organization;

import net.simpleframework.ctx.AbstractModuleRef;
import net.simpleframework.ctx.ModuleContextFactory;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class OrganizationRef extends AbstractModuleRef {

	@Override
	public IOrganizationContext getModuleContext() {
		return ModuleContextFactory.get(IOrganizationContext.class);
	}

	public String toOrgText(final Object id) {
		final Department dept = getDepartmentService().getBean(id);
		return dept != null ? dept.toString() : null;
	}

	public IDepartmentService getDepartmentService() {
		return getModuleContext().getDepartmentService();
	}

	public IAccountService getAccountService() {
		return getModuleContext().getAccountService();
	}

	public IUserService getUserService() {
		return getModuleContext().getUserService();
	}

	public IRoleService getRoleService() {
		return getModuleContext().getRoleService();
	}

	public IRoleChartService getRoleChartService() {
		return getModuleContext().getRoleChartService();
	}
}
