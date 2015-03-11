package net.simpleframework.organization.impl;

import java.util.Map;

import net.simpleframework.organization.Account;
import net.simpleframework.organization.EAccountStatus;
import net.simpleframework.organization.User;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class BuiltInRole {

	public static class All extends AbstractRoleHandler {

		@Override
		public boolean isMember(final User user, final Map<String, Object> variables) {
			final Account account = orgContext.getUserService().getAccount(user.getId());
			return account != null && account.getStatus() == EAccountStatus.normal;
		}
	}

	public static class Lock extends AbstractRoleHandler {

		@Override
		public boolean isMember(final User user, final Map<String, Object> variables) {
			final Account account = orgContext.getUserService().getAccount(user.getId());
			return account != null && account.getStatus() == EAccountStatus.locked;
		}
	}

	public static class Anonymous extends AbstractRoleHandler {

		@Override
		public boolean isMember(final User user, final Map<String, Object> variables) {
			return true;
		}
	}

	public static class InDept extends AbstractRoleHandler {
		@Override
		public boolean isMember(final User user, final Map<String, Object> variables) {
			return false;
		}
	}
}
