package net.simpleframework.organization;

import static net.simpleframework.common.I18n.$m;
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
	/* 机构id */
	private ID orgId;
	/* 是否内置的 */
	private ERoleChartMark chartMark;

	public ID getOrgId() {
		return orgId;
	}

	public void setOrgId(final ID orgId) {
		this.orgId = orgId;
	}

	@Override
	public void setName(final String name) {
		if (name != null && name.contains(":")) {
			throw OrganizationException.of($m("RoleChart.0"));
		}
		super.setName(name);
	}

	public ERoleChartMark getChartMark() {
		return chartMark == null ? ERoleChartMark.normal : chartMark;
	}

	public void setChartMark(final ERoleChartMark chartMark) {
		this.chartMark = chartMark;
	}

	public static enum ERoleChartMark {
		/**
		 * 正常标识
		 */
		normal,

		/**
		 * 内置角色视图标识
		 */
		builtIn
	}

	private static final long serialVersionUID = 7240516228770129459L;
}
