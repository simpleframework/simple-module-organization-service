package net.simpleframework.organization;

import java.util.Collection;
import java.util.Map;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
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
	boolean isMember(IUser user, Map<String, Object> variables);

	/**
	 * 当前规则角色中的成员，一般可不实现此方法
	 * 
	 * @param variables
	 * @return
	 */
	Collection<IUser> members(Map<String, Object> variables);
}
