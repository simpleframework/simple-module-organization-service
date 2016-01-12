package net.simpleframework.organization.role;

import java.util.Iterator;
import java.util.Map;

import net.simpleframework.ado.query.DataQueryUtils;
import net.simpleframework.common.coll.CollectionUtils;
import net.simpleframework.ctx.permission.PermissionConst;
import net.simpleframework.organization.Account;
import net.simpleframework.organization.Account.EAccountStatus;
import net.simpleframework.organization.Department;
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
			final Account account = _userService.getAccount(user.getId());
			return account != null && account.getStatus() == EAccountStatus.normal;
		}
	}

	public static class Lock extends AbstractRoleHandler {

		@Override
		public boolean isMember(final User user, final Map<String, Object> variables) {
			final Account account = _userService.getAccount(user.getId());
			return account != null && account.getStatus() == EAccountStatus.locked;
		}
	}

	public static class Anonymous extends AbstractRoleHandler {

		@Override
		public boolean isMember(final User user, final Map<String, Object> variables) {
			return true;
		}
	}

	public static class InOrg extends AbstractRoleHandler {

		@Override
		public boolean isMember(final User user, final Map<String, Object> variables) {
			final Object orgId = variables.get(PermissionConst.VAR_ORGID);
			return orgId != null && orgId.toString().equals(user.getOrgId().toString());
		}
	}

	public static class InDept extends AbstractRoleHandler {
		@Override
		public boolean isMember(final User user, final Map<String, Object> variables) {
			final Object deptId = variables.get(PermissionConst.VAR_DEPTID);
			return deptId != null && deptId.toString().equals(user.getDepartmentId().toString());
		}

		@Override
		public Iterator<User> members(final Map<String, Object> variables) {
			if (variables != null) {
				Department dept = _deptService.getBean(variables.get(PermissionConst.VAR_DEPTID));
				if (dept == null) {
					final User user = _userService.getBean(variables.get(PermissionConst.VAR_USERID));
					if (user != null) {
						dept = _deptService.getBean(user.getDepartmentId());
					}
				}
				if (dept != null) {
					return DataQueryUtils.toIterator(_userService.queryUsers(dept));
				}
			}
			return CollectionUtils.EMPTY_ITERATOR();
		}
	}
}
