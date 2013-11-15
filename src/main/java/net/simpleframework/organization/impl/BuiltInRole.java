package net.simpleframework.organization.impl;

import java.util.Map;

import net.simpleframework.organization.EAccountStatus;
import net.simpleframework.organization.IAccount;
import net.simpleframework.organization.IUser;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class BuiltInRole {

	public static class All extends AbstractRoleHandler {

		@Override
		public boolean isMember(final IUser user, final Map<String, Object> variables) {
			final IAccount account = context.getUserService().getAccount(user.getId());
			return account != null && account.getStatus() == EAccountStatus.normal;
		}
	}

	public static class Lock extends AbstractRoleHandler {

		@Override
		public boolean isMember(final IUser user, final Map<String, Object> variables) {
			final IAccount account = context.getUserService().getAccount(user.getId());
			return account != null && account.getStatus() == EAccountStatus.locked;
		}
	}

	public static class Anonymous extends AbstractRoleHandler {

		@Override
		public boolean isMember(final IUser user, final Map<String, Object> variables) {
			return true;
		}
	}
}
