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

package org.symphonyoss.proxydesk.models.actions;

import org.symphonyoss.ai.models.AiAction;
import org.symphonyoss.ai.models.AiCommand;
import org.symphonyoss.ai.models.AiResponse;
import org.symphonyoss.ai.models.AiResponseSequence;
import org.symphonyoss.client.util.MlMessageParser;
import org.symphonyoss.proxydesk.models.users.Member;
import org.symphonyoss.proxydesk.utils.MemberCache;
import org.symphonyoss.symphony.clients.model.SymMessage;

import org.symphonyoss.symphony.pod.model.UserIdList;

import java.util.LinkedHashSet;

/**
 * Created by nicktarsillo on 6/24/16.
 */
public class SetTagsAction implements AiAction {
    /**
     * Sets a members tags, as specified by the member.
     * Get the member.
     * Split the string into a list of tags.
     * Delete old tags.
     * Set new tags.
     *
     * @param mlMessageParser the parser containing the message in ML
     * @param message         the message received
     * @param command         the command that called this action
     * @return success message
     */
    public AiResponseSequence respond(MlMessageParser mlMessageParser, SymMessage message, AiCommand command) {
        AiResponseSequence aiResponseSequence = new AiResponseSequence();
        UserIdList userIdList = new UserIdList();
        userIdList.add(message.getFromUserId());

        Member member = MemberCache.getMember(message);

        String text = mlMessageParser.getText();
        String[] chunks = text.substring(command.getCommand().length()).replace(",", "").trim().split("\\s+");

        member.setTags(new LinkedHashSet<String>());
        for (String tag : chunks) {
            member.getTags().add(tag);
        }

        MemberCache.writeMember(member);

        aiResponseSequence.addResponse(new AiResponse("Tags have been successfully set.",
                SymMessage.Format.TEXT, userIdList));

        return aiResponseSequence;
    }
}
