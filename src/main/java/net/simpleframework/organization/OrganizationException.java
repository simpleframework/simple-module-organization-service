package net.simpleframework.organization;

import net.simpleframework.ctx.ModuleException;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class OrganizationException extends ModuleException {
	private static final long serialVersionUID = 6885538428759225872L;

	public OrganizationException(final String msg, final Throwable cause) {
		super(msg, cause);
	}

	public static ModuleException of(final Throwable throwable) {
		return (ModuleException) _of(OrganizationException.class, null, throwable);
	}

	public static OrganizationException of(final String msg) {
		return _of(OrganizationException.class, msg);
	}

	public static int CODE_LOGGED = 1;
}
