package Server.model;

import Common.Client;
import java.rmi.RemoteException;

public class LoggedInUser {

    private final String id;
    private final Client remoteNode;
    private final ConnectionManager participantMgr;
    private String username;

    public LoggedInUser(String id, String username, Client remoteNode, ConnectionManager mgr) {
        this.id = id;
        this.username = username;
        this.remoteNode = remoteNode;
        this.participantMgr = mgr;
    }

    public void send(String msg) throws RejectedException, RemoteException {
        remoteNode.recieveNotification(msg);
    }
}
