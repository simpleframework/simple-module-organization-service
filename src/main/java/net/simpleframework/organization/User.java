package net.simpleframework.organization;

import static net.simpleframework.common.I18n.$m;

import java.util.Date;

import net.simpleframework.ado.ColumnMeta;
import net.simpleframework.ado.bean.AbstractTextDescriptionBean;
import net.simpleframework.ado.bean.IOrderBeanAware;
import net.simpleframework.ado.db.common.EntityInterceptor;
import net.simpleframework.common.ID;
import net.simpleframework.common.StringUtils;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
@EntityInterceptor(listenerTypes = { "net.simpleframework.module.log.EntityUpdateLogAdapter",
		"net.simpleframework.module.log.EntityDeleteLogAdapter" }, columns = { "text", "email",
		"mobile", "departmentId" })
public class User extends AbstractTextDescriptionBean implements IOrderBeanAware,
		IOrganizationContextAware {
	private static final long serialVersionUID = -4938630954415307539L;

	/** 性别 **/
	private String sex;

	/** 生日 **/
	private Date birthday;

	/* ----------------------联系方式------------------------- */
	/** 邮件 **/
	@ColumnMeta(columnText = "#(User.0)")
	private String email;

	/** 家庭电话 **/
	private String homePhone;

	/** 办公电话 **/
	private String officePhone;

	/** 移动电话 **/
	@ColumnMeta(columnText = "#(User.1)")
	private String mobile;

	/** 移动电话2 **/
	private String mobile2;

	/** 地址 **/
	@ColumnMeta(columnText = "#(User.2)")
	private String address;

	/** 所在地 **/
	private String hometown;

	/** 邮政编码 **/
	private String postcode;

	private String qq;

	private String msn;

	/* 部门id */
	private ID departmentId;
	/* 机构id */
	private ID orgId;

	/** 排序 **/
	private int oorder;

	public ID getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(final ID departmentId) {
		this.departmentId = departmentId;
		if (departmentId != null) {
			final Department org = orgContext.getDepartmentService().getOrg(departmentId);
			if (org != null) {
				setOrgId(org.getId());
			}
		}
	}

	public ID getOrgId() {
		return orgId;
	}

	public void setOrgId(final ID orgId) {
		this.orgId = orgId;
	}

	@Override
	public int getOorder() {
		return oorder;
	}

	@Override
	public void setOorder(final int oorder) {
		this.oorder = oorder;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(final Date birthday) {
		this.birthday = birthday;
	}

	public String getSex() {
		return sex != null ? sex : $m("User.3");
	}

	public void setSex(final String sex) {
		this.sex = sex;
	}

	public String getHometown() {
		return hometown;
	}

	public void setHometown(final String hometown) {
		this.hometown = hometown;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(final String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(final String mobile) {
		this.mobile = mobile;
	}

	public String getMobile2() {
		return StringUtils.hasText(mobile2) ? mobile2 : getMobile();
	}

	public void setMobile2(final String mobile2) {
		this.mobile2 = mobile2;
	}

	public String getHomePhone() {
		return homePhone;
	}

	public void setHomePhone(final String homePhone) {
		this.homePhone = homePhone;
	}

	public String getOfficePhone() {
		return officePhone;
	}

	public void setOfficePhone(final String officePhone) {
		this.officePhone = officePhone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(final String address) {
		this.address = address;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(final String postcode) {
		this.postcode = postcode;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(final String qq) {
		this.qq = qq;
	}

	public String getMsn() {
		return msn;
	}

	public void setMsn(final String msn) {
		this.msn = msn;
	}
}
