package com.miaxis.btfingerprinterlib.utils.cipher;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Digest {
	/**
	 * This Function is JAVA Digest For SHA-256 
	 * */
	public static byte[] JAVADigest(byte[] inputData)
	{ 
		//Create security Object MessageDigest Use "SHA-256"
        MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}          
        messageDigest.update(inputData);  
        byte byteBuffer[] = messageDigest.digest();  		        
        return byteBuffer;
	}
}


