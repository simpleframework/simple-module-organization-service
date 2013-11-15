package net.simpleframework.organization;

import net.simpleframework.ctx.IModuleRef;
import net.simpleframework.ctx.service.ado.db.IDbModuleContext;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public interface IOrganizationContext extends IDbModuleContext {

	static final String MODULE_NAME = "simple-module-organization";

	/**
	 * 获取系统缺省角色视图
	 * 
	 * @return
	 */
	IRoleChart getSystemChart();

	/**
	 * 获取账号管理器
	 * 
	 * @return
	 */
	IAccountService getAccountService();

	/**
	 * 获取用户管理器
	 * 
	 * @return
	 */
	IUserService getUserService();

	/**
	 * 获取部门管理器
	 * 
	 * @return
	 */
	IDepartmentService getDepartmentService();

	/**
	 * 获取角色管理器
	 * 
	 * @return
	 */
	IRoleService getRoleService();

	/**
	 * 获取角色成员管理器
	 * 
	 * @return
	 */
	IRoleMemberService getRoleMemberService();

	/**
	 * 获取角色视图管理器
	 * 
	 * @return
	 */
	IRoleChartService getRoleChartService();

	/**
	 * 
	 * @return
	 */
	IModuleRef getMessageRef();
}
