package multicastchat;

import java.nio.charset.StandardCharsets;

public class utils {
	
    public static byte[] stringToUtf8(String merkkijono){
    	return merkkijono.getBytes(StandardCharsets.UTF_8);
    }
}
