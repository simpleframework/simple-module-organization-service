package net.simpleframework.organization;

import static net.simpleframework.common.I18n.$m;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public enum ERoleMemberType {
	/* 用户 */
	user {

		@Override
		public String toString() {
			return $m("ERoleMemberType.user");
		}
	},

	/* 角色，不能嵌套 */
	role {

		@Override
		public String toString() {
			return $m("ERoleMemberType.role");
		}
	},

	/* 部门 */
	dept {
		@Override
		public String toString() {
			return $m("ERoleMemberType.dept");
		}
	}
}
