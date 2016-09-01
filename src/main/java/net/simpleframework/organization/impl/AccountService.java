package net.simpleframework.organization.impl;

import static net.simpleframework.common.I18n.$m;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.simpleframework.ado.ColumnData;
import net.simpleframework.ado.IParamsValue;
import net.simpleframework.ado.db.IDbEntityManager;
import net.simpleframework.ado.db.common.SQLValue;
import net.simpleframework.ado.query.DataQueryUtils;
import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.AlgorithmUtils;
import net.simpleframework.common.BeanUtils;
import net.simpleframework.common.ID;
import net.simpleframework.common.LngLatUtils;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.ArrayUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.common.object.ObjectUtils;
import net.simpleframework.common.th.NotImplementedException;
import net.simpleframework.ctx.IModuleRef;
import net.simpleframework.ctx.permission.PermissionConst;
import net.simpleframework.ctx.task.ExecutorRunnableEx;
import net.simpleframework.ctx.trans.Transaction;
import net.simpleframework.organization.IAccountService;
import net.simpleframework.organization.IOrganizationContext;
import net.simpleframework.organization.OrganizationException;
import net.simpleframework.organization.OrganizationMessageRef;
import net.simpleframework.organization.bean.Account;
import net.simpleframework.organization.bean.Account.EAccountStatus;
import net.simpleframework.organization.bean.AccountStat;
import net.simpleframework.organization.bean.Department;
import net.simpleframework.organization.bean.Department.EDepartmentType;
import net.simpleframework.organization.bean.RoleMember.ERoleMemberType;
import net.simpleframework.organization.bean.User;
import net.simpleframework.organization.login.IAccountSession;
import net.simpleframework.organization.login.LoginObject;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class AccountService extends AbstractOrganizationService<Account> implements IAccountService {

	@Override
	public Account getAccountByName(final String name) {
		return getBean("name=?", name);
	}

	@Override
	public Account getAdmin() {
		return getAccountByName(PermissionConst.ADMIN);
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
		User user = _userService.getBean(account != null ? account.getId() : id);
		if (user == null && (account != null || (account = getBean(id)) != null)) {
			user = _userService.createBean();
			user.setId(account.getId());
			user.setText(account.getName());
			_userService.insert(user);
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
			_logout(account, clearSession);
		}
		accountSession.logout();
	}

	@Override
	public void logout(final Object... ids) {
		for (final Object id : ids) {
			_logout(getBean(id), true);
		}
	}

	private void _logout(final Account account, final boolean clearSession) {
		account.setOnlineMillis(account.getOnlineMillis()
				+ (System.currentTimeMillis() - account.getLastLoginDate().getTime()));
		account.setLogin(false);
		if (clearSession) {
			account.setSessionid("");
			update(new String[] { "login", "sessionid", "onlineMillis" }, account);
		} else {
			update(new String[] { "login", "onlineMillis" }, account);
		}
	}

	@Override
	public boolean verifyPassword(final Account account, final String password) {
		return account.getPassword().equals(AlgorithmUtils.encryptPass(password));
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
				"(longitude<>0 and latitude<>0 and status=?) and ")
				.append("(longitude between ? and ?) and (latitude between ? and ?)");
		final List<Object> params = ArrayUtils.toParams(EAccountStatus.normal, around[0], around[1],
				around[2], around[3]);
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
			return query(new SQLValue(sql2, params.toArray()));
		} else {
			return query(sql, params.toArray());
		}
	}

	@Override
	public IDataQuery<Account> queryAccounts(final Department dept, final int accountType,
			final ColumnData order) {
		final IDataQuery<Account> dq = query(toAccountsSQLValue(dept, accountType, true,
				new ColumnData[] { order != null ? order : ColumnData.DESC("a.createdate") }));
		dq.setCount(getAccountCount(dept, accountType));
		return dq;
	}

	@Override
	public IDataQuery<Account> queryAccounts(final Department dept, final int accountType) {
		return queryAccounts(dept, accountType, null);
	}

	protected int getAccountCount(final Department dept, final int accountType) {
		// 大数据下，count性能太差
		// 使用统计数据
		AccountStat stat = null;
		if (dept == null) {
			stat = _accountStatService.getAllAccountStat();
		} else {
			if (dept.getDepartmentType() == EDepartmentType.organization) {
				stat = _accountStatService.getOrgAccountStat(dept);
			}
		}

		if (stat != null) {
			if (accountType == Account.TYPE_ALL) {
				return stat.getRnums();
			} else if (accountType == Account.TYPE_STATE_NORMAL) {
				return stat.getState_normal();
			} else if (accountType == Account.TYPE_STATE_LOCKED) {
				return stat.getState_locked();
			} else if (accountType == Account.TYPE_STATE_REGISTRATION) {
				return stat.getState_registration();
			} else if (accountType == Account.TYPE_STATE_DELETE) {
				return stat.getState_delete();
			} else if (accountType == Account.TYPE_ONLINE) {
				return stat.getOnline_nums();
			}
		}
		return -1;
	}

	protected SQLValue toAccountsSQLValue(final Department dept, final int accountType,
			final boolean account, final ColumnData[] orderCols) {
		final StringBuilder sql = new StringBuilder();
		final ArrayList<Object> params = new ArrayList<Object>();
		sql.append("select ").append(account ? "a" : "u").append(".* from ")
				.append(getTablename(Account.class)).append(" a ").append(account ? "left" : "right")
				.append(" join ").append(getTablename(User.class)).append(" u on a.id=u.id where 1=1");

		// 状态
		final boolean _status = accountType >= Account.TYPE_STATE_DELETE
				&& accountType <= Account.TYPE_STATE_NORMAL;
		if (_status) {
			sql.append(" and a.status=?");
			params.add(EAccountStatus.values()[Account.TYPE_STATE_NORMAL - accountType]);
		} else {
			sql.append(" and a.status<>?");
			params.add(EAccountStatus.delete);

			// 部门
			boolean isdept = false;
			if (dept != null) {
				if (accountType == Account.TYPE_DEPT) {
					sql.append(" and u.departmentid=?");
					isdept = true;
				} else {
					sql.append(" and u.orgid=?");
				}
				params.add(dept.getId());
			}

			if (!isdept) {
				if (accountType == Account.TYPE_NO_DEPT) { // 没有部门
					sql.append(" and u.departmentid is null");
				} else if (accountType == Account.TYPE_DEPT) {
					sql.append(" and u.departmentid is not null");
				} else if (accountType == Account.TYPE_ONLINE) { // 在线
					sql.append(" and a.login=?");
					params.add(Boolean.TRUE);
				}
			}
		}
		// left join => createdate排序
		sql.append(toOrderSQL(orderCols));
		return new SQLValue(sql, params.toArray());
	}

	@Override
	public Account doSave(Account account, final String name, final String password,
			final EAccountStatus status, final Map<String, Object> userData) {
		final boolean insert = account == null;
		if (insert) {
			account = createBean();
			account.setCreateDate(new Date());
		}

		if (password != null && !password.equals(account.getPassword())) {
			if (password.startsWith("encrypt:")) {
				account.setPassword(password.substring(8));
			} else {
				account.setPassword(AlgorithmUtils.encryptPass(password));
			}
		}
		account.setName(name);
		account.setStatus(status);

		// final String openid = (String) userData.get("openid");
		// if (StringUtils.hasText(openid)) {
		// account.setOpenid(openid);
		// }

		if (insert) {
			insert(account);
		} else {
			update(account);
		}

		User user = _userService.getBean(account.getId());
		if (user == null) {
			user = _userService.createBean();
			user.setId(account.getId());
			for (final Map.Entry<String, Object> e : userData.entrySet()) {
				BeanUtils.setProperty(user, e.getKey(), e.getValue());
			}
			_userService.insert(user);
		} else {
			for (final Map.Entry<String, Object> e : userData.entrySet()) {
				BeanUtils.setProperty(user, e.getKey(), e.getValue());
			}
			_userService.update(user);
		}
		return account;
	}

	@Override
	public void regist(final String username, final String password,
			final Map<String, Object> userData) {
		doSave(null, username, password, EAccountStatus.registration, userData);
	}

	@Override
	public int delete(final Object... ids) {
		int i = 0;
		for (final Object id : ids) {
			final Account account = getBean(id);
			if (account.getStatus() == EAccountStatus.delete) {
				i += super.delete(id);
			} else {
				account.setStatus(EAccountStatus.delete);
				update(new String[] { "status" }, account);
				i++;
			}
		}
		return i;
	}

	@Override
	public void undelete(final Object... ids) {
		_status(EAccountStatus.delete, EAccountStatus.normal, ids);
	}

	@Override
	public void lock(final Object... ids) {
		_status(EAccountStatus.normal, EAccountStatus.locked, ids);
	}

	@Override
	public void unlock(final Object... ids) {
		_status(EAccountStatus.locked, EAccountStatus.normal, ids);
	}

	private void _status(final EAccountStatus from, final EAccountStatus to, final Object... ids) {
		for (final Object id : ids) {
			final Account account = getBean(id);
			if (account.getStatus() != from) {
				throw OrganizationException.of($m("AccountService.0", account.getStatus(), from));
			}
			account.setStatus(to);
			update(new String[] { "status" }, account);
		}
	}

	private final Set<ID> _UPDATE_ASYNC = new HashSet<ID>();

	@Override
	public void onInit() throws Exception {
		super.onInit();

		final Account admin = getAccountByName(PermissionConst.ADMIN);
		if (admin == null) {
			doSave(null, PermissionConst.ADMIN, PermissionConst.ADMIN, null,
					new KVMap().add("text", PermissionConst.ADMIN));
		}

		addListener(new DbEntityAdapterEx<Account>() {
			@Override
			public void onBeforeDelete(final IDbEntityManager<Account> manager,
					final IParamsValue paramsValue) throws Exception {
				super.onBeforeDelete(manager, paramsValue);
				for (final Account account : coll(manager, paramsValue)) {
					// 删除成员
					deleteMember(account);
					// 删除用户
					_userService.delete(account.getId());
				}
			}

			@Override
			public void onAfterInsert(final IDbEntityManager<Account> manager, final Account[] beans)
					throws Exception {
				super.onAfterInsert(manager, beans);
				for (final Account account : beans) {
					final IModuleRef ref = ((IOrganizationContext) getModuleContext()).getMessageRef();
					if (ref != null) {
						((OrganizationMessageRef) ref).doAccountCreatedMessage(account);
					}
				}
			}

			@Override
			public void onAfterUpdate(final IDbEntityManager<Account> service, final String[] columns,
					final Account[] beans) throws Exception {
				super.onAfterUpdate(service, columns, beans);

				final Set<ID> _UPDATE_SYNC = new HashSet<ID>();
				for (final Account account : beans) {
					if (ArrayUtils.contains(columns, "status", true)) {
						_UPDATE_SYNC.add(account.getId());
					} else if (ArrayUtils.isEmpty(columns)
							|| ArrayUtils.contains(columns, "login", true)) {
						// 其它异步更新
						_UPDATE_ASYNC.add(account.getId());
					}
				}
				_updateDeptStats(_UPDATE_SYNC);
			}
		});

		getTaskExecutor().addScheduledTask(new ExecutorRunnableEx("account_stat") {
			@Override
			protected void task(final Map<String, Object> cache) throws Exception {
				doStatTask_inTran();
			}
		});
	}

	@Transaction(context = IOrganizationContext.class)
	public void doStatTask_inTran() {
		// 校正在线状态
		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR_OF_DAY, -12);
		final IDataQuery<Account> dq = query("login=? and lastlogindate<?", Boolean.TRUE,
				cal.getTime());
		Account account;
		while ((account = dq.next()) != null) {
			account.setLogin(false);
			update(new String[] { "login" }, account);
		}
		// 统计
		_updateDeptStats(_UPDATE_ASYNC);
	}

	private synchronized void _updateDeptStats(final Set<ID> set) {
		if (set == null || set.size() == 0) {
			return;
		}
		final ArrayList<User> users = new ArrayList<User>();
		for (final ID id : set) {
			User user;
			if ((user = getUser(id)) != null) {
				users.add(user);
			}
		}
		((AccountStatService) _accountStatService).updateStats(users.toArray(new User[users.size()]));
		set.clear();
	}

	void deleteMember(final Account account) {
		// 删除成员角色
		_rolemService
				.deleteWith("membertype=? and memberid=?", ERoleMemberType.user, account.getId());
	}

	@Override
	public void sentBindingSMS(final String mobile) {
		throw NotImplementedException.of(getClass(), "sentBindingSMS");
	}
}
