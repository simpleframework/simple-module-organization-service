package net.simpleframework.organization.impl;

import java.util.ArrayList;
import java.util.List;

import net.simpleframework.ado.IParamsValue;
import net.simpleframework.ado.db.IDbEntityManager;
import net.simpleframework.ado.query.DataQueryUtils;
import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.coll.ArrayUtils;
import net.simpleframework.organization.Department;
import net.simpleframework.organization.ERoleMemberType;
import net.simpleframework.organization.ERoleType;
import net.simpleframework.organization.IRoleMemberService;
import net.simpleframework.organization.Role;
import net.simpleframework.organization.RoleMember;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class RoleMemberService extends AbstractOrganizationService<RoleMember> implements
		IRoleMemberService {

	@Override
	public IDataQuery<RoleMember> queryRoleMembers(final Role role, final Department dept) {
		if (role == null || role.getRoleType() != ERoleType.normal) {
			return DataQueryUtils.nullQuery();
		}
		final StringBuilder sb = new StringBuilder("roleid=?");
		final List<Object> params = ArrayUtils.toParams(role.getId());
		if (dept != null) {
			sb.append(" and deptid=?");
			params.add(dept.getId());
		}
		return query(sb.append(" order by oorder"), params.toArray());
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

	@Override
	public void onInit() throws Exception {
		super.onInit();

		addListener(new DbEntityAdapterEx<RoleMember>() {
			@Override
			public void onBeforeDelete(final IDbEntityManager<RoleMember> manager,
					final IParamsValue paramsValue) throws Exception {
				super.onBeforeDelete(manager, paramsValue);
				for (final RoleMember rm : coll(manager, paramsValue)) {
					doUpdateMembers(rm, -1);
				}
			}

			@Override
			public void onAfterInsert(final IDbEntityManager<RoleMember> manager,
					final RoleMember[] beans) throws Exception {
				super.onAfterInsert(manager, beans);
				for (final RoleMember rm : beans) {
					doUpdateMembers(rm, 0);
				}
			}

			private void doUpdateMembers(final RoleMember rm, final int delta) {
				final Role role = _roleService.getBean(rm.getRoleId());
				role.setMembers(count("roleid=?", role.getId()) + delta);
				_roleService.update(new String[] { "members" }, role);
			}
		});
	}
}
