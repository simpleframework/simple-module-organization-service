package net.simpleframework.organization.bean;

import net.simpleframework.ado.bean.AbstractDateAwareBean;
import net.simpleframework.common.ID;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class AccountSSO extends AbstractDateAwareBean {
	/* 帐号id */
	private ID accountId;

	/* 第三方帐号服务商 */
	private String openprovider;
	/* 开放平台appId */
	private String appId;
	/* 第三方帐号id */
	private String openid;

	public ID getAccountId() {
		return accountId;
	}

	public void setAccountId(final ID accountId) {
		this.accountId = accountId;
	}

	public String getOpenprovider() {
		return openprovider;
	}

	public void setOpenprovider(final String openprovider) {
		this.openprovider = openprovider;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(final String appId) {
		this.appId = appId;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(final String openid) {
		this.openid = openid;
	}

	private static final long serialVersionUID = -5044278411352969789L;
}
