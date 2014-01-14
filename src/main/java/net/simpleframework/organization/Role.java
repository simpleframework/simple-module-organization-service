package net.simpleframework.organization;

import net.simpleframework.ado.ColumnData;
import net.simpleframework.ado.bean.ITreeBeanAware;
import net.simpleframework.ado.db.DbEntityTable;
import net.simpleframework.ado.db.common.EntityInterceptor;
import net.simpleframework.common.ID;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
@EntityInterceptor(listenerTypes = { "net.simpleframework.module.log.EntityDeleteLogAdapter" })
public class Role extends AbstractOrganizationBean implements ITreeBeanAware {

	/* 关联的角色视图id */
	private ID roleChartId;

	private ID parentId;

	private ERoleType roleType;

	private String ruleHandler, ruleScript;

	private ERoleMark roleMark;

	/* 标识用户识别的角色，比如，全体注册用户，这个用户是不能理解的，也没有意义的 */
	private boolean userRole;

	@Override
	public ID getParentId() {
		return parentId;
	}

	@Override
	public void setParentId(final ID parentId) {
		this.parentId = parentId;
	}

	public ERoleType getRoleType() {
		return roleType == null ? ERoleType.normal : roleType;
	}

	public void setRoleType(final ERoleType roleType) {
		this.roleType = roleType;
	}

	public ID getRoleChartId() {
		return roleChartId;
	}

	public void setRoleChartId(final ID roleChartId) {
		this.roleChartId = roleChartId;
	}

	public String getRuleHandler() {
		return ruleHandler;
	}

	public void setRuleHandler(final String ruleHandler) {
		this.ruleHandler = ruleHandler;
	}

	public String getRuleScript() {
		return ruleScript;
	}

	public void setRuleScript(final String ruleScript) {
		this.ruleScript = ruleScript;
	}

	public ERoleMark getRoleMark() {
		return roleMark == null ? ERoleMark.normal : roleMark;
	}

	public void setRoleMark(final ERoleMark roleMark) {
		this.roleMark = roleMark;
	}

	public boolean isUserRole() {
		return userRole;
	}

	public void setUserRole(final boolean userRole) {
		this.userRole = userRole;
	}

	public static DbEntityTable TBL = new DbEntityTable(Role.class, "sf_organization_role")
			.setDefaultOrder(ColumnData.ASC("oorder"));

	private static final long serialVersionUID = -911479175612535742L;
}
