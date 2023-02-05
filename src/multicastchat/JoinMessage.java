/**
 * 
 */
package multicastchat;

import java.io.UnsupportedEncodingException;

/**
 * @author jussi
 *
 */
public class JoinMessage extends Message{
	
	public JoinMessage(byte[] data) {
		try {
			super.parseMessage(data);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
