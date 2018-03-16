package com.miaxis.btfingerprinterlib.utils;

/**
 * Created by xu.nan on 2018/3/12.
 */

public interface ProtocolOrderCode {

    byte PROTOCOL_HEAD = 0x02;
    byte PROTOCOL_END = 0x03;

    byte GET_FINGER = (byte) 0x98;
    byte CANCEL_GET_FINGER = (byte) 0x91;
    byte PRINT = (byte) 0xA0;
    byte SCROLL_PAPER = (byte) 0xA1;

    byte DEV_OPEN_CLOSE_LED = 0x3C;     //led on off  1 byte 1-on，0-off。
    byte CMD_GET_DEVICE_INFO = 0x09;    //Read device version number
    byte DEV_GET_DEVICE_SN = 0x0E;      //Read device serial number.
    byte CMD_GET_DEVICE_UUID = 0x40;    //Read the device UUID
    byte DEV_GET_IMAGE = (byte) 0x97;    //get finger image 300 * 400
    byte DEV_GET_TEE_UUID = (byte) 0x94;        //Get TEE UUID  zzGetDevZ32UUID
    byte DEV_GET_TEE_INFO = 0x34;       //Get the TEE version number

    byte DEV_GET_RANDOM_DATA = 0x41;    //get random data as session key for AES-256

    byte DEV_GEN_RSAKEY_PAIR = 0x47;    //Generating the RSA key pair

    byte DEV_GET_PUBLICK_KEY = 0x70;    //Get Public key from device

    byte DEV_GET_BASE64ENCODE = 0x57;   //BASE64 encoding of input data


    byte DEV_SET_SESSION_KEY = 0x72;    //Set the session key of the communication

    byte DEV_SET_MODE = (byte) 0x92;   //Set device mode L0 L1
    byte DEV_GET_MODE = (byte) 0x93;   //get device mode L0 L1

    byte DEV_EXPORT_RSA_PUBKEY = 0x53;  //Export RSA public key

    byte DEV_IMPORT_CERTIFICATE = 0x42; //import certificate
    byte DEV_EXPORT_CERTIFICATE = 0x43; //Export certificate

    byte DEV_SET_ENCRYPT_MODE_L0 = (byte) 0x95;    //Set the communication encryption method for HOST and DEVICE under Level 0 mode.
    byte DEV_GET_ENCRYPT_MODE_L0 = (byte) 0x96;    //Get Level 0 interface communication mode

    byte DEV_RSA_SIGN_DATA = 0x54;

    byte DEV_AESGCM_ENCRYPT = 0x58;     //AESGCM group encrypt
    byte DEV_AESGCM_DECRYPT = 0x59;     //AESGCM group decrypt

    byte DEV_GEN_DIGEST = 0x46;         //generate digest



}