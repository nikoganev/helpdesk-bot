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

package org.symphonyoss.roomdesk.utils;

import org.junit.Test;
import org.symphonyoss.proxydesk.utils.ClientCache;

import static org.junit.Assert.fail;

/**
 * Created by nicktarsillo on 6/23/16.
 */
public class ClientCacheTest {

    @Test
    public void testAddClient() throws Exception {
        try {
            ClientCache.addClient(null);
        } catch (Exception e) {
            fail("New call test failed");
        }
    }

    @Test
    public void testHasClient() throws Exception {
        try {
            ClientCache.hasClient(null);
        } catch (Exception e) {
            fail("New call test failed");
        }
    }
}