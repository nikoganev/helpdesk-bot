/*
 * Licensed to the Apache Software Foundation (ASF) under one
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

package org.symphonyoss.symphony.services;

import org.symphonyoss.symphony.SymphonyClient;
import org.symphonyoss.symphony.service.model.PresenceList;
import org.symphonyoss.symphony.service.model.UserPresence;

import java.util.ArrayList;

/**
 * Created by Frank Tarsillo on 5/15/2016.
 */
public class PresenceService implements PresenceListener {


    SymphonyClient symphonyClient;

    PresenceList presenceList;
    PresenceWorker presenceWorker;
    ArrayList<PresenceListener> presenceListeners;

    public PresenceService(SymphonyClient symphonyClient) {

        this.symphonyClient = symphonyClient;
        presenceListeners = new ArrayList<PresenceListener>();

        try {
            presenceList = getAllUserPresence();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public PresenceList getAllUserPresence() throws Exception {


        return symphonyClient.getServiceClient().getAllUserPresence();

    }

    public void registerPresenceListener(PresenceListener presenceListener) {

        if(presenceWorker == null){
            new Thread(new PresenceWorker(symphonyClient,this, presenceList)).start();
        }

        presenceListeners.add(presenceListener);

    }

    public void removePresenceListener(PresenceListener presenceListener) {

        presenceListeners.remove(presenceListener);

    }


    public void onUserPresence(UserPresence userPresence) {

        for(PresenceListener listener: presenceListeners){
            listener.onUserPresence(userPresence);

        }



    }


}
