package net.simpleframework.organization.role;

import static net.simpleframework.common.I18n.$m;
import static net.simpleframework.organization.impl.OrganizationContext.ROLE_ORGANIZATION_MANAGER;

import java.util.HashMap;
import java.util.Map;

import net.simpleframework.common.StringUtils;
import net.simpleframework.common.object.TextNamedObject;
import net.simpleframework.ctx.IApplicationContext;
import net.simpleframework.ctx.hdl.IApplicationStartupHandler;
import net.simpleframework.ctx.permission.PermissionConst;
import net.simpleframework.organization.Role.ERoleType;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class RolenameW implements IApplicationStartupHandler {

	public static final String ROLECHART_SYSTEM = "syschart";
	public static final String ROLECHART_ORG_DEFAULT = "odefault";

	private static final Map<String, ChartW> _charts = new HashMap<String, ChartW>();
	private static final Map<String, RoleW> _roles = new HashMap<String, RoleW>();

	public static void registChart(final String name, final String text, final String description,
			final boolean org) {
		final ChartW c = new ChartW(name, text, description);
		c.setOrg(org);
		_charts.put(c.getName(), c);
	}

	public static void registRole(final String name, final String text, final String description,
			final ERoleType roleType) {
		final RoleW r = new RoleW(name, text, description);
		r.setRoleType(roleType);
		_roles.put(r.getName(), r);
	}

	public static ChartW getBuiltInChart(final String name) {
		return _charts.get(name);
	}

	public static RoleW getBuiltInRole(final String name) {
		return _roles.get(name);
	}

	@Override
	public void onStartup(final IApplicationContext application) throws Exception {
		PermissionConst.ROLE_ANONYMOUS = toUniqueRolename(ROLECHART_SYSTEM, "anonymous");
		PermissionConst.ROLE_ALL_ACCOUNT = toUniqueRolename(ROLECHART_SYSTEM, "account_all");
		PermissionConst.ROLE_LOCK_ACCOUNT = toUniqueRolename(ROLECHART_SYSTEM, "account_lock");
		PermissionConst.ROLE_INDEPT = toUniqueRolename(ROLECHART_SYSTEM, "indept");
		PermissionConst.ROLE_MANAGER = toUniqueRolename(ROLECHART_SYSTEM, "manager");

		ROLE_ORGANIZATION_MANAGER = RolenameW.toUniqueRolename(RolenameW.ROLECHART_SYSTEM, "orgmgr");

		// 注册chart
		registChart(ROLECHART_SYSTEM, $m("RoleChartService.0"), $m("RoleChartService.1"), false);
		registChart(ROLECHART_ORG_DEFAULT, $m("RoleChartService.5"), $m("RoleChartService.6"), true);

		// 注册角色
		for (final String r : new String[] { "anonymous", "account_all", "account_lock", "indept" }) {
			registRole(r, $m("RoleChartService." + r), null, ERoleType.handle);
		}
		for (final String r : new String[] { "manager", "orgmgr" }) {
			registRole(r, $m("RoleChartService." + r), null, ERoleType.normal);
		}
	}

	public static String toUniqueRolename(final String chart, final String role) {
		return toUniqueRolename(null, chart, role);
	}

	public static String toUniqueRolename(final String org, final String chart, final String role) {
		final StringBuilder sb = new StringBuilder();
		if (StringUtils.hasText(org)) {
			sb.append(org).append(":");
		}
		if (StringUtils.hasText(chart)) {
			sb.append(chart).append(":");
		}
		sb.append(role);
		return sb.toString();
	}

	public static String toUniqueChartname(final String org, final String chart) {
		final StringBuilder sb = new StringBuilder();
		if (StringUtils.hasText(org)) {
			sb.append(org).append(":");
		}
		sb.append(chart);
		return sb.toString();
	}

	public static String[] split(final String role) {
		return StringUtils.split(role, ":");
	}

	public static class RoleW extends _W<RoleW> {
		RoleW(final String name, final String text, final String description) {
			super(name, text, description);
		}

		private ERoleType roleType;

		public ERoleType getRoleType() {
			return roleType == null ? ERoleType.normal : roleType;
		}

		public void setRoleType(final ERoleType roleType) {
			this.roleType = roleType;
		}
	}

	public static class ChartW extends _W<ChartW> {
		ChartW(final String name, final String text, final String description) {
			super(name, text, description);
		}

		private boolean org;

		public boolean isOrg() {
			return org;
		}

		public void setOrg(final boolean org) {
			this.org = org;
		}
	}

	static class _W<T extends _W<T>> extends TextNamedObject<T> {
		_W(final String name, final String text, final String description) {
			setName(name);
			setText(text);
			setDescription(description);
		}

		private String description;

		public String getDescription() {
			return description;
		}

		public void setDescription(final String description) {
			this.description = description;
		}
	}
}
