package net.simpleframework.organization.impl;

import net.simpleframework.ado.bean.AbstractTextDescriptionBean;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
@SuppressWarnings("serial")
public abstract class AbstractOrganizationBean extends AbstractTextDescriptionBean {

	private String name;

	/** 排序 **/
	private long oorder;

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public long getOorder() {
		return oorder;
	}

	public void setOorder(final long oorder) {
		this.oorder = oorder;
	}
}
