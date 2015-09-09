package net.simpleframework.organization;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.ctx.service.ado.db.IDbBeanService;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public interface IRoleMemberService extends IDbBeanService<RoleMember> {

	/**
	 * 获取指定角色的成员
	 * 
	 * @param role
	 * @param dept
	 * @return 当角色类型为规则角色时，返回空集合
	 */
	IDataQuery<RoleMember> queryRoleMembers(Role role, Department dept);

	/**
	 * 设置当前成员为主要成员
	 * 
	 * @param member
	 */
	void setPrimary(RoleMember member);
}
