package net.simpleframework.organization.impl;

import static net.simpleframework.common.I18n.$m;
import net.simpleframework.ado.ColumnData;
import net.simpleframework.ado.IParamsValue;
import net.simpleframework.ado.db.IDbEntityManager;
import net.simpleframework.ctx.service.ado.db.AbstractDbBeanService;
import net.simpleframework.organization.Department;
import net.simpleframework.organization.IDepartmentService;
import net.simpleframework.organization.OrganizationException;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class DepartmentService extends AbstractDbBeanService<Department> implements
		IDepartmentService, IOrganizationServiceImplAware {

	@Override
	protected ColumnData[] getDefaultOrderColumns() {
		return ORDER_OORDER;
	}

	@Override
	public void onInit() throws Exception {
		addListener(new DbEntityAdapterEx() {
			@Override
			public void onBeforeDelete(final IDbEntityManager<?> service,
					final IParamsValue paramsValue) {
				super.onBeforeDelete(service, paramsValue);

				for (final Department dept : coll(paramsValue)) {
					// 存在子部门
					if (queryChildren(dept).getCount() > 0) {
						throw OrganizationException.of($m("DepartmentService.0"));
					}
					// 存在用户
					if (uService.query("departmentId=?", dept.getId()).getCount() > 0) {
						throw OrganizationException.of($m("DepartmentService.1"));
					}
				}
			}
		});
	}
}
