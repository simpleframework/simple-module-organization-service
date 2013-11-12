package net.simpleframework.organization;

import java.util.Date;

import net.simpleframework.ado.bean.IDescriptionBeanAware;
import net.simpleframework.ado.bean.IIdBeanAware;
import net.simpleframework.ado.bean.IOrderBeanAware;
import net.simpleframework.ado.bean.ITextBeanAware;
import net.simpleframework.common.ID;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public interface IUser extends IIdBeanAware, IOrderBeanAware, ITextBeanAware, IDescriptionBeanAware {
	/**
	 * 用户的部门id
	 * 
	 * @return
	 */
	ID getDepartmentId();

	void setDepartmentId(final ID departmentId);

	Date getBirthday();

	String getEmail();

	void setEmail(String email);

	String getMobile();

	void setMobile(final String mobile);

	String getSex();
}
