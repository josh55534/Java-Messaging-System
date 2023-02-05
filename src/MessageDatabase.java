/* ============================================
 *  Author: Joshuah Collins
 *  File: Database.java
 *  Project: Java Messaging Program
 * ============================================
 */
import java.io.File;
import java.sql.*;
import java.util.ArrayList;

/** A {@code MessageDatabase} is responsible for creating, reading, and updating
 *  a SQLite database used for storing user's login information and messages.
 */
public class MessageDatabase {
	private String connStr;
	private Connection connection;
	private PreparedStatement statement;
	private ResultSet result;
	private String dbPathLoc;
	
	// -- CONSTRUCTOR METHODS --

	/** Creates a new {@code MessageDatabase}
	 * @param dbPathname path of the database to be used/created 
	 */
	public MessageDatabase(String dbPathname) {
		dbPathLoc = dbPathname;
		connStr = "jdbc:sqlite:" + dbPathname;
		dbInit();
	}
	public MessageDatabase() {
		new MessageDatabase("messageDatabase.db");
	}
	
	// -- DATABASE INITIALIZATION METHODS --

	/* Checks overall status of database setup. if it needs to create files, it does so
	   before moving onto creation of tables inside database
	*/
	private void dbInit() {
		File database = new File(dbPathLoc);

		if (database.isFile()) createTables(); // if database file exists, create tables
		else { // if database file doesn't exist, create file then create tables
			createDatabaseFile(database);
			createTables();
		}
	}

	/* Creates necessary database files using dbPathname parameter from constructor method
	*/
	private void createDatabaseFile(File database) {
		try {
			if(!database.isDirectory()) database.getParentFile().mkdirs();
			database.createNewFile();
			
		}
		catch (Exception e) {
			System.out.println("Failed with exception: " + e.getMessage());
		}
	}

	/* Creates tables in database. Table creation used with "CREATE TABLE IF NOT EXISTS" 
	 * so as to avoid creation of duplicate tables.
	*/
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
	
	// -- CONNECTION OPEN/CLOSE METHODS
	private void conOpen() { //opens connection with SQLite database
		try {
			connection = DriverManager.getConnection(connStr);
		}
		catch (SQLException e) {
			System.out.println("It broke Connecting");
		}
	}
	private void conClose() { //closes connection with SQLite database
		try {
			connection.close();
		}
		catch (SQLException e) {
			System.out.println("It broke Disconnecting");
		}
	}

	
	public boolean login(String userName, String password) {
		boolean temp = false;
		try {
			conOpen();
			statement = connection.prepareStatement("SELECT * FROM userProfile WHERE username = ? AND password = ?");
			statement.setString(1, userName);
			statement.setString(2, password);
			result = statement.executeQuery();
			
			if (result.next()) temp = true;
			else temp = false;
			
			conClose();
		}
		catch (SQLException e) {
			System.out.println("It broke checking login");
		}
		
		return temp;
	}
	
	public boolean createAccount(String userName, String password)
	{
		boolean temp = false;
		try {
			conOpen();
			statement = connection.prepareStatement("SELECT * FROM userProfile WHERE username = ?");
			statement.setString(1, userName);
			result = statement.executeQuery();
			
			if (result.next()) temp = false;
			else {
				statement = connection.prepareStatement("INSERT INTO userProfile VALUES (?, ?)");
				statement.setString(1, userName);
				statement.setString(2, password);
				statement.executeUpdate();
				
				temp = true;
			}
			
			conClose();
		}
		catch (SQLException e) {
			System.out.println("It broke creating account");
		}
		
		return temp;
	}
	
	public ArrayList<Message> returnReceivedMessageList(String userName) {
		ArrayList<Message> temp = new ArrayList<Message>();

		try { //try to get connection
			conOpen();
			statement = connection.prepareStatement("SELECT * FROM userMessage WHERE recipient = ? ORDER BY date desc");
			statement.setString(1, userName);
			result = statement.executeQuery();
			
			try {
				do {	
					temp.add(new Message(result.getInt("rowid"), result.getString("sender"), result.getString("recipient"), result.getString("messageBody"), result.getString("date")));
				}
				while(result.next());
			}
			catch(SQLException e) {
				
			}
			
			conClose();
		}
		catch (SQLException e) {
			System.out.println("It broke looking for received messages");
		}
		
		return temp;
	}
	
	public ArrayList<Message> returnSentMessageList(String userName) {
		ArrayList<Message> temp = new ArrayList<Message>();
		
		try  { // get results of all messages to userName input
			conOpen();
			
			statement = connection.prepareStatement("SELECT * FROM userMessage WHERE sender = ? ORDER BY date DESC");
			statement.setString(1, userName);
			result = statement.executeQuery();
			
			try {
				do {	
					temp.add(new Message(result.getInt("rowid"), result.getString("sender"), result.getString("recipient"), result.getString("messageBody"), result.getString("date")));
				}
				while(result.next());
			}
			catch(SQLException e) {
				
			}

			conClose();
		}
		catch (SQLException e) {
			System.out.println("It broke looking for sent messages");
		}
		
		return temp;
	}
	
	public void addMessage(String sender, String recipient, String message) {	
		try {
			conOpen();
			
			statement = connection.prepareStatement("INSERT INTO userMessage(sender, recipient, messageBody, date) VALUES (?, ?, ?, current_timestamp)");
			statement.setString(1, sender);
			statement.setString(2, recipient);
			statement.setString(3, message);
			statement.executeUpdate();
			
			conClose();
		}
		catch (SQLException e) {
			System.out.println("It broke creating message");
		}
	}
	
	public boolean checkUserName(String username) {
		boolean temp = false;
		try {	
			conOpen();
			
			statement = connection.prepareStatement("SELECT username FROM userProfile where username = ?");
			statement.setString(1, username);
			result = statement.executeQuery();
			temp = result.next();
			
			conClose();
		}
		catch (SQLException e) {
			System.out.println("It broke checking username");
		}
		
		return temp;
	}
}
