package com.miaxis.btfingerprinterlib.utils;

/**
 * Created by xu.nan on 2018/3/13.
 */

public interface MXErrCode {

    int ERR_OK            	= 0;

    int ERR_IOSEND			= 10;	//IO通信发送数据包失败
    int ERR_IORECV			= 11;	//IO通信接收数据包失败
    int ERR_HEAD_FLAG		= 12;	//包标识错误
    int ERR_END_FLAG		= 13;	//包标识错误
    int ERR_CRC		  		= 14;	//数据校验错误
    int ERR_DATA_LEN		= 15;  //数据长度有误
    int ERR_IMG_WDITH		= 16;  //图像宽度有误
    int ERR_IMG_HEIGHT		= 17;  //图像高度有误
    int ERR_MEMORY_OVER 	= 18;	//内存溢出
    int ERR_KEY_LEN      	= 19;	//对称加解密密钥长度不是32字节
    int ERR_ENDATA_LEN      = 20;	//对称加密数据长度不是16字节的整数倍
    int ERR_DEV_MODE      	= 21;	//App Mode can’t up image
    int ERR_PACKAGE_STATUS  = 22;	//Package status error
    int ERR_BASE64          = 23;   //Base64 encode error
    int ERR_X509            = 24;  //X509证书有错
    int ERR_PUBKEY_ENCRYPT  = 25;  //RSA公钥加密失败

    int ERR_RESIDUAL_FINFER = 30;  //残留指纹
    int ERR_FINFER_NUM		= 31;  //指纹数量超出范围
    int ERR_FINFER_TYPE		= 32;  //指纹数量超出范围

    int ERR_CAPTURE_FP		= 34;  //采集图像失败
    int ERR_DIGEST			= 35;  //摘要失败
    int ERR_SESSIONKEY		= 36;  //产生会话密钥失败
    int ERR_AESGCM			= 37;  //对称加密失败
    int ERR_CANCEL			= 38;  //取消操作
    int ERR_MODEL 			= 39;

    int ERR_IMAGE_FAIL		= 40;  //读取图像失败
    int ERR_TIME_OUT		= 41;  //指令超时
    int ERR_NO_SESSION_KEY  = 42;  //无通讯会话密钥
    int ERR_MATCH_FAIL	    = 43;
    int ERR_EXTRACT_FEATURE_FAIL = 44;

    int ERR_NO_DEV		  	= 100;   //无设备
    int ERR_NO_PERMISION  	= 101;   //无访问权限
    int ERR_NO_CONTEXT    	= 102;   //无Context

    int ERR_OPEN			= ERR_NO_DEV;    //打开失败


    int ERR_FAILED_TEE		= 161;
    int ERR_KEY_LEN_TEE		= 162;
    int ERR_DATA_LEN_TEE	= 163;
    int ERR_KEY_ENCRYPT_TEE	= 164;
    int ERR_KEY_DECRYPT_TEE	= 165;
    int ERR_NO_CERT_TEE		= 166;
    int ERR_NO_KEY_TEE		= 167;
    int ERR_NO_PIN_TEE		= 168;
    int ERR_INVALID_PARAMETER_TEE	= 169;
    int ERR_VERIFIED_FAILED_TEE		= 170;
    int ERR_NO_CMD_TEE		= 171;
    int ERR_NO_ALG_TEE		= 172;

    int ERR_VERIFY_APP_TEE	= 205;
}
