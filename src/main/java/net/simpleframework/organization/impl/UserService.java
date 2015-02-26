package net.simpleframework.organization.impl;

import java.io.InputStream;
import java.util.ArrayList;

import net.simpleframework.ado.ColumnData;
import net.simpleframework.ado.db.IDbEntityManager;
import net.simpleframework.ado.db.common.SQLValue;
import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.Convert;
import net.simpleframework.common.ID;
import net.simpleframework.common.coll.ArrayUtils;
import net.simpleframework.common.object.ObjectUtils;
import net.simpleframework.ctx.service.ado.db.AbstractDbBeanService;
import net.simpleframework.organization.Account;
import net.simpleframework.organization.Department;
import net.simpleframework.organization.EAccountStatus;
import net.simpleframework.organization.EDepartmentType;
import net.simpleframework.organization.IUserService;
import net.simpleframework.organization.User;
import net.simpleframework.organization.UserLob;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class UserService extends AbstractDbBeanService<User> implements IUserService,
		IOrganizationServiceImplAware {

	@Override
	protected ColumnData[] getDefaultOrderColumns() {
		return ORDER_OORDER;
	}

	@Override
	public Account getAccount(final Object id) {
		User user = null;
		if (id instanceof User) {
			user = (User) id;
		}

		Account account = aService.getBean(user != null ? user.getId() : id);
		if (account == null && (user != null || (user = getBean(id)) != null)) {
			account = aService.createBean();
			account.setId(user.getId());
			account.setName(ObjectUtils.hashStr(user));
			aService.insert(account);
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
	public IDataQuery<User> queryUsers(final Department dept, final EAccountStatus status) {
		final StringBuilder sql = new StringBuilder();
		sql.append("select u.* from ").append(getTablename(User.class)).append(" u left join ")
				.append(getTablename(Account.class)).append(" a on u.id=a.id where ");
		if (dept.getDepartmentType() == EDepartmentType.department) {
			sql.append("u.departmentid=?");
		} else {
			sql.append("u.orgid=?");
		}
		final ArrayList<Object> params = new ArrayList<Object>();
		params.add(dept.getId());
		if (status == null) {
			sql.append(" and a.status<>?");
			params.add(EAccountStatus.delete);
		} else {
			sql.append(" and a.status=?");
			params.add(status);
		}
		sql.append(" order by u.oorder desc");
		return query(new SQLValue(sql.toString(), params.toArray()));
	}

	@Override
	public IDataQuery<User> queryUsers(final Department dept) {
		return queryUsers(dept, null);
	}

	@Override
	public void onInit() throws Exception {
		super.onInit();

		addListener(new DbEntityAdapterEx() {
			@Override
			public void onBeforeUpdate(final IDbEntityManager<?> manager, final String[] columns,
					final Object[] beans) {
				super.onBeforeUpdate(manager, columns, beans);
				if (ArrayUtils.isEmpty(columns) || ArrayUtils.contains(columns, "departmentId", true)) {
					for (final Object o : beans) {
						final User user = (User) o;
						final Object _deptId = queryFor("departmentId", "id=?", user.getId());
						if (_deptId != null) {
							user.setAttr("_deptId", _deptId);
						}
					}
				}
			}

			@Override
			public void onAfterUpdate(final IDbEntityManager<?> manager, final String[] columns,
					final Object[] beans) {
				super.onAfterUpdate(manager, columns, beans);
				for (final Object o : beans) {
					final User user = (User) o;
					final String _deptId = Convert.toString(user.getAttr("_deptId"));
					final String deptId = Convert.toString(user.getDepartmentId());
					if (!ObjectUtils.objectEquals(_deptId, deptId)) {
						// 更新变化前后部门的统计值
						aService.updateStats(deptId);
						aService.updateStats(_deptId);
					}
				}
				aService.updateAllStats();
			}
		});
	}
}
