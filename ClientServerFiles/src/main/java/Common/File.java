package Common;

import Server.integration.FileServerDAO;

public class File implements FileDTO {

    private String fileName;
    private String owner;
    private Boolean write;
    private int size;
    private transient FileServerDAO fileServerDB;

    public File(String name, String owner, int size, Boolean write, FileServerDAO fsd) {
        this.fileServerDB = fsd;
        this.fileName = name;
        this.owner = owner;
        this.size = size;
        this.write = write;
    }

    public File(String name, String owner, int size, Boolean write) {
        this.fileName = name;
        this.owner = owner;
        this.size = size;
        this.write = write;
    }

// size of 512 by default, readonly;
    public File(String name, String owner) {
        this.fileName = name;
        this.owner = owner;
        this.size = 512;
        this.write = false;

    }

    public File(String name, String owner, int size) {
        this.fileName = name;
        this.owner = owner;
        this.size = size;
        this.write = false;

    }

    public File(String name, String owner, Boolean write) {
        this.fileName = name;
        this.owner = owner;
        this.size = 512;
        this.write = write;

    }

    public File(String name, FileServerDAO fileDB) {
        this.fileName = name;
        this.fileServerDB = fileDB;

    }

    private String accountInfo() {
        return " " + this;
    }

    public String toString() {
        StringBuilder stringRepresentation = new StringBuilder();
        stringRepresentation.append("Filename: '");
        stringRepresentation.append(fileName);
        stringRepresentation.append("', Owner: ");
        stringRepresentation.append(owner);
        stringRepresentation.append("Size: ");
        stringRepresentation.append(size);
        stringRepresentation.append(", Permission to all users: ");
        if(write)
          stringRepresentation.append("write.");          
        else
        stringRepresentation.append("read-only.]");
        return stringRepresentation.toString();
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public String getOwner() {
        return owner;
    }

    @Override
    public Boolean getPermission() {
        return write;
    }

    @Override
    public int getSize() {
        return size;
    }
}
