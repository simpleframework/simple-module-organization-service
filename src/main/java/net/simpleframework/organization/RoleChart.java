package net.simpleframework.organization;

import net.simpleframework.ado.db.common.EntityInterceptor;
import net.simpleframework.common.ID;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
@EntityInterceptor(listenerTypes = { "net.simpleframework.module.log.EntityDeleteLogAdapter" })
public class RoleChart extends AbstractOrganizationBean {

	private ID departmentId;

	/**
	 * 是否内置的
	 */
	private ERoleChartMark chartMark;

	public ID getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(final ID departmentId) {
		this.departmentId = departmentId;
	}

	public ERoleChartMark getChartMark() {
		return chartMark == null ? ERoleChartMark.normal : chartMark;
	}

	public void setChartMark(final ERoleChartMark chartMark) {
		this.chartMark = chartMark;
	}

	private static final long serialVersionUID = 7240516228770129459L;
}
