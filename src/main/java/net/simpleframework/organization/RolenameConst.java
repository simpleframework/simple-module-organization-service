package net.simpleframework.organization;

import net.simpleframework.common.StringUtils;
import net.simpleframework.ctx.IApplicationContext;
import net.simpleframework.ctx.IApplicationStartup;
import net.simpleframework.ctx.permission.PermissionConst;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class RolenameConst extends PermissionConst implements IApplicationStartup {

	public static final String ROLECHART_SYSTEM = "syschart";

	/* 机构管理员角色 */
	public static final String ROLE_ORGANIZATION_MANAGER = RolenameConst.toUniqueRolename(
			RolenameConst.ROLECHART_SYSTEM, "orgmgr");

	@Override
	public void onStartup(final IApplicationContext application) throws Exception {
		ROLE_ANONYMOUS = toUniqueRolename(ROLECHART_SYSTEM, "anonymous");
		ROLE_ALL_ACCOUNT = toUniqueRolename(ROLECHART_SYSTEM, "account_all");
		ROLE_LOCK_ACCOUNT = toUniqueRolename(ROLECHART_SYSTEM, "account_lock");
		ROLE_MANAGER = toUniqueRolename(ROLECHART_SYSTEM, "manager");
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
}
