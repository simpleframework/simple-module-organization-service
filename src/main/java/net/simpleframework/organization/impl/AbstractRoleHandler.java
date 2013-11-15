package net.simpleframework.organization.impl;

import java.util.Collection;
import java.util.Map;

import net.simpleframework.common.object.ObjectEx;
import net.simpleframework.organization.IOrganizationContextAware;
import net.simpleframework.organization.IRoleHandler;
import net.simpleframework.organization.IUser;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class AbstractRoleHandler extends ObjectEx implements IRoleHandler,
		IOrganizationContextAware {

	@Override
	public Collection<IUser> members(final Map<String, Object> variables) {
		return null;
	}
}
