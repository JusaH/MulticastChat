package multicastchat;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;

public class TextMessage extends Message{
	public TextMessage(byte[] data) {
		try {
			this.parseMessage(data);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void parseMessage(byte[] data) throws UnsupportedEncodingException {
		this.setVersion(data[0]>>4);
		
		int year = ((data[2] & ~(0x80))<<4) | (data[3] >> 4);
		int month = (data[1] << 1 | data[2] >> 7) & 0x0F;
		int day = data[1]>>3; 
		this.setDate(new Date(year,month,day));
	
        int clientNameLength = data[4];
        int userNameLength   = data[5 + clientNameLength];
        int messageLength    = data[6 + clientNameLength+userNameLength];
		byte[] temp = new byte[data[5+data[4]]];
		
		for(int i=0;i<temp.length;i++) {
			temp[i]=data[5+data[4]+1+i];
		}
			
	    this.setUserName(new String(temp,"UTF-8"));
	    
		temp = new byte[data[4]];
		for(int i=0;i<temp.length;i++) {
			temp[i]=data[5+i];
		}
			
		this.setClientName(new String(temp, "UTF-8"));
		
		//this code gets the text from data into a separate array
		//starting index is the position of messagelength + 1
		int startingIndex = 6 + clientNameLength+userNameLength +1;
		byte[] textMessage = Arrays.copyOfRange(data, startingIndex, startingIndex + messageLength);
		this.setText(new String(textMessage, StandardCharsets.UTF_8));
		
	}

}
