/*
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
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.symphonyoss.collaboration.virtualdesk.command;

import org.symphonyoss.collaboration.virtualdesk.data.User;
import org.symphonyoss.collaboration.virtualdesk.muc.Desk;
import org.symphonyoss.collaboration.virtualdesk.muc.IDeskDirectory;
import org.symphonyoss.collaboration.virtualdesk.packet.MessageResponse;

public class InvalidCommand extends AbstractCommand
{
	@Override
	protected boolean canProcess(String command, User senderUser, Desk deskRoom)
	{
		return true;
	}

	@Override
	protected void internalProcess(String command, User senderUser, Desk deskRoom, IDeskDirectory deskDirectory)
	{
		String invalidCommandMessage = "Invalid command.";
		packetList.add(MessageResponse.createDeskMessageResponse(invalidCommandMessage, senderUser, deskRoom.getJID()));
	}
}