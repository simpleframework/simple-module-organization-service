package net.simpleframework.organization.impl;

import static net.simpleframework.common.I18n.$m;

import java.io.InputStream;
import java.util.Collection;

import net.simpleframework.ado.ColumnData;
import net.simpleframework.ado.IParamsValue;
import net.simpleframework.ado.db.IDbEntityManager;
import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.Convert;
import net.simpleframework.common.ID;
import net.simpleframework.common.coll.ArrayUtils;
import net.simpleframework.common.object.ObjectUtils;
import net.simpleframework.organization.IUserService;
import net.simpleframework.organization.OrganizationException;
import net.simpleframework.organization.bean.Account;
import net.simpleframework.organization.bean.Department;
import net.simpleframework.organization.bean.Department.EDepartmentType;
import net.simpleframework.organization.bean.User;
import net.simpleframework.organization.bean.UserLob;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class UserService extends AbstractOrganizationService<User> implements IUserService {

	@Override
	public Account getAccount(final Object id) {
		User user = null;
		if (id instanceof User) {
			user = (User) id;
		}

		Account account = _accountService.getBean(user != null ? user.getId() : id);
		if (account == null && (user != null || (user = getBean(id)) != null)) {
			account = _accountService.createBean();
			account.setId(user.getId());
			account.setName(ObjectUtils.hashStr(user));
			_accountService.insert(account);
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
	public IDataQuery<User> queryUsers(final Department dept, final int accountType,
			final ColumnData order) {
		final AccountService _accountServiceImpl = (AccountService) _accountService;
		final IDataQuery<User> dq = query(_accountServiceImpl.toAccountsSQLValue(dept, accountType,
				false, new ColumnData[] { order != null ? order : ColumnData.ASC("u.oorder") }));
		dq.setCount(_accountServiceImpl.getAccountCount(dept, accountType));
		return dq;
	}

	@Override
	public IDataQuery<User> queryUsers(final Department dept, final int accountType) {
		return queryUsers(dept, accountType, null);
	}

	@Override
	public IDataQuery<User> queryUsers(final Department dept) {
		return queryUsers(dept, dept.getDepartmentType() == EDepartmentType.organization
				? Account.TYPE_ALL : Account.TYPE_DEPT);
	}

	@Override
	public void onInit() throws Exception {
		super.onInit();

		addListener(new DbEntityAdapterEx<User>() {
			@Override
			public void onBeforeInsert(final IDbEntityManager<User> manager, final User[] beans)
					throws Exception {
				super.onBeforeInsert(manager, beans);
				for (final User user : beans) {
					final String email = user.getEmail();
					if (email != null && getUserByEmail(email) != null) {
						throw OrganizationException.of($m("UserService.0", email));
					}
					final String mobile = user.getMobile();
					if (mobile != null && getUserByMobile(mobile) != null) {
						throw OrganizationException.of($m("UserService.1", mobile));
					}
				}
			}

			@Override
			public void onBeforeUpdate(final IDbEntityManager<User> manager, final String[] columns,
					final User[] beans) throws Exception {
				super.onBeforeUpdate(manager, columns, beans);
				for (final User user : beans) {
					final String email = user.getEmail();
					User user2;
					if (email != null && (user2 = getUserByEmail(email)) != null
							&& !user2.equals(user)) {
						throw OrganizationException.of($m("UserService.0", email));
					}
					final String mobile = user.getMobile();
					if (mobile != null && (user2 = getUserByMobile(mobile)) != null
							&& !user2.equals(user)) {
						throw OrganizationException.of($m("UserService.1", mobile));
					}
				}
			}
		});

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
						final Object _orgId = queryFor("orgId", "id=?", user.getOrgId());
						if (_orgId != null) {
							user.setAttr("_orgId", _orgId);
						}
					}
				}
			}

			@Override
			public void onAfterUpdate(final IDbEntityManager<User> manager, final String[] columns,
					final User[] beans) throws Exception {
				super.onAfterUpdate(manager, columns, beans);
				for (final User user : beans) {
					final AccountStatService statService = (AccountStatService) _accountStatService;
					final String _deptId = Convert.toString(user.getAttr("_deptId"));
					final String deptId = Convert.toString(user.getDepartmentId());
					if (!ObjectUtils.objectEquals(_deptId, deptId)) {
						// 更新变化前后部门的统计值
						statService.updateDeptStat(_deptId);
						statService.updateDeptStat(deptId);
					}
					final String _orgId = Convert.toString(user.getAttr("_orgId"));
					final String orgId = Convert.toString(user.getOrgId());
					if (!ObjectUtils.objectEquals(_orgId, orgId)) {
						statService.updateOrgStat(_orgId);
						statService.updateOrgStat(orgId);
					}
				}
			}

			@Override
			public void onAfterInsert(final IDbEntityManager<User> manager, final User[] beans)
					throws Exception {
				super.onAfterInsert(manager, beans);
				((AccountStatService) _accountStatService).updateStats(beans);
			}

			@Override
			public void onBeforeDelete(final IDbEntityManager<User> manager,
					final IParamsValue paramsValue) throws Exception {
				super.onBeforeDelete(manager, paramsValue);
				coll(manager, paramsValue);
			}

			@Override
			public void onAfterDelete(final IDbEntityManager<User> manager,
					final IParamsValue paramsValue) throws Exception {
				super.onAfterDelete(manager, paramsValue);
				final Collection<User> coll = coll(manager, paramsValue);
				((AccountStatService) _accountStatService)
						.updateStats(coll.toArray(new User[coll.size()]));
			}
		});
	}
}
