package net.simpleframework.organization.impl;

import net.simpleframework.ado.ColumnData;
import net.simpleframework.ado.db.DbEntityTable;
import net.simpleframework.ado.db.common.EntityInterceptor;
import net.simpleframework.common.ID;
import net.simpleframework.organization.ERoleChartMark;
import net.simpleframework.organization.IRoleChart;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
@EntityInterceptor(listenerTypes = { "net.simpleframework.module.log.EntityDeleteLogAdapter" })
public class RoleChart extends AbstractOrganizationBean implements IRoleChart {

	private ID departmentId;

	/**
	 * 是否内置的
	 */
	private ERoleChartMark chartMark;

	@Override
	public ID getDepartmentId() {
		return departmentId;
	}

	@Override
	public void setDepartmentId(final ID departmentId) {
		this.departmentId = departmentId;
	}

	@Override
	public ERoleChartMark getChartMark() {
		return chartMark == null ? ERoleChartMark.normal : chartMark;
	}

	@Override
	public void setChartMark(final ERoleChartMark chartMark) {
		this.chartMark = chartMark;
	}

	public static DbEntityTable TBL = new DbEntityTable(RoleChart.class, "sf_organization_rolechart")
			.setDefaultOrder(ColumnData.ORDER);

	private static final long serialVersionUID = 7240516228770129459L;
}
