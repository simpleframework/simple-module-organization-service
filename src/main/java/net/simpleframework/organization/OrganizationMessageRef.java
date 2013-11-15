package net.simpleframework.organization;

import static net.simpleframework.common.I18n.$m;
import net.simpleframework.common.ClassUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.common.object.ObjectUtils;
import net.simpleframework.ctx.IModuleContext;
import net.simpleframework.module.msg.MessageRef;
import net.simpleframework.module.msg.plugin.NoticeMessageCategoryPlugin;
import net.simpleframework.module.msg.plugin.NoticeMessagePlugin;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class OrganizationMessageRef extends MessageRef {

	protected NoticeMessageCategoryPlugin MC_ACCOUNT;

	@Override
	public void onInit(final IModuleContext context) throws Exception {
		super.onInit(context);

		final NoticeMessagePlugin plugin = getNoticeMessagePlugin();

		// 创建帐号
		MC_ACCOUNT = new NoticeMessageCategoryPlugin(ObjectUtils.hashInt("MC_ACCOUNT"),
				$m("OrganizationMessageRef.0"), $m("OrganizationMessageRef.1"),
				ClassUtils.getResourceAsString(OrganizationMessageRef.class, "MC_ACCOUNT.txt"));
		plugin.registMessageCategoryPlugin(setGroup(MC_ACCOUNT));
	}

	protected NoticeMessageCategoryPlugin setGroup(final NoticeMessageCategoryPlugin plugin) {
		plugin.setGroupText($m("OrganizationMessageRef.2"));
		return plugin;
	}

	public void doAccountCreatedMessage(final IAccount account) {
		if (MC_ACCOUNT == null) {
			return;
		}
		getNoticeMessagePlugin().sentMessage(account.getId(), MC_ACCOUNT,
				new KVMap().add("account", account));
	}
}
