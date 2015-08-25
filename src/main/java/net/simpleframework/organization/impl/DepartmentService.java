package net.simpleframework.organization.impl;

import static net.simpleframework.common.I18n.$m;
import net.simpleframework.ado.ColumnData;
import net.simpleframework.ado.FilterItems;
import net.simpleframework.ado.IParamsValue;
import net.simpleframework.ado.db.IDbEntityManager;
import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.coll.ArrayUtils;
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
public class DepartmentService extends AbstractOrganizationService<Department> implements
		IDepartmentService {

	@Override
	public Department getDepartmentByName(final String name) {
		return getBean("name=?", name);
	}

	@Override
	public Department getOrg(Object dept) {
		if (!(dept instanceof Department)) {
			dept = getBean(dept);
		}
		Department org = (Department) dept;
		while (org != null && org.getDepartmentType() != EDepartmentType.organization) {
			org = getBean(org.getParentId());
		}
		return org;
	}

	@Override
	public IDataQuery<Department> queryDepartments(final Department parent,
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

		final AccountStatService statService = (AccountStatService) orgContext
				.getAccountStatService();
		addListener(new DbEntityAdapterEx<Department>() {
			@Override
			public void onBeforeInsert(final IDbEntityManager<Department> manager,
					final Department[] beans) throws Exception {
				super.onAfterInsert(manager, beans);
				for (final Department o : beans) {
					checkDeptType(o);
				}
			}

			@Override
			public void onBeforeUpdate(final IDbEntityManager<Department> manager,
					final String[] columns, final Department[] beans) throws Exception {
				super.onAfterUpdate(manager, columns, beans);
				if (ArrayUtils.isEmpty(columns) || ArrayUtils.contains(columns, "parentId", true)) {
					for (final Department o : beans) {
						checkDeptType(o);
					}
				}
			}

			private void checkDeptType(final Department dept) {
				if (dept.getDepartmentType() == EDepartmentType.organization) {
					final Department parent = getBean(dept.getParentId());
					if (parent != null && parent.getDepartmentType() == EDepartmentType.department) {
						throw OrganizationException.of($m("DepartmentService.2"));
					}
				}
			}

			@Override
			public void onBeforeDelete(final IDbEntityManager<Department> manager,
					final IParamsValue paramsValue) throws Exception {
				super.onBeforeDelete(manager, paramsValue);
				for (final Department dept : coll(manager, paramsValue)) {
					// 存在子部门
					if (queryChildren(dept).getCount() > 0) {
						throw OrganizationException.of($m("DepartmentService.0"));
					}
					// 存在用户
					if (((UserService) _userService).query("departmentId=?", dept.getId()).getCount() > 0) {
						throw OrganizationException.of($m("DepartmentService.1"));
					}

					// 删除统计
					statService.deleteWith("deptId=?", dept.getId());
				}
			}
		});
	}
}
