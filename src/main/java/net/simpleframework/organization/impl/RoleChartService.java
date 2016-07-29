package net.simpleframework.organization.impl;

import static net.simpleframework.common.I18n.$m;
import net.simpleframework.ado.IParamsValue;
import net.simpleframework.ado.db.IDbEntityManager;
import net.simpleframework.ado.query.DataQueryUtils;
import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.organization.IRoleChartService;
import net.simpleframework.organization.OrganizationException;
import net.simpleframework.organization.bean.Department;
import net.simpleframework.organization.bean.Department.EDepartmentType;
import net.simpleframework.organization.bean.RoleChart;
import net.simpleframework.organization.role.RolenameW;
import net.simpleframework.organization.role.RolenameW.ChartW;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
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
	public RoleChart getDefaultSysChart() {
		return getRoleChartByName(RolenameW.ROLECHART_SYSTEM);
	}

	@Override
	public RoleChart getDefaultOrgChart(final Department org) {
		return getRoleChartByName(org, RolenameW.ROLECHART_ORG_DEFAULT);
	}

	@Override
	public IDataQuery<RoleChart> queryGlobalCharts() {
		return query("orgid is null");
	}

	@Override
	public String toUniqueName(final RoleChart chart) {
		final Department org = _deptService.getBean(chart.getOrgId());
		return RolenameW.toUniqueChartname(org != null ? org.getName() : null, chart.getName());
	}

	@Override
	public RoleChart getRoleChartByName(final String name) {
		RoleChart chart = null;
		final String[] arr = RolenameW.split(name);
		if (arr.length == 2) {
			chart = getRoleChartByName(_deptService.getDepartmentByName(arr[0]), arr[1]);
		} else {
			chart = getBean("orgid is null and name=?", name);
			ChartW w;
			if (chart == null && (w = RolenameW.getBuiltInChart(name)) != null && !w.isOrg()) {
				_rolecService.insert(chart = createChart(w));
			}
		}
		return chart;
	}

	@Override
	public RoleChart getRoleChartByName(final Department org, final String name) {
		if (org == null) {
			return null;
		}
		RoleChart chart = getBean("orgid=? and name=?", org.getId(), name);
		ChartW w;
		if (chart == null && (w = RolenameW.getBuiltInChart(name)) != null && w.isOrg()) {
			chart = createChart(w);
			chart.setOrgId(org.getId());
			_rolecService.insert(chart);
		}
		return chart;
	}

	private RoleChart createChart(final ChartW w) {
		final RoleChart chart = createBean();
		chart.setName(w.getName());
		chart.setText(w.getText());
		chart.setDescription(w.getDescription());
		return chart;
	}

	@Override
	public void onInit() throws Exception {
		super.onInit();

		addListener(new DbEntityAdapterEx<RoleChart>() {
			@Override
			public void onBeforeDelete(final IDbEntityManager<RoleChart> manager,
					final IParamsValue paramsValue) throws Exception {
				super.onBeforeDelete(manager, paramsValue);

				for (final RoleChart chart : coll(manager, paramsValue)) {
					// 已存在角色
					if (_roleService.queryRoot(chart).getCount() > 0) {
						throw OrganizationException.of($m("RoleChartService.2"));
					}
				}
			}

			@Override
			public void onBeforeInsert(final IDbEntityManager<RoleChart> manager,
					final RoleChart[] beans) throws Exception {
				super.onBeforeInsert(manager, beans);
				// 视图只能加在机构上
				for (final RoleChart chart : beans) {
					final Department org = _deptService.getBean(chart.getOrgId());
					if (org != null && org.getDepartmentType() == EDepartmentType.department) {
						throw OrganizationException.of($m("RoleChartService.4"));
					}
				}
			}
		});
	}
}
