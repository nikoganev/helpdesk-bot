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

package org.symphonyoss.proxydesk.utils;

/**
 * Created by nicktarsillo on 6/28/16.
 */
public class Statistics {
    public static int getNumClients() {
        return ClientCache.ALL_CLIENTS.size();
    }

    public static int getNumMembers() {
        return MemberCache.MEMBERS.size();
    }

    public static int getNumOnlineMembers() {
        return MemberCache.getOnlineMembers().size();
    }

    public static int getNumHolds() {
        return HoldCache.ONHOLD.size();
    }

    public static int getNumDeskUsers() {
        return DeskUserCache.ALL_USERS.size();
    }

    public static int getNumCalls() {
        return CallCache.ACTIVECALLS.size();
    }

    public static int getHelpTranscriptMessagesNum() {
        return ClientCache.getTotalHelpMessages();
    }

    public static double getMeanCallTime(){return CallCache.getMeanCallTime();}

    public static double getMaxCallTime(){return CallCache.maxCallTime();}

    public static double getMaxHoldTime(){return HoldCache.getMaxHoldTime();}

    public static double getMeanHoldTime(){return HoldCache.getMeanHoldTime();}

}
