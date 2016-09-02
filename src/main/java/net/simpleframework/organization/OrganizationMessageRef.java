package net.simpleframework.organization;

import static net.simpleframework.common.I18n.$m;

import java.io.IOException;

import net.simpleframework.common.ClassUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.ctx.IContextBase;
import net.simpleframework.module.msg.MessageRef;
import net.simpleframework.module.msg.plugin.NoticeMessageCategory;
import net.simpleframework.module.msg.plugin.NoticeMessagePlugin;
import net.simpleframework.organization.bean.Account;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class OrganizationMessageRef extends MessageRef {

	protected NoticeMessageCategory MC_ACCOUNT_CREATED;

	@Override
	public void onInit(final IContextBase context) throws Exception {
		super.onInit(context);

		final NoticeMessagePlugin plugin = getNoticeMessagePlugin();

		// 创建帐号
		plugin.registMessageCategory(setGroup(MC_ACCOUNT_CREATED = MC_ACCOUNT_CREATED()));
	}

	protected NoticeMessageCategory setGroup(final NoticeMessageCategory plugin) {
		plugin.setGroupText($m("OrganizationMessageRef.2"));
		return plugin;
	}

	protected NoticeMessageCategory MC_ACCOUNT_CREATED() throws IOException {
		return new NoticeMessageCategory("MC_ACCOUNT", $m("OrganizationMessageRef.0"),
				$m("OrganizationMessageRef.1"),
				ClassUtils.getResourceAsString(OrganizationMessageRef.class, "MC_ACCOUNT_CREATED.txt"));
	}

	public void doAccountCreatedMessage(final Account account) {
		if (MC_ACCOUNT_CREATED == null) {
			return;
		}
		getNoticeMessagePlugin().sentMessage(account.getId(), MC_ACCOUNT_CREATED,
				new KVMap().add("account", account));
	}
}
