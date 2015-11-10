package net.simpleframework.organization;

import java.util.Map;

import net.simpleframework.ado.ColumnData;
import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.ID;
import net.simpleframework.ctx.service.ado.db.IDbBeanService;
import net.simpleframework.organization.Account.EAccountMark;
import net.simpleframework.organization.Account.EAccountStatus;
import net.simpleframework.organization.login.IAccountSession;
import net.simpleframework.organization.login.LoginObject;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public interface IAccountService extends IDbBeanService<Account> {

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
	 * 通过设备id获取
	 * 
	 * @param mdevid
	 * @return
	 */
	Account getAccountByMdevid(String mdevid);

	Account getAdmin();

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
	Account doSave(Account account, String name, String password, EAccountMark accountMark,
			EAccountStatus status, Map<String, Object> userData);

	Account doSave(Account account, String name, String password, EAccountStatus status,
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
	 */
	void undelete(Object... ids);

	/**
	 * 锁定帐号
	 * 
	 * @param ids
	 */
	void lock(Object... ids);

	/**
	 * 解锁帐号
	 * 
	 * @param ids
	 */
	void unlock(Object... ids);

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
	 * @param ids
	 */
	void logout(Object... ids);

	/**
	 * 注销
	 * 
	 * @param accountSession
	 * @param clearSession
	 */
	void logout(IAccountSession accountSession, boolean clearSession);

	/**
	 * 检测密码是否正确
	 * 
	 * @param user
	 * @param password
	 * @return
	 */
	boolean verifyPassword(Account account, String password);

	/**
	 * 获取账号列表
	 * 
	 * @param dept
	 * @param accountType
	 * @param order
	 * @return
	 */
	IDataQuery<Account> queryAccounts(Department dept, int accountType, ColumnData order);

	IDataQuery<Account> queryAccounts(Department dept, int accountType);

	/**
	 * 更新经纬度
	 * 
	 * @param account
	 * @param lat
	 * @param lng
	 */
	void updateLatLng(Account account, double lat, double lng);

	/**
	 * 更新所在城市
	 * 
	 * @param account
	 * @param cityCode
	 */
	void updateCityCode(Account account, String cityCode);

	void updateMdevid(Account account, String mdevid);

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
