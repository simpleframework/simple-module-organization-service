package net.simpleframework.organization;

import java.io.InputStream;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.ctx.service.ado.db.IDbBeanService;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public interface IUserService extends IDbBeanService<User> {

	/**
	 * 根据用户获取账号
	 * 
	 * @param user
	 * @return
	 */
	Account getAccount(Object id);

	/**
	 * 获取用户的头像
	 * 
	 * @param user
	 * @return
	 */
	InputStream getPhoto(User user);

	/**
	 * 根据email获取用户
	 * 
	 * @param mail
	 * @return
	 */
	User getUserByEmail(String email);

	/**
	 * 根据手机号获取用户
	 * 
	 * @param mobile
	 * @return
	 */
	User getUserByMobile(String mobile);

	/**
	 * 更改用户的照片
	 * 
	 * @param id
	 * @param photo
	 */
	void updatePhoto(User user, InputStream photo);

	/**
	 * 获取部门下的用户
	 * 
	 * @param dept
	 * @param status
	 * @param all
	 * @return
	 */
	IDataQuery<User> queryUsers(Department dept, EAccountStatus status, boolean all);

	IDataQuery<User> queryUsers(Department dept);
}
