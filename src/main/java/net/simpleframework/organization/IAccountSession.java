package net.simpleframework.organization;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public interface IAccountSession {

	static final String LOGIN_KEY = "$$login";

	/**
	 * 获取登录对象
	 * 
	 * @return
	 */
	LoginObject getLogin();

	/**
	 * 设置登录对象
	 * 
	 * @param login
	 */
	void setLogin(LoginObject login);

	/**
	 * 获取能自动登录的对象
	 */
	LoginObject getAutoLogin();

	/**
	 * 注销
	 */
	void logout();

	/**
	 * 
	 * 
	 * @return
	 */
	long getOnlineMillis();

	/**
	 * 获取ip地址
	 * 
	 * @return
	 */
	String getRemoteAddr();
}
