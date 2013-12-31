package net.simpleframework.organization.impl;

import java.io.InputStream;

import net.simpleframework.ado.db.IDbEntityManager;
import net.simpleframework.ado.db.common.ExpressionValue;
import net.simpleframework.ado.db.common.SQLValue;
import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.ID;
import net.simpleframework.common.object.ObjectUtils;
import net.simpleframework.organization.Account;
import net.simpleframework.organization.Department;
import net.simpleframework.organization.EAccountStatus;
import net.simpleframework.organization.IAccountService;
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
		final IAccountService service = getAccountService();
		Account account = service.getBean(user != null ? user.getId() : id);
		if (account == null && (user != null || (user = getBean(id)) != null)) {
			account = service.createBean();
			account.setId(user.getId());
			account.setName(ObjectUtils.hashStr(user));
			service.insert(account);
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
	public void updatePhoto(final Object id, final InputStream photo) {
		final IDbEntityManager<UserLob> entity = getEntityManager(UserLob.class);
		UserLob lob = entity.getBean(id);
		if (lob == null) {
			lob = new UserLob();
			lob.setId(ID.of(id));
			lob.setPhoto(photo);
			entity.insert(lob);
		} else {
			lob.setPhoto(photo);
			entity.update(lob);
		}
	}

	@Override
	public User getUserByMail(final String mail) {
		return getEntityManager().queryForBean(new ExpressionValue("email=?", mail));
	}

	@Override
	public IDataQuery<User> query(final Department dept) {
		return getEntityManager().queryBeans(
				new SQLValue("select u.* from " + User.TBL.getName() + " u left join "
						+ Account.TBL.getName()
						+ " a on u.id=a.id where u.DEPARTMENTID=? and a.status<>?", dept.getId(),
						EAccountStatus.delete));
	}
}
