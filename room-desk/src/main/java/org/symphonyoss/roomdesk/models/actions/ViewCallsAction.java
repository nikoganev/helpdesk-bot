package org.symphonyoss.roomdesk.models.actions;

import org.symphonyoss.ai.constants.MLTypes;
import org.symphonyoss.ai.models.AiAction;
import org.symphonyoss.ai.models.AiCommand;
import org.symphonyoss.ai.models.AiResponse;
import org.symphonyoss.ai.models.AiResponseSequence;
import org.symphonyoss.client.util.MlMessageParser;
import org.symphonyoss.roomdesk.constants.HelpBotConstants;
import org.symphonyoss.roomdesk.utils.CallCache;
import org.symphonyoss.symphony.clients.model.SymMessage;

import org.symphonyoss.symphony.pod.model.UserIdList;

/**
 * Created by nicktarsillo on 7/14/16.
 */
public class ViewCallsAction implements AiAction{

    public AiResponseSequence respond(MlMessageParser mlMessageParser, SymMessage message, AiCommand command) {
        AiResponseSequence aiResponseSequence = new AiResponseSequence();
        UserIdList userIdList = new UserIdList();
        userIdList.add(message.getFromUserId());

        aiResponseSequence.addResponse(new AiResponse(MLTypes.START_ML.toString() + HelpBotConstants.CALL_CACHE_LABEL
                + CallCache.listCache() + MLTypes.END_ML, SymMessage.Format.MESSAGEML,
                userIdList));

        return aiResponseSequence;
    }

}
