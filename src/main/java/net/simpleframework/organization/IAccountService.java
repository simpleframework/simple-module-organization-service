package net.simpleframework.organization;

import java.util.Map;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.ID;
import net.simpleframework.ctx.service.ado.db.IDbBeanService;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public interface IAccountService extends IDbBeanService<Account> {
	static final int ALL = -1, STATE_NORMAL_ID = -11, STATE_REGISTRATION_ID = -12,
			STATE_LOCKED_ID = -13, STATE_DELETE_ID = -14;

	static final int ONLINE_ID = -2;

	static final int NO_DEPARTMENT_ID = -3;

	static final int DEPARTMENT_ID = -4;

	/**
	 * 通过账号名获取
	 * 
	 * @param name
	 * @return
	 */
	Account getAccountByName(String name);

	/**
	 * 通过openid获取帐号
	 * 
	 * @param openid
	 * @return
	 */
	Account getAccountByOpenid(String openid);

	/**
	 * 通过sessionid获取
	 * 
	 * @param sessionid
	 * @return
	 */
	Account getAccountBySessionid(String sessionid);

	/**
	 * 根据账号获取用户
	 * 
	 * @param account
	 * @return
	 */
	User getUser(Object id);

	/**
	 * 账号保存及插入操作
	 * 
	 * @param id
	 * @param name
	 * @param password
	 * @param accountMark
	 * @param status
	 * @param userData
	 */
	void doSave(Account account, String name, String password, EAccountMark accountMark,
			EAccountStatus status, Map<String, Object> userData);

	void doSave(Account account, String name, String password, EAccountStatus status,
			Map<String, Object> userData);

	/**
	 * 注册帐号
	 * 
	 * @param username
	 * @param password
	 * @param userData
	 */
	void regist(String username, String password, Map<String, Object> userData);

	/**
	 * 恢复删除的账号
	 * 
	 * @param ids
	 * @return
	 */
	int undelete(Object... ids);

	/**
	 * 锁定帐号
	 * 
	 * @param ids
	 * @return
	 */
	int lock(Object... ids);

	/**
	 * 解锁帐号
	 * 
	 * @param ids
	 * @return
	 */
	int unlock(Object... ids);

	/**
	 * 设置登录帐号
	 * 
	 * @param accountSession
	 * @param loginId
	 */
	void setLogin(IAccountSession accountSession, LoginObject login);

	/**
	 * 获取当前的登录帐号
	 * 
	 * @param accountSession
	 * @return
	 */
	ID getLoginId(IAccountSession accountSession);

	/**
	 * 注销
	 * 
	 * @param accountSession
	 */
	void logout(IAccountSession accountSession);

	/**
	 * 检测密码是否正确
	 * 
	 * @param user
	 * @param password
	 * @return
	 */
	boolean verifyPassword(Account account, String password);

	/**
	 * 根据类型获取账号列表。
	 * 
	 * @param type
	 * @return
	 */
	IDataQuery<Account> queryAccounts(int type);

	/**
	 * 获取部门下的账号
	 * 
	 * @param dept
	 * @return
	 */
	IDataQuery<Account> queryAccounts(Department dept);

	/**
	 * 获取部门下的账号数量
	 * 
	 * @param dept
	 * @return
	 */
	int count(Department dept);

	/**
	 * 更新经纬度
	 * 
	 * @param account
	 */
	void updateLatLng(Account account, double lat, double lng);

	/**
	 * 查找附近的账号
	 * 
	 * @param lat
	 * @param lng
	 * @param dis
	 * @param sex
	 * @return
	 */
	IDataQuery<Account> queryAccounts(double lng, double lat, double dis, String sex);
}
