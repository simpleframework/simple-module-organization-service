package net.simpleframework.organization.bean;

import net.simpleframework.ado.ColumnMeta;
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

	/* 统计类型 */
	private EStatType statType;

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

	public EStatType getStatType() {
		return statType == null ? EStatType.dept : statType;
	}

	public void setStatType(final EStatType statType) {
		this.statType = statType;
	}

	public int getNums() {
		return nums;
	}

	@ColumnMeta(ignore = true)
	public int getRnums() {
		return getNums() - getState_delete();
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

	public static enum EStatType {
		all,

		org,

		dept
	}

	private static final long serialVersionUID = 6410017892099090654L;
}
