/* ============================================
 *  Author: Joshuah Collins
 *  File: MessageDatabase.java
 *  Project: Java Messaging Program
 * ============================================
 */
import java.io.File;
import java.sql.*;
import java.util.ArrayList;

public class MessageDatabase {
	private String connStr;
	private Connection connection;
	private PreparedStatement statement;
	private ResultSet result;
	private String dbPathLoc;
	
	// ----- CONSTRUCTOR METHODS -----

	/* Constructor method for MessageDatabase. Paremeter dbPathname sets the directory
	 * path to save the data at. Must end the parameter with "{filename}.db" to create
	 * the SQLite file.
	 */
	public MessageDatabase(String dbPathname) {
		dbPathLoc = dbPathname;
		connStr = "jdbc:sqlite:" + dbPathname;
		dbInit();
	}

	/* Default constructor method. Sets the database to be created in the source folder
	 * with a filename of "messageDatabase.db".
	 */
	public MessageDatabase() {
		new MessageDatabase("messageDatabase.db");
	}
	
	// ----- DATABASE INITIALIZATION METHODS -----

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
			
			// creates userProfile table
			String sql = "CREATE TABLE IF NOT EXISTS userProfile (\n"
					+ "	username varchar(20) primary key,\n"
					+ "	password varchar(20)\n"
					+ ");";
			stmt.execute(sql);
			
			// creates userMessage table
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
	
	// ----- CONNECTION OPEN/CLOSE METHODS -----
	private void conOpen() { //opens connection with SQLite database
		try {
			connection = DriverManager.getConnection(connStr);
		}
		catch (SQLException e) {
			System.out.println("It broke connecting");
		}
	}
	private void conClose() { //closes connection with SQLite database
		try {
			connection.close();
		}
		catch (SQLException e) {
			System.out.println("It broke disconnecting");
		}
	}
	
	/* Given input string parameters userName and password, MessageDatabase will open an
	 * SQLite connection and search the database for an entry in the userProfile table
	 * searching for a matching username and password combination. If found, the login()
	 * method will return true. Otherwise it will return false.
	 */
	public boolean login(String userName, String password) {
		boolean temp = false;
		try {
			conOpen();
			statement = connection.prepareStatement("SELECT * FROM userProfile WHERE username = ? AND password = ?");
			statement.setString(1, userName);
			statement.setString(2, password);
			result = statement.executeQuery();
			
			if (result.next()) temp = true; // if account details found, return true
			else temp = false; //otherwise return false
			
			conClose();
		}
		catch (SQLException e) {
			System.out.println("It broke checking login");
		}
		
		return temp;
	}
	
	/* Given string input parameters userName and password, MessageDatabase will open an
	 * SQLite connection and search the database for any matching usernames. If there is
	 * already a username with the same value, return false and break out of the method.
	 * Otherwise, the createAccount() method will insert the username/password combination
	 * as a new entry into the userProfile table.
	 */
	public boolean createAccount(String userName, String password)
	{
		boolean temp = false;
		try {
			conOpen();
			statement = connection.prepareStatement("SELECT * FROM userProfile WHERE username = ?");
			statement.setString(1, userName);
			result = statement.executeQuery();
			
			if (result.next()) temp = false; // username already exists, return false
			else { // username doesn't exist, add to table and return true
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
	
	/* Given a string input userName, returnReceivedMessageList() will select all
	 * messages in the table userMessage where input string userName is in the
	 * recipient row. The ResultSet of this selection will then be returned as an
	 * ArrayList of Message objects.
	 */
	public ArrayList<Message> returnReceivedMessageList(String userName) {
		ArrayList<Message> temp = new ArrayList<Message>();

		try {
			conOpen();
			statement = connection.prepareStatement("SELECT * FROM userMessage WHERE recipient = ? ORDER BY date desc");
			statement.setString(1, userName);
			result = statement.executeQuery();
			
			do { // loop through ResultSet, put the information into a new Message object, and add the new Message object to ArrayList temp
				temp.add(new Message(result.getInt("rowid"), result.getString("sender"), result.getString("recipient"), result.getString("messageBody"), result.getString("date")));
			}
			while(result.next());
			
			conClose();
		}
		catch (SQLException e) {
			System.out.println("It broke looking for received messages");
		}
		
		return temp;
	}
	
	/* Given a string input userName, returnReceivedMessageList() will select all
	 * messages in the table userMessage where input string userName is in the
	 * sender row. The ResultSet of this selection will then be returned as an
	 * ArrayList of Message objects.
	 */
	public ArrayList<Message> returnSentMessageList(String userName) {
		ArrayList<Message> temp = new ArrayList<Message>();
		
		try  {
			conOpen();
			
			statement = connection.prepareStatement("SELECT * FROM userMessage WHERE sender = ? ORDER BY date DESC");
			statement.setString(1, userName);
			result = statement.executeQuery();

			do { // loop through ResultSet, put the information into a new Message object, and add the new Message object to ArrayList temp
				temp.add(new Message(result.getInt("rowid"), result.getString("sender"), result.getString("recipient"), result.getString("messageBody"), result.getString("date")));
			}
			while(result.next());

			conClose();
		}
		catch (SQLException e) {
			System.out.println("It broke looking for sent messages");
		}
		
		return temp;
	}
	
	/* Given Message object input message, addMessage() insert a new entry into
	 * the userMessage table using the message data from the Message object.
	 */
	public void addMessage(Message message) {	
		try {
			conOpen();
			
			statement = connection.prepareStatement("INSERT INTO userMessage(sender, recipient, messageBody, date) VALUES (?, ?, ?, current_timestamp)");
			statement.setString(1, message.getSender());
			statement.setString(2, message.getRecipient());
			statement.setString(3, message.getMessage());
			statement.executeUpdate();
			
			conClose();
		}
		catch (SQLException e) {
			System.out.println("It broke creating message");
		}
	}
	
	/* Given a string input username, checkUserName will search the userProfile
	 * table and return true if username is found in the table. Otherwise, it
	 * will return false.
	 */
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
