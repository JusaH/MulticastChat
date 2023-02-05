/**
 * 
 */
package multicastchat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketAddress;

/**
 * @author Jussi Höykinpuro
 *
 */
public class MulticastChatSocket extends MulticastSocket {

	/**
	 * @throws IOException
	 */
	public MulticastChatSocket() throws IOException {
		super();
	}

	/**
	 * @param port
	 * @throws IOException
	 */
	public MulticastChatSocket(int port) throws IOException {
		super(port);
	}

	/**
	 * @param bindaddr
	 * @throws IOException
	 */
	public MulticastChatSocket(SocketAddress bindaddr) throws IOException {
		super(bindaddr);
		
	}
	
	public byte[] receivePacket (DatagramPacket p) throws IOException {
		super.receive(p);
		byte[] data = p.getData();
		return data;

	}
	
	public void send(byte[] header, InetAddress address, int port) throws IOException {
		DatagramPacket p = new DatagramPacket(header,header.length,address,port);
		super.send(p);
	}

}
