package net.simpleframework.organization;

import java.util.Iterator;
import java.util.Map;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public interface IRoleHandler {

	/**
	 * 判断用户是否属于当前的规则角色
	 * 
	 * @param user
	 * @param variables
	 * @return
	 */
	boolean isMember(User user, Map<String, Object> variables);

	/**
	 * 当前规则角色中的成员
	 * 
	 * @param variables
	 * @return
	 */
	Iterator<User> members(Map<String, Object> variables);
}
