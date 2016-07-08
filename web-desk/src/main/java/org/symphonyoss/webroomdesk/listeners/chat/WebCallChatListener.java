package org.symphonyoss.webroomdesk.listeners.chat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.ai.listeners.AiCommandListener;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.services.ChatListener;
import org.symphonyoss.client.util.MlMessageParser;
import org.symphonyoss.symphony.agent.model.Message;
import org.symphonyoss.webroomdesk.models.users.Member;
import org.symphonyoss.webroomdesk.models.users.WebClient;
import org.symphonyoss.webservice.models.web.WebMessage;

/**
 * Created by nicktarsillo on 7/8/16.
 */
public class WebCallChatListener implements ChatListener {
    private final Logger logger = LoggerFactory.getLogger(WebCallChatListener.class);
    private Member member;
    private WebClient client;
    private SymphonyClient symClient;

    public WebCallChatListener(Member member, WebClient client, SymphonyClient symCLient){
        this.member = member;
        this.client =client;
        this.symClient = symCLient;
    }

    public void onChatMessage(Message message) {
        logger.info("New Message {}", message.getMessage());
        if (message == null
                || AiCommandListener.isCommand(message, symClient))
            return;

        MlMessageParser mlMessageParser;

        try {

            mlMessageParser = new MlMessageParser(symClient);
            mlMessageParser.parseMessage(message.getMessage());

        } catch (Exception e) {
            return;
        }

        if(!member.isUseAlias()) {
            client.getWebSession().sendMessageToWebService(
                    new WebMessage(System.currentTimeMillis(), "",
                            member.getEmail(), mlMessageParser.getText()));
        }else{
            client.getWebSession().sendMessageToWebService(
                    new WebMessage(System.currentTimeMillis(), member.getAlias(),
                            "",  mlMessageParser.getText()));
        }
    }
}