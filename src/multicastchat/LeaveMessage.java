package multicastchat;

import java.io.UnsupportedEncodingException; 

public class LeaveMessage extends Message{
	
	public LeaveMessage(byte[] data) {
		try {
			super.parseMessage(data);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
