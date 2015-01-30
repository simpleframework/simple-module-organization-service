package net.simpleframework.organization.impl;

import static net.simpleframework.common.I18n.$m;
import net.simpleframework.ado.IParamsValue;
import net.simpleframework.ado.db.IDbEntityManager;
import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.organization.Department;
import net.simpleframework.organization.EDepartmentType;
import net.simpleframework.organization.ERoleChartMark;
import net.simpleframework.organization.IRoleChartService;
import net.simpleframework.organization.OrganizationException;
import net.simpleframework.organization.RoleChart;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class RoleChartService extends AbstractOrganizationService<RoleChart> implements
		IRoleChartService {

	@Override
	public IDataQuery<RoleChart> query(final Department dept) {
		return dept == null ? query("departmentid is null") : query("departmentid=?", dept.getId());
	}

	@Override
	public RoleChart getRoleChartByName(final String name) {
		return getBean("name=?", name);
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
					final Department dept = dService.getBean(chart.getDepartmentId());
					if (dept != null && dept.getDepartmentType() == EDepartmentType.department) {
						throw OrganizationException.of($m("RoleChartService.4"));
					}
				}
			}
		});
	}
}
