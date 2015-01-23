package net.simpleframework.organization.impl;

import static net.simpleframework.common.I18n.$m;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.simpleframework.ado.IParamsValue;
import net.simpleframework.ado.db.IDbDataQuery;
import net.simpleframework.ado.db.IDbEntityManager;
import net.simpleframework.ado.db.common.SQLValue;
import net.simpleframework.ado.query.DataQueryUtils;
import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.BeanUtils;
import net.simpleframework.common.Convert;
import net.simpleframework.common.ID;
import net.simpleframework.common.LngLatUtils;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.ArrayUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.common.object.ObjectUtils;
import net.simpleframework.ctx.IModuleRef;
import net.simpleframework.ctx.permission.IPermissionConst;
import net.simpleframework.ctx.service.ado.db.AbstractDbBeanService;
import net.simpleframework.organization.Account;
import net.simpleframework.organization.AccountStat;
import net.simpleframework.organization.Department;
import net.simpleframework.organization.EAccountMark;
import net.simpleframework.organization.EAccountStatus;
import net.simpleframework.organization.ERoleMemberType;
import net.simpleframework.organization.IAccountService;
import net.simpleframework.organization.IAccountSession;
import net.simpleframework.organization.IOrganizationContext;
import net.simpleframework.organization.LoginObject;
import net.simpleframework.organization.OrganizationException;
import net.simpleframework.organization.OrganizationMessageRef;
import net.simpleframework.organization.User;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class AccountService extends AbstractDbBeanService<Account> implements IAccountService,
		IPermissionConst, IOrganizationServiceImplAware {

	@Override
	public Account getAccountByName(final String name) {
		return getBean("name=?", name);
	}

	@Override
	public Account getAdmin() {
		return getAccountByName(IPermissionConst.ADMIN);
	}

	@Override
	public Account getAccountByOpenid(final String openid) {
		return openid == null ? null : getBean("openid=?", openid);
	}

	@Override
	public Account getAccountBySessionid(final String sessionid) {
		return sessionid == null ? null : getBean("sessionid=?", sessionid);
	}

	@Override
	public Account getAccountByMdevid(final String mdevid) {
		return mdevid == null ? null : getBean("mdevid=?", mdevid);
	}

	@Override
	public User getUser(final Object id) {
		Account account = null;
		if (id instanceof Account) {
			account = (Account) id;
		}
		User user = uService.getBean(account != null ? account.getId() : id);
		if (user == null && (account != null || (account = getBean(id)) != null)) {
			user = uService.createBean();
			user.setId(account.getId());
			user.setText(account.getName());
			uService.insert(user);
		}
		return user;
	}

	@Override
	public ID getLoginId(final IAccountSession accountSession) {
		final LoginObject lObj = accountSession.getLogin();
		Account account;
		if (lObj != null && (account = getBean(lObj.getAccountId())) != null) {
			if (!account.isLogin()) {
				setLogin(accountSession, lObj);
			}
			return account.getId();
		}
		return null;
	}

	@Override
	public void setLogin(final IAccountSession accountSession, final LoginObject oLogin) {
		final Account login = getBean(oLogin.getAccountId());
		if (login != null) {
			login.setLogin(true);
			login.setSessionid(accountSession.getSessionId());
			login.setLastLoginIP(accountSession.getRemoteAddr());
			login.setLastLoginDate(new Date());
			login.setLoginTimes(login.getLoginTimes() + 1);
			update(
					new String[] { "login", "sessionid", "lastLoginIP", "lastLoginDate", "loginTimes" },
					login);
			accountSession.setLogin(oLogin);
		}
	}

	@Override
	public void logout(final IAccountSession accountSession, final boolean clearSession) {
		final LoginObject lObj = accountSession.getLogin();
		final Account account;
		if (lObj != null && (account = getBean(lObj.getAccountId())) != null) {
			account.setOnlineMillis(account.getOnlineMillis() + accountSession.getOnlineMillis());
			account.setLogin(false);
			if (clearSession) {
				account.setSessionid("");
				update(new String[] { "login", "sessionid", "onlineMillis" }, account);
			} else {
				update(new String[] { "login", "onlineMillis" }, account);
			}
		}
		accountSession.logout();
	}

	@Override
	public boolean verifyPassword(final Account account, final String password) {
		return account.getPassword().equals(Account.encrypt(password));
	}

	@Override
	public void updateLatLng(final Account account, final double lat, final double lng) {
		if (account == null || (account.getLatitude() == lat && account.getLongitude() == lng)) {
			return;
		}
		account.setLatitude(lat);
		account.setLongitude(lng);
		update(new String[] { "latitude", "longitude" }, account);
	}

	@Override
	public void updateCityCode(final Account account, final String cityCode) {
		if (account == null || ObjectUtils.objectEquals(cityCode, account.getCityCode())) {
			return;
		}
		account.setCityCode(cityCode);
		update(new String[] { "citycode" }, account);
	}

	@Override
	public void updateMdevid(final Account account, final String mdevid) {
		if (account == null || ObjectUtils.objectEquals(mdevid, account.getMdevid())) {
			return;
		}
		final Account account2 = getAccountByMdevid(mdevid);
		if (account2 != null) {
			account2.setMdevid(null);
			update(new String[] { "mdevid" }, account2);
		}
		account.setMdevid(mdevid);
		update(new String[] { "mdevid" }, account);
	}

	@Override
	public IDataQuery<Account> queryAccounts(final double lng, final double lat, final double dis,
			final String sex) {
		if ((lat == 0 && lng == 0) || dis == 0) {
			return DataQueryUtils.nullQuery();
		}
		final double[] around = LngLatUtils.getRange(lng, lat, dis);
		final StringBuilder sql = new StringBuilder(
				"(longitude<>0 and latitude<>0 and status=? and accountmark=?) and ")
				.append("(longitude between ? and ?) and (latitude between ? and ?)");
		final List<Object> params = ArrayUtils.toParams(EAccountStatus.normal, EAccountMark.normal,
				around[0], around[1], around[2], around[3]);
		if (StringUtils.hasText(sex)) {
			final StringBuilder sql2 = new StringBuilder();
			sql2.append("select a.* from ")
					.append(getTablename(Account.class))
					.append(" a left join ")
					.append(getTablename(User.class))
					.append(" u on a.id=u.id where ")
					.append("(a.longitude<>0 and a.latitude<>0 and a.status=? and a.accountmark=?) and ")
					.append("(a.longitude between ? and ?) and (a.latitude between ? and ?) and u.sex=?");
			params.add(sex);
			return getEntityManager().queryBeans(new SQLValue(sql2.toString(), params.toArray()));
		} else {
			return query(sql.toString(), params.toArray());
		}
	}

	@Override
	public IDataQuery<Account> queryAccounts(final Department dept) {
		final StringBuilder sql = new StringBuilder();
		sql.append("select a.* from ")
				.append(getTablename(Account.class))
				.append(" a left join ")
				.append(getTablename(User.class))
				.append(" u on a.id=u.id where u.departmentid=? and a.status<>? order by u.oorder desc");
		return getEntityManager().queryBeans(
				new SQLValue(sql.toString(), dept.getId(), EAccountStatus.delete));
	}

	@Override
	public IDataQuery<Account> queryAccounts(final int type) {
		final String uTable = getTablename(User.class);
		final String aTable = getTablename(Account.class);
		final StringBuilder sql = new StringBuilder();
		final ArrayList<Object> params = new ArrayList<Object>();
		sql.append("select a.* from ").append(aTable).append(" a left join ").append(uTable)
				.append(" u on a.id=u.id where 1=1");
		if (type == ONLINE_ID) {
			sql.append(" and a.login=? and a.status=?");
			params.add(Boolean.TRUE);
			params.add(EAccountStatus.normal);
		} else if (type == NO_DEPARTMENT_ID) {
			sql.append(" and u.departmentid is null and a.status<>?");
			params.add(EAccountStatus.delete);
		} else if (type == DEPARTMENT_ID) {
			sql.append(" and u.departmentid is not null and a.status<>?");
			params.add(EAccountStatus.delete);
		} else if (type >= STATE_DELETE_ID && type <= STATE_NORMAL_ID) {
			sql.append(" and a.status=?");
			params.add(EAccountStatus.values()[STATE_NORMAL_ID - type]);
		}
		sql.append(" order by a.createdate");
		return getEntityManager().queryBeans(new SQLValue(sql.toString(), params.toArray()));
	}

	@Override
	public Account doSave(final Account account, final String name, final String password,
			final EAccountStatus status, final Map<String, Object> userData) {
		return doSave(account, name, password, EAccountMark.normal, status, userData);
	}

	@Override
	public Account doSave(Account account, final String name, final String password,
			final EAccountMark accountMark, final EAccountStatus status,
			final Map<String, Object> userData) {
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

		final String openid = (String) userData.get("openid");
		if (StringUtils.hasText(openid)) {
			account.setOpenid(openid);
		}

		if (insert) {
			account.setName(name); // 不允许修改
			insert(account);
		} else {
			update(account);
		}

		User user = uService.getBean(account.getId());
		if (user == null) {
			user = uService.createBean();
			user.setId(account.getId());
			for (final Map.Entry<String, Object> e : userData.entrySet()) {
				BeanUtils.setProperty(user, e.getKey(), e.getValue());
			}
			uService.insert(user);
		} else {
			for (final Map.Entry<String, Object> e : userData.entrySet()) {
				BeanUtils.setProperty(user, e.getKey(), e.getValue());
			}
			uService.update(user);
		}
		return account;
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
			final Account account = getBean(id);
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
			final Account account = getBean(id);
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
		super.onInit();

		final Account admin = getAccountByName(ADMIN);
		if (admin == null) {
			doSave(null, ADMIN, ADMIN, EAccountMark.builtIn, null, new KVMap().add("text", ADMIN));
		}

		addListener(new DbEntityAdapterEx() {
			@Override
			public void onBeforeDelete(final IDbEntityManager<?> service,
					final IParamsValue paramsValue) {
				super.onBeforeDelete(service, paramsValue);
				for (final Account account : coll(paramsValue)) {
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
					final Account account = (Account) bean;
					if (account.getStatus() == EAccountStatus.delete
							&& account.getAccountMark() == EAccountMark.builtIn) {
						throw OrganizationException.of($m("AccountService.0"));
					}
				}
			}

			/*------------------------------after ope--------------------------------*/

			@Override
			public void onAfterDelete(final IDbEntityManager<?> manager, final IParamsValue paramsValue) {
				super.onAfterDelete(manager, paramsValue);
				for (final Account account : coll(paramsValue)) {
					// 删除用户
					uService.delete(account.getId());
					deleteMember(account);
					// 同步统计
					updateStats(account);
				}
				updateAllStats();
			}

			@Override
			public void onAfterUpdate(final IDbEntityManager<?> service, final String[] columns,
					final Object[] beans) {
				super.onAfterUpdate(service, columns, beans);
				for (final Object bean : beans) {
					final Account account = (Account) bean;
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
					final Account account = (Account) bean;
					final IModuleRef ref = ((IOrganizationContext) getModuleContext()).getMessageRef();
					if (ref != null) {
						((OrganizationMessageRef) ref).doAccountCreatedMessage(account);
					}
					// 同步统计
					updateStats(account);
				}
				updateAllStats();
			}
		});

	}

	private void deleteMember(final Account account) {
		// 删除成员角色
		rmService.deleteWith("membertype=? and memberid=?", ERoleMemberType.user, account.getId());
	}

	void updateAllStats() {
		final AccountStat stat = sService.getAccountStat();
		final IDbDataQuery<Map<String, Object>> dq = getQueryManager().query(
				new SQLValue("select status, count(*) as c from " + getTablename(Account.class)
						+ " group by status"));
		int nums = 0;
		Map<String, Object> data;
		while ((data = dq.next()) != null) {
			final int c = Convert.toInt(data.get("c"));
			final EAccountStatus status = Convert.toEnum(EAccountStatus.class, data.get("status"));
			if (status != null)
				BeanUtils.setProperty(stat, "state_" + status.name(), c);
			nums += c;
		}
		// 全部及在线
		stat.setNums(nums);
		stat.setOnline_nums(count("login=? and status=?", Boolean.TRUE, EAccountStatus.normal));
		sService.update(stat);
	}

	void updateStats(final Account account) {
		final ID departmentId = getUser(account.getId()).getDepartmentId();
		final Department dept = dService.getBean(departmentId);
		if (dept != null) {
			final AccountStat stat = sService.getAccountStat(dept);
			final IDbDataQuery<Map<String, Object>> dq = getQueryManager().query(
					new SQLValue("select status, count(*) as c from " + getTablename(Account.class)
							+ " a left join " + getTablename(User.class)
							+ " u on a.id=u.id where u.departmentid=? group by status", dept.getId()));
			int nums = 0;
			Map<String, Object> data;
			while ((data = dq.next()) != null) {
				final int c = Convert.toInt(data.get("c"));
				BeanUtils.setProperty(stat, "state_" + data.get("status"), c);
				nums += c;
			}
			stat.setNums(nums);
			sService.update(stat);
		}
	}
}
