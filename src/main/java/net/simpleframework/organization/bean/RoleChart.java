package net.simpleframework.organization.bean;

import static net.simpleframework.common.I18n.$m;
import net.simpleframework.ado.db.common.EntityInterceptor;
import net.simpleframework.common.ID;
import net.simpleframework.organization.OrganizationException;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
@EntityInterceptor(listenerTypes = { "net.simpleframework.module.log.EntityDeleteLogAdapter" })
public class RoleChart extends AbstractOrganizationBean {
	/* 机构id */
	private ID orgId;

	/* 统计字段，角色数 */
	private int roles;

	public ID getOrgId() {
		return orgId;
	}

	public void setOrgId(final ID orgId) {
		this.orgId = orgId;
	}

	@Override
	public void setName(final String name) {
		if (name != null && name.contains(":")) {
			throw OrganizationException.of($m("RoleChart.0"));
		}
		super.setName(name);
	}

	public int getRoles() {
		return roles;
	}

	public void setRoles(final int roles) {
		this.roles = roles;
	}

	private static final long serialVersionUID = 7240516228770129459L;
}
