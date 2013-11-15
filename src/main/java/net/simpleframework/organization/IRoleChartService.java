package net.simpleframework.organization;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.ctx.service.ado.db.IDbBeanService;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public interface IRoleChartService extends IDbBeanService<IRoleChart> {

	/**
	 * 获取部门下的角色视图
	 * 
	 * @param dept
	 *           null返回全局视图
	 * @return
	 */
	IDataQuery<? extends IRoleChart> query(IDepartment dept);

	/**
	 * 根据名称获取视图
	 * 
	 * @param name
	 * @return
	 */
	IRoleChart getRoleChartByName(String name);
}
