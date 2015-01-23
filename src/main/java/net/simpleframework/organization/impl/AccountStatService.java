package net.simpleframework.organization.impl;

import net.simpleframework.ado.db.common.ExpressionValue;
import net.simpleframework.ctx.service.ado.db.AbstractDbBeanService;
import net.simpleframework.organization.AccountStat;
import net.simpleframework.organization.Department;
import net.simpleframework.organization.IAccountStatService;

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
				if (org != null) {
					stat.setOrgId(org.getId());
				}
			}
			insert(stat);
		}
		return stat;
	}
}
