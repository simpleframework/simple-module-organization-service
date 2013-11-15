package net.simpleframework.organization;

import java.util.Collection;
import java.util.Map;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.ctx.service.ado.IADOTreeBeanServiceAware;
import net.simpleframework.ctx.service.ado.db.IDbBeanService;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public interface IRoleService extends IDbBeanService<IRole>, IADOTreeBeanServiceAware<IRole> {

	/**
	 * 获取规则角色的handler
	 * 
	 * @param role
	 * @return 非规则角色则返回null
	 */
	IRoleHandler getRoleHandler(IRole role);

	/**
	 * 获取唯一角色名
	 * 
	 * @return
	 */
	String toUniqueName(IRole role);

	/**
	 * 获取角色所在的视图
	 * 
	 * @param role
	 * @return
	 */
	IRoleChart getRoleChart(IRole role);

	/**
	 * 根据名字获取角色对象.
	 * 
	 * 名字的规则为chartname:rolename
	 * 
	 * @param name
	 * @return
	 */
	IRole getRoleByName(String name);

	/**
	 * 根据视图和角色显示名称获取角色对象
	 * 
	 * @param roleChart
	 * @param text
	 * @return
	 */
	IRole getRoleByName(IRoleChart roleChart, String text);

	/**
	 * 根据角色视图的根角色
	 * 
	 * @param roleChart
	 * @return
	 */
	IDataQuery<IRole> queryRoot(IRoleChart roleChart);

	/**
	 * 获取视图下所有角色
	 * 
	 * @param roleChart
	 * @return
	 */
	IDataQuery<IRole> queryRoles(IRoleChart roleChart);

	/**
	 * 判断指定用户是否为指定角色的成员
	 * 
	 * @param user
	 * @param role
	 * @param variables
	 *           环境变量
	 * @return
	 */
	boolean isMember(IUser user, IRole role, Map<String, Object> variables);

	/**
	 * 判断指定用户是否为参数roleRule指定角色的成员
	 * 
	 * @param user
	 * @param roleRule
	 *           按规则指定的角色，目前形式为 role1;role2;#user1, 当以#开头表示用户名
	 * @param variables
	 * @return
	 */
	boolean isMember(IUser user, String roleRule, Map<String, Object> variables);

	/**
	 * 是否是管理员角色
	 * 
	 * @param user
	 * @param variables
	 * @return
	 */
	boolean isManager(IUser user, Map<String, Object> variables);

	/**
	 * 获取指定角色的成员
	 * 
	 * @param role
	 * @return 当角色类型为规则角色时，返回空集合
	 */
	IDataQuery<? extends IRoleMember> members(IRole role);

	/**
	 * 获取角色的成员列表，和{@link #members(IRole)}的区别：members返回成员的关系，而users返回用户
	 * 
	 * @param role
	 * @param variables
	 * @return
	 */
	Collection<? extends IUser> users(IRole role, Map<String, Object> variables);
}
