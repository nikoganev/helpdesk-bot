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

package org.symphonyoss.roomdesk.models.actions;

import org.symphonyoss.ai.models.AiAction;
import org.symphonyoss.ai.models.AiCommand;
import org.symphonyoss.ai.models.AiResponse;
import org.symphonyoss.ai.models.AiResponseSequence;
import org.symphonyoss.client.util.MlMessageParser;
import org.symphonyoss.roomdesk.models.calls.MultiChatHelpCall;
import org.symphonyoss.roomdesk.models.users.DeskUser;
import org.symphonyoss.roomdesk.utils.DeskUserCache;
import org.symphonyoss.symphony.clients.model.SymMessage;

import org.symphonyoss.symphony.pod.model.UserIdList;

/**
 * Created by nicktarsillo on 6/17/16.
 * An AiAction that provides a member or client with information about the room.
 */
public class RoomInfoAction implements AiAction {

    /**
     * Send back a message containing all the information about the room.
     * Includes all clients in room.
     * Includes all members in room.
     * Retain member identity preference.
     *
     * @param mlMessageParser the parser contains the input in ML
     * @param message         the received message
     * @param command         the command that triggered this action
     * @return the sequence of responses generated from this action
     */
    public AiResponseSequence respond(MlMessageParser mlMessageParser, SymMessage message, AiCommand command) {
        AiResponseSequence aiResponseSequence = new AiResponseSequence();
        UserIdList userIdList = new UserIdList();
        userIdList.add(message.getFromUserId());

        DeskUser deskUser = DeskUserCache.getDeskUser(message.getFromUserId().toString());
        if (deskUser != null) {

            aiResponseSequence.addResponse(new AiResponse(((MultiChatHelpCall) deskUser.getCall()).getRoomInfo(),
                    SymMessage.Format.MESSAGEML, userIdList));

        } else {

            aiResponseSequence.addResponse(new AiResponse("ERROR: DESK USER NOT FOUND. PLEASE CONTACT AND ADMIN.",
                    SymMessage.Format.TEXT, userIdList));

        }
        return aiResponseSequence;
    }
}
