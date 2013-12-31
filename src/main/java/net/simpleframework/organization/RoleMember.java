package net.simpleframework.organization;

import net.simpleframework.ado.bean.AbstractDescriptionBean;
import net.simpleframework.ado.bean.IOrderBeanAware;
import net.simpleframework.ado.db.DbEntityTable;
import net.simpleframework.ado.db.common.EntityInterceptor;
import net.simpleframework.common.ID;
import net.simpleframework.ctx.ModuleContextFactory;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
@EntityInterceptor(listenerTypes = { "net.simpleframework.module.log.EntityDeleteLogAdapter" })
public class RoleMember extends AbstractDescriptionBean implements IOrderBeanAware {
	private ID roleId;

	private ERoleMemberType memberType;

	private ID memberId;

	private boolean primaryRole;

	/** 排序 **/
	private int oorder;

	/**
	 * 成员的角色id，角色类型一定为 {@link ERoleType}.normal
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
	 * 获取成员的类型， {@link ERoleMemberType}.role可能会产生嵌套
	 * 
	 * @return
	 */
	public ERoleMemberType getMemberType() {
		return memberType;
	}

	public void setMemberType(final ERoleMemberType memberType) {
		this.memberType = memberType;
	}

	public ID getMemberId() {
		return memberId;
	}

	public void setMemberId(final ID memberId) {
		this.memberId = memberId;
	}

	/**
	 * 当前角色是否为成员的主要角色。
	 * 
	 * 此含义为用户存在多个角色时，又没有具体明确需要的角色，则使用该主要角色
	 * 
	 * @return
	 */
	public boolean isPrimaryRole() {
		return primaryRole;
	}

	public void setPrimaryRole(final boolean primaryRole) {
		this.primaryRole = primaryRole;
	}

	@Override
	public int getOorder() {
		return oorder;
	}

	@Override
	public void setOorder(final int oorder) {
		this.oorder = oorder;
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