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

import java.util.Collection;
import org.junit.Before;
import org.junit.Test;
import org.symphonyoss.collaboration.virtualdesk.data.PresenceType;
import org.symphonyoss.collaboration.virtualdesk.data.User;
import org.symphonyoss.collaboration.virtualdesk.muc.Desk;
import org.symphonyoss.collaboration.virtualdesk.utils.JIDUtils;
import org.xmpp.packet.JID;
import org.xmpp.packet.Presence;
import org.symphonyoss.collaboration.virtualdesk.packet.PresenceResponse;
import mockit.Expectations;
import mockit.Mocked;
import mockit.NonStrict;

public class OccupantLeaveHandlerTest
{
	private OccupantLeaveHandler occupantLeaveHandler;
	
	private Presence presence;
	
	private @Mocked @NonStrict
	Desk deskRoom;



	@Before
	public void before()
	{
		presence = new Presence();
		
		presence.setFrom(JIDUtils.getUserJID("user1"));
		presence.setTo(JIDUtils.getNicknameJID("desk1", "abc"));

		occupantLeaveHandler = new OccupantLeaveHandler(presence, deskRoom);
	}
	
	@SuppressWarnings ("unchecked")
	@Test
	public void handle_SenderIsNotInDeskAndThereAreOccupantsLeftInDesk_ResponseNothing()
	{
		new Expectations()
		{
			@Mocked @NonStrict PresenceResponse presenceResponse;
			{
				deskRoom.getOccupantByJID((JID)any); result = null;
				
				deskRoom.getCurrentMemberCount(); result = 4;
				
				PresenceResponse.createLeaveResponse((User)any, (User)any, (JID)any); times = 0;
				
				PresenceResponse.createPresenceUpdate((User)any, (Collection<User>)any, (JID)any, (PresenceType)any); times = 0;
			}
		};
		
		occupantLeaveHandler.handle();
	}
	
	@SuppressWarnings ("unchecked")
	@Test
	public void handle_SenderIsNotInDeskAndThereIsNoOccupantLeftInDesk_ResponseVirtualDeskUserPresenceUpdate()
	{
		new Expectations()
		{
			@Mocked @NonStrict PresenceResponse presenceResponse;
			{
				deskRoom.getOccupantByJID((JID)any); result = null;
				
				deskRoom.getCurrentMemberCount(); result = 0;
				
				PresenceResponse.createLeaveResponse((User)any, (User)any, (JID)any); times = 0;
				
				PresenceResponse.createPresenceUpdate((User)any, (Collection<User>)any, (JID)any, (PresenceType)any); times = 1;
			}
		};
		
		occupantLeaveHandler.handle();
	}
	
	@SuppressWarnings ("unchecked")
	@Test
	public void handle_SenderIsInDeskAndThereIsNoOccupantLeftInDesk_ResponseLeaveAndVirtualDeskUserPresenceUpdate()
	{
		new Expectations()
		{
			@Mocked @NonStrict PresenceResponse presenceResponse;
			{
				User senderUser = new User(JIDUtils.getUserJID("user1"), "user1");
				
				deskRoom.getOccupantByJID((JID)any); result = senderUser;
				
				deskRoom.getCurrentMemberCount(); result = 0;
				
				PresenceResponse.createLeaveResponse((User)any, (User)any, (JID)any); times = 1;
				PresenceResponse.createLeaveResponse((User)any, (Collection<User>)any, (JID)any); times = 1;
				
				PresenceResponse.createPresenceUpdate((User)any, (Collection<User>)any, (JID)any, (PresenceType)any); times = 1;
			}
		};
		
		occupantLeaveHandler.handle();
	}
	
	@SuppressWarnings ("unchecked")
	@Test
	public void handle_SenderIsInDeskAndThereAreOccupantsLeftInDesk_ResponseLeavePresence()
	{
		new Expectations()
		{
			@Mocked @NonStrict PresenceResponse presenceResponse;
			{
				User senderUser = new User(JIDUtils.getUserJID("user1"), "user1");
				
				deskRoom.getOccupantByJID((JID)any); result = senderUser;
				
				deskRoom.getCurrentMemberCount(); result = 4;
				
				PresenceResponse.createLeaveResponse((User)any, (User)any, (JID)any); times = 1;
				PresenceResponse.createLeaveResponse((User)any, (Collection<User>)any, (JID)any); times = 1;
				
				PresenceResponse.createPresenceUpdate((User)any, (Collection<User>)any, (JID)any, (PresenceType)any); times = 0;
			}
		};
		
		occupantLeaveHandler.handle();
	}
}