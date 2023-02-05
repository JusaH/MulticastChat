package multicastchat;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;

public class UpdateMessage extends Message{
	private LinkedList<String> users = new LinkedList<>();
	
	public UpdateMessage(byte[] data) {
		try {
			this.parseMessage(data);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
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
		
		//index of the first names length
        int startingPoint=7+clientNameLength+userNameLength;
        //i is the index of first users first character
        int i = startingPoint+1;
        while(data[i]!=0){
        	//gets the userName in bytes starting from index i and ending at index i+ usernames length
        	byte[] nameInBytes = Arrays.copyOfRange(data, i, i+(int)data[i-1]);
            users.add(new String(nameInBytes,"UTF8"));
            //moves i to the index of the first character of the next name
            i += data[i-1]+1;
        }
     
	}
	
	public LinkedList<String> getUsers(){
		return this.users;
	}

}
