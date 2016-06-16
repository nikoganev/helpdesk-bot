package org.symphonyoss.helpdesk.utils;

import org.symphonyoss.helpdesk.models.users.HelpClient;
import org.symphonyoss.symphony.agent.model.Message;
import org.symphonyoss.symphony.pod.model.User;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by nicktarsillo on 6/16/16.
 */
public class ClientDatabase {
    public static final ConcurrentHashMap<String, HelpClient> ALLCLIENTS = new ConcurrentHashMap<String, HelpClient>();

    public static HelpClient addClient(User user) {
        HelpClient helpClient = new HelpClient(user.getEmailAddress(), user.getId());
        ALLCLIENTS.put(user.getId().toString(),
                helpClient);
        return helpClient;
    }

    public static HelpClient retrieveClient(Message message) {
        return ALLCLIENTS.get(message.getFromUserId().toString());
    }

    public static HelpClient removeClient(User user) {
        return ALLCLIENTS.remove(user.getId());
    }

    public static HelpClient retrieveClient(User user) {
        return ALLCLIENTS.get(user.getId());
    }
}
