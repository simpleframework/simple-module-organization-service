package net.simpleframework.organization.impl;

import java.util.Map;

import net.simpleframework.ado.db.IDbDataQuery;
import net.simpleframework.ado.db.IDbEntityManager;
import net.simpleframework.ado.db.common.ExpressionValue;
import net.simpleframework.ado.db.common.SQLValue;
import net.simpleframework.common.BeanUtils;
import net.simpleframework.common.Convert;
import net.simpleframework.common.ID;
import net.simpleframework.ctx.service.ado.db.AbstractDbBeanService;
import net.simpleframework.organization.Account;
import net.simpleframework.organization.AccountStat;
import net.simpleframework.organization.Department;
import net.simpleframework.organization.EAccountStatus;
import net.simpleframework.organization.EDepartmentType;
import net.simpleframework.organization.IAccountStatService;
import net.simpleframework.organization.User;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class AccountStatService extends AbstractDbBeanService<AccountStat> implements
		IAccountStatService, IOrganizationServiceImplAware {
	@Override
	public AccountStat getAccountStat() {
		return getAccountStat(null);
	}

	@Override
	public AccountStat getAccountStat(final Object dept) {
		final Object id = getIdParam(dept);
		AccountStat stat = getEntityManager().queryForBean(
				id == null ? new ExpressionValue("deptid is null")
						: new ExpressionValue("deptid=?", id));
		if (stat == null) {
			stat = new AccountStat();
			final Department _dept = dService.getBean(id);
			if (_dept != null) {
				stat.setDeptId(_dept.getId());
				final Department org = dService.getOrg(_dept);
				if (org != null && !org.equals(_dept)) {
					stat.setOrgId(org.getId());
				}
				setUpdateStats(stat);
			}
			insert(stat);
		}
		return stat;
	}

	void setUpdateStats(final AccountStat stat) {
		final IDbDataQuery<Map<String, Object>> dq = getQueryManager().query(
				new SQLValue("select status, count(*) as c from " + getTablename(Account.class)
						+ " a left join " + getTablename(User.class)
						+ " u on a.id=u.id where u.departmentid=? group by status", stat.getDeptId()));
		int nums = 0;
		Map<String, Object> data;
		while ((data = dq.next()) != null) {
			final int c = Convert.toInt(data.get("c"));
			final EAccountStatus status = Convert.toEnum(EAccountStatus.class, data.get("status"));
			if (status != null) {
				BeanUtils.setProperty(stat, "state_" + status.name(), c);
			}
			nums += c;
		}
		stat.setNums(nums);
	}

	void reset(final AccountStat stat) {
		stat.setNums(0);
		stat.setOnline_nums(0);
		stat.setState_normal(0);
		stat.setState_registration(0);
		stat.setState_locked(0);
		stat.setState_delete(0);
	}

	@Override
	public void onInit() throws Exception {
		super.onInit();

		addListener(new DbEntityAdapterEx() {
			private void updateOrgStat(final ID orgId) {
				if (orgId == null) {
					return;
				}
				final AccountStat _stat = getAccountStat(orgId);
				reset(_stat);
				final IDbDataQuery<Map<String, Object>> dq = getQueryManager().query(
						new SQLValue("select sum(nums) as s1, sum(online_nums) as s2, "
								+ "sum(state_normal) as s3, sum(state_registration) as s4, "
								+ "sum(state_locked) as s5, sum(state_delete) as s6 from "
								+ getTablename(AccountStat.class) + " where orgid=?", orgId));
				Map<String, Object> data;
				while ((data = dq.next()) != null) {
					_stat.setNums(Convert.toInt(data.get("s1")));
					_stat.setOnline_nums(Convert.toInt(data.get("s2")));
					_stat.setState_normal(Convert.toInt(data.get("s3")));
					_stat.setState_registration(Convert.toInt(data.get("s4")));
					_stat.setState_locked(Convert.toInt(data.get("s5")));
					_stat.setState_delete(Convert.toInt(data.get("s6")));
				}
				update(_stat);
			}

			@Override
			public void onAfterUpdate(final IDbEntityManager<?> manager, final String[] columns,
					final Object[] beans) {
				super.onAfterUpdate(manager, columns, beans);
				for (final Object o : beans) {
					final AccountStat stat = (AccountStat) o;
					final Department dept = orgContext.getDepartmentService().getBean(stat.getDeptId());
					if (dept != null && dept.getDepartmentType() == EDepartmentType.department) {
						updateOrgStat(stat.getOrgId());
					}
				}
			}
		});
	}
}
