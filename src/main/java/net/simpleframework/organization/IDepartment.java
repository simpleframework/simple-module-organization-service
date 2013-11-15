package net.simpleframework.organization;

import net.simpleframework.ado.bean.IDescriptionBeanAware;
import net.simpleframework.ado.bean.IIdBeanAware;
import net.simpleframework.ado.bean.INameBeanAware;
import net.simpleframework.ado.bean.IOrderBeanAware;
import net.simpleframework.ado.bean.ITextBeanAware;
import net.simpleframework.ado.bean.ITreeBeanAware;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public interface IDepartment extends IIdBeanAware, INameBeanAware, ITextBeanAware,
		IDescriptionBeanAware, ITreeBeanAware, IOrderBeanAware {
	/**
	 * 获取部门的类型
	 * 
	 * @return
	 */
	EDepartmentType getDepartmentType();

	void setDepartmentType(EDepartmentType departmentType);
}
