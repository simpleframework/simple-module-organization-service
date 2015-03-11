package net.simpleframework.organization.impl;

import static net.simpleframework.common.I18n.$m;
import net.simpleframework.ado.IParamsValue;
import net.simpleframework.ado.db.IDbEntityManager;
import net.simpleframework.ado.query.DataQueryUtils;
import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.organization.Department;
import net.simpleframework.organization.EDepartmentType;
import net.simpleframework.organization.ERoleChartMark;
import net.simpleframework.organization.IRoleChartService;
import net.simpleframework.organization.OrganizationException;
import net.simpleframework.organization.RoleChart;
import net.simpleframework.organization.RolenameConst;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class RoleChartService extends AbstractOrganizationService<RoleChart> implements
		IRoleChartService {
	@Override
	public IDataQuery<RoleChart> queryOrgCharts(final Department org) {
		if (org == null) {
			return DataQueryUtils.nullQuery();
		}
		return query("orgid=?", org.getId());
	}

	@Override
	public IDataQuery<RoleChart> queryGlobalCharts() {
		return query("orgid is null");
	}

	@Override
	public String toUniqueName(final RoleChart chart) {
		final Department org = dService.getBean(chart.getOrgId());
		return RolenameConst.toUniqueChartname(org != null ? org.getName() : null, chart.getName());
	}

	@Override
	public RoleChart getRoleChartByName(final String name) {
		RoleChart chart = null;
		final String[] arr = RolenameConst.split(name);
		if (arr.length == 2) {
			final Department org = dService.getDepartmentByName(arr[0]);
			if (org != null) {
				chart = getRoleChartByName(org, name);
			}
		} else {
			chart = getBean("orgid is null and name=?", name);
		}
		return chart;
	}

	@Override
	public RoleChart getRoleChartByName(final Department org, final String name) {
		if (org == null) {
			return null;
		}
		return getBean("orgid=? and name=?", org.getId(), name);
	}

	@Override
	public void onInit() throws Exception {
		super.onInit();

		addListener(new DbEntityAdapterEx() {
			@Override
			public void onBeforeDelete(final IDbEntityManager<?> service,
					final IParamsValue paramsValue) {
				super.onBeforeDelete(service, paramsValue);

				for (final RoleChart chart : coll(paramsValue)) {
					// 内置视图
					if (chart.getChartMark() == ERoleChartMark.builtIn) {
						throw OrganizationException.of($m("RoleChartService.2"));
					}

					// 已存在角色
					if (rService.queryRoot(chart).getCount() > 0) {
						throw OrganizationException.of($m("RoleChartService.3"));
					}
				}
			}

			@Override
			public void onBeforeInsert(final IDbEntityManager<?> manager, final Object[] beans) {
				super.onBeforeInsert(manager, beans);
				// 视图只能加在机构上
				for (final Object o : beans) {
					final RoleChart chart = (RoleChart) o;
					final Department org = dService.getBean(chart.getOrgId());
					if (org != null && org.getDepartmentType() == EDepartmentType.department) {
						throw OrganizationException.of($m("RoleChartService.4"));
					}
				}
			}
		});
	}
}
