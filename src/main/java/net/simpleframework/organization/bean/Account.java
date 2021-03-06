package net.simpleframework.organization.bean;

import static net.simpleframework.common.I18n.$m;

import java.util.Date;

import net.simpleframework.ado.ColumnMeta;
import net.simpleframework.ado.bean.AbstractNameBean;
import net.simpleframework.ado.db.common.EntityInterceptor;
import net.simpleframework.common.ID;
import net.simpleframework.common.StringUtils;
import net.simpleframework.ctx.permission.PermissionConst;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
@EntityInterceptor(listenerTypes = { "net.simpleframework.module.log.EntityUpdateLogAdapter",
		"net.simpleframework.module.log.EntityDeleteLogAdapter" }, columns = { "name", "password",
				"status", "mailbinding", "mobilebinding" })
public class Account extends AbstractNameBean {
	/* 密码 */
	@ColumnMeta(columnText = "#(Account.0)")
	private String password;

	/* 状态 */
	@ColumnMeta(columnText = "#(Account.1)")
	private EAccountStatus status;

	/* 创建时间 */
	private Date createDate;

	/* 是否登录 */
	private boolean login;

	/* 用户会话id */
	private String sessionid;

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

	/* 账号位置信息经度 */
	private double longitude;

	/* 账号位置信息纬度 */
	private double latitude;

	/* 所在的城市编码 */
	private String cityCode;

	/* 移动设备号 */
	private String mdevid;

	/* 帐号过期时间 */
	private Date expireDate;

	/* 推荐人id */
	private ID refereeId;

	@ColumnMeta(ignore = true)
	public boolean isAdmin() {
		return PermissionConst.ADMIN.equals(getName());
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

	public boolean isLogin() {
		return login;
	}

	public void setLogin(final boolean login) {
		this.login = login;
	}

	public String getSessionid() {
		// 不允许为null
		return StringUtils.blank(sessionid);
	}

	public void setSessionid(final String sessionid) {
		this.sessionid = sessionid;
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

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(final double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(final double latitude) {
		this.latitude = latitude;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(final String cityCode) {
		this.cityCode = cityCode;
	}

	public String getMdevid() {
		return mdevid;
	}

	public void setMdevid(final String mdevid) {
		this.mdevid = mdevid;
	}

	public Date getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(final Date expireDate) {
		this.expireDate = expireDate;
	}

	public ID getRefereeId() {
		return refereeId;
	}

	public void setRefereeId(final ID refereeId) {
		this.refereeId = refereeId;
	}

	@Override
	public String toString() {
		final String name = getName();
		return StringUtils.hasText(name) ? name : super.toString();
	}

	public static enum EAccountStatus {

		/**
		 * 正常
		 */
		normal {
			@Override
			public String toString() {
				return $m("EAccountStatus.normal");
			}
		},

		/**
		 * 注册
		 */
		registration {
			@Override
			public String toString() {
				return $m("EAccountStatus.registration");
			}
		},

		/**
		 * 锁定
		 */
		locked {
			@Override
			public String toString() {
				return $m("EAccountStatus.locked");
			}
		},

		/**
		 * 删除
		 */
		delete {
			@Override
			public String toString() {
				return $m("EAccountStatus.delete");
			}
		}
	}

	public static enum EAccountType {

		/**
		 * 正常账号
		 */
		normal,

		/**
		 * 用电子邮件作为账号登录
		 */
		email,

		/**
		 * 用手机作为账号登录
		 */
		mobile
	}

	public static final int TYPE_ALL = -1;
	public static final int TYPE_ONLINE = -2;

	public static final int TYPE_NO_DEPT = -3;
	public static final int TYPE_DEPT = -4;

	public static final int TYPE_STATE_NORMAL = -11;
	public static final int TYPE_STATE_REGISTRATION = -12;
	public static final int TYPE_STATE_LOCKED = -13;
	public static final int TYPE_STATE_DELETE = -14;

	private static final long serialVersionUID = -2003319378229277570L;
}
