package Common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import Server.model.AccountException;
import Server.model.FileException;
import Server.model.RejectedException;

public interface FileServer extends Remote {

    public static final String FILESERVER_NAME_IN_REGISTRY = "fserver";

    // Creates an account with the specified name and password. In the account table
    public void createAccount(String name, String password) throws RemoteException, AccountException;

    //Login Method Returns account if name and password are correct, otherwise, asks for a new attempt
    public String login(Client remoteNode, String name, String password) throws RemoteException, AccountException;

    // Uploads the files metadata including owner, size, name, write and read permissions
    public void upload(FileDTO file) throws RemoteException, RejectedException, FileException;
    //Lists all files owned by the logged in person.

    public List<? extends FileDTO> listFiles(String owner) throws RemoteException, FileException;

    // Lists all files in the catalog
    public List<? extends FileDTO> listAll() throws RemoteException, FileException;

    //downloads file if allowed
    public FileDTO download(String name, String activeUser) throws RemoteException, FileException;

    //Changes the metadata of a given file. Doing so notifies the file owner with a name of the changer and changes done.
    public Boolean change(String fileToChange, int size, Boolean permission, String user) throws RemoteException, RejectedException, FileException;

    // Gets file's metadate by it's name. (basically download without user check) Used only by the program.
    public FileDTO getFile(String name) throws RemoteException, FileException;
    // Deletes the file if allowed

    public Boolean delete(String fileName, String user) throws RemoteException, RejectedException, FileException;

}
