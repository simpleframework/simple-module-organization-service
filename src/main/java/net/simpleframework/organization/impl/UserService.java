package net.simpleframework.organization.impl;

import java.io.InputStream;

import net.simpleframework.ado.ColumnData;
import net.simpleframework.ado.db.IDbEntityManager;
import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.Convert;
import net.simpleframework.common.ID;
import net.simpleframework.common.coll.ArrayUtils;
import net.simpleframework.common.object.ObjectUtils;
import net.simpleframework.organization.Account;
import net.simpleframework.organization.Department;
import net.simpleframework.organization.IUserService;
import net.simpleframework.organization.User;
import net.simpleframework.organization.UserLob;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class UserService extends AbstractOrganizationService<User> implements IUserService {

	@Override
	public Account getAccount(final Object id) {
		User user = null;
		if (id instanceof User) {
			user = (User) id;
		}

		Account account = accountService.getBean(user != null ? user.getId() : id);
		if (account == null && (user != null || (user = getBean(id)) != null)) {
			account = accountService.createBean();
			account.setId(user.getId());
			account.setName(ObjectUtils.hashStr(user));
			accountService.insert(account);
		}
		return account;
	}

	@Override
	public InputStream getPhoto(final User user) {
		UserLob lob;
		if (user != null && (lob = getEntityManager(UserLob.class).getBean(user.getId())) != null) {
			return lob.getPhoto();
		}
		return null;
	}

	@Override
	public void updatePhoto(final User user, final InputStream photo) {
		final IDbEntityManager<UserLob> entity = getEntityManager(UserLob.class);
		final ID userId = user.getId();
		UserLob lob = entity.getBean(userId);
		if (lob == null) {
			lob = new UserLob();
			lob.setId(userId);
			lob.setPhoto(photo);
			entity.insert(lob);
		} else {
			lob.setPhoto(photo);
			entity.update(lob);
		}
	}

	@Override
	public User getUserByEmail(final String email) {
		return getBean("email=?", email);
	}

	@Override
	public User getUserByMobile(final String mobile) {
		return getBean("mobile=?", mobile);
	}

	@Override
	protected ColumnData[] getDefaultOrderColumns() {
		return ORDER_OORDER;
	}

	@Override
	public IDataQuery<User> queryUsers(final Department dept, final int accountType,
			final ColumnData order) {
		final IDataQuery<User> dq = query(accountService.toAccountsSQLValue(dept, accountType, false,
				new ColumnData[] { order != null ? order : ColumnData.DESC("u.oorder") }));
		dq.setCount(accountService.getAccountCount(dept, accountType));
		return dq;
	}

	@Override
	public IDataQuery<User> queryUsers(final Department dept, final int accountType) {
		return queryUsers(dept, accountType, null);
	}

	@Override
	public IDataQuery<User> queryUsers(final Department dept) {
		return queryUsers(dept, Account.TYPE_ALL);
	}

	@Override
	public void onInit() throws Exception {
		super.onInit();

		addListener(new DbEntityAdapterEx<User>() {
			@Override
			public void onBeforeUpdate(final IDbEntityManager<User> manager, final String[] columns,
					final User[] beans) throws Exception {
				super.onBeforeUpdate(manager, columns, beans);
				if (ArrayUtils.isEmpty(columns) || ArrayUtils.contains(columns, "departmentId", true)) {
					for (final User user : beans) {
						final Object _deptId = queryFor("departmentId", "id=?", user.getId());
						if (_deptId != null) {
							user.setAttr("_deptId", _deptId);
						}
					}
				}
			}

			@Override
			public void onAfterUpdate(final IDbEntityManager<User> manager, final String[] columns,
					final User[] beans) throws Exception {
				super.onAfterUpdate(manager, columns, beans);
				for (final User user : beans) {
					final String _deptId = Convert.toString(user.getAttr("_deptId"));
					final String deptId = Convert.toString(user.getDepartmentId());
					if (!ObjectUtils.objectEquals(_deptId, deptId)) {
						// 更新变化前后部门的统计值
						accountService.updateStats(deptId);
						accountService.updateStats(_deptId);
					}
				}
				accountService.updateAllStats();
			}
		});
	}
}
