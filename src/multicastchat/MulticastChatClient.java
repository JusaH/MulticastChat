/**
 * 
 */
package multicastchat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.LinkedList;
/**
 * @author jussi
 *
 */
public class MulticastChatClient {
	
	private String userName="jussi";
	private String clientName = "testi";
	private InetAddress group;
	private int port=42000;
	private MulticastChatSocket socket;
	private boolean exit = false;
	private static BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
	private int month = 1;
	private int day   = 1;
	private int year  = 2000;
	private int version = 2;
	private boolean latest = true;
	private LinkedList<String> users = new LinkedList<>(); 
	

	/**
	 * 
	 */
	public MulticastChatClient() {
		
	}
	
	/**
	 * 
	 * @param user User who is connecting to socket 
	 * @param port Port that the socket operates in
	 * @param ip Ip that the group operates in
	 * @return message to UI
	 */
	public void connect() {
    	
    	
    	
    	try {
    		this.group=InetAddress.getByName("239.0.0.1");
    		this.socket = new MulticastChatSocket(port);
    		joinGroup();
    		
    	}catch (Exception e) {
    		System.out.println(e.getMessage());
    	}
    	
    	try {
			socket.setSoTimeout(2000);
		} catch (SocketException e) {
			System.out.println("ei uusia viestejä");
		}
    	listen();
		
	}
	
	@SuppressWarnings("deprecation")
	private void joinGroup() throws IOException {
	this.users.add(this.userName);
	socket.joinGroup(this.group);
	
	socket.send(asetaTavut("/join",1), this.group, port);
	}

	/**
	 * method that is running while you are connected to a group
	 */
	private void listen() { 
		
		//loops until clients exit-attribute is set true. Then client moves on to disconnect() method 
		while(!exit) {
			try {
				if(!input.ready())
					try {
						System.out.println(handlePost(socket.receivePacket(new DatagramPacket(new byte[256],256))));
					} catch (IOException e) {
						//System.out.println("ei uusia viestejä!");
					}
				else {
					String viesti = input.readLine();
					System.out.println("lähetettävä viesti:" + viesti);
					send(viesti,3);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
				}
		
		disconnect();
	}

	/**
	 * method for handling sending correct data to the socket
	 * @param message 
	 * @param type 1=join, 2=leave, 3=text, 4=update
	 */
	private void send(String message,int type) {
		//if you write exit to the console, you will disconnect
		if(message.equals("exit")){
			this.exit=true;
		}
		
		
		else {
			
			try {
				socket.send(asetaTavut(message,type),this.group,this.port);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		
		
		
		
	}

	@SuppressWarnings("deprecation")
	/**
	 * Handles disconnect from the chat
	 */
	private void disconnect() {
		try {
			socket.send(asetaTavut("/leave",2),this.group,port);
			socket.leaveGroup(group);
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();	
		}
	}

	/**
	 * Method for forwarding the received message to a correct handler
	 * @param data received message
	 * @return the message in a proper String
	 * @throws UnsupportedEncodingException
	 */
    public String handlePost(byte[] data) throws UnsupportedEncodingException {
    	
    	int messageType = (data[0] & 0xf);
    	
    	if(messageType == 1) {
    		return this.handleJoinPost(data);
    	}
    	
    	if(messageType == 2) {
    		return this.handleLeavePost(data);
    	}
    	
    	if(messageType == 3) {
    		return this.handleTextPost(data);
    	}
    	
    	if(messageType == 4) {
    		return this.handleUpdatePost(data);
    	}
    	
    	return "error; wrong type of message";
    }
    
    /**
     * Method for handling received update message (type 4)
     * @param data
     * @return new users
     */
    private String handleUpdatePost(byte[] data) {
    	
		UpdateMessage update = new UpdateMessage(data);
		this.users = update.getUsers();
		return "\nCurrent users:" + this.users.toString();
	}

    /**
     * method for handling received text message (type 3)
     * @param data
     * @return the text as a String
     */
	private String handleTextPost(byte[] data) {
    	TextMessage text = new TextMessage(data);
    	return text.getUserName() + ": " + text.getText();
	}

	/**
	 * method for handling received leave message (type2)
	 * @param data
	 * @return "user left"
	 */
	private String handleLeavePost(byte[] data) {
    	LeaveMessage leave = new LeaveMessage(data);
    	
    	//Removes the leaving user from userlist
    	String leavingUser = leave.getUserName();
    	for(String user : this.users) {
    		if(user.equals(leavingUser)) {
    			users.remove(user);
    			break;
    		}
    	}
    	
    	//makes sure somebodys latest parameter is true incase the latest user left
    	if(users.getFirst().equals(this.userName)) {
    		this.latest=true;
    	}
    	return "user " + leave.getUserName() + " left.\nCurrent users: " + this.users.toString();
	}

	/**
     * Handles receiving a join post
     * @param data is the post
     * @return Printable message in appropriate form
     */
    private  String handleJoinPost(byte[] data) {
		JoinMessage join = new JoinMessage(data);
		
		if(join.getUserName().equals(this.userName))
			return "succesfully joined" + this.group + ".\nWrite !exit to leave safely.";
		
		users.addFirst(join.getUserName());
		
		//if this user was the previous to join, their job is to send an update message.
		if(this.latest == true) {
				send("",4);
				latest = false;
		}		
		
		return "user " + join.getUserName() + " joined";
		
	}

    /**
     * sets the list of users into one string for sending in update message
     * @return string to be sent
     */
	private String usersToText() {
        StringBuilder text = new StringBuilder();
        
        for(String user : this.users) {
        	text.append(String.valueOf(user.length()));
        	text.append(utils.stringToUtf8(user));
        }
		return text.toString();
	}
	
	/// <summary>
    /// Funktio palauttaa tavu-taulukon, missä on Multicastchat protokollan
    /// kehysrakenteen kenttien mukaiset informaatiot
    /// </summary>
    /// <returns>parametreista muodostetut tavut</returns>
    public byte[] asetaTavut(String teksti, int tyyppi)
    {
    	
    	int viesti = tyyppi;
       
        int clientLength = utils.stringToUtf8(clientName).length;
        int userLength = utils.stringToUtf8(userName).length;
        int dataLength;
        if(tyyppi == 4) {
        	int apu = users.size();
        	for(String user : this.users) {
        		apu+=user.length();
        	}
        	dataLength=apu;
        }
        else dataLength = utils.stringToUtf8(teksti).length;
        
        int constLength = 7;
        byte[] tavut = new byte[constLength + clientLength + userLength + dataLength];
        
        tavut[0] = (byte)(version << 4 | viesti);
        tavut[1] = (byte)(day<<3 | month>>1);
        tavut[2] = (byte)(month<<7 | year >> 4);
        tavut[3] = (byte)(year << 4);
        tavut[4] = (byte)clientLength;

        
        byte[] merkkijonoUtf8 = utils.stringToUtf8(clientName);
        for(int i=0;i<clientLength;i++){
            tavut[5+i] = merkkijonoUtf8[i];
        }
        
        
        tavut[5+clientLength]=(byte)userLength;
        
        merkkijonoUtf8 = utils.stringToUtf8(userName);
        for(int i=0;i<userLength;i++){
            tavut[4+clientLength+2+i]= merkkijonoUtf8[i];
        }
        
        tavut[5+clientLength+userLength+1]=(byte)dataLength;
        
        if(tyyppi==4) {
        	int vakio = 6+clientLength+userLength; //indeksi,jossa on dataLength;
            tavut[vakio]=(byte)dataLength;
            int pituus=0;
            int monesNimi=0;
            while(monesNimi<users.size()){
                
                byte[] nimi = utils.stringToUtf8(users.get(monesNimi));
                tavut[vakio+pituus+1] = (byte)nimi.length;   
                for(int i=1;i<=tavut[vakio+1+pituus];i++){
                        tavut[vakio+pituus+1+i]=nimi[i-1];
                    }
                pituus+=nimi.length+1;
                monesNimi++;
                
                
            }
            
            return tavut;
        }

        merkkijonoUtf8 = utils.stringToUtf8(teksti);

        for(int i=0;i<dataLength;i++){
            tavut[constLength+clientLength+userLength+i]=merkkijonoUtf8[i];
        }

        
        
        return tavut;
    }

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MulticastChatClient client = new MulticastChatClient();
		client.userName = args[0];
		client.connect();

	}

}
