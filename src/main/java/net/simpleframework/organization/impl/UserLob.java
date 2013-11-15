package net.simpleframework.organization.impl;

import java.io.InputStream;

import net.simpleframework.ado.bean.AbstractIdBean;
import net.simpleframework.ado.db.DbEntityTable;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class UserLob extends AbstractIdBean {
	private InputStream photo;

	public InputStream getPhoto() {
		return photo;
	}

	public void setPhoto(final InputStream photo) {
		this.photo = photo;
	}

	public static DbEntityTable TBL = new DbEntityTable(UserLob.class, "sf_organization_user_lob")
			.setNoCache(true);

	private static final long serialVersionUID = -3981205005752155025L;
}
