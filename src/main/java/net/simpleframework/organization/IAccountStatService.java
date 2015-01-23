package net.simpleframework.organization;

import net.simpleframework.ctx.service.ado.db.IDbBeanService;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public interface IAccountStatService extends IDbBeanService<AccountStat> {

	/**
	 * 获取部门的统计数据
	 * 
	 * @param dept
	 * @return
	 */
	AccountStat getAccountStat(Object dept);

	/**
	 * 获取全局的统计数据
	 * 
	 * @return
	 */
	AccountStat getAccountStat();
}
