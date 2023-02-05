import java.util.Scanner;
import java.util.ArrayList;


public class Driver {
	private static Database db = new Database("../database/messageDatabase.db");
	private static boolean loggedIn = false;
	private static int SENTINEL = 0;
	
	private static Scanner input = new Scanner(System.in);
	private static String userName;

	public static void main(String[] args) {
		while(SENTINEL == 0) {
			printMenu();
		}
		System.out.println("Goodbye!");
	}

	public static void printMenu() { // print menu options
		if(!loggedIn) { // not logged in
			System.out.println("---------------- MAIN MENU ----------------");
			System.out.println("1. Login");
			System.out.println("2. Create Account");
			System.out.println("3. Quit");
			System.out.println("------------------------------------------");
			System.out.println("Enter option number: ");
			
			try {
				int userInt = Integer.parseInt(input.nextLine());
				
				switch(userInt) {
					case 1:
						login();
						break;
					case 2:
						createAccount();
						break;
					case 3:
						SENTINEL = -1;
						break;
				}
			}
			catch(NumberFormatException e) {
				System.out.println("Invalid input");
			}
		}
		else { // logged in
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
				
				switch(userInt) {
					case 1:
						messageHistory(db.returnReceivedMessageList(userName), 1);
						break;
					case 2:
						sendMessage();
						break;
					case 3:
						messageHistory(db.returnSentMessageList(userName), 0);
						break;
					case 4:
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
	
	public static void login() {
		System.out.println("Enter username: ");
		userName = input.nextLine();
		System.out.println("Enter password: ");
		String password = input.nextLine();
		
		if(db.login(userName, password)) {
			loggedIn = true;
			System.out.println("Login Success!");
		}
		else {
			loggedIn = false;
			System.out.println("Incorrect Username/Password");
		}
	}
	
	public static void createAccount() {
		System.out.println("Enter a username: ");
		String tempUserName = input.nextLine();
		
		if (db.checkUserName(tempUserName)) {
			System.out.println("An account with this username exists already");
			return;
		}
		
		System.out.println("Enter a password: ");
		String password = input.nextLine();
		
		if(password.length() <= 20 && tempUserName.length() <= 20 && !tempUserName.matches(".*\\s.*") && !password.matches(".*\\s.*")) {
			db.createAccount(tempUserName, password);
			System.out.println("Account Created Successfully!");
		}
		else {
			System.out.println("Usernames and passwords must be within 20 \n"
					+ "chars and have no spaces.");
		}
	}
	
	public static void sendMessage() {
		System.out.println("Send a message to: ");
		String sendUserName = input.nextLine();
		
		if (!db.checkUserName(sendUserName)) {
			System.out.println("No accounts exist with this username");
			return;
		}
		
		System.out.println("Enter message: ");
		String tempMessage = input.nextLine();
		
		if (tempMessage.length() > 300) {
			System.out.println("Messages can only have a max of 300 characters");
			return;
		}
		
		db.addMessage(userName, sendUserName, tempMessage);
	}
	
	public static void messageHistory(ArrayList<Message> temp, int x) {		
		int intInput = -1;
		int listNum = 0;
		
		while(intInput != 13) {
			if(x == 1) System.out.println("------------ RECEIVED MESSAGES ------------");
			else if (x == 0) System.out.println("-------------- SENT MESSAGES --------------");

			System.out.println("Page " + (listNum + 1) + " of " + (int)(Math.ceil(temp.size() / 10.0)));
			
			for (int i = (listNum * 10) + 1; i < temp.size() && i <= 10; i++) {
				if(x == 1) {
					System.out.println(i + ". From: " + temp.get(i).getSender());
					System.out.println("Received: " + temp.get(i).getDate());
				}
				else if(x==0) {
					System.out.println(i + ". To: " + temp.get(i).getRecipient());
					System.out.println("Sent: " + temp.get(i).getDate());
				}
				
			}
			
			if(temp.size() == 0) {
				System.out.println("There are no messages to display");
			}
			
			if (temp.size() > (listNum*10)+10) {
				System.out.println("11. Next Page");
			}
			if (listNum != 0) {
				System.out.println("12. Previous Page");
			}
			System.out.println("13. Exit Message List");
			System.out.println("------------------------------------------");
			System.out.println("Enter option: ");
			
			try {
				intInput = Integer.parseInt(input.nextLine());
			}
			catch(NumberFormatException e) {
			}
			
			if(intInput > listNum*10 && intInput <= (listNum*10)+10 && intInput < temp.size()) {
				System.out.println();
				displayMessage(temp.get(intInput));
				
				System.out.println("Press Enter key to continue...");
				input.nextLine();
			}
			else if(intInput == 11 && temp.size() > (listNum*10)+10) {
				listNum += 1;
			}
			else if(intInput == 12 && listNum != 0) {
				listNum -= 1;
			}
			else if (intInput != 13) {
				System.out.println("Invalid option");
			}
			
			System.out.println();
		}
	}
	
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
