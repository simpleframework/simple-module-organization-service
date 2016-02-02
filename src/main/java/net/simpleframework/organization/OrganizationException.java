package net.simpleframework.organization;

import net.simpleframework.common.th.RuntimeExceptionEx;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class OrganizationException extends RuntimeExceptionEx {

	public OrganizationException(final String msg, final Throwable cause) {
		super(msg, cause);
	}

	public static OrganizationException of(final Throwable throwable) {
		return _of(OrganizationException.class, null, throwable);
	}

	public static OrganizationException of(final String msg) {
		return _of(OrganizationException.class, msg);
	}

	private static final long serialVersionUID = 6885538428759225872L;
}
