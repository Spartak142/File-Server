package Server.integration;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import Server.model.Account;
import Common.AccountDTO;
import Common.FileDTO;
import Common.File;

// This data access object encapsulates all database calls in the FileServer application.
public class FileServerDAO {

    private static final String TABLE_NAME = "ACCOUNTS";
    private static final String PASSWORD_COLUMN_NAME = "PASSWORD";
    private static final String HOLDER_COLUMN_NAME = "NAME";

    private static final String TABLE_NAME1 = "FILES";
    private static final String FILENAME_COLUMN_NAME = "FILENAME";
    private static final String OWNER_COLUMN_NAME = "OWNER";
    private static final String SIZE_COLUMN_NAME = "FILESIZE";
    private static final String READORWRITE_COLUMN_NAME = "READONLY";

    private PreparedStatement createAccountStmt;
    private PreparedStatement findAccountStmt;

    private PreparedStatement createFileStmt;
    private PreparedStatement findFileStmt;
    private PreparedStatement findAllFilesStmt;
    private PreparedStatement findAllMyFilesStmt;
    private PreparedStatement changeFileStatement;
    private PreparedStatement changeFileStatement1;
    private PreparedStatement deleteFileStmt;

//Constructs a new DAO object connected to the specified database.
    public FileServerDAO(String dbms, String datasource) throws FileServerDBException {
        try {
            Connection connection = createDatasource(dbms, datasource);
            System.out.println("Before prepare statements");
            prepareStatements(connection);
            System.out.println("After prepare statements");
        } catch (ClassNotFoundException | SQLException exception) {
            throw new FileServerDBException("Could not connect to datasource.", exception);
        }
    }

    // Creates a new account.
    public void createAccount(AccountDTO account) throws FileServerDBException, SQLException {
        String failureMsg = "Could not create the account: " + account;
        try {
            createAccountStmt.setString(1, account.getUserName());
            createAccountStmt.setString(2, account.getPassword());
            int rows = createAccountStmt.executeUpdate();
            if (rows != 1) {
                throw new FileServerDBException(failureMsg);
            }
        } catch (SQLException sqle) {
            failureMsg = "Username already taken";
            throw new FileServerDBException(failureMsg, sqle);
        }
    }

//Uploads the provided file
    public void uploadFile(FileDTO file) throws FileServerDBException, SQLException {
        String failureMsg = "Could not upload the file : " + file;
        try {
            createFileStmt.setString(1, file.getFileName());
            createFileStmt.setString(2, file.getOwner());
            createFileStmt.setInt(3, file.getSize());
            createFileStmt.setBoolean(4, file.getPermission());
            int rows = createFileStmt.executeUpdate();
            if (rows != 1) {

                throw new FileServerDBException(failureMsg);
            }
        } catch (SQLException sqle) {
            failureMsg = "Filename already taken";
            throw new FileServerDBException(failureMsg, sqle);
        }

    }

    //Searches for an account whose holder has the specified name..
    public Account findAccountByName(String holderName) throws FileServerDBException {
        String failureMsg = "Could not search for specified account.";
        ResultSet result = null;
        try {
            findAccountStmt.setString(1, holderName);
            result = findAccountStmt.executeQuery();
            if (result.next()) {
                return new Account(holderName, result.getString(PASSWORD_COLUMN_NAME), this);
            }
        } catch (SQLException sqle) {
            throw new FileServerDBException(failureMsg, sqle);
        } finally {
            try {
                result.close();
            } catch (Exception e) {
                throw new FileServerDBException(failureMsg, e);
            }
        }
        return null;
    }

    // Searches for the file by it's name   
    public File findFileByName(String fileName) throws FileServerDBException {
        String failureMsg = "Could not search for specified account.";
        ResultSet result = null;
        try {
            findFileStmt.setString(1, fileName);
            result = findFileStmt.executeQuery();
            if (result.next()) {
                return new File(fileName, result.getString(OWNER_COLUMN_NAME), result.getInt(SIZE_COLUMN_NAME), result.getBoolean(READORWRITE_COLUMN_NAME), this);
            }
        } catch (SQLException sqle) {
            throw new FileServerDBException(failureMsg, sqle);
        } finally {
            try {
                result.close();
            } catch (Exception e) {
                throw new FileServerDBException(failureMsg, e);
            }
        }
        return null;
    }

    // Searches for the file by it's name   
    public List<File> findFileByOwnerName(String owner) throws FileServerDBException, SQLException {
        String failureMsg = "Could not search for specified owner.";
        List<File> files = new ArrayList<>();
        findAllMyFilesStmt.setString(1, owner);
        try (ResultSet result = findAllMyFilesStmt.executeQuery()) {
            while (result.next()) {
                files.add(new File(result.getString(FILENAME_COLUMN_NAME), owner, result.getInt(SIZE_COLUMN_NAME), result.getBoolean(READORWRITE_COLUMN_NAME)));
            }
        } catch (SQLException sqle) {
            throw new FileServerDBException(failureMsg, sqle);
        }
        return files;
    }

    // Retrieves all existing files
    public List<File> findAllFiles() throws FileServerDBException {
        String failureMsg = "Could not list the files.";
        List<File> files = new ArrayList<>();
        try (ResultSet result = findAllFilesStmt.executeQuery()) {
            while (result.next()) {
                files.add(new File(result.getString(FILENAME_COLUMN_NAME), result.getString(OWNER_COLUMN_NAME), result.getInt(SIZE_COLUMN_NAME), result.getBoolean(READORWRITE_COLUMN_NAME)));
            }
        } catch (SQLException sqle) {
            throw new FileServerDBException(failureMsg, sqle);
        }
        return files;
    }

    //Changes the size of the file
    public void changeFileSize(String name, int size) throws FileServerDBException {
        try {
            changeFileStatement.setInt(1, size);
            changeFileStatement.setString(2, name);
            changeFileStatement.executeUpdate();

        } catch (SQLException sqle) {
            throw new FileServerDBException("Could not update the file data: " + name, sqle);
        }
    }

    //Changes the read/write permission of the file
    public void changeFilePermission(String fileName, Boolean permission) throws FileServerDBException {
        try {
            changeFileStatement1.setBoolean(1, permission);
            changeFileStatement1.setString(2, fileName);
            changeFileStatement1.executeUpdate();

        } catch (SQLException sqle) {
            throw new FileServerDBException("Could not update the file data: " + fileName, sqle);
        }
    }

    //Deletes the fuile by it's name
    public void deleteFile(FileDTO fileToDelete) throws FileServerDBException {
        try {
            deleteFileStmt.setString(1, fileToDelete.getFileName());
            deleteFileStmt.executeUpdate();
        } catch (SQLException sqle) {
            throw new FileServerDBException("Could not delete the file: " + fileToDelete, sqle);
        }
    }

    private Connection createDatasource(String dbms, String datasource) throws
            ClassNotFoundException, SQLException, FileServerDBException {
        Connection connection = connectToFileServerDB(dbms, datasource);
        if (!fserverTableExists(connection)) {
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE " + TABLE_NAME
                    + " (" + HOLDER_COLUMN_NAME + " VARCHAR(32) PRIMARY KEY, "
                    + PASSWORD_COLUMN_NAME + " VARCHAR(32))");
        }
        if (!fileTableExists(connection)) {
            Statement statement = connection.createStatement();

            statement.executeUpdate("CREATE TABLE " + TABLE_NAME1
                    + " (" + FILENAME_COLUMN_NAME + " VARCHAR(32) PRIMARY KEY, "
                    + OWNER_COLUMN_NAME + " VARCHAR(32), " + SIZE_COLUMN_NAME + " FLOAT, " + READORWRITE_COLUMN_NAME + " BOOLEAN)");
        }
        return connection;
    }

    private boolean fserverTableExists(Connection connection) throws SQLException {
        int tableNameColumn = 3;
        DatabaseMetaData dbm = connection.getMetaData();
        try (ResultSet rs = dbm.getTables(null, null, null, null)) {
            for (; rs.next();) {
                if (rs.getString(tableNameColumn).equals(TABLE_NAME)) {
                    return true;
                }
            }
            return false;
        }
    }

    private boolean fileTableExists(Connection connection) throws SQLException {
        int tableNameColumn = 3;
        DatabaseMetaData dbm = connection.getMetaData();
        try (ResultSet rs = dbm.getTables(null, null, null, null)) {
            for (; rs.next();) {
                if (rs.getString(tableNameColumn).equals(TABLE_NAME1)) {
                    return true;
                }
            }
            return false;
        }
    }

    private Connection connectToFileServerDB(String dbms, String datasource)
            throws ClassNotFoundException, SQLException, FileServerDBException {
        try {
            Class.forName("org.apache.derby.jdbc.ClientXADataSource");
            System.out.println("Successful try");
        } catch (ClassNotFoundException ex) {
            System.out.println("Failed to connect");
            ex.printStackTrace();
        }
        return DriverManager.getConnection("jdbc:derby://localhost:1527/Fservertrial;create=true");
    }

    private void prepareStatements(Connection connection) throws SQLException {
        createAccountStmt = connection.prepareStatement("INSERT INTO "
                + TABLE_NAME + " VALUES (?, ?)");
        findAccountStmt = connection.prepareStatement("SELECT * from "
                + TABLE_NAME + " WHERE NAME = ?");
        
        findFileStmt = connection.prepareStatement("SELECT * from "
                + TABLE_NAME1 + " WHERE FILENAME= ?");
        findAllFilesStmt = connection.prepareStatement("SELECT * from "
                + TABLE_NAME1);
        findAllMyFilesStmt = connection.prepareStatement("SELECT * from "
                + TABLE_NAME1 + " WHERE OWNER= ?");
        createFileStmt = connection.prepareStatement("INSERT INTO "
                + TABLE_NAME1 + " VALUES (?, ?, ?, ?)");
        changeFileStatement = connection.prepareStatement("UPDATE "
                + TABLE_NAME1
                + " SET filesize = ? WHERE filename= ? ");
        changeFileStatement1 = connection.prepareStatement("UPDATE "
                + TABLE_NAME1
                + " SET readonly = ? WHERE filename= ? ");
        deleteFileStmt = connection.prepareStatement("DELETE FROM "
                + TABLE_NAME1
                + " WHERE filename = ?");
    }
}
