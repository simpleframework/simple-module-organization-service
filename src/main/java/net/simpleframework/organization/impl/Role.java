package net.simpleframework.organization.impl;

import net.simpleframework.ado.ColumnData;
import net.simpleframework.ado.db.DbEntityTable;
import net.simpleframework.ado.db.common.EntityInterceptor;
import net.simpleframework.common.ID;
import net.simpleframework.organization.ERoleMark;
import net.simpleframework.organization.ERoleType;
import net.simpleframework.organization.IRole;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
@EntityInterceptor(listenerTypes = { "net.simpleframework.module.log.EntityDeleteLogAdapter" })
public class Role extends AbstractOrganizationBean implements IRole {
	/**
	 * 关联的角色视图id
	 */
	private ID roleChartId;

	private ID parentId;

	private ERoleType roleType;

	private String ruleHandler, ruleScript;

	private ERoleMark roleMark;

	@Override
	public ID getParentId() {
		return parentId;
	}

	@Override
	public void setParentId(final ID parentId) {
		this.parentId = parentId;
	}

	@Override
	public ERoleType getRoleType() {
		return roleType == null ? ERoleType.normal : roleType;
	}

	@Override
	public void setRoleType(final ERoleType roleType) {
		this.roleType = roleType;
	}

	@Override
	public ID getRoleChartId() {
		return roleChartId;
	}

	@Override
	public void setRoleChartId(final ID roleChartId) {
		this.roleChartId = roleChartId;
	}

	@Override
	public String getRuleHandler() {
		return ruleHandler;
	}

	@Override
	public void setRuleHandler(final String ruleHandler) {
		this.ruleHandler = ruleHandler;
	}

	@Override
	public String getRuleScript() {
		return ruleScript;
	}

	@Override
	public void setRuleScript(final String ruleScript) {
		this.ruleScript = ruleScript;
	}

	@Override
	public ERoleMark getRoleMark() {
		return roleMark == null ? ERoleMark.normal : roleMark;
	}

	@Override
	public void setRoleMark(final ERoleMark roleMark) {
		this.roleMark = roleMark;
	}

	public static DbEntityTable TBL = new DbEntityTable(Role.class, "sf_organization_role")
			.setDefaultOrder(ColumnData.ASC("oorder"));

	private static final long serialVersionUID = -911479175612535742L;
}
