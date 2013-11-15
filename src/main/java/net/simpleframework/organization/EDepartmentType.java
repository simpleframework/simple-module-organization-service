package net.simpleframework.organization;

import static net.simpleframework.common.I18n.$m;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public enum EDepartmentType {

	/**
	 * 部门
	 */
	department {

		@Override
		public String toString() {
			return $m("EDepartmentType.department");
		}
	},
	/**
	 * 机构
	 */
	organization {

		@Override
		public String toString() {
			return $m("EDepartmentType.organization");
		}
	}
}
