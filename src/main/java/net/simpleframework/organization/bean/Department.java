package net.simpleframework.organization.bean;

import static net.simpleframework.common.I18n.$m;
import net.simpleframework.ado.bean.ITreeBeanAware;
import net.simpleframework.ado.db.common.EntityInterceptor;
import net.simpleframework.common.ID;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
@EntityInterceptor(listenerTypes = { "net.simpleframework.module.log.EntityDeleteLogAdapter" })
public class Department extends AbstractOrganizationBean implements ITreeBeanAware {

	private ID parentId;

	/* 部门类型 */
	private EDepartmentType departmentType;

	@Override
	public ID getParentId() {
		return parentId;
	}

	@Override
	public void setParentId(final ID parentId) {
		this.parentId = parentId;
	}

	public EDepartmentType getDepartmentType() {
		return departmentType == null ? EDepartmentType.department : departmentType;
	}

	public void setDepartmentType(final EDepartmentType departmentType) {
		this.departmentType = departmentType;
	}

	// public int getUsers() {
	// return users;
	// }
	//
	// public void setUsers(final int users) {
	// this.users = users;
	// }
	public static enum EDepartmentType {
		/**
		 * 部门
		 */
		department {

			@Override
			public String toString() {
				return $m("EDepartmentType.department");
			}
		},
		/**
		 * 机构
		 */
		organization {

			@Override
			public String toString() {
				return $m("EDepartmentType.organization");
			}
		}
	}

	private static final long serialVersionUID = 4763200601974069965L;
}
