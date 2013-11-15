package net.simpleframework.organization;

import net.simpleframework.ado.bean.IDescriptionBeanAware;
import net.simpleframework.ado.bean.IIdBeanAware;
import net.simpleframework.ado.bean.INameBeanAware;
import net.simpleframework.ado.bean.IOrderBeanAware;
import net.simpleframework.ado.bean.ITextBeanAware;
import net.simpleframework.common.ID;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public interface IRoleChart extends IIdBeanAware, INameBeanAware, ITextBeanAware,
		IDescriptionBeanAware, IOrderBeanAware {

	/**
	 * 获取角色视图所在的部门id。返回null表示全局视图
	 * 
	 * @return
	 */
	ID getDepartmentId();

	void setDepartmentId(ID departmentId);

	/**
	 * 
	 * @return
	 */
	ERoleChartMark getChartMark();

	void setChartMark(ERoleChartMark chartMark);
}