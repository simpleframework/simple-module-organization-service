package net.simpleframework.organization.impl;

import java.util.Enumeration;
import java.util.Map;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.object.ObjectEx;
import net.simpleframework.organization.IOrganizationContextAware;
import net.simpleframework.organization.IRoleHandler;
import net.simpleframework.organization.User;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class AbstractRoleHandler extends ObjectEx implements IRoleHandler,
		IOrganizationContextAware {

	@Override
	public Enumeration<User> members(final Map<String, Object> variables) {
		final IDataQuery<User> dq = context.getUserService().queryAll();
		return new Enumeration<User>() {
			@Override
			public boolean hasMoreElements() {
				return (user = dq.next()) != null && isMember(user, variables);
			}

			@Override
			public User nextElement() {
				return user;
			}

			private User user = null;
		};
	}
}
