package net.simpleframework.organization;

import java.util.Iterator;
import java.util.Map;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.ctx.service.ado.ITreeBeanServiceAware;
import net.simpleframework.ctx.service.ado.db.IDbBeanService;
import net.simpleframework.organization.role.IRoleHandler;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public interface IRoleService extends IDbBeanService<Role>, ITreeBeanServiceAware<Role> {

	/**
	 * 获取规则角色的handler
	 * 
	 * @param role
	 * @return 非规则角色则返回null
	 */
	IRoleHandler getRoleHandler(Role role);

	/**
	 * 获取唯一角色名
	 * 
	 * @return
	 */
	String toUniqueName(Role role);

	/**
	 * 获取角色所在的视图
	 * 
	 * @param role
	 * @return
	 */
	RoleChart getRoleChart(Role role);

	/**
	 * 根据名字获取角色对象.
	 * 
	 * 名字的规则为chartname:rolename
	 * 
	 * @param name
	 * @return
	 */
	Role getRoleByName(String name);

	/**
	 * 根据视图和角色显示名称获取角色对象
	 * 
	 * @param roleChart
	 * @param name
	 * @return
	 */
	Role getRoleByName(RoleChart roleChart, String name);

	/**
	 * 根据角色视图的根角色
	 * 
	 * @param roleChart
	 * @return
	 */
	IDataQuery<Role> queryRoot(RoleChart roleChart);

	/**
	 * 获取视图下所有角色
	 * 
	 * @param roleChart
	 * @return
	 */
	IDataQuery<Role> queryRoles(RoleChart roleChart);

	/**
	 * 判断指定用户是否为指定角色的成员
	 * 
	 * @param user
	 * @param role
	 * @param variables
	 *        环境变量
	 * @return
	 */
	boolean isMember(User user, Role role, Map<String, Object> variables);

	/**
	 * 判断指定用户是否为参数roleRule指定角色的成员
	 * 
	 * @param user
	 * @param roleRule
	 *        按规则指定的角色，目前形式为 role1;role2;#user1, 当以#开头表示用户名
	 * @param variables
	 * @return
	 */
	boolean isMember(User user, String roleRule, Map<String, Object> variables);

	/**
	 * 是否是管理员角色
	 * 
	 * @param user
	 * @param variables
	 * @return
	 */
	boolean isManager(User user, Map<String, Object> variables);

	/**
	 * 获取角色的成员列表，和{@link #members(IRole)}的区别：members返回成员的关系，而users返回用户
	 * 
	 * @param role
	 * @param variables
	 * @return
	 */
	Iterator<User> users(Role role, Map<String, Object> variables);

	/**
	 * 获取某一部门下的所有用户
	 * 
	 * @param dept
	 * @param variables
	 * @return
	 */
	Iterator<User> users(Department dept, Map<String, Object> variables);

	/**
	 * 获取指定用户的角色
	 * 
	 * @param user
	 * @param variables
	 *        ruleRole: 是否返回规则角色
	 * 
	 *        userRole: 是否仅返回用户角色
	 * @return
	 */
	Iterator<RoleM> roles(User user, Map<String, Object> variables);

	Iterator<RoleM> roles(User user);

	/**
	 * 获取用户的主要角色
	 * 
	 * @param user
	 * @return
	 */
	Role getPrimaryRole(User user);

	public static class RoleM {
		public RoleMember rm;
		public Role role;

		public RoleM(final Role role, final RoleMember rm) {
			this.role = role;
			this.rm = rm;
		}

		public RoleM(final Role role) {
			this(role, null);
		}
	}
}
