package net.simpleframework.organization;

import java.util.Date;

import net.simpleframework.ado.ColumnMeta;
import net.simpleframework.ado.bean.AbstractNameBean;
import net.simpleframework.ado.db.common.EntityInterceptor;
import net.simpleframework.common.AlgorithmUtils;
import net.simpleframework.common.StringUtils;
import net.simpleframework.ctx.permission.IPermissionConst;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
@EntityInterceptor(listenerTypes = { "net.simpleframework.module.log.EntityUpdateLogAdapter",
		"net.simpleframework.module.log.EntityDeleteLogAdapter" }, columns = { "password", "status",
		"mailbinding", "mobilebinding" })
public class Account extends AbstractNameBean {
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
	public boolean isAdmin() {
		return IPermissionConst.ADMIN.equals(getName());
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

	public EAccountStatus getStatus() {
		return status == null ? EAccountStatus.normal : status;
	}

	public void setStatus(final EAccountStatus status) {
		this.status = status;
	}

	public EAccountMark getAccountMark() {
		return accountMark == null ? EAccountMark.normal : accountMark;
	}

	public void setAccountMark(final EAccountMark accountMark) {
		this.accountMark = accountMark;
	}

	public boolean isLogin() {
		return login;
	}

	public void setLogin(final boolean login) {
		this.login = login;
	}

	public int getLoginTimes() {
		return loginTimes;
	}

	public void setLoginTimes(final int loginTimes) {
		this.loginTimes = loginTimes;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(final Date createDate) {
		this.createDate = createDate;
	}

	public Date getLastLoginDate() {
		return lastLoginDate;
	}

	public void setLastLoginDate(final Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}

	public String getLastLoginIP() {
		return lastLoginIP;
	}

	public void setLastLoginIP(final String lastLoginIP) {
		this.lastLoginIP = lastLoginIP;
	}

	public long getOnlineMillis() {
		return onlineMillis;
	}

	public void setOnlineMillis(final long onlineMillis) {
		this.onlineMillis = onlineMillis;
	}

	public boolean isMailbinding() {
		return mailbinding;
	}

	public void setMailbinding(final boolean mailbinding) {
		this.mailbinding = mailbinding;
	}

	public boolean isMobilebinding() {
		return mobilebinding;
	}

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

	private static final long serialVersionUID = -2003319378229277570L;
}
