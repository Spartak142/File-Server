# File-Server
A File Catalog client-server application.  The communication is done via Remote Method Invocation. The files and user accounts are stored in a database (derby plugin in Netbeans for testin purposes). Therefore the server has a Database Access Object class that communicates with the database on requests.
To successfully run the code a database should be created. I have written the programm using derby in Netbeans, hence it is hardcoded into the program.
The name I have used for the database is "Fservertrial", therefore to run the program, a database with that name should be used, or the code should be slightly modified.
