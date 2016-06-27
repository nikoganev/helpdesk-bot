package org.symphonyoss.helpdesk.models.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.ai.listeners.AiCommandListener;
import org.symphonyoss.ai.models.AiAction;
import org.symphonyoss.ai.models.AiCommand;
import org.symphonyoss.ai.models.AiResponse;
import org.symphonyoss.ai.models.AiResponseSequence;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.model.Chat;
import org.symphonyoss.client.util.MlMessageParser;
import org.symphonyoss.helpdesk.constants.HelpBotConstants;
import org.symphonyoss.helpdesk.listeners.chat.HelpClientListener;
import org.symphonyoss.helpdesk.listeners.command.MemberCommandListener;
import org.symphonyoss.helpdesk.models.HelpBotSession;
import org.symphonyoss.helpdesk.models.users.Member;
import org.symphonyoss.helpdesk.utils.ClientCache;
import org.symphonyoss.helpdesk.utils.MemberCache;
import org.symphonyoss.symphony.agent.model.Message;
import org.symphonyoss.symphony.agent.model.MessageSubmission;
import org.symphonyoss.symphony.pod.model.User;
import org.symphonyoss.symphony.pod.model.UserIdList;

/**
 * Created by nicktarsillo on 6/15/16.
 * An AiAction that allows a member to add a member to the member cache.
 */
public class AddMemberAction implements AiAction {
    private final Logger logger = LoggerFactory.getLogger(AddMemberAction.class);

    private HelpClientListener helpClientListener;
    private MemberCommandListener memberCommandListener;
    private SymphonyClient symClient;

    public AddMemberAction(HelpBotSession helpBotSession) {
        this.helpClientListener = helpBotSession.getHelpClientListener();
        this.memberCommandListener = helpBotSession.getMemberListener();
        this.symClient = helpBotSession.getSymphonyClient();
    }

    /**
     * Promotes a client to member, as commanded from another member.
     * Parse message for email.
     * Find client by email.
     * Create new member, remove old client.
     * Add to cache, write to file.
     *
     * @param mlMessageParser   the parser contains the input in ML
     * @param message   the received message
     * @param command   the command that triggered this action
     * @return   the sequence of responses generated from this action
     */
    public AiResponseSequence respond(MlMessageParser mlMessageParser, Message message, AiCommand command) {
        AiResponseSequence responseList = new AiResponseSequence();
        UserIdList userIdList = new UserIdList();

        String[] chunks = mlMessageParser.getTextChunks();
        String email = String.join(" ", chunks);
        email = email.substring(email.indexOf(command.getPrefixRequirement(0)) + 1);

        try{
            User user = symClient.getUsersClient().getUserFromEmail(email);

            if (user != null
                    && user.getId() != null
                    && !MemberCache.hasMember(user.getId().toString())) {

                if (ClientCache.hasClient(user.getId()))
                    ClientCache.removeClient(user);

                Member member = new Member(email,
                        user.getId());
                MemberCache.addMember(member);

                userIdList.add(message.getFromUserId());
                responseList.addResponse(new AiResponse(HelpBotConstants.PROMOTED_USER + email
                        + HelpBotConstants.TO_MEMBER, MessageSubmission.FormatEnum.TEXT, userIdList));

                userIdList = new UserIdList();
                userIdList.add(user.getId());
                responseList.addResponse(new AiResponse(HelpBotConstants.PROMOTED,
                        MessageSubmission.FormatEnum.TEXT, userIdList));

                Chat chat = symClient.getChatService().getChatByStream(
                        symClient.getStreamsClient().getStream(userIdList).getId());

                if(chat != null) {
                    helpClientListener.stopListening(chat);
                    memberCommandListener.listenOn(chat);
                }
            } else {

                userIdList.add(message.getFromUserId());
                responseList.addResponse(new AiResponse(HelpBotConstants.PROMOTION_FAILED, MessageSubmission.FormatEnum.TEXT,
                        userIdList));

            }


        }catch(Exception e){
            logger.error("An error occurred when finding an email.", e);
        }

        return responseList;
    }



}
