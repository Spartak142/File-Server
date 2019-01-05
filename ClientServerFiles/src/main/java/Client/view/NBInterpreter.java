package Client.view;

import java.util.List;
import java.util.Scanner;
import Common.FileServer;
import Common.AccountDTO;
import Common.Client;
import Common.File;
import Common.FileDTO;
import Server.model.AccountException;
import Server.model.FileException;
import Server.model.RejectedException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class NBInterpreter implements Runnable {

    private static final String PROMPT = "Type your command: ";
    private final Scanner console = new Scanner(System.in);
    private final StdOut outMgr = new StdOut();
    private FileServer fserver;
    private boolean rCmds = false;
    private String activeUser = null;
    private String[] commandList = new String[11];
    /*Keeps track if someone is logged in. The main reason why this check is done on the client side
    for most of the commands is to improve server performance, since it will not have to even deal with the requests
    from non-logged in users. This is important since server is not multithreaded and all the requesting lients are "queing" up for service.*/
    private boolean lIn = false;
    private String nBID;
    private final Client myRemoteObj;

    public NBInterpreter() throws RemoteException {
        myRemoteObj = new ClientOutput();
    }

    // Starts the interpreter
    public void start(FileServer fserver) {
        this.fserver = fserver;
        if (rCmds) {
            return;
        }
        rCmds = true;
        new Thread(this).start();
    }

    /**
     * Interprets and performs user commands, by calling appropriate commands
     * from server.Some of th used methods are defined below
     */
    @Override
    public void run() {
        AccountDTO acct = null;
        while (rCmds) {
            try {
                CmdLine cmdLine = new CmdLine(readNextLine());
                switch (cmdLine.getCmd()) {
                    case HELP:
                        defineCommandUsage();
                        break;
                    case QUIT:
                        activeUser = null;
                        rCmds = false;
                        break;
                    case REGISTER:
                        fserver.createAccount(cmdLine.getParameter(0), cmdLine.getParameter(1));
                        break;
                    case LOGIN:
                        login(cmdLine.getParameter(0), cmdLine.getParameter(1), acct);
                        break;
                    case LISTMY:
                        listMy();
                        break;
                    case LISTALL:
                        listAll();
                        break;
                    case UPLOAD:
                        upload(cmdLine.getParameter(0), cmdLine.getParameter(1), cmdLine.getParameter(2));
                        break;
                    case DOWNLOAD:
                        download(cmdLine.getParameter(0));
                        break;
                    case CHANGE:
                        change(cmdLine.getParameter(0), cmdLine.getParameter(1), cmdLine.getParameter(2));
                        break;
                    case DELETE:
                        delete(cmdLine.getParameter(0));
                        break;
                    case LOGOUT:
                        activeUser = null;
                        lIn = false;
                        break;
                    default:
                        outMgr.println("illegal command");
                }
            } catch (Exception e) {
                outMgr.println("Operation failed");
                outMgr.println(e.getMessage());
            }
        }
    }

    private String readNextLine() {
        outMgr.print(PROMPT);
        return console.nextLine();
    }

    private void login(String username, String password, AccountDTO acct) throws RemoteException, AccountException {
        if (lIn) {
            outMgr.println("Already logged in as " + activeUser);
            return;
        }
        nBID = fserver.login(myRemoteObj, username, password);
        if (nBID != null) {
            activeUser = username;
            lIn = true;
        }
    }

    private void listMy() throws RemoteException, FileException {
        if (lIn) {
            outMgr.println("Listing files for " + activeUser);
            List<? extends FileDTO> myFiles = fserver.listFiles(activeUser);
            for (FileDTO file : myFiles) {
                outMgr.println(file.getFileName() + ": " + file.getSize() + ": " + file.getPermission());
            }
        }
    }

    private void listAll() throws RemoteException, FileException {
        if (lIn) {
            // Tweak to get files metadata
            List<? extends FileDTO> files = fserver.listAll();
            for (FileDTO file : files) {
                outMgr.println(file.getFileName() + ": " + file.getOwner() + ": " + file.getSize() + ": " + file.getPermission());
            }
        } else {
            outMgr.println("You have to be logged in to view files");
        }
    }

    private void upload(String fileName, String size, String permission) throws RemoteException, FileException, RejectedException {
        if (lIn) {
            Boolean write = permission.equalsIgnoreCase("write");
            fserver.upload(new File(fileName, activeUser, Integer.parseInt(size), write));
        } else {
            outMgr.println("You have to be logged in to upload files.");
        }
    }

    private void download(String fileName) throws RemoteException, RejectedException, FileException {
        if (!lIn) {
            outMgr.println("You have to be logged in to delete files");
            return;
        }
        FileDTO fileToDownload = fserver.download(fileName, activeUser);
        if (fileToDownload != null) {
            outMgr.println("The file '" + fileToDownload.getFileName() + "' has been successfully downloaded.");
            outMgr.println("The file '" + fileToDownload.getFileName() + "' has the following attributes. " + fileToDownload.toString());
        }
    }

    private void change(String fileName, String size, String permission) throws RemoteException, FileException, RejectedException {
        if (!lIn) {
            outMgr.println("You have to be logged in to change files");
            return;
        }
        Boolean success = fserver.change(fileName, Integer.parseInt(size), permission.equalsIgnoreCase("write"), activeUser);
        if (success) {
            FileDTO changed = fserver.getFile(fileName);
            outMgr.println("The file has been successfully changed. Current state of the required file: " + changed.toString());
        }
    }

    private void delete(String fileName) throws RemoteException, FileException, RejectedException {
        if (!lIn) {
            outMgr.println("You have to be logged in to delete files");
            return;
        }
        Boolean success = fserver.delete(fileName, activeUser);
        if (success) {
            outMgr.println("The file '" + fileName + "' has been successfully deleted.");
        }
    }

    private void defineCommandUsage() {
        commandList[0] = new String(" Help- lists all the available commands in the program and the way they are supposed to be executed. Commands are not case sensitive." + "\n");
        commandList[1] = new String(" Register- type register, and submit your desired username and password. If the username is taken you will have to choose a new one." + "\n" + " Username and password are case sensitive. To execute: 'register user password'" + "\n");
        commandList[2] = new String(" Login- type login and submit your username and password sperated by a space to login onto the server. To execute 'login username password'" + "\n");
        commandList[3] = new String(" Upload- Uploads a file with a given name (provided that such file does not exist) and parameters(size and permission). " + "\n" + " If you would like to have the file available type 'write' in the permission field, else type anything. To execute: 'Upload myfile 100 write'" + "\n");
        commandList[4] = new String(" Download- downalods a file with the provided name, if you have the permission, of course. " + "\n" + " To execute: 'Download myfile' The owner of the file (unless it is you) will be notified." + "\n");
        commandList[5] = new String(" Change- changes files data if you have the permission, same as with upload, type filename new size and permission.  " + "\n" + " To execute: 'myfile, 100, write' The owner of the file (unless it is you will be notified." + "\n");
        commandList[6] = new String(" Listmy- lists all the files that you have uploaded. To execute: 'listmy'" + "\n");
        commandList[7] = new String(" Listall- lists all the files that are present in the catalog. To execute: 'listall'" + "\n");
        commandList[8] = new String(" Delete- deletes a file with the provided name, if you have the permission, of course.  " + "\n" + " To execute: (Delete myfile). The owner of the file (unless it is you will be notified." + "\n");
        commandList[9] = new String(" Logout- Logs you out of the system but does not close the program, enabling ghost mode.  " + "\n" + " You will receive notifications if anyone accesses your files until you log in as another user or quit." + "\n");
        commandList[10] = new String(" Quit- Closes the Program and logs you off." + "\n");
        for (int i = 0; i < 10; i++) {
            outMgr.println(commandList[i]);
        }
    }

    private class ClientOutput extends UnicastRemoteObject implements Client {

        public ClientOutput() throws RemoteException {
        }

        @Override
        public void recieveNotification(String notification) {
            outMgr.println((String) notification);
        }
    }

}
