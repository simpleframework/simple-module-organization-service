package net.simpleframework.organization.impl;

import java.util.List;

import net.simpleframework.ado.query.DataQueryUtils;
import net.simpleframework.common.ID;
import net.simpleframework.common.StringUtils;
import net.simpleframework.organization.IAccountSSOService;
import net.simpleframework.organization.bean.AccountSSO;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class AccountSSOService extends AbstractOrganizationService<AccountSSO>
		implements IAccountSSOService {
	@Override
	public AccountSSO addAccountSSO(final ID accountId, final String openprovider,
			final String openid) {
		return addAccountSSO(accountId, openprovider, null, openid);
	}

	@Override
	public AccountSSO addAccountSSO(final ID accountId, final String openprovider,
			final String appid, final String openid) {
		final AccountSSO sso = new AccountSSO();
		sso.setAccountId(accountId);
		sso.setOpenprovider(openprovider);
		sso.setAppId(appid);
		sso.setOpenid(openid);
		insert(sso);
		return sso;
	}

	@Override
	public List<AccountSSO> getAccountSSOList(final Object account) {
		return DataQueryUtils.toList(query("accountid=?", getIdParam(account)));
	}

	@Override
	public AccountSSO getAccountSSO(final String openprovider, final String appId,
			final String openid) {
		if (StringUtils.hasText(appId)) {
			return getBean("openprovider=? and appid=? and openid=?", openprovider, appId, openid);
		} else {
			return getBean("openprovider=? and openid=?", openprovider, openid);
		}
	}

	@Override
	public AccountSSO getAccountSSO(final String openprovider, final String openid) {
		return getAccountSSO(openprovider, null, openid);
	}

	@Override
	public AccountSSO getAccountSSO(final String openprovider, final ID accountId) {
		return getBean("openprovider=? and accountid=?", openprovider, accountId);
	}

}
