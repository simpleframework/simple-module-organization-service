package net.simpleframework.organization;

import java.util.List;

import net.simpleframework.common.ID;
import net.simpleframework.ctx.service.ado.db.IDbBeanService;
import net.simpleframework.organization.bean.AccountSSO;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public interface IAccountSSOService extends IDbBeanService<AccountSSO> {

	/**
	 * 添加帐号单点对象
	 * 
	 * @param accountId
	 * @param openprovider
	 * @param openid
	 * @return
	 */
	AccountSSO addAccountSSO(ID accountId, String openprovider, String openid);

	/**
	 * 添加帐号单点对象
	 * @param accountId
	 * @param openprovider
	 * @param appid 授权appid
	 * @param openid
	 * @return
	 */
	AccountSSO addAccountSSO(ID accountId, String openprovider, String appid, String openid);

	/**
	 * 获取帐号单点列表
	 * 
	 * @param account
	 * @return
	 */
	List<AccountSSO> getAccountSSOList(Object account);

	/**
	 * 获取帐号单点对象
	 * 
	 * @param openprovider
	 * @param appId
	 * @param openid
	 * @return
	 */
	AccountSSO getAccountSSO(String openprovider, String appId, String openid);

	AccountSSO getAccountSSO(String openprovider, String openid);

	AccountSSO getAccountSSO(String openprovider, ID accountId);
}
