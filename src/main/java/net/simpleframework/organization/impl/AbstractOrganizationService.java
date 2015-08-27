package net.simpleframework.organization.impl;

import net.simpleframework.ado.ColumnData;
import net.simpleframework.ctx.IModuleContext;
import net.simpleframework.ctx.service.ado.db.AbstractDbBeanService;
import net.simpleframework.organization.IOrganizationContextAware;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class AbstractOrganizationService<T> extends AbstractDbBeanService<T> implements
		IOrganizationContextAware {

	@Override
	public IModuleContext getModuleContext() {
		return orgContext;
	}

	@Override
	protected ColumnData[] getDefaultOrderColumns() {
		return ORDER_OORDER;
	}
}