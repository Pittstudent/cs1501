import java.util.*;
import java.io.*;
import java.net.*;
import java.math.*;
import java.security.SecureRandom;

public class Add128 implements SymCipher{

	private byte[] key;

	public Add128(){
		Random rand = new SecureRandom();
		key = new byte[128];
		rand.nextBytes(key);
	}

	public Add128(byte [] setKey){
		key = setKey;
	}

	public byte [] getKey(){
		return key;
	}	

	public byte [] encode(String S){

		byte [] msg = S.getBytes();
		byte [] encMsg = new byte[msg.length];
		int j = 0;

		for(int i = 0; i < msg.length; i++){
			if(j == key.length){
				j = 0;
			}else{
				encMsg[i] = (byte)(msg[i] + key[j]);
				j++;
			}
		}
		return encMsg;
	}

	public String decode(byte [] bytes){
		
		byte [] decodedArray = new byte[bytes.length];
		int j = 0;

		for(int i = 0; i < bytes.length; i++){
			if(j == key.length){
				j = 0;
			}else{
				decodedArray[i] = (byte)(bytes[i] - key[j]); 
				j++;
			}
		}
		String decoded = new String(decodedArray);
		return decoded;
	}
}