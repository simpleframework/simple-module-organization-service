package net.simpleframework.organization;

import net.simpleframework.ado.bean.IDescriptionBeanAware;
import net.simpleframework.ado.bean.IIdBeanAware;
import net.simpleframework.ado.bean.INameBeanAware;
import net.simpleframework.ado.bean.IOrderBeanAware;
import net.simpleframework.ado.bean.ITextBeanAware;
import net.simpleframework.ado.bean.ITreeBeanAware;
import net.simpleframework.common.ID;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public interface IRole extends IIdBeanAware, ITreeBeanAware, INameBeanAware, ITextBeanAware,
		IDescriptionBeanAware, IOrderBeanAware {

	/**
	 * 获取角色的类型
	 * 
	 * @return
	 */
	ERoleType getRoleType();

	void setRoleType(ERoleType roleType);

	/**
	 * 获取角色所在的视图id
	 * 
	 * @return
	 */
	ID getRoleChartId();

	void setRoleChartId(ID roleChartId);

	/**
	 * 获取接口型规则角色的名称，全类名
	 * 
	 * @return
	 */
	String getRuleHandler();

	void setRuleHandler(String ruleHandle);

	/**
	 * 获取脚本型规则角色的脚本内容
	 * 
	 * @return
	 */
	String getRuleScript();

	void setRuleScript(String ruleScript);

	/**
	 * 返回角色标记
	 * 
	 * @return
	 */
	ERoleMark getRoleMark();

	void setRoleMark(ERoleMark roleMark);
}
