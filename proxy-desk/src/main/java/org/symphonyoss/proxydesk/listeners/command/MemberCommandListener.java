/*
 *
 *
 * Copyright 2016 The Symphony Software Foundation
 *
 * Licensed to The Symphony Software Foundation (SSF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package org.symphonyoss.proxydesk.listeners.command;

import org.symphonyoss.ai.listeners.AiCommandListener;
import org.symphonyoss.ai.models.AiCommand;
import org.symphonyoss.client.model.Chat;
import org.symphonyoss.proxydesk.config.ProxyBotConfig;
import org.symphonyoss.proxydesk.models.HelpBotSession;
import org.symphonyoss.proxydesk.models.actions.*;
import org.symphonyoss.proxydesk.models.calls.Call;
import org.symphonyoss.proxydesk.models.permissions.IsMember;
import org.symphonyoss.proxydesk.models.permissions.OffCall;
import org.symphonyoss.proxydesk.utils.CallCache;
import org.symphonyoss.proxydesk.utils.DeskUserCache;
import org.symphonyoss.symphony.pod.model.User;

/**
 * Created by nicktarsillo on 6/20/16.
 * A extension of the ai command listener.
 * Initializes all commands a member can command the ai.
 */
public class MemberCommandListener extends AiCommandListener {
    private HelpBotSession helpBotSession;
    private Call call;

    public MemberCommandListener(HelpBotSession helpBotSession) {
        super(helpBotSession.getSymphonyClient());
        helpBotSession.setMemberListener(this);
        this.helpBotSession = helpBotSession;
        call = CallCache.newCall(symClient, false);
        init();
    }

    /**
     * Create and add the commands used by members
     */
    public void init() {

        AiCommand acceptNextHelpClient = new AiCommand(ProxyBotConfig.Config.getString(ProxyBotConfig.ACCEPT_NEXT), 0);
        acceptNextHelpClient.addAction(new AcceptHelpAction(helpBotSession));
        acceptNextHelpClient.addPermission(new IsMember());
        acceptNextHelpClient.addPermission(new OffCall());

        AiCommand acceptHelpClient = new AiCommand(ProxyBotConfig.Config.getString(ProxyBotConfig.ACCEPT), 1);
        acceptHelpClient.setArgument(0, "Client");
        acceptHelpClient.setPrefixRequirement(0, "@");
        acceptHelpClient.addAction(new AcceptHelpAction(helpBotSession));
        acceptHelpClient.addPermission(new IsMember());
        acceptHelpClient.addPermission(new OffCall());

        AiCommand toggleHelp = new AiCommand(ProxyBotConfig.Config.getString(ProxyBotConfig.TOGGLE_ONLINE), 0);
        toggleHelp.addAction(new ToggleSeeHelpAction());
        toggleHelp.addPermission(new IsMember());
        toggleHelp.addPermission(new OffCall());

        AiCommand toggleIdentity = new AiCommand(ProxyBotConfig.Config.getString(ProxyBotConfig.TOGGLE_SHOW_IDENTITY), 0);
        toggleIdentity.addAction(new ToggleIdentityAction());
        toggleIdentity.addPermission(new IsMember());
        toggleIdentity.addPermission(new OffCall());

        AiCommand addMember = new AiCommand(ProxyBotConfig.Config.getString(ProxyBotConfig.ADD_MEMBER), 1);
        addMember.setArgument(0, "Client");
        addMember.setPrefixRequirement(0, "@");
        addMember.addAction(new AddMemberAction(helpBotSession));
        addMember.addPermission(new IsMember());
        addMember.addPermission(new OffCall());

        AiCommand joinChat = new AiCommand(ProxyBotConfig.Config.getString(ProxyBotConfig.JOIN_CHAT), 1);
        joinChat.setArgument(0, "Client/Member");
        joinChat.setPrefixRequirement(0, "@");
        joinChat.addAction(new JoinChatAction(symClient));
        joinChat.addPermission(new IsMember());
        joinChat.addPermission(new OffCall());

        AiCommand setTags = new AiCommand(ProxyBotConfig.Config.getString(ProxyBotConfig.SET_TAGS), 1);
        setTags.setArgument(0, "Tags(ex. Password Reset)");
        setTags.addAction(new SetTagsAction());
        setTags.addPermission(new IsMember());
        setTags.addPermission(new OffCall());

        AiCommand addTags = new AiCommand(ProxyBotConfig.Config.getString(ProxyBotConfig.ADD_TAGS), 1);
        addTags.setArgument(0, "Tags");
        addTags.addAction(new AddTagsAction());
        addTags.addPermission(new IsMember());
        addTags.addPermission(new OffCall());

        AiCommand removeTags = new AiCommand(ProxyBotConfig.Config.getString(ProxyBotConfig.REMOVE_TAGS), 1);
        removeTags.setArgument(0, "Tags");
        removeTags.addAction(new RemoveTagsAction());
        removeTags.addPermission(new IsMember());
        removeTags.addPermission(new OffCall());

        AiCommand onlineMembers = new AiCommand(ProxyBotConfig.Config.getString(ProxyBotConfig.ONLINE_MEMBERS), 0);
        onlineMembers.addAction(new OnlineMembersAction());

        AiCommand queueResponse = new AiCommand(ProxyBotConfig.Config.getString(ProxyBotConfig.CLIENT_QUEUE), 0);
        queueResponse.addAction(new ClientQueueAction());
        queueResponse.addPermission(new IsMember());
        queueResponse.addPermission(new OffCall());

        AiCommand mySettings = new AiCommand(ProxyBotConfig.Config.getString(ProxyBotConfig.MY_SETTINGS), 0);
        mySettings.addAction(new MySettingsAction());
        mySettings.addPermission(new IsMember());
        mySettings.addPermission(new OffCall());

        getActiveCommands().add(acceptNextHelpClient);
        getActiveCommands().add(acceptHelpClient);
        getActiveCommands().add(toggleHelp);
        getActiveCommands().add(toggleIdentity);
        getActiveCommands().add(addMember);
        getActiveCommands().add(joinChat);
        getActiveCommands().add(setTags);
        getActiveCommands().add(addTags);
        getActiveCommands().add(removeTags);
        getActiveCommands().add(onlineMembers);
        getActiveCommands().add(queueResponse);
        getActiveCommands().add(mySettings);
    }

    @Override
    public void listenOn(Chat chat) {
        super.listenOn(chat);

        if (chat != null) {

            User user = chat.getRemoteUsers().iterator().next();

            call.enter(DeskUserCache.getDeskUser(user.getId().toString()));

        }

    }

    @Override
    public void stopListening(Chat chat) {
        super.stopListening(chat);

        if (chat != null) {

            User user = chat.getRemoteUsers().iterator().next();

            call.exit(DeskUserCache.getDeskUser(user.getId().toString()));

        }

    }
}
