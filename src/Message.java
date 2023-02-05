
public class Message {
	private int messageID;
	private String senderName;
	private String recipientName;
	private String messageBody;
	private String dateReceived;
	
	public Message() {
		this(-1,"","","","");
	}
	public Message(int messageID, String sender, String recipient, String message, String date) {
		senderName = sender;
		recipientName = recipient;
		messageBody = message;
		dateReceived = date;
	}
	
	public int getMsgID() { return messageID; }
	public String getSender() { return senderName; }
	public String getRecipient() { return recipientName; }
	public String getMessage() { return messageBody; }
	public String getDate() { return dateReceived; }
	
	public void setMsgID(int msgID) { messageID = msgID; }
	public void setSender(String msgSender) { senderName = msgSender; }
	public void setRecipient(String msgSender) { recipientName = msgSender; }
	public void setMessage(String msgBody) { messageBody = msgBody; }
	public void setDate(String msgDate) { dateReceived = msgDate; }
	
	@Override
	public String toString() {
		String temp = "";
		
		temp += "To: " + recipientName + "\n";
		temp += "From: " + senderName + "\n";
		temp += "Message: " + messageBody;
		
		return temp;
	}
}
