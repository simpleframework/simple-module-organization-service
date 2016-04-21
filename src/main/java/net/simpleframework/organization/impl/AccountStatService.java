package net.simpleframework.organization.impl;

import java.util.ArrayList;
import java.util.Map;

import net.simpleframework.ado.db.IDbDataQuery;
import net.simpleframework.ado.db.IDbQueryManager;
import net.simpleframework.ado.db.common.SQLValue;
import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.BeanUtils;
import net.simpleframework.common.BeanUtils.PropertyWrapper;
import net.simpleframework.common.Convert;
import net.simpleframework.common.ID;
import net.simpleframework.organization.IAccountStatService;
import net.simpleframework.organization.bean.Account;
import net.simpleframework.organization.bean.Account.EAccountStatus;
import net.simpleframework.organization.bean.AccountStat;
import net.simpleframework.organization.bean.AccountStat.EStatType;
import net.simpleframework.organization.bean.Department;
import net.simpleframework.organization.bean.Department.EDepartmentType;
import net.simpleframework.organization.bean.User;

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
		final IDbDataQuery<Map<String, Object>> dq = getQueryManager().query(
				"select status, count(*) as c from " + getTablename(Account.class) + " a left join "
						+ getTablename(User.class) + " u on a.id=u.id where u.orgid=? group by status",
				orgId);
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
		// 求机构的在线人数
		stat.setOnline_nums(getQueryManager().queryForInt(
				new SQLValue("select count(*) from " + getTablename(Account.class) + " a left join "
						+ getTablename(User.class) + " u on a.id=u.id where a.login=? and u.orgid=?",
						Boolean.TRUE, orgId)));
	}

	void updateStats(final User... users) {
		final ArrayList<Object> depts = new ArrayList<Object>();
		final ArrayList<Object> orgs = new ArrayList<Object>();
		for (final User user : users) {
			// 同步统计
			final ID deptId = user.getDepartmentId();
			if (deptId != null) {
				depts.add(deptId);
			}
			final ID orgId = user.getOrgId();
			if (orgId != null) {
				orgs.add(orgId);
			}
		}

		// 更新部门
		for (final Object dept : depts) {
			updateDeptStat(dept);
		}
		// 更新org
		for (final Object org : orgs) {
			updateOrgStat(org);
		}
		// 更新全部
		updateAllStat();
	}

	void updateDeptStat(final Object dept) {
		final AccountStat stat = getDeptAccountStat(dept);
		if (stat != null) {
			reset(stat);
			setDeptStat(stat);
			update(stat);
		}
	}

	void updateOrgStat(final Object orgId) {
		final AccountStat stat = getOrgAccountStat(orgId);
		if (stat != null) {
			reset(stat);
			setOrgStat(stat);
			update(stat);
		}
	}

	void updateAllStat() {
		final AccountStat stat = getAllAccountStat();
		reset(stat);

		final IDbQueryManager qmgr = getQueryManager();
		final IDbDataQuery<Map<String, Object>> dq = qmgr.query("select status, count(*) as c from "
				+ _accountService.getTablename() + " group by status");
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

		// 全部及在线
		stat.setNums(nums);

		stat.setOnline_nums(qmgr.queryForInt(new SQLValue("select count(*) from "
				+ _accountService.getTablename() + " where login=?", Boolean.TRUE)));

		update(stat);
	}

	@Override
	public void onInit() throws Exception {
		super.onInit();

		if (count() == 0) {
			// 初始化
			final IDataQuery<Department> dq = _deptService.queryAll();
			Department oDept;
			final ArrayList<Department> depts = new ArrayList<Department>();
			final ArrayList<Department> orgs = new ArrayList<Department>();
			while ((oDept = dq.next()) != null) {
				// 没有则创建
				if (oDept.getDepartmentType() == EDepartmentType.department) {
					depts.add(oDept);
				} else {
					orgs.add(oDept);
				}
			}
			// 更新部门
			for (final Department dept : depts) {
				updateDeptStat(dept);
			}
			// 更新org
			for (final Object org : orgs) {
				updateOrgStat(org);
			}
			// 更新全部
			updateAllStat();
		}
	}
}
