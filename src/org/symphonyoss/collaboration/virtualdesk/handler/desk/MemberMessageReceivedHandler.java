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

package org.symphonyoss.collaboration.virtualdesk.handler.desk;

import org.apache.log4j.MDC;
import org.symphonyoss.collaboration.virtualdesk.command.InvalidCommand;
import org.symphonyoss.collaboration.virtualdesk.data.User;
import org.symphonyoss.collaboration.virtualdesk.handler.AbstractActionHandler;
import org.xmpp.packet.Message;
import org.symphonyoss.collaboration.virtualdesk.command.GetHistoryByMemberCommand;
import org.symphonyoss.collaboration.virtualdesk.command.ICommand;
import org.symphonyoss.collaboration.virtualdesk.command.TakeQuestionCommand;
import org.symphonyoss.collaboration.virtualdesk.command.ViewActiveConversationCommand;
import org.symphonyoss.collaboration.virtualdesk.command.ViewMemberAvailableCommand;
import org.symphonyoss.collaboration.virtualdesk.command.ViewPostedMessageInQueueCommand;
import org.symphonyoss.collaboration.virtualdesk.config.IServiceConfiguration;
import org.symphonyoss.collaboration.virtualdesk.muc.Desk;
import org.symphonyoss.collaboration.virtualdesk.muc.IDeskDirectory;
import org.symphonyoss.collaboration.virtualdesk.packet.MessageResponse;
import org.symphonyoss.collaboration.virtualdesk.search.HistorySearcherSingleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MemberMessageReceivedHandler extends AbstractActionHandler
{
	private static final Logger Logger = LoggerFactory.getLogger(MemberMessageReceivedHandler.class);
	
	private IServiceConfiguration serviceConfigurarion;
	
	private Message message;

	private Desk deskRoom;
	
	private IDeskDirectory deskDirectory;

	private ICommand command;

	public MemberMessageReceivedHandler(IServiceConfiguration serviceConfigurarion, Message message, Desk deskRoom, IDeskDirectory deskDirectory)
	{
		this.serviceConfigurarion = serviceConfigurarion;
		this.message = message;
		this.deskRoom = deskRoom;
		this.deskDirectory = deskDirectory;
	}

	@Override
	protected void internalHandle()
	{
		MDC.put("deskjid", message.getTo().toBareJID());
		
		String messageBody = message.getBody().trim();

		User senderUser = deskRoom.getOccupantByJID(message.getFrom());

		if (messageBody.startsWith("@"))
		{
			Logger.info("{} has sent the command to desk [{}]", message.getFrom(), deskRoom.getJID());

			ICommand getHistoryCommand = new GetHistoryByMemberCommand(serviceConfigurarion, HistorySearcherSingleton.getInstance());
			getHistoryCommand.setNext(new InvalidCommand());
			
			ICommand viewPostedQueueCommand = new ViewPostedMessageInQueueCommand();
			viewPostedQueueCommand.setNext(getHistoryCommand);
			
			ICommand viewActiveConversationCommand = new ViewActiveConversationCommand();
			viewActiveConversationCommand.setNext(viewPostedQueueCommand);
			
			ICommand viewMemberCommand = new ViewMemberAvailableCommand();
			viewMemberCommand.setNext(viewActiveConversationCommand);
			
			this.command = new TakeQuestionCommand();
			this.command.setNext(viewMemberCommand);
			
			packetList.add(MessageResponse.createMessageResponse(senderUser, senderUser, deskRoom.getJID(), message));
			packetList.addAll(command.process(messageBody, senderUser, deskRoom, deskDirectory));
		}
		else
		{
			Logger.info("{} has sent the message to desk [{}]", message.getFrom(), deskRoom.getJID());

			packetList.addAll(MessageResponse.createMessageResponse(senderUser, deskRoom.getCurrentMembers(), deskRoom.getJID(), message));
		}
	}
}