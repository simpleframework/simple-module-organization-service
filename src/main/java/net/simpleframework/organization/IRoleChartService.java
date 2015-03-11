package net.simpleframework.organization;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.ctx.service.ado.db.IDbBeanService;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public interface IRoleChartService extends IDbBeanService<RoleChart> {

	/**
	 * 获取机构下的角色视图
	 * 
	 * @param org
	 * @return
	 */
	IDataQuery<RoleChart> queryOrgCharts(Department org);

	IDataQuery<RoleChart> queryGlobalCharts();

	/**
	 * 根据名称获取视图
	 * 
	 * @param name
	 * @return
	 */
	RoleChart getRoleChartByName(String name);

	/**
	 * 根据显示名称获取视图
	 * 
	 * @param org
	 * @param text
	 * @return
	 */
	RoleChart getRoleChartByName(Department org, String name);

	String toUniqueName(RoleChart chart);
}
