package net.simpleframework.organization;

import net.simpleframework.ado.bean.IDescriptionBeanAware;
import net.simpleframework.ado.bean.IIdBeanAware;
import net.simpleframework.common.ID;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public interface IRoleMember extends IIdBeanAware, IDescriptionBeanAware {

	/**
	 * 成员的角色id，角色类型一定为 {@link ERoleType}.normal
	 * 
	 * @return
	 */
	ID getRoleId();

	void setRoleId(ID roleId);

	/**
	 * 获取成员的类型， {@link ERoleMemberType}.role可能会产生嵌套
	 * 
	 * @return
	 */
	ERoleMemberType getMemberType();

	void setMemberType(ERoleMemberType memberType);

	/**
	 * 成员id，userid或roleid
	 * 
	 * @return
	 */
	ID getMemberId();

	void setMemberId(ID memberId);

	/**
	 * 当前角色是否为成员的主要角色。
	 * 
	 * 此含义为用户存在多个角色时，又没有具体明确需要的角色，则使用该主要角色
	 * 
	 * @return
	 */
	boolean isPrimaryRole();

	void setPrimaryRole(boolean primaryRole);
}
