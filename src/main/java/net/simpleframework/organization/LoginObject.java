package net.simpleframework.organization;

import java.io.Serializable;

import net.simpleframework.common.ID;
import net.simpleframework.common.object.DescriptionObject;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public class LoginObject extends DescriptionObject<LoginObject> implements Serializable {
	{
		enableAttributes();
	}

	private final ID accountId;

	public LoginObject(final ID accountId) {
		this.accountId = accountId;
	}

	public ID getAccountId() {
		return accountId;
	}

	private static final long serialVersionUID = -7561042148238301694L;
}
