package net.simpleframework.organization.impl;

import static net.simpleframework.common.I18n.$m;
import net.simpleframework.ado.IParamsValue;
import net.simpleframework.ado.db.IDbEntityManager;
import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.organization.ERoleChartMark;
import net.simpleframework.organization.IDepartment;
import net.simpleframework.organization.IRoleChart;
import net.simpleframework.organization.IRoleChartService;
import net.simpleframework.organization.OrganizationException;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class RoleChartService extends AbstractOrganizationService<IRoleChart, RoleChart> implements
		IRoleChartService {

	@Override
	public IDataQuery<? extends IRoleChart> query(final IDepartment dept) {
		return dept == null ? query("departmentid is null") : query("departmentid=?", dept.getId());
	}

	@Override
	public IRoleChart getRoleChartByName(final String name) {
		return getBean("name=?", name);
	}

	@Override
	public void onInit() throws Exception {
		addListener(new DbEntityAdapterEx() {
			@Override
			public void onBeforeDelete(final IDbEntityManager<?> service,
					final IParamsValue paramsValue) {
				super.onBeforeDelete(service, paramsValue);

				for (final IRoleChart chart : coll(paramsValue)) {
					// 内置视图
					if (chart.getChartMark() == ERoleChartMark.builtIn) {
						throw OrganizationException.of($m("RoleChartService.2"));
					}

					// 已存在角色
					if (getRoleService().queryRoot(chart).getCount() > 0) {
						throw OrganizationException.of($m("RoleChartService.3"));
					}
				}
			}
		});
	}
}
