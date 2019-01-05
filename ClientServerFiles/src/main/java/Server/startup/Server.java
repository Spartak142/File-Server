package Server.startup;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import Common.FileServer;
import Server.controller.Controller;
import Server.integration.FileServerDBException;

//Starts the File server
public class Server {

    private static final String USAGE = "java fserverjdbc.Server [fserver name in rmi registry] "
            + "[fileserver database name] [dbms: derby or mysql]";
    private String serverName = FileServer.FILESERVER_NAME_IN_REGISTRY;
    private String datasource = "Fservertrial";
    private String dbms = "derby";

    public static void main(String[] args) {

        try {
            Server server = new Server();
            server.parseCommandLineArgs(args);
            server.startRMIServant();
            System.out.println("File Server started.");
        } catch (RemoteException | MalformedURLException | FileServerDBException e) {
            System.out.println("Failed to start File Server.");
        }
    }

    private void startRMIServant() throws RemoteException, MalformedURLException, FileServerDBException {
        try {
            LocateRegistry.getRegistry().list();
        } catch (RemoteException noRegistryRunning) {
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        }
        Controller contr = new Controller(datasource, dbms);
        Naming.rebind(serverName, contr);
    }

    private void parseCommandLineArgs(String[] args) {
        if (args.length > 3 || (args.length > 0 && args[0].equalsIgnoreCase("-h"))) {
            System.out.println(USAGE);
            System.exit(1);
        }

        if (args.length > 0) {
            serverName = args[0];
        }

        if (args.length > 1) {
            datasource = args[1];
        }

        if (args.length > 2) {
            dbms = args[2];
        }
    }
}
