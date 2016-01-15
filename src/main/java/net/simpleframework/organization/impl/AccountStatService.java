package net.simpleframework.organization.impl;

import java.util.ArrayList;
import java.util.Map;

import net.simpleframework.ado.db.IDbDataQuery;
import net.simpleframework.ado.db.IDbEntityManager;
import net.simpleframework.ado.db.common.SQLValue;
import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.BeanUtils;
import net.simpleframework.common.BeanUtils.PropertyWrapper;
import net.simpleframework.common.Convert;
import net.simpleframework.common.ID;
import net.simpleframework.organization.Account;
import net.simpleframework.organization.Account.EAccountStatus;
import net.simpleframework.organization.AccountStat;
import net.simpleframework.organization.AccountStat.EStatType;
import net.simpleframework.organization.Department;
import net.simpleframework.organization.Department.EDepartmentType;
import net.simpleframework.organization.IAccountStatService;
import net.simpleframework.organization.User;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class AccountStatService extends AbstractOrganizationService<AccountStat> implements
		IAccountStatService {

	@Override
	public AccountStat getAllAccountStat() {
		return _getAccountStat(null, EStatType.all);
	}

	@Override
	public AccountStat getDeptAccountStat(final Object dept) {
		return _getAccountStat(dept, EStatType.dept);
	}

	@Override
	public AccountStat getOrgAccountStat(final Object org) {
		return _getAccountStat(org, EStatType.org);
	}

	private AccountStat _getAccountStat(final Object obj, final EStatType statType) {
		Department _dept = null;
		AccountStat stat = null;
		if (statType == EStatType.all) {
			stat = getBean("stattype=?", statType);
		} else {
			_dept = obj instanceof Department ? (Department) obj : _deptService.getBean(obj);
			if (_dept == null) {
				return null;
			}
			if (statType == EStatType.dept) {
				stat = getBean("deptid=? and stattype=?", _dept.getId(), statType);
			} else if (statType == EStatType.org) {
				stat = getBean("orgid=? and stattype=?", getIdParam(obj), statType);
			}
		}

		if (stat == null) {
			stat = new AccountStat();
			stat.setStatType(statType);
			if (_dept != null) {
				if (statType == EStatType.dept) {
					stat.setDeptId(_dept.getId());
					final Department org = _deptService.getOrg(_dept);
					if (org != null) {
						stat.setOrgId(org.getId());
					}
					setDeptStat(stat);
				} else if (statType == EStatType.org) {
					stat.setOrgId(_dept.getId());
					setOrgStat(stat);
				}
			}
			insert(stat);
		}
		return stat;
	}

	void setDeptStat(final AccountStat stat) {
		final IDbDataQuery<Map<String, Object>> dq = getQueryManager().query(
				"select status, count(*) as c from " + getTablename(Account.class) + " a left join "
						+ getTablename(User.class)
						+ " u on a.id=u.id where u.departmentid=? group by status", stat.getDeptId());
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
		for (final PropertyWrapper p : BeanUtils.getProperties(AccountStat.class).values()) {
			if ("int".equals(p.type.getName())) {
				BeanUtils.setProperty(stat, p.name, 0);
			}
		}
	}

	private void setOrgStat(final AccountStat stat) {
		final ID orgId = stat.getOrgId();
		final Map<String, Object> data = getQueryManager().queryForMap(
				"select sum(nums) as s1, sum(state_normal) as s2, sum(state_registration) as s3, "
						+ "sum(state_locked) as s4, sum(state_delete) as s5 from "
						+ getTablename(AccountStat.class) + " where orgid=? and stattype=?", orgId,
				EStatType.dept);
		if (data != null) {
			stat.setNums(Convert.toInt(data.get("s1")));
			stat.setState_normal(Convert.toInt(data.get("s2")));
			stat.setState_registration(Convert.toInt(data.get("s3")));
			stat.setState_locked(Convert.toInt(data.get("s4")));
			stat.setState_delete(Convert.toInt(data.get("s5")));
		}

		// 求机构的在线人数
		stat.setOnline_nums(getQueryManager().queryForInt(
				new SQLValue("select count(*) from " + _accountService.getTablename() + " a left join "
						+ _userService.getTablename()
						+ " u on a.id=u.id where a.login=? and a.status=? and u.orgid=?", Boolean.TRUE,
						EAccountStatus.normal, orgId)));
	}

	void updateDeptStats(final Object... depts) {
		final ArrayList<AccountStat> stats = new ArrayList<AccountStat>();
		for (final Object dept : depts) {
			final AccountStat stat = getDeptAccountStat(dept);
			if (stat != null) {
				reset(stat);
				setDeptStat(stat);
				stats.add(stat);
			}
		}
		update(stats.toArray(new AccountStat[stats.size()]));
	}

	private void updateOrgStat(final Object orgId) {
		final AccountStat stat = getOrgAccountStat(orgId);
		if (stat != null) {
			reset(stat);
			setOrgStat(stat);
			update(stat);
		}
	}

	private void updateAllStat() {
		final AccountStat stat = getAllAccountStat();
		reset(stat);
		final Map<String, Object> data = getQueryManager().queryForMap(
				"select sum(nums) as s1, sum(online_nums) as s2, "
						+ "sum(state_normal) as s3, sum(state_registration) as s4, "
						+ "sum(state_locked) as s5, sum(state_delete) as s6 from "
						+ getTablename(AccountStat.class) + " where stattype=?", EStatType.org);
		if (data != null) {
			stat.setNums(Convert.toInt(data.get("s1")));
			stat.setOnline_nums(Convert.toInt(data.get("s2")));
			stat.setState_normal(Convert.toInt(data.get("s3")));
			stat.setState_registration(Convert.toInt(data.get("s4")));
			stat.setState_locked(Convert.toInt(data.get("s5")));
			stat.setState_delete(Convert.toInt(data.get("s6")));
		}
		update(stat);
	}

	void updateStat(final Object... depts) {
		final ArrayList<AccountStat> stats = new ArrayList<AccountStat>();
		final AccountStatService _accountStatServiceImpl = (AccountStatService) _accountStatService;
		for (final Object dept : depts) {
			final AccountStat stat = getDeptAccountStat(dept);
			_accountStatServiceImpl.reset(stat);
			_accountStatServiceImpl.setDeptStat(stat);
			stats.add(stat);
		}
		update(stats.toArray(new AccountStat[stats.size()]));
	}

	@Override
	public void onInit() throws Exception {
		super.onInit();

		// 初始化
		final IDataQuery<Department> dq = _deptService.queryAll();
		Department dept;
		final ArrayList<Object> depts = new ArrayList<Object>();
		while ((dept = dq.next()) != null) {
			if (dept.getDepartmentType() == EDepartmentType.department) {
				// 没有则创建
				depts.add(dept);
			}
		}
		updateDeptStats(depts.toArray());

		addListener(new DbEntityAdapterEx<AccountStat>() {
			@Override
			public void onAfterUpdate(final IDbEntityManager<AccountStat> manager,
					final String[] columns, final AccountStat[] beans) throws Exception {
				super.onAfterUpdate(manager, columns, beans);
				boolean orgUpdate = false;
				for (final AccountStat stat : beans) {
					final Department dept = _deptService.getBean(stat.getDeptId());
					if (dept != null && dept.getDepartmentType() == EDepartmentType.department) {
						updateOrgStat(stat.getOrgId());
						orgUpdate = true;
					}
				}
				if (orgUpdate) {
					updateAllStat();
				}
			}
		});
	}
}
