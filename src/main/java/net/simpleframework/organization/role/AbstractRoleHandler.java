package net.simpleframework.organization.role;

import java.util.Iterator;
import java.util.Map;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.coll.CollectionUtils.AbstractIterator;
import net.simpleframework.common.object.ObjectEx;
import net.simpleframework.organization.IOrganizationContextAware;
import net.simpleframework.organization.bean.User;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class AbstractRoleHandler extends ObjectEx
		implements IRoleHandler, IOrganizationContextAware {

	@Override
	public Iterator<User> members(final Map<String, Object> variables) {
		final IDataQuery<User> dq = _userService.queryAll().setFetchSize(0);
		return new AbstractIterator<User>() {
			@Override
			public boolean hasNext() {
				return (user = dq.next()) != null && isMember(user, variables);
			}

			@Override
			public User next() {
				return user;
			}

			private User user = null;
		};
	}
}
