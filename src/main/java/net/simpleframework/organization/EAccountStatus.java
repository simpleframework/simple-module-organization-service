package net.simpleframework.organization;

import static net.simpleframework.common.I18n.$m;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public enum EAccountStatus {

	/**
	 * 正常
	 */
	normal {
		@Override
		public String toString() {
			return $m("EAccountStatus.normal");
		}
	},

	/**
	 * 注册
	 */
	registration {
		@Override
		public String toString() {
			return $m("EAccountStatus.registration");
		}
	},

	/**
	 * 锁定
	 */
	locked {
		@Override
		public String toString() {
			return $m("EAccountStatus.locked");
		}
	},

	/**
	 * 删除
	 */
	delete {
		@Override
		public String toString() {
			return $m("EAccountStatus.delete");
		}
	}
}
