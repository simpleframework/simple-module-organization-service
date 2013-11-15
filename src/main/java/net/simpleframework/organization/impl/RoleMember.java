package net.simpleframework.organization.impl;

import net.simpleframework.ado.bean.AbstractIdBean;
import net.simpleframework.ado.db.DbEntityTable;
import net.simpleframework.ado.db.common.EntityInterceptor;
import net.simpleframework.common.ID;
import net.simpleframework.ctx.ModuleContextFactory;
import net.simpleframework.organization.ERoleMemberType;
import net.simpleframework.organization.IOrganizationContext;
import net.simpleframework.organization.IRoleMember;
import net.simpleframework.organization.IRoleService;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
@EntityInterceptor(listenerTypes = { "net.simpleframework.module.log.EntityDeleteLogAdapter" })
public class RoleMember extends AbstractIdBean implements IRoleMember {
	private ID roleId;

	private ERoleMemberType memberType;

	private ID memberId;

	private boolean primaryRole;

	private String description;

	@Override
	public ID getRoleId() {
		return roleId;
	}

	@Override
	public void setRoleId(final ID roleId) {
		this.roleId = roleId;
	}

	@Override
	public ERoleMemberType getMemberType() {
		return memberType;
	}

	@Override
	public void setMemberType(final ERoleMemberType memberType) {
		this.memberType = memberType;
	}

	@Override
	public ID getMemberId() {
		return memberId;
	}

	@Override
	public void setMemberId(final ID memberId) {
		this.memberId = memberId;
	}

	@Override
	public boolean isPrimaryRole() {
		return primaryRole;
	}

	@Override
	public void setPrimaryRole(final boolean primaryRole) {
		this.primaryRole = primaryRole;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(final String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		final IOrganizationContext context = ModuleContextFactory.get(IOrganizationContext.class);
		final StringBuilder sb = new StringBuilder();
		final IRoleService rService = context.getRoleService();
		sb.append(rService.getBean(getRoleId())).append(" - ");
		final ERoleMemberType mType = getMemberType();
		if (mType == ERoleMemberType.user) {
			sb.append(context.getUserService().getBean(getMemberId()));
		} else {
			sb.append(rService.getBean(getMemberId()));
		}
		return sb.toString();
	}

	public static DbEntityTable TBL = new DbEntityTable(RoleMember.class,
			"sf_organization_rolemember");

	private static final long serialVersionUID = -6268885963912176924L;
}