package net.simpleframework.organization.impl;

import java.util.ArrayList;

import net.simpleframework.ado.query.DataQueryUtils;
import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.ctx.service.ado.db.AbstractDbBeanService;
import net.simpleframework.organization.ERoleMemberType;
import net.simpleframework.organization.IOrganizationContextAware;
import net.simpleframework.organization.IRoleMemberService;
import net.simpleframework.organization.Role;
import net.simpleframework.organization.RoleMember;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class RoleMemberService extends AbstractDbBeanService<RoleMember> implements
		IRoleMemberService, IOrganizationContextAware {

	@Override
	public IDataQuery<RoleMember> queryMembers(final Role role) {
		if (role == null) {
			return DataQueryUtils.nullQuery();
		}
		return query("roleid=? order by oorder", role.getId());
	}

	@Override
	public void setPrimary(final RoleMember member) {
		if (member == null || member.getMemberType() != ERoleMemberType.user) {
			return;
		}

		if (member.isPrimaryRole()) {
			member.setPrimaryRole(false);
			update(new String[] { "primaryrole" }, member);
			return;
		}

		final ArrayList<RoleMember> beans = new ArrayList<RoleMember>();
		member.setPrimaryRole(true);
		beans.add(member);

		final IDataQuery<RoleMember> qd = query("membertype=? and memberid=?", ERoleMemberType.user,
				member.getMemberId());
		RoleMember member2;
		while ((member2 = qd.next()) != null) {
			if (!member2.equals(member) && member2.isPrimaryRole()) {
				member2.setPrimaryRole(false);
				beans.add(member2);
			}
		}

		update(new String[] { "primaryrole" }, beans.toArray(new RoleMember[beans.size()]));
	}
}
