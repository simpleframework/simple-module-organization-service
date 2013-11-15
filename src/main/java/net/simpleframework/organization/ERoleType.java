package net.simpleframework.organization;

import static net.simpleframework.common.I18n.$m;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public enum ERoleType {

	/**
	 * 正常的角色类型，需要分配角色成员
	 */
	normal {

		@Override
		public String toString() {
			return $m("ERoleType.normal");
		}
	},

	/**
	 * 接口类型
	 */
	handle {

		@Override
		public String toString() {
			return $m("ERoleType.handle");
		}
	},

	/**
	 * 表达式返回Boolean，判断是否角色成员
	 */
	script {

		@Override
		public String toString() {
			return $m("ERoleType.script");
		}
	}
}
