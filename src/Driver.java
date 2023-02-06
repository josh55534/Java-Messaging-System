/* ============================================
 *  Author: Joshuah Collins
 *  File: Driver.java
 *  Project: Java Messaging Program
 * ============================================
 */
import java.util.Scanner;
import java.util.ArrayList;


public class Driver {
	// ----- CLASS VARIABLES ----- 
	private static MessageDatabase db = new MessageDatabase("../database/messageDatabase.db");
	private static boolean loggedIn = false;
	private static int SENTINEL = 0;
	
	private static Scanner input = new Scanner(System.in);
	private static String userName;

	// ----- PROGRAM START LOOP -----
	public static void main(String[] args) {
		while(SENTINEL == 0) {
			printMenu();
		}
		System.out.println("Goodbye!");
	}

	/* Prints the menu options in terminal based on whether or not the program user
	 * is logged in or not.
	 */
	public static void printMenu() { // print menu options
		if(!loggedIn) { // not logged in menu
			System.out.println("---------------- MAIN MENU ----------------");
			System.out.println("1. Login");
			System.out.println("2. Create Account");
			System.out.println("3. Quit");
			System.out.println("------------------------------------------");
			System.out.println("Enter option number: ");
			
			try {
				int userInt = Integer.parseInt(input.nextLine());
				
				switch(userInt) { // not logged in menu handler
					case 1: // option 1: login
						login();
						break;
					case 2: // option 2: create new user account
						createAccount();
						break;
					case 3: // option 3: quit
						SENTINEL = -1;
						break;
				}
			}
			catch(NumberFormatException e) {
				System.out.println("Invalid input");
			}
		}
		else { // logged in menu
			System.out.println("Logged in as: " + userName);
			System.out.println("---------------- USER MENU ----------------");
			System.out.println("1. Read Messages");
			System.out.println("2. Send Message");
			System.out.println("3. View Sent Messages");
			System.out.println("4. Logout");
			System.out.println("------------------------------------------");
			System.out.println("Enter option number: ");
			
			try {
				int userInt = Integer.parseInt(input.nextLine());
				
				switch(userInt) { // logged in menu handler
					case 1: // option 1: read messages
						messageHistory(db.returnReceivedMessageList(userName), 1);
						break;
					case 2: // option 2: send a new message
						sendMessage();
						break;
					case 3: // option 3: read sent messages
						messageHistory(db.returnSentMessageList(userName), 0);
						break;
					case 4: // option 4: log out
						loggedIn = false;
						userName = "";
						break;
				}
				
			}
			catch(NumberFormatException e) {
				System.out.println("Invalid input");
			}
		}
		System.out.println();
	}
	
	// ----- LOGGED OUT MENU METHODS -----

	/* Prompts the user for a username and password and looks them up in database
	 * with MessageDatabase.login() method. If it returns true, set loggedIn to true.
	 * Otherwise, display a message to the user that username/password is incorrect.
	 */
	public static void login() {
		System.out.println("Enter username: ");
		userName = input.nextLine();
		System.out.println("Enter password: ");
		String password = input.nextLine();
		
		if(db.login(userName, password)) { // login success, set loggedIn to true
			loggedIn = true;
			System.out.println("Login Success!");
		}
		else { // login fail, keep loggedIn set to false, diplay message to user
			loggedIn = false;
			System.out.println("Incorrect Username/Password");
		}
	}

	/* Prompts the user for a username and password. If username provided already
	 * exists within MessageDatabase, then display a message to the user that the  
	 * username is already in use by another user. Usernames and passwords must be 
	 * less than 20 characters long, and contain no spaces. If one of these is true,
	 * display to user that username/password is invaleid and break out of method. 
	 * If all inputs are valid, then add account to database with 
	 * MessageDatabase.createAccount().
	 */
	public static void createAccount() {
		System.out.println("Enter a username: ");
		String tempUserName = input.nextLine();
		
		if (db.checkUserName(tempUserName)) { // username already exists, break out of method
			System.out.println("An account with this username exists already");
			return;
		}
		
		System.out.println("Enter a password: ");
		String password = input.nextLine();
		
		if(password.length() <= 20 && tempUserName.length() <= 20 && !tempUserName.matches(".*\\s.*") && !password.matches(".*\\s.*")) { //make sure that password is correclty formatted
			db.createAccount(tempUserName, password);
			System.out.println("Account Created Successfully!");
		}
		else {
			System.out.println("Usernames and passwords must be within 20 \n"
					+ "chars and have no spaces.");
		}
	}
	
	// ----- LOGGED IN MENU METHODS ----- 

	/* Prompts user for a username to send a message to, if  given input is
	 * invalid, then display a message to user. Otherwise, prompt user for
	 * message to send. If message is longer than 300 characters, display a
	 * message to the user. If both parameters are valid, then add messsage
	 * to the database with MessageDatabase.addMessage().
	*/
	public static void sendMessage() {
		System.out.println("Send a message to: ");
		String sendUserName = input.nextLine();
		
		if (!db.checkUserName(sendUserName)) { // check database if username exists
			System.out.println("No accounts exist with this username");
			return;
		}
		
		System.out.println("Enter message: ");
		String tempMessage = input.nextLine();
		
		if (tempMessage.length() > 300) { // validate message is less than 300 characters long
			System.out.println("Messages can only have a max of 300 characters");
			return;
		}
		
		db.addMessage(new Message(-1, sendUserName, userName, tempMessage, null));
	}
	
	/* Prints either received or sent messages based on second parameter, x.
	 * If x is passed as a 1, then display all messages that were received.
	 * If x is passed as a 0, then display all messages that were sent.
	 * Messages are displayed as menu options sorted by date in descending
	 * order. Program will display groups of 10 messages with options to display
	 * the next 10 or previous 10 messages. Selecting a message calls the method
	 * displayMessage().
	 */
	public static void messageHistory(ArrayList<Message> temp, int x) {		
		int intInput = -1;
		int listNum = 0;
		
		while(intInput != 13) {
			if(x == 1) System.out.println("------------ RECEIVED MESSAGES ------------"); // received messages menu header
			else if (x == 0) System.out.println("-------------- SENT MESSAGES --------------"); // sent messages menu header

			System.out.println("Page " + (listNum + 1) + " of " + (int)(Math.ceil(temp.size() / 10.0))); // display page number
			
			for (int i = (listNum * 10) + 1; i < temp.size() && i <= 10; i++) { // prints groupings of 10 messages
				if(x == 1) { // print received message
					System.out.println(i + ". From: " + temp.get(i).getSender());
					System.out.println("Received: " + temp.get(i).getDate());
				}
				else if(x==0) { // print sent message
					System.out.println(i + ". To: " + temp.get(i).getRecipient());
					System.out.println("Sent: " + temp.get(i).getDate());
				}
				
			}
			
			if(temp.size() == 0) { // if there are no messages, display "No messages" to user
				System.out.println("There are no messages to display");
			}
			
			if (temp.size() > (listNum*10)+10) { // if there are 10+ messages, display prompt to go to next 10 messages
				System.out.println("11. Next Page");
			}
			if (listNum != 0) { // if user is not viewing first group of 10 messages, display prompt to go to previous 10 messages
				System.out.println("12. Previous Page");
			}
			System.out.println("13. Exit Message List");
			System.out.println("------------------------------------------");
			System.out.println("Enter option: ");
			
			try {
				intInput = Integer.parseInt(input.nextLine());
			}
			catch(NumberFormatException e) {
				System.out.println("Invalid Input");
			}
			
			if(intInput > listNum*10 && intInput <= (listNum*10)+10 && intInput < temp.size()) { // option 1-10: display numbered message, then display prompt to leave message
				System.out.println();
				displayMessage(temp.get(intInput));
				
				System.out.println("Press Enter key to continue...");
				input.nextLine();
			}
			else if(intInput == 11 && temp.size() > (listNum*10)+10) { // option 11: increase listNum by 1 to view next 10 messages
				listNum += 1;
			}
			else if(intInput == 12 && listNum != 0) { // option 12: decreasee listNum by 1 to view previous 10 messages
				listNum -= 1;
			}
			else if (intInput != 13) { // display error message if user inputted anything other than 1-13
				System.out.println("Invalid option");
			}
			
			System.out.println();
		}
	}
	
	/* Given a Message object, print the Message objects attributes
	 * senderName, recipientName, dateReceibed, and messageBody.
	 */
	public static void displayMessage(Message message) {
		System.out.println("------------------------------------------");
		System.out.println("From: " + message.getSender());
		System.out.println("To: " + message.getRecipient());
		System.out.println("Received: " + message.getDate());
		System.out.println("------------------------------------------");
		System.out.println(message.getMessage());
		System.out.println("------------------------------------------");
	}
}
