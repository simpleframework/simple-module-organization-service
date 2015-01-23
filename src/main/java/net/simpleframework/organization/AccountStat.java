package net.simpleframework.organization;

import net.simpleframework.ado.bean.AbstractIdBean;
import net.simpleframework.common.ID;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class AccountStat extends AbstractIdBean {
	/* 部门id，可为空 */
	private ID deptId;
	/* 机构id */
	private ID orgId;

	/* 总数 */
	private int nums;
	/* 在线用户数 */
	private int online_nums;

	/* 正常用户数 */
	private int state_normal;
	/* 注册用户数 */
	private int state_registration;
	/* 锁定用户数 */
	private int state_locked;
	/* 删除用户数 */
	private int state_delete;

	public ID getDeptId() {
		return deptId;
	}

	public void setDeptId(final ID deptId) {
		this.deptId = deptId;
	}

	public ID getOrgId() {
		return orgId;
	}

	public void setOrgId(final ID orgId) {
		this.orgId = orgId;
	}

	public int getNums() {
		return nums;
	}

	public void setNums(final int nums) {
		this.nums = nums;
	}

	public int getOnline_nums() {
		return online_nums;
	}

	public void setOnline_nums(final int online_nums) {
		this.online_nums = online_nums;
	}

	public int getState_normal() {
		return state_normal;
	}

	public void setState_normal(final int state_normal) {
		this.state_normal = state_normal;
	}

	public int getState_registration() {
		return state_registration;
	}

	public void setState_registration(final int state_registration) {
		this.state_registration = state_registration;
	}

	public int getState_locked() {
		return state_locked;
	}

	public void setState_locked(final int state_locked) {
		this.state_locked = state_locked;
	}

	public int getState_delete() {
		return state_delete;
	}

	public void setState_delete(final int state_delete) {
		this.state_delete = state_delete;
	}

	private static final long serialVersionUID = 6410017892099090654L;
}
