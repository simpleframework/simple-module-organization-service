package net.simpleframework.organization.impl;

import static net.simpleframework.common.I18n.$m;
import net.simpleframework.ado.ColumnData;
import net.simpleframework.ado.FilterItems;
import net.simpleframework.ado.IParamsValue;
import net.simpleframework.ado.db.IDbEntityManager;
import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.ctx.service.ado.db.AbstractDbBeanService;
import net.simpleframework.organization.Department;
import net.simpleframework.organization.EDepartmentType;
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
	public Department getDepartmentByName(final String name) {
		return getBean("name=?", name);
	}

	@Override
	public Department getOrg(final Department dept) {
		Department org = dept;
		while (org != null && org.getDepartmentType() != EDepartmentType.organization) {
			org = getBean(org.getParentId());
		}
		return org;
	}

	@Override
	public IDataQuery<Department> queryChildren(final Department parent,
			final EDepartmentType departmentType, final ColumnData... orderColumns) {
		final FilterItems items = FilterItems.of().addEqual("parentid",
				parent == null ? null : parent.getId());
		if (departmentType != null) {
			items.addEqual("departmentType", departmentType);
		}
		return queryByParams(items, orderColumns);
	}

	@Override
	protected ColumnData[] getDefaultOrderColumns() {
		return ORDER_OORDER;
	}

	@Override
	public void onInit() throws Exception {
		super.onInit();

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
