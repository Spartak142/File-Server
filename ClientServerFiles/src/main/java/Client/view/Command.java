package Client.view;

public enum Command {
    /**
     * Creates a new account with username and password provided.
     */
    REGISTER,
    /**
     * Logs the user in if password and name are correct.
     */
    LOGIN,
    /**
     * Uploads provided file-metadata to the server's database
     */
    UPLOAD,
    /**
     * Downloads metadata of a specified file if the user has permissions
     */
    DOWNLOAD,
    /**
     * Lists all existing files in the database uploaded by the current user
     */
    LISTMY,
    /**
     * Lists all files present in the catalog
     */
    LISTALL,
    /**
     * Checks whether the user has a permission to change the file and changes
     * it to the new values if allowed
     */
    CHANGE,
    /**
     * Deletes a file with the given name if allowed
     */
    DELETE,
    /**
     * Lists all commands.
     */
    HELP,
    /**
     * logout to e.g. change the user basically ghost mode
     */
    LOGOUT,
    /**
     * Leave the chat application.
     */
    QUIT,
    /**
     * None of the valid commands above was specified.
     */
    ILLEGAL_COMMAND
}
