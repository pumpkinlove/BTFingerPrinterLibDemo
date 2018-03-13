package com.miaxis.btfingerprinterlib.utils.cipher;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class GetX509PubKey {
	
	/**
	 * Reverse bytes 
	 * */
	public static void  Inv_Bytes(byte[] data)
	{
		int datalen = data.length;
		byte[] temp = new byte[datalen];
		
		for (int i = 0; i < datalen; i++) {
			temp[i]= data[datalen-i-1];
		}
		
		System.arraycopy(temp, 0,data ,0,datalen);
	}
	
	/**
	 * byte[] x509CertificateData IN int x509Len IN byte[] OutRSAPubKeyData OUT
	 * return PublicKey Length
	 * */
	public static byte[] ExtractPubKey(byte[] x509CertificateData, int x509Len) {
		CertificateFactory cf = null;
		try {
			cf = CertificateFactory.getInstance("X.509");
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		java.security.cert.Certificate c = null;
		try {
			InputStream in = new ByteArrayInputStream(x509CertificateData);
			c = cf.generateCertificate(in);
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		X509Certificate t = (X509Certificate) c;
		// for test other dn subject
//		System.out.println("VERSION: " + t.getVersion());
//		System.out.println("SN: " + t.getSerialNumber().toString(16));
//		System.out.println("SUBJECT: " + t.getSubjectDN());
//		System.out.println("ISSUER:" + t.getIssuerDN());
//		System.out.println("NOBEFORE: " + t.getNotBefore());
//		System.out.println("NOAFTER: " + t.getNotAfter());
//		System.out.println("SIGALG: " + t.getSigAlgName());
//		byte[] sig = t.getSignature();
//		System.out.println("SIGDATA: " + new BigInteger(sig).toString(16));
				
		// change java publickey to dev key
		return JavaPubKeyToDeviceRSAPubKey((RSAPublicKey) t.getPublicKey());
	}
	
	public static String ExtractExpirationDate(byte[] x509CertificateData, int x509Len) {
		CertificateFactory cf = null;
		try {
			cf = CertificateFactory.getInstance("X.509");
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		java.security.cert.Certificate c = null;
		try {
			InputStream in = new ByteArrayInputStream(x509CertificateData);
			c = cf.generateCertificate(in);
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		X509Certificate t = (X509Certificate) c;
		
		SimpleDateFormat ciDateFormat = new SimpleDateFormat("yyyyMMdd");
		ciDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		Date date = t.getNotAfter();
		String certificateIdentifier = ciDateFormat.format(date);
		
		// change java publickey to dev key
		return certificateIdentifier;//JavaPubKeyToDeviceRSAPubKey((RSAPublicKey) t.getPublicKey());		
	}
		
	
	public static byte[] JavaPubKeyToDeviceRSAPubKey(RSAPublicKey pk)
	{		
		//define
		byte[] modulusbyte =new byte[256];
		byte[] exponentbyte =new byte[256];
		byte[] DeviceRSAPubKey =new byte[514];
		
		//new RSAPublicKeySpec use Modulus and Exponent
		RSAPublicKeySpec spec = new RSAPublicKeySpec(pk.getModulus(), pk.getPublicExponent());
		
		//this get pubkey is 0x00+128byte modulus				
		System.arraycopy(pk.getModulus().toByteArray(), 1,modulusbyte ,0, pk.getModulus().toByteArray().length-1);
		 			
		//this get public is exponent
		System.arraycopy(pk.getPublicExponent().toByteArray(),0,exponentbyte ,0, pk.getPublicExponent().toByteArray().length);
			
		//if modulusbyte.length is  257 , rsa bitlen is 2048 
		//if modulusbyte.length is  129 , rsa bitlen is 1024
		int bitlen = (pk.getModulus().toByteArray().length-1)*8;
		
		//fill bitlens 
		if (bitlen==2048) {
			DeviceRSAPubKey[0]=0x00;
			DeviceRSAPubKey[1]=0x08;
		}else
		{
			DeviceRSAPubKey[0]=0x00;
			DeviceRSAPubKey[1]=0x04;
		}
		
		//Reverse bytes 
		Inv_Bytes(exponentbyte);
				
		//fill modulus 
		System.arraycopy(modulusbyte, 0,DeviceRSAPubKey ,2, bitlen/8);
		
		//file exponent
		System.arraycopy(exponentbyte,0,DeviceRSAPubKey, 2+256, exponentbyte.length);
		
		return DeviceRSAPubKey;
	}

}
