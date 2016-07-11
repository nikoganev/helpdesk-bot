package org.symphonyoss.roomdesk.models.calls;

import junit.framework.TestCase;
import org.mockito.Mockito;
import org.symphonyoss.roomdesk.listeners.chat.TranscriptListener;

import static org.mockito.Mockito.mock;

/**
 * Created by nicktarsillo on 7/11/16.
 */
public class MultiChatHelpCallTest extends TestCase {
    static MultiChatHelpCall multiChatHelpCall = mock(MultiChatHelpCall.class);


    public void testInitiateCall() throws Exception {
        Mockito.doCallRealMethod().when(multiChatHelpCall).initiateCall();

        try {
            multiChatHelpCall.initiateCall();
        }catch(Exception e){
            fail("Initiate call failed.");
        }

    }

    public void testEndCall() throws Exception {
        Mockito.doCallRealMethod().when(multiChatHelpCall).endCall();

        try {
            multiChatHelpCall.endCall();
        }catch(Exception e){
            fail("Initiate call failed.");
        }
    }
}