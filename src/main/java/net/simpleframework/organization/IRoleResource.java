package net.simpleframework.organization;

import net.simpleframework.ado.bean.IDescriptionBeanAware;
import net.simpleframework.ado.bean.IIdBeanAware;
import net.simpleframework.common.ID;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public interface IRoleResource extends IIdBeanAware, IDescriptionBeanAware {

	/**
	 * 与资源关联的角色id
	 * 
	 * @return
	 */
	ID getRoleId();

	void setRoleId(ID roleId);

	/**
	 * 资源类型被定义一个整型，由使用者确定其具体值
	 * 
	 * @return
	 */
	int getResourceType();

	void setResourceType(int resourceType);

	/**
	 * 资源id
	 * 
	 * @return
	 */
	ID getResourceId();

	void setResourceId(ID resourceId);

	/**
	 * 资源的文本，好查看
	 * 
	 * @return
	 */
	String getResourceText();

	void setResourceText(String resourceText);
}
