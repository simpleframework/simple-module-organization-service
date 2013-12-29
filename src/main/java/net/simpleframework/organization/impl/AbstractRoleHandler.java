package net.simpleframework.organization.impl;

import java.util.Enumeration;
import java.util.Map;

import net.simpleframework.ado.query.IDataQuery;
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
	public Enumeration<IUser> members(final Map<String, Object> variables) {
		final IDataQuery<IUser> dq = context.getUserService().queryAll();
		return new Enumeration<IUser>() {
			@Override
			public boolean hasMoreElements() {
				return (user = dq.next()) != null && isMember(user, variables);
			}

			@Override
			public IUser nextElement() {
				return user;
			}

			private IUser user = null;
		};
	}
}
