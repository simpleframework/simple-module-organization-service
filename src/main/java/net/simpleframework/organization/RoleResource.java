package net.simpleframework.organization;

import net.simpleframework.ado.bean.AbstractDescriptionBean;
import net.simpleframework.ado.db.common.EntityInterceptor;
import net.simpleframework.common.ID;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
@EntityInterceptor(listenerTypes = { "net.simpleframework.module.log.EntityDeleteLogAdapter" })
public class RoleResource extends AbstractDescriptionBean {
	private ID roleId;

	private int resourceType;

	private ID resourceId;

	private String resourceText;

	/**
	 * 与资源关联的角色id
	 * 
	 * @return
	 */
	public ID getRoleId() {
		return roleId;
	}

	public void setRoleId(final ID roleId) {
		this.roleId = roleId;
	}

	/**
	 * 资源类型被定义一个整型，由使用者确定其具体值
	 * 
	 * @return
	 */
	public int getResourceType() {
		return resourceType;
	}

	public void setResourceType(final int resourceType) {
		this.resourceType = resourceType;
	}

	public ID getResourceId() {
		return resourceId;
	}

	public void setResourceId(final ID resourceId) {
		this.resourceId = resourceId;
	}

	public String getResourceText() {
		return resourceText;
	}

	public void setResourceText(final String resourceText) {
		this.resourceText = resourceText;
	}

	private static final long serialVersionUID = 6142589899318910488L;
}
