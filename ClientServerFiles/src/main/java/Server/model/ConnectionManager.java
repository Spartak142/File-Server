package Server.model;

import Common.Client;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ConnectionManager {

    private final Map<String, LoggedInUser> participants = Collections.synchronizedMap(new HashMap<>());

    public String createParticipant(Client remoteNode, String credentials) {
        String participantId = credentials;
        LoggedInUser newParticipant = new LoggedInUser(participantId, credentials, remoteNode, this);
        participants.put(participantId, newParticipant);
        return participantId;
    }

    public void sendMsg(String id, String message) throws RejectedException, RemoteException {
        LoggedInUser participant = participants.get(id);
        participant.send(message);
    }

    public Boolean isOnline(String id) {
        return participants.containsKey(id);
    }
}
