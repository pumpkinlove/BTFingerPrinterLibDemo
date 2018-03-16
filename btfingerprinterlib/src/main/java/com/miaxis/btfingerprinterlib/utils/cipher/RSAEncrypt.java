package com.miaxis.btfingerprinterlib.utils.cipher;


import com.miaxis.btfingerprinterlib.utils.MXErrCode;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

//http://blog.csdn.net/wangqiuyun/article/details/42143957/

public class RSAEncrypt {

	public static int encryptX509(byte[] x509CertificateData, byte[] inputContent, byte[] outputContent, int[] outputContentLen) {
		CertificateFactory cf;
		try {
			cf = CertificateFactory.getInstance("X.509");
		} catch (CertificateException e) {
			return MXErrCode.ERR_X509;
		}
		java.security.cert.Certificate c;
		try {
			InputStream in = new ByteArrayInputStream(x509CertificateData);
			c = cf.generateCertificate(in);
		} catch (CertificateException e) {
			return MXErrCode.ERR_X509;
		}

		X509Certificate t = (X509Certificate) c;

		PublicKey pk = t.getPublicKey();
		byte[] pkenc = pk.getEncoded();
		byte[] output = null;
		try {
			output = RSAEncrypt.encrypt((RSAPublicKey) pk, inputContent);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (output == null) {
			return MXErrCode.ERR_PUBKEY_ENCRYPT;
		}
		for (int i = 0; i < output.length; i++) {
			outputContent[i] = output[i];
		}
		outputContentLen[0] = output.length;
		return 0;
	}

	/**
	 * 公钥加密过程
	 * 
	 * @param publicKey
	 *            公钥
	 * @param plainTextData
	 *            明文数据
	 * @return
	 * @throws Exception
	 *             加密过程中的异常信息
	 */
	public static byte[] encrypt(RSAPublicKey publicKey, byte[] plainTextData)
			throws Exception {
		if (publicKey == null) {
			throw new Exception("加密公钥为空, 请设置");
		}
		Cipher cipher = null;
		try {
			// 使用默认RSA
			cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			// cipher= Cipher.getInstance("RSA", new BouncyCastleProvider());
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			byte[] output = cipher.doFinal(plainTextData);
			return output;
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此加密算法");
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
			return null;
		} catch (InvalidKeyException e) {
			throw new Exception("加密公钥非法,请检查");
		} catch (IllegalBlockSizeException e) {
			throw new Exception("明文长度非法");
		} catch (BadPaddingException e) {
			throw new Exception("明文数据已损坏");
		}
	}

	/**
	 * 私钥解密过程
	 * 
	 * @param privateKey
	 *            私钥
	 * @param cipherData
	 *            密文数据
	 * @return 明文
	 * @throws Exception
	 *             解密过程中的异常信息
	 */
	public static byte[] decrypt(RSAPrivateKey privateKey, byte[] cipherData)
			throws Exception {
		if (privateKey == null) {
			throw new Exception("解密私钥为空, 请设置");
		}
		Cipher cipher = null;
		try {
			// 使用默认RSA
			cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding"); // RSA
																	// RSA/ECB/PKCS1Padding
			// cipher= Cipher.getInstance("RSA", new BouncyCastleProvider());
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			byte[] output = cipher.doFinal(cipherData);
			return output;
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此解密算法");
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
			return null;
		} catch (InvalidKeyException e) {
			throw new Exception("解密私钥非法,请检查");
		} catch (IllegalBlockSizeException e) {
			throw new Exception("密文长度非法");
		} catch (BadPaddingException e) {
			throw new Exception("密文数据已损坏");
		}
	}

	/**
	 * @author chen.gs
	 * @category 读取文件数据到byte数组
	 * @param filepath
	 *            - 文件路径
	 * @return byte数组
	 * */
	public static byte[] ReadData(String filepath) {
		File f = new File(filepath);
		if (!f.exists()) {
			return null;
		}
		ByteArrayOutputStream bos = new ByteArrayOutputStream((int) f.length());
		BufferedInputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(f));
			int buf_size = 1024;
			byte[] buffer = new byte[buf_size];
			int len = 0;
			while (-1 != (len = in.read(buffer, 0, buf_size))) {
				bos.write(buffer, 0, len);
			}
			return bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				bos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * @author chen.gs
	 * @category 保存数据为文件
	 * @param filepath
	 *            - 文件路径 buffer - 数据缓存 size - 数据长度
	 * @return 0 - 成功 其他 - 失败
	 * */
	public static int SaveData(String filepath, byte[] buffer, int size) {
		File f = new File(filepath);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
		try {
			fos.write(buffer, 0, size);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -2;
		}
		return 0;
	}

	public static byte[] JavaPubKeyToDeviceRSAPubKey(RSAPublicKey pk) {
		// define
		byte[] modulusbyte = new byte[256];
		byte[] exponentbyte = new byte[256];
		byte[] DeviceRSAPubKey = new byte[514];

		// new RSAPublicKeySpec use Modulus and Exponent
		RSAPublicKeySpec spec = new RSAPublicKeySpec(pk.getModulus(),
				pk.getPublicExponent());

		// this get pubkey is 0x00+128byte modulus
		System.arraycopy(pk.getModulus().toByteArray(), 1, modulusbyte, 0, pk
				.getModulus().toByteArray().length - 1);

		// this get public is exponent
		System.arraycopy(pk.getPublicExponent().toByteArray(), 0, exponentbyte,
				0, pk.getPublicExponent().toByteArray().length);

		// if modulusbyte.length is 257 , rsa bitlen is 2048
		// if modulusbyte.length is 129 , rsa bitlen is 1024
		int bitlen = (pk.getModulus().toByteArray().length - 1) * 8;

		// fill bitlens
		if (bitlen == 2048) {
			DeviceRSAPubKey[0] = 0x00;
			DeviceRSAPubKey[1] = 0x08;
		} else {
			DeviceRSAPubKey[0] = 0x00;
			DeviceRSAPubKey[1] = 0x04;
		}

		// Reverse bytes
		Inv_Bytes(exponentbyte);

		// fill modulus
		System.arraycopy(modulusbyte, 0, DeviceRSAPubKey, 2, bitlen / 8);

		// file exponent
		System.arraycopy(exponentbyte, 0, DeviceRSAPubKey, 2 + 256,
				exponentbyte.length);

		return DeviceRSAPubKey;
	}

	/**
	 * This Function is Change To JAVA RSAPublicKey Where From Device Out
	 * RSApublickey
	 * */
	public static RSAPublicKey DeviceRSAPubKeyToJavaPubKey(
			byte[] DeviceRSAPubKey) {
		PublicKey pubKey = null;
		KeyFactory keyFactory = null;
		byte[] modulusbyte = new byte[256];
		// copy device rsa publickey modulusbytes
		System.arraycopy(DeviceRSAPubKey, 2, modulusbyte, 0, 256);

		// change modulusbytes to hexstring
		String szModulus = bytesToHexString(modulusbyte);

		// exponent string
		String szexponent = "10001";

		// change to biginteger
		BigInteger modulus = new BigInteger(szModulus, 16);
		BigInteger exponent = new BigInteger(szexponent, 16);

		// gen publickeyspec
		RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(modulus, exponent);

		// instance RSA

		try {
			keyFactory = KeyFactory.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			pubKey = keyFactory.generatePublic(pubKeySpec);
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return (RSAPublicKey) pubKey;
	}

	/**
	 * Reverse bytes
	 * */
	public static void Inv_Bytes(byte[] data) {
		int datalen = data.length;
		byte[] temp = new byte[datalen];

		for (int i = 0; i < datalen; i++) {
			temp[i] = data[datalen - i - 1];
		}

		System.arraycopy(temp, 0, data, 0, datalen);
	}

	/**
	 * This Function is Change byte[] to HexString
	 * */
	public static final String bytesToHexString(byte[] bArray) {
		StringBuffer sb = new StringBuffer(bArray.length);
		String sTemp;
		for (int i = 0; i < bArray.length; i++) {
			sTemp = Integer.toHexString(0xFF & bArray[i]);
			if (sTemp.length() < 2)
				sb.append(0);
			sb.append(sTemp.toUpperCase());
		}
		return sb.toString();
	}
}
