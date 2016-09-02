package net.simpleframework.organization.bean;

import net.simpleframework.ado.bean.AbstractTextDescriptionBean;
import net.simpleframework.ado.bean.INameBeanAware;
import net.simpleframework.ado.bean.IOrderBeanAware;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
@SuppressWarnings("serial")
public abstract class AbstractOrganizationBean extends AbstractTextDescriptionBean
		implements INameBeanAware, IOrderBeanAware {

	private String name;

	/** 排序 **/
	private int oorder;

	@Override
	public String getName() {
		return name != null ? name.trim() : null;
	}

	@Override
	public void setName(final String name) {
		this.name = name;
	}

	@Override
	public int getOorder() {
		return oorder;
	}

	@Override
	public void setOorder(final int oorder) {
		this.oorder = oorder;
	}
}
