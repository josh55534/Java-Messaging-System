/* ============================================
 *  Author: Joshuah Collins
 *  File: Message.java
 *  Project: Java Messaging Program
 * ============================================
 */
public class Message {
	// ----- CLASS VARIABLES -----
	private int messageID;
	private String senderName;
	private String recipientName;
	private String messageBody;
	private String dateReceived;
	
	// ----- CONSTRUCTOR METHODS -----

	/* Constructor method for Message. Parameter ID is the message ID. Parameter sender
	 * is the sender usnername. Parameter recipient is the message receiver's username.
	 * Parameter message is the message body. Parameter date is the date that the message
	 * was created.
	*/
	public Message(int ID, String sender, String recipient, String message, String date) {
		messageID = ID;
		senderName = sender;
		recipientName = recipient;
		messageBody = message;
		dateReceived = date;
	}
	/* Default constructor method for Message. Sets all string values to "" and the message
	 * ID to be equal to -1.
	 */
	public Message() {
		this(-1,"","","","");
	}
	
	// ----- GETTER METHODS -----
	public int getMsgID() { return messageID; }
	public String getSender() { return senderName; }
	public String getRecipient() { return recipientName; }
	public String getMessage() { return messageBody; }
	public String getDate() { return dateReceived; }
	
	// ----- SETTER METHODS -----
	public void setMsgID(int msgID) { messageID = msgID; }
	public void setSender(String msgSender) { senderName = msgSender; }
	public void setRecipient(String msgSender) { recipientName = msgSender; }
	public void setMessage(String msgBody) { messageBody = msgBody; }
	public void setDate(String msgDate) { dateReceived = msgDate; }
	
	// ----- toString() METHOD -----
	@Override
	public String toString() {
		String temp = "";
		
		temp += "To: " + recipientName + "\n";
		temp += "From: " + senderName + "\n";
		temp += "Message: " + messageBody;
		
		return temp;
	}
}
