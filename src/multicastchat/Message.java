/**
 * 
 */
package multicastchat;

import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * @author jussi
 *
 */

public abstract class Message {
	private int version = 2;
	private String clientName = "testiClient";
	private String userName = "testiUser";
	@SuppressWarnings("deprecation")
	private Date date = new Date(2000,12,20);
	private String text;
	
	
	@SuppressWarnings("deprecation")
	/**
	 * parses message info from byte array. Leaves some of the job for the inheriting class 
	 * @param data
	 * @throws UnsupportedEncodingException
	 */
	public void parseMessage(byte[] data) throws UnsupportedEncodingException {
		this.setVersion(data[0]>>4);
		
		int year = ((data[2] & ~(0x80))<<4) | (data[3] >> 4);
		int month = (data[1] << 1 | data[2] >> 7) & 0x0F;
		int day = data[1]>>3; 
		this.setDate(new Date(year,month,day));
	
        //int clientNameLength = data[4];
        //int userNameLength   = data[5 + clientNameLength];
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
		
	}

	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}


	public int getVersion() {
		return version;
	}


	public void setVersion(int version) {
		this.version = version;
	}


	public String getClientName() {
		return clientName;
	}


	public void setClientName(String clientName) {
		this.clientName = clientName;
	}


	public String getText() {
		return text;
	}


	public void setText(String text) {
		this.text = text;
	}


	public Date getDate() {
		return date;
	}


	public void setDate(Date date) {
		this.date = date;
	}


}
