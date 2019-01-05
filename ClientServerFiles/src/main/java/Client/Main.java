package Client;

import Client.view.NBInterpreter;
import Common.FileServer;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Main {
//starts client and invokes the Interpreter which handles the user commands

    public static void main(String[] args) {
        try {
            FileServer fserver = (FileServer) Naming.lookup(FileServer.FILESERVER_NAME_IN_REGISTRY);
            System.out.println("Welcome to the file server program. Here you way store as many files as you wish!");
            System.out.println("In order to be able to do anything you should register and login.");
            System.out.println("Type 'Help' to see all possible commands");
            new NBInterpreter().start(fserver);
        } catch (NotBoundException | MalformedURLException | RemoteException ex) {
            System.out.println("Could not start file client.");
        }
    }
}
