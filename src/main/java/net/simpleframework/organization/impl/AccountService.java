package net.simpleframework.organization.impl;

import static net.simpleframework.common.I18n.$m;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.simpleframework.ado.IParamsValue;
import net.simpleframework.ado.db.IDbEntityManager;
import net.simpleframework.ado.db.common.SQLValue;
import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.BeanUtils;
import net.simpleframework.common.Convert;
import net.simpleframework.common.ID;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.ctx.IModuleRef;
import net.simpleframework.ctx.permission.IPermissionConst;
import net.simpleframework.organization.EAccountMark;
import net.simpleframework.organization.EAccountStatus;
import net.simpleframework.organization.ERoleMemberType;
import net.simpleframework.organization.IAccount;
import net.simpleframework.organization.IAccountService;
import net.simpleframework.organization.IAccountSession;
import net.simpleframework.organization.IDepartment;
import net.simpleframework.organization.IOrganizationContext;
import net.simpleframework.organization.IUser;
import net.simpleframework.organization.IUserService;
import net.simpleframework.organization.LoginObject;
import net.simpleframework.organization.OrganizationException;
import net.simpleframework.organization.OrganizationMessageRef;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public class AccountService extends AbstractOrganizationService<IAccount, Account> implements
		IAccountService, IPermissionConst {

	@Override
	public IAccount getAccountByName(final String name) {
		return getBean("name=?", name);
	}

	@Override
	public IUser getUser(final Object id) {
		IAccount account = null;
		if (id instanceof IAccount) {
			account = (IAccount) id;
		}
		final IUserService service = getUserService();
		IUser user = service.getBean(account != null ? account.getId() : id);
		if (user == null && (account != null || (account = getBean(id)) != null)) {
			user = service.createBean();
			user.setId(account.getId());
			user.setText(account.getName());
			service.insert(user);
		}
		return user;
	}

	@Override
	public ID getLoginId(final IAccountSession accountSession) {
		final LoginObject lObj = accountSession.getLogin();
		if (lObj != null) {
			final IAccount account;
			if ((account = getBean(lObj.getAccountId())) != null && account.isLogin()) {
				return account.getId();
			} else {
				accountSession.logout();
			}
		}
		return null;
	}

	@Override
	public void setLogin(final IAccountSession accountSession, final LoginObject oLogin) {
		final IAccount login = getBean(oLogin.getAccountId());
		if (login != null) {
			login.setLogin(true);
			login.setLastLoginIP(accountSession.getRemoteAddr());
			login.setLastLoginDate(new Date());
			login.setLoginTimes(login.getLoginTimes() + 1);

			update(new String[] { "login", "lastLoginIP", "lastLoginDate", "loginTimes" }, login);
			accountSession.setLogin(oLogin);
		}
	}

	@Override
	public void logout(final IAccountSession accountSession) {
		final LoginObject lObj = accountSession.getLogin();
		final IAccount account;
		if (lObj != null && (account = getBean(lObj.getAccountId())) != null) {
			account.setOnlineMillis(account.getOnlineMillis() + accountSession.getOnlineMillis());
			account.setLogin(false);
			update(new String[] { "login", "onlineMillis" }, account);
		}
		accountSession.logout();
	}

	@Override
	public boolean verifyPassword(final IAccount account, final String password) {
		return account.getPassword().equals(Account.encrypt(password));
	}

	@Override
	public IDataQuery<? extends IAccount> query(final IDepartment dept) {
		final StringBuilder sql = new StringBuilder();
		sql.append("select a.* from ").append(Account.TBL.getName()).append(" a left join ")
				.append(User.TBL.getName())
				.append(" u on a.id=u.id where u.departmentid=? and a.status<>?");
		return getEntityManager().queryBeans(
				new SQLValue(sql.toString(), dept.getId(), EAccountStatus.delete));
	}

	private final Map<String, Integer> countCache = new HashMap<String, Integer>();

	@Override
	public int count(final IDepartment dept) {
		if (countCache.size() == 0) {
			final StringBuilder sql = new StringBuilder();
			sql.append("select count(*) as cc, u.departmentid as dept from ")
					.append(Account.TBL.getName()).append(" a left join ").append(User.TBL.getName())
					.append(" u on a.id=u.id where a.status<>? group by u.departmentid");
			final IDataQuery<Map<String, Object>> rs = getEntityManager().queryMapSet(
					new SQLValue(sql.toString(), EAccountStatus.delete));
			Map<String, Object> data;
			while ((data = rs.next()) != null) {
				Object key = data.get("DEPT");
				if (key == null) {
					key = NO_DEPARTMENT_ID;
				}
				countCache.put(Convert.toString(key), Convert.toInt(data.get("CC")));
			}
		}
		return Convert.toInt(countCache.get(Convert.toString(dept == null ? NO_DEPARTMENT_ID : dept
				.getId())));
	}

	@Override
	public IDataQuery<? extends IAccount> query(final int type) {
		final String uTable = User.TBL.getName();
		final String aTable = Account.TBL.getName();
		final StringBuilder sql = new StringBuilder();
		final ArrayList<Object> params = new ArrayList<Object>();
		sql.append("select a.* from ").append(aTable).append(" a left join ").append(uTable)
				.append(" u on a.id=u.id");
		if (type == ALL) {
			sql.append(" where a.status<>?");
			params.add(EAccountStatus.delete);
		} else if (type == ONLINE_ID) {
			sql.append(" where a.login=? and a.status=?");
			params.add(Boolean.TRUE);
			params.add(EAccountStatus.normal);
		} else if (type == NO_DEPARTMENT_ID) {
			sql.append(" where u.departmentid is null and a.status<>?");
			params.add(EAccountStatus.delete);
		} else if (type == DEPARTMENT_ID) {
			sql.append(" where u.departmentid is not null and a.status<>?");
			params.add(EAccountStatus.delete);
		} else if (type >= STATE_DELETE_ID && type <= STATE_NORMAL_ID) {
			sql.append(" where a.status=?");
			params.add(EAccountStatus.values()[STATE_NORMAL_ID - type]);
		}
		return getEntityManager().queryBeans(new SQLValue(sql.toString(), params.toArray()));
	}

	@Override
	public void doSave(final Object id, final String name, final String password,
			final EAccountMark accountMark, final EAccountStatus status,
			final Map<String, Object> userData) {
		IAccount account = getBean(id);
		final boolean insert = account == null;
		if (insert) {
			account = createBean();
			account.setCreateDate(new Date());
		}

		if (password != null && !password.equals(account.getPassword())) {
			account.setPassword(Account.encrypt(password));
		}
		account.setAccountMark(accountMark);
		account.setStatus(status);

		if (insert) {
			account.setName(name); // 不允许修改
			insert(account);
		} else {
			update(account);
		}

		final IUserService service = getUserService();
		IUser user = service.getBean(id);
		if (user == null) {
			user = service.createBean();
			user.setId(account.getId());
			for (final Map.Entry<String, Object> e : userData.entrySet()) {
				BeanUtils.setProperty(user, e.getKey(), e.getValue());
			}
			service.insert(user);
		} else {
			for (final Map.Entry<String, Object> e : userData.entrySet()) {
				BeanUtils.setProperty(user, e.getKey(), e.getValue());
			}
			service.update(user);
		}
	}

	@Override
	public void regist(final String username, final String password,
			final Map<String, Object> userData) {
		doSave(null, username, password, null, EAccountStatus.registration, userData);
	}

	@Override
	public int delete(final Object... ids) {
		if (ids == null) {
			return 0;
		}
		int i = 0;
		for (final Object id : ids) {
			final IAccount account = getBean(id);
			if (account == null) {
				continue;
			}
			if (account.isLogin()) {
				// 如果在线,则恢复
				account.setLogin(false);
				update(new String[] { "login" }, account);
			} else if (account.getStatus() == EAccountStatus.delete) {
				super.delete(id);
			} else {
				account.setStatus(EAccountStatus.delete);
				update(new String[] { "status" }, account);
			}
			i++;
		}
		return i;
	}

	@Override
	public int undelete(final Object... ids) {
		return _status(EAccountStatus.delete, EAccountStatus.normal, ids);
	}

	@Override
	public int lock(final Object... ids) {
		return _status(EAccountStatus.normal, EAccountStatus.locked, ids);
	}

	@Override
	public int unlock(final Object... ids) {
		return _status(EAccountStatus.locked, EAccountStatus.normal, ids);
	}

	private int _status(final EAccountStatus from, final EAccountStatus to, final Object... ids) {
		if (ids == null) {
			return 0;
		}
		int i = 0;
		for (final Object id : ids) {
			final IAccount account = getBean(id);
			if (account == null || account.isAdmin()) {
				continue;
			}
			if (account.getStatus() != from) {
				throw OrganizationException.of($m("AccountService.1", account.getStatus(), from));
			}
			account.setStatus(to);
			update(new String[] { "status" }, account);
			i++;
		}
		return i;
	}

	@Override
	public void onInit() throws Exception {
		final IAccount admin = getAccountByName(ADMIN);
		if (admin == null) {
			doSave(null, ADMIN, ADMIN, EAccountMark.builtIn, null, new KVMap().add("text", ADMIN));
		}

		addListener(new DbEntityAdapterEx() {
			@Override
			public void onBeforeDelete(final IDbEntityManager<?> service,
					final IParamsValue paramsValue) {
				super.onBeforeDelete(service, paramsValue);
				for (final IAccount account : coll(paramsValue)) {
					if (account.getAccountMark() == EAccountMark.builtIn) {
						throw OrganizationException.of($m("AccountService.0"));
					}
				}
			}

			@Override
			public void onBeforeUpdate(final IDbEntityManager<?> service, final String[] columns,
					final Object[] beans) {
				super.onBeforeUpdate(service, columns, beans);
				for (final Object bean : beans) {
					final IAccount account = (IAccount) bean;
					if (account.getStatus() == EAccountStatus.delete
							&& account.getAccountMark() == EAccountMark.builtIn) {
						throw OrganizationException.of($m("AccountService.0"));
					}
				}
			}

			private void deleteMember(final IAccount account) {
				// 删除成员角色
				getRoleMemberService().deleteWith("membertype=? and memberid=?", ERoleMemberType.user,
						account.getId());
			}

			@Override
			public void onAfterDelete(final IDbEntityManager<?> service, final IParamsValue paramsValue) {
				super.onAfterDelete(service, paramsValue);
				for (final IAccount account : coll(paramsValue)) {
					// 删除用户
					getUserService().delete(account.getId());
					deleteMember(account);
				}
			}

			@Override
			public void onAfterUpdate(final IDbEntityManager<?> service, final String[] columns,
					final Object[] beans) {
				super.onAfterUpdate(service, columns, beans);
				for (final Object bean : beans) {
					final IAccount account = (IAccount) bean;
					if (account.getStatus() == EAccountStatus.delete) {
						// 删除成员角色
						deleteMember(account);
					}
				}
			}

			@Override
			public void onAfterInsert(final IDbEntityManager<?> manager, final Object[] beans) {
				super.onAfterInsert(manager, beans);
				for (final Object bean : beans) {
					final IAccount account = (IAccount) bean;
					final IModuleRef ref = ((IOrganizationContext) getModuleContext()).getMessageRef();
					if (ref != null) {
						((OrganizationMessageRef) ref).doAccountCreatedMessage(account);
					}
				}
			}
		});
	}
}
