package org.symphonyoss.helpdesk.models.responses;

import org.symphonyoss.client.util.MlMessageParser;
import org.symphonyoss.helpdesk.models.BotResponse;

/**
 * Created by nicktarsillo on 6/15/16.
 */
public class LastBotResponse {
    private MlMessageParser mlMessageParser;
    private BotResponse botResponse;

    public LastBotResponse(MlMessageParser mlMessageParser, BotResponse botResponse) {
        this.botResponse = botResponse;
        this.mlMessageParser = mlMessageParser;
    }

    public MlMessageParser getMlMessageParser() {
        return mlMessageParser;
    }

    public void setMlMessageParser(MlMessageParser mlMessageParser) {
        this.mlMessageParser = mlMessageParser;
    }

    public BotResponse getBotResponse() {
        return botResponse;
    }

    public void setBotResponse(BotResponse botResponse) {
        this.botResponse = botResponse;
    }
}
