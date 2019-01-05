package Server.controller;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import Common.*;
import Server.model.*;
import Server.integration.*;

// Implementations of the server's remote methods.
public class Controller extends UnicastRemoteObject implements FileServer {

    private final FileServerDAO fserverDB;
    private final ConnectionManager connectedClients = new ConnectionManager();

    public Controller(String datasource, String dbms) throws RemoteException, FileServerDBException {

        super();
        System.out.println("Successfully called super");
        fserverDB = new FileServerDAO(dbms, datasource);
        System.out.println("successfully created FSDAO");
    }

    @Override
    public synchronized void createAccount(String holderName, String password) throws AccountException {
        String acctExistsMsg = "Account for: " + holderName + " already exists";
        String failureMsg = "Could not create account for: " + holderName;
        try {
            if (fserverDB.findAccountByName(holderName) != null) {
                throw new AccountException(acctExistsMsg);
            }
            fserverDB.createAccount(new Account(holderName, password));
        } catch (Exception e) {
            throw new AccountException(failureMsg, e);
        }
    }

    @Override
    public synchronized String login(Client remoteNode, String holderName, String password) throws AccountException {
        String errMsg = "Could not search for account.";
        if (holderName == null) {
            return null;
        }
        try {
            String correctPassword = fserverDB.findAccountByName(holderName).getPassword();
            if (correctPassword.equals(password)) {
                String participantId = connectedClients.createParticipant(remoteNode, holderName);
                return participantId;
            } else {
                errMsg = ("Incorrect username or password");
                throw new AccountException(errMsg);
            }
        } catch (Exception e) {
            throw new AccountException(errMsg, e);
        }
    }

    @Override
    public synchronized List<? extends FileDTO> listFiles(String owner) throws RemoteException, FileException {
        try {
            return fserverDB.findFileByOwnerName(owner);
        } catch (Exception e) {
            throw new FileException("Unable to list accounts.", e);
        }
    }

    @Override
    public synchronized List<? extends FileDTO> listAll() throws FileException {
        try {
            return fserverDB.findAllFiles();
        } catch (Exception e) {
            throw new FileException("Unable to list accounts.", e);
        }
    }

    @Override
    public synchronized void upload(FileDTO file) throws RemoteException, RejectedException, FileException {
        String fileName = file.getFileName();
        String failureMsg = "Could not upload the following file: " + fileName;
        try {
            if (fserverDB.findFileByName(fileName) != null) {
                failureMsg = "File with the name: '" + fileName + "' already exists";
                throw new FileException(failureMsg);
            }
            fserverDB.uploadFile(file);
        } catch (Exception e) {
            throw new FileException(failureMsg, e);
        }
    }

    @Override
    public synchronized FileDTO getFile(String name) throws RemoteException, FileException {
        String errMsg = "Could not search for the file.";
        try {
            return fserverDB.findFileByName(name);
        } catch (Exception e) {
            throw new FileException(errMsg, e);
        }
    }

    @Override
    public synchronized FileDTO download(String fileName, String user) throws FileException {
        String errMsg = "Could not search for the file.";
        if (fileName == null) {
            return null;
        }
        try {
            FileDTO file = fserverDB.findFileByName(fileName);
            if (checkPermission(file, user)) {
                if (!user.equals(file.getOwner())) {
                    if (connectedClients.isOnline(file.getOwner())) {
                        connectedClients.sendMsg(file.getOwner(), (String) ("Your file has been downloaded by " + user));
                    }
                }
                return file;
            } else {
                if (connectedClients.isOnline(file.getOwner())) {
                    connectedClients.sendMsg(file.getOwner(), (String) (user + " has tried to download your file. Don't worry your file is well protected!"));
                }
                errMsg = "The file is protected by the owner, thus you cannot download it.";
                throw new FileException(errMsg);
            }
        } catch (Exception e) {
            throw new FileException(errMsg, e);
        }
    }

    @Override
    public synchronized Boolean change(String file, int size, Boolean permission, String user) throws RemoteException, RejectedException, FileException {
        String failureMsg = "Could not change the following file: " + file;
        Boolean changed = false;

        try {
            FileDTO fileTC = fserverDB.findFileByName(file);
            if (checkPermission(fileTC, user)) {
                Boolean sameUser = (user.equals(fileTC.getOwner()));
                if (size > 0) {
                    fserverDB.changeFileSize(file, size);
                    changed = true;
                }
                if (permission != null) {
                    fserverDB.changeFileSize(file, size);
                    changed = true;
                }
                if (!sameUser) {
                    fileTC = fserverDB.findFileByName(file);
                    if (connectedClients.isOnline(fileTC.getOwner())) {
                        connectedClients.sendMsg(fileTC.getOwner(), (String) ("Your file has been been changed by " + user + ". The updated file looks like this: " + fileTC.toString()));
                    }
                }
            } else {
                if (connectedClients.isOnline(fileTC.getOwner())) {
                    connectedClients.sendMsg(fileTC.getOwner(), (String) (user + " has tried to change your file. Don't worry your file is well protected!"));
                }
                failureMsg = "The file is protected by the owner, thus you cannot change it.";
                throw new FileException(failureMsg);
            }
        } catch (Exception e) {
            throw new FileException(failureMsg, e);
        }
        return changed;
    }

    @Override
    public synchronized Boolean delete(String file, String user) throws RemoteException, RejectedException, FileException {
        Boolean deleted = false;
        String failureMsg = "Could not delete the following file: " + file;
        try {
            FileDTO fileToDelete = fserverDB.findFileByName(file);
            if (checkPermission(fileToDelete, user)) {
                fserverDB.deleteFile(fileToDelete);
                if (connectedClients.isOnline(fileToDelete.getOwner())) {
                    connectedClients.sendMsg(fileToDelete.getOwner(), (String) (user + " has deleted your file: " + fileToDelete.getFileName()));
                }
                deleted = true;
            } else {
                if (connectedClients.isOnline(fileToDelete.getOwner())) {
                    connectedClients.sendMsg(fileToDelete.getOwner(), (String) (user + " has tried to delee your file. Don't worry your file is well protected!"));
                }
                failureMsg = "The file is protected by the owner, thus you cannot delete it.";
                throw new FileException(failureMsg);
            }
        } catch (Exception e) {
            throw new FileException(failureMsg, e);
        }
        return deleted;
    }

    private synchronized Boolean checkPermission(FileDTO fileToCheck, String user) {
        Boolean write = true;
        if (fileToCheck.getPermission() == false) {
            if (!fileToCheck.getOwner().equals(user)) {
                write = false;
            }
        }
        return write;
    }
}
