package org.symphonyoss.roomdesk.models.calls;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.ai.constants.MLTypes;
import org.symphonyoss.ai.utils.Messenger;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.model.Chat;
import org.symphonyoss.roomdesk.constants.HelpBotConstants;
import org.symphonyoss.roomdesk.listeners.chat.HelpClientListener;
import org.symphonyoss.roomdesk.listeners.chat.TranscriptListener;
import org.symphonyoss.roomdesk.listeners.command.HelpCallCommandListener;
import org.symphonyoss.roomdesk.listeners.command.MemberCommandListener;
import org.symphonyoss.roomdesk.models.HelpBotSession;
import org.symphonyoss.roomdesk.models.users.HelpClient;
import org.symphonyoss.roomdesk.models.users.Member;
import org.symphonyoss.roomdesk.utils.CallCache;

import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.pod.model.Stream;
import org.symphonyoss.symphony.clients.model.SymUser;
import org.symphonyoss.symphony.pod.model.UserIdList;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A model that represents a call between a member and a client.
 */
public class MultiChatHelpCall extends MultiChatCall {
    private final Logger logger = LoggerFactory.getLogger(MultiChatHelpCall.class);

    private SymphonyClient symClient;

    private HelpCallCommandListener helpCallCommandListener;

    private MemberCommandListener memberCommandListener;
    private HelpClientListener helpClientListener;
    private TranscriptListener transcriptListener;

    private Chat helpChat;

    private Member member;
    private HelpClient client;

    private Timer callTimer;

    public MultiChatHelpCall(Member member, HelpClient client, HelpBotSession session) {
        super();
        this.member = member;
        this.client = client;
        this.symClient = session.getSymphonyClient();

        this.memberCommandListener = session.getMemberListener();
        this.helpClientListener = session.getHelpClientListener();
        this.transcriptListener = session.getTranscriptListener();

        callTimer = new Timer();
    }

    /**
     * Starts the call.
     * Cross listeners.
     * Add new command listener.
     * Set on call.
     * Send initial web messages from the bot.
     */
    public void initiateCall() {
        if(client == null
                || member == null){

            if(logger != null)
                logger.warn("Cal started when member or client were null.");

            return;
        }

        callTimer.start();

        helpChat = new Chat();
        helpChat.setLocalUser(symClient.getLocalUser());

        client.setCall(this);
        client.setOnCall(true);

        member.setCall(this);
        member.setOnCall(true);

        Set<SymUser> users = new HashSet<SymUser>();

        try {

            users.add(symClient.getUsersClient().getUserFromId(client.getUserID()));
            users.add(symClient.getUsersClient().getUserFromId(member.getUserID()));

            helpChat.setRemoteUsers(users);
            helpChat.setStream(symClient.getStreamsClient().getStream(users));

            helpClientListener.stopListening(getUserChat(client.getUserID()));
            memberCommandListener.stopListening(getUserChat(member.getUserID()));

            helpCallCommandListener = new HelpCallCommandListener(symClient, this);
            helpCallCommandListener.listenOn(getUserChat(client.getUserID()));
            helpCallCommandListener.listenOn(getUserChat(member.getUserID()));

            helpChat.registerListener(transcriptListener);

            Messenger.sendMessage(HelpBotConstants.CONNECTED_TO_CALL, SymMessage.Format.TEXT,
                    helpChat, symClient);

            Messenger.sendMessage(getRoomInfo(), SymMessage.Format.MESSAGEML,
                    helpChat, symClient);

            Messenger.sendMessage(getHelpSummary(), SymMessage.Format.MESSAGEML,
                    helpChat, symClient);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * End the call.
     * Cross listeners back.
     * Notify that the call has ended.
     */
    public void endCall() {
        if(client == null
                || member == null
                || callTimer == null){

            if(logger != null)
                logger.warn("Cal ended when member or client were null.");

            return;
        }

        callTimer.stop();
        callTimer = null;

        helpCallCommandListener.stopListening(getUserChat(client.getUserID()));
        helpCallCommandListener.stopListening(getUserChat(member.getUserID()));

        helpClientListener.listenOn(getUserChat(client.getUserID()));

        memberCommandListener.listenOn(getUserChat(member.getUserID()));

        helpChat.removeListener(transcriptListener);

        Messenger.sendMessage(HelpBotConstants.EXIT_CALL, SymMessage.Format.TEXT,
                helpChat, symClient);

        client.setCall(null);
        client.setOnCall(false);
        client.setHelpRequests(new LinkedHashSet<String>());

        member.setCall(null);
        member.setOnCall(false);
    }

    public Chat getCallChat() {
        return helpChat;
    }

    public CallTypes getCallType() {
        return CallTypes.HELP_CALL;
    }

    /**
     * Parses a string to contain the a description of the rooms current
     * state,
     *
     * @return the room info
     */
    public String getRoomInfo() {
        String roomInfo = MLTypes.START_ML + "Room Info:" + MLTypes.BREAK + "   Client: ";

        if (client.getEmail() == "" || client.getEmail() == null) {
            roomInfo += client.getUserID().toString() + MLTypes.BREAK;
        } else {
            roomInfo += client.getEmail() + MLTypes.BREAK;
        }

        roomInfo += "   " + HelpBotConstants.MEMBER_LABEL + ": " + member.getEmail() + MLTypes.BREAK;

        return roomInfo + MLTypes.END_ML;
    }

    /**
     * Gets the help summary from the client.
     *
     * @return the help summary
     */
    public String getHelpSummary() {
        return MLTypes.START_ML + "Help Summary:" + MLTypes.BREAK + client.getHelpSummary() + MLTypes.END_ML;
    }

    /**
     * Get the one on one user web.
     *
     * @param userID the user's ID
     * @return the web the user belongs to
     */
    private Chat getUserChat(Long userID) {
        UserIdList list = new UserIdList();
        list.add(userID);
        Stream stream = null;
        try {
            stream = symClient.getStreamsClient().getStream(list);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return symClient.getChatService().getChatByStream(stream.getId());
    }

    @Override
    public String toString(){
        String text = "Call " + (CallCache.getCallID(this)+1) + ": [ Member: ";
            if(!member.isUseAlias())
                text += member.getEmail() + ", ";
            else
                text += member.getAlias() + ", ";

        text = text.substring(0, text.length() -2) + " | Client: ";
        text += client.getEmail() + ", ";

        return text.substring(0, text.length() - 2) + " ]";
    }


    public Timer getCallTimer() {
        return callTimer;
    }

    public void setCallTimer(Timer callTimer) {
        this.callTimer = callTimer;
    }
}
