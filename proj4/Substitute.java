import java.util.*;
import java.io.*;
import java.net.*;
import java.math.*;
import java.security.SecureRandom;

public class Substitute implements SymCipher
{
    private byte[] key;

    public Substitute(){
        key = new byte[256];
        for(int i = 0; i < key.length; i++){
            key[i] = (byte)i; 
        }

        for(int i = 0; i < 512; i++){
            Random rand1 = new Random();
            Random rand2 = new Random();
            int randNum1 = rand1.nextInt(256);
            int randNum2 = rand2.nextInt(256);
            byte temp;
            temp = key[randNum1];
            key[randNum1] = key[randNum2];
            key[randNum2] = temp;
        }
    }
    public Substitute( byte [] setKey){
        key = setKey;
    }

    public byte [] getKey(){
        return key;
    }   

    public byte [] encode(String S){
        byte [] msg = S.getBytes();
        byte [] encMsg = new byte[msg.length];
        
        for(int i = 0; i < msg.length; i++){
            encMsg[i] = key[(int)(msg[i] & 0xff)];
        }
        return encMsg;
    }

    public String decode(byte [] bytes){

        byte[] inverseKey = new byte[256];
        for(int i = 0; i < key.length; i++){
            inverseKey[(int)(key[i] & 0xff)] = (byte)i;
        }

        byte [] decodedArray = new byte[bytes.length];
        for(int i = 0; i < bytes.length; i++){
            decodedArray[i] = inverseKey[(int)(bytes[i] & 0xff)]; 
        }

        String decoded = new String(decodedArray);
        return decoded;
    }
}
