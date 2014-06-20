package net.simpleframework.organization.impl;

import java.io.InputStream;

import net.simpleframework.ado.ColumnData;
import net.simpleframework.ado.db.IDbEntityManager;
import net.simpleframework.ado.db.common.SQLValue;
import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.ID;
import net.simpleframework.common.object.ObjectUtils;
import net.simpleframework.ctx.service.ado.db.AbstractDbBeanService;
import net.simpleframework.organization.Account;
import net.simpleframework.organization.Department;
import net.simpleframework.organization.EAccountStatus;
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
	public IDataQuery<User> query(final Department dept) {
		return getEntityManager().queryBeans(
				new SQLValue("select u.* from " + getTablename(User.class) + " u left join "
						+ getTablename(Account.class)
						+ " a on u.id=a.id where u.DEPARTMENTID=? and a.status<>?", dept.getId(),
						EAccountStatus.delete));
	}
}
