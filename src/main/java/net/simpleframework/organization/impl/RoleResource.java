package net.simpleframework.organization.impl;

import net.simpleframework.ado.bean.AbstractDescriptionBean;
import net.simpleframework.ado.db.common.EntityInterceptor;
import net.simpleframework.common.ID;
import net.simpleframework.organization.IRoleResource;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
@EntityInterceptor(listenerTypes = { "net.simpleframework.module.log.EntityDeleteLogAdapter" })
public class RoleResource extends AbstractDescriptionBean implements IRoleResource {
	private ID roleId;

	private int resourceType;

	private ID resourceId;

	private String resourceText;

	@Override
	public ID getRoleId() {
		return roleId;
	}

	@Override
	public void setRoleId(final ID roleId) {
		this.roleId = roleId;
	}

	@Override
	public int getResourceType() {
		return resourceType;
	}

	@Override
	public void setResourceType(final int resourceType) {
		this.resourceType = resourceType;
	}

	@Override
	public ID getResourceId() {
		return resourceId;
	}

	@Override
	public void setResourceId(final ID resourceId) {
		this.resourceId = resourceId;
	}

	@Override
	public String getResourceText() {
		return resourceText;
	}

	@Override
	public void setResourceText(final String resourceText) {
		this.resourceText = resourceText;
	}

	private static final long serialVersionUID = 6142589899318910488L;
}
