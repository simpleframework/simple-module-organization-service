package net.simpleframework.organization.impl;

import net.simpleframework.ado.db.DbEntityTable;
import net.simpleframework.ado.db.common.EntityInterceptor;
import net.simpleframework.common.ID;
import net.simpleframework.organization.EDepartmentType;
import net.simpleframework.organization.IDepartment;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
@EntityInterceptor(listenerTypes = { "net.simpleframework.module.log.EntityDeleteLogAdapter" })
public class Department extends AbstractOrganizationBean implements IDepartment {

	private ID parentId;

	private EDepartmentType departmentType;

	@Override
	public ID getParentId() {
		return parentId;
	}

	@Override
	public void setParentId(final ID parentId) {
		this.parentId = parentId;
	}

	@Override
	public EDepartmentType getDepartmentType() {
		return departmentType == null ? EDepartmentType.department : departmentType;
	}

	@Override
	public void setDepartmentType(final EDepartmentType departmentType) {
		this.departmentType = departmentType;
	}

	public static DbEntityTable TBL = new DbEntityTable(Department.class,
			"sf_organization_department");

	private static final long serialVersionUID = 4763200601974069965L;
}
