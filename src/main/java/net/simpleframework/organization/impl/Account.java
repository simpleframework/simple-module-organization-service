package net.simpleframework.organization.impl;

import java.util.Date;

import net.simpleframework.ado.ColumnMeta;
import net.simpleframework.ado.bean.AbstractNameBean;
import net.simpleframework.ado.db.DbEntityTable;
import net.simpleframework.ado.db.common.EntityInterceptor;
import net.simpleframework.common.AlgorithmUtils;
import net.simpleframework.common.StringUtils;
import net.simpleframework.ctx.permission.IPermissionConst;
import net.simpleframework.organization.EAccountMark;
import net.simpleframework.organization.EAccountStatus;
import net.simpleframework.organization.IAccount;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
@EntityInterceptor(listenerTypes = { "net.simpleframework.module.log.EntityUpdateLogAdapter",
		"net.simpleframework.module.log.EntityDeleteLogAdapter" }, columns = { "password", "status",
		"mailbinding", "mobilebinding" })
public class Account extends AbstractNameBean implements IAccount {
	/* 密码 */
	@ColumnMeta(columnText = "#(Account.0)")
	private String password;

	/* 状态 */
	@ColumnMeta(columnText = "#(Account.1)")
	private EAccountStatus status;

	/* 标识 */
	private EAccountMark accountMark;

	/* 创建时间 */
	private Date createDate;

	/* 是否登录 */
	private boolean login;

	/* 最后一次登录时间 */
	private Date lastLoginDate;

	/* 最后一次登录IP */
	private String lastLoginIP;

	/* 总登录次数 */
	private int loginTimes;

	/* 总在线时间 */
	private long onlineMillis;

	/* 是否绑定邮件 */
	private boolean mailbinding;

	/* 是否绑定手机号 */
	private boolean mobilebinding;

	@ColumnMeta(ignore = true)
	@Override
	public boolean isAdmin() {
		return IPermissionConst.ADMIN.equals(getName());
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public void setPassword(final String password) {
		this.password = password;
	}

	@Override
	public EAccountStatus getStatus() {
		return status == null ? EAccountStatus.normal : status;
	}

	@Override
	public void setStatus(final EAccountStatus status) {
		this.status = status;
	}

	@Override
	public EAccountMark getAccountMark() {
		return accountMark == null ? EAccountMark.normal : accountMark;
	}

	@Override
	public void setAccountMark(final EAccountMark accountMark) {
		this.accountMark = accountMark;
	}

	@Override
	public boolean isLogin() {
		return login;
	}

	@Override
	public void setLogin(final boolean login) {
		this.login = login;
	}

	@Override
	public int getLoginTimes() {
		return loginTimes;
	}

	@Override
	public void setLoginTimes(final int loginTimes) {
		this.loginTimes = loginTimes;
	}

	@Override
	public Date getCreateDate() {
		return createDate;
	}

	@Override
	public void setCreateDate(final Date createDate) {
		this.createDate = createDate;
	}

	@Override
	public Date getLastLoginDate() {
		return lastLoginDate;
	}

	@Override
	public void setLastLoginDate(final Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}

	@Override
	public String getLastLoginIP() {
		return lastLoginIP;
	}

	@Override
	public void setLastLoginIP(final String lastLoginIP) {
		this.lastLoginIP = lastLoginIP;
	}

	@Override
	public long getOnlineMillis() {
		return onlineMillis;
	}

	@Override
	public void setOnlineMillis(final long onlineMillis) {
		this.onlineMillis = onlineMillis;
	}

	@Override
	public boolean isMailbinding() {
		return mailbinding;
	}

	@Override
	public void setMailbinding(final boolean mailbinding) {
		this.mailbinding = mailbinding;
	}

	@Override
	public boolean isMobilebinding() {
		return mobilebinding;
	}

	@Override
	public void setMobilebinding(final boolean mobilebinding) {
		this.mobilebinding = mobilebinding;
	}

	@Override
	public String toString() {
		return StringUtils.text(getName(), super.toString());
	}

	public static String encrypt(final String password) {
		return AlgorithmUtils.md5Hex(password == null ? "" : password.trim());
	}

	public static DbEntityTable TBL = new DbEntityTable(Account.class, "sf_organization_account");

	private static final long serialVersionUID = -2003319378229277570L;
}
