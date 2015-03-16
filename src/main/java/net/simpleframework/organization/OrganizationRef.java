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

	protected void createRole_SystemChart2(final String name, final String text) {
		createRole_SystemChart2(name, text, ERoleType.normal);
	}

	protected void createRole_SystemChart2(final String name, final String text,
			final ERoleType rType) {
		final RoleChart sc = getModuleContext().getSystemChart();
		final String[] arr = RolenameW.split(name);
		final String r = arr != null && arr.length == 2 ? arr[1] : name;
		final IRoleService service = getRoleService();
		Role role = service.getRoleByName(sc, r);
		if (role == null) {
			role = service.createBean();
			role.setName(r);
			role.setText(text);
			role.setRoleChartId(sc.getId());
			role.setRoleMark(ERoleMark.builtIn);
			role.setRoleType(rType);
			service.insert(role);
		}
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
