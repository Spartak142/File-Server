package Server.model;

import Common.AccountDTO;
import Server.integration.FileServerDAO;

public class Account implements AccountDTO {

    private String username;
    private String password;
    private transient FileServerDAO fileServerDB;

    public Account(String name, String password, FileServerDAO fsd) {
        this.fileServerDB = fsd;
        this.password = password;
        this.username = name;

    }

    // Creates an account for the specified holder with the specified password.
    
    public Account(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public String getUserName() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    private String accountInfo() {
        return " " + this;
    }
}
