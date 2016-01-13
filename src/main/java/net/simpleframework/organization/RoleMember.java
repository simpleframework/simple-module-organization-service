package net.simpleframework.organization;

import static net.simpleframework.common.I18n.$m;
import net.simpleframework.ado.bean.AbstractDescriptionBean;
import net.simpleframework.ado.bean.IOrderBeanAware;
import net.simpleframework.ado.db.common.EntityInterceptor;
import net.simpleframework.common.ID;
import net.simpleframework.organization.Role.ERoleType;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
@EntityInterceptor(listenerTypes = { "net.simpleframework.module.log.EntityDeleteLogAdapter" })
public class RoleMember extends AbstractDescriptionBean implements IOrderBeanAware,
		IOrganizationContextAware {
	private ID roleId;

	private ERoleMemberType memberType;

	private ID memberId;

	private ID deptId;

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

	public ID getDeptId() {
		if (deptId == null) {
			final ERoleMemberType mt = getMemberType();
			if (mt == ERoleMemberType.user) {
				final User user = _userService.getBean(getMemberId());
				return user != null ? user.getDepartmentId() : null;
			} else if (mt == ERoleMemberType.dept) {
				return getMemberId();
			}
		}
		return deptId;
	}

	public void setDeptId(final ID deptId) {
		this.deptId = deptId;
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
		final StringBuilder sb = new StringBuilder();
		final ERoleMemberType mType = getMemberType();
		if (mType == ERoleMemberType.user) {
			sb.append(_userService.getBean(getMemberId()));
		} else if (mType == ERoleMemberType.role) {
			final Role role = _roleService.getBean(getMemberId());
			if (role != null) {
				final RoleChart chart = _roleService.getRoleChart(role);
				sb.append(chart.getText()).append(":").append(role.getText());
			}
		} else {
			sb.append(_deptService.getBean(getMemberId()));
		}
		return sb.toString();
	}

	public static enum ERoleMemberType {
		/* 用户 */
		user {
			@Override
			public String toString() {
				return $m("ERoleMemberType.user");
			}
		},

		/* 角色，不能嵌套 */
		role {
			@Override
			public String toString() {
				return $m("ERoleMemberType.role");
			}
		},

		/* 部门 */
		dept {
			@Override
			public String toString() {
				return $m("ERoleMemberType.dept");
			}
		}
	}

	private static final long serialVersionUID = -6268885963912176924L;
}