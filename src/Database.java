import java.io.File;
import java.sql.*;
import java.util.ArrayList;

public class Database {
    private String connStr;
    private Connection connection;
    private PreparedStatement statement;
    private ResultSet result;
    private String dbPathLoc;

    public Database(String dbPathname) {
    	dbPathLoc = dbPathname;
        connStr = "jdbc:sqlite:" + dbPathname;
        dbInit();
    }
    
    public Database() {
    	new Database("messageDatabase.db");
    }
    
    private void dbInit() {
    	File database = new File(dbPathLoc);
    	if (database.isFile()) {
    		createTables();
    	}
    	else {
    		createDatabaseFile(database);
    		createTables();
    	}
    }
    
    private void createDatabaseFile(File database) {
    	try {
    		if(!database.isDirectory()) database.getParentFile().mkdirs();
    		database.createNewFile();
    		
    	}
    	catch (Exception e) {
    		System.out.println("Failed with exception: " + e.getMessage());
    	}
    }
    
   private void createTables() {
	   try {
		   connection = DriverManager.getConnection(connStr);
		
		   Statement stmt = connection.createStatement();
		
		   String sql = "CREATE TABLE IF NOT EXISTS userProfile (\n"
				   + "	username varchar(20) primary key,\n"
				   + "	password varchar(20)\n"
				   + ");";
		   stmt.execute(sql);
			
		   sql = "CREATE TABLE IF NOT EXISTS userMessage (\n"
				   + "	rowid integer primary key,\n"
				   + "	sender varchar(20),\n"
				   + "	recipient varchar(20),\n"
				   + "	messageBody varchar(300),\n"
				   + "	date date,\n"
				   + "	CONSTRAINT fk_sender FOREIGN KEY(sender) REFERENCES userProfile(username),\n"
				   + "	CONSTRAINT fk_recipient FOREIGN KEY(recipient) REFERENCES userProfile(username)\n"
				   + ");";
		   stmt.execute(sql);
	   }
	   catch (SQLException e) {
		   System.out.println("Error building tables");
	   }
   }
    
    private void conOpen() {
		try {
			connection = DriverManager.getConnection(connStr);
		}
		catch (SQLException e) {
			System.out.println("It broke Connecting");
		}
	}
	private void conClose() {
		try {
			connection.close();
		}
		catch (SQLException e) {
			System.out.println("It broke Disconnecting");
		}
	}
}
