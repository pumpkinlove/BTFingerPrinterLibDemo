package com.miaxis.btfingerprinterlib;

import android.app.Application;

import com.miaxis.btfingerprinterlib.utils.cipher.RSAEncrypt;

/**
 * Created by xu.nan on 2018/3/7.
 */

public class MxBlePrinterApi {

    private static final String TAG = "MxBlePrinterApi";
    private BleComm bleComm;
    private Application app;
    private static volatile MxBlePrinterApi api;

    public static MxBlePrinterApi getInstance(Application app) {
        if (api == null) {
            api = new MxBlePrinterApi(app);
        }
        return api;
    }

    private MxBlePrinterApi(Application app) {
        this.app = app;
        bleComm = new BleComm(app);
    }

    public int initBle() {
        return bleComm.initBle();
    }

    public void connect(String mac, BleComm.ConnectCallBack callBack) {
        bleComm.connect(mac, callBack);
    }

    public void print(String content, BleComm.PrintCallBack callBack) {
        bleComm.print(content, callBack);
    }

    public void scrollPaper(int num, BleComm.ScrollPaperCallBack callBack) {
        bleComm.scrollPaper(num, callBack);
    }

    public void getDeviceSN(BleComm.CommonCallBack callBack) {
        bleComm.getDeviceSN(callBack);
    }

    public void getFingerImage(BleComm.CommonCallBack callBack) {
        bleComm.getFingerImage(callBack);
    }

    public void getDeviceUUID(BleComm.CommonCallBack callBack) {
        bleComm.getDeviceUUID(callBack);
    }

    public void getDeviceInfo(BleComm.CommonCallBack callBack) {
        bleComm.getDeviceInfo(callBack);
    }

    public void lampControl(int lampOnOff, BleComm.CommonCallBack callBack) {
        bleComm.lampControl(lampOnOff, callBack);
    }

    public void setDeviceMode(int mode, BleComm.CommonCallBack callBack) {
        bleComm.setDeviceMode(mode, callBack);
    }

    public void base64Encode(byte[] inputData, BleComm.CommonCallBack callBack) {
        bleComm.base64Encode(inputData, callBack);
    }

    public void tmfGenerateRSAKeyPair(BleComm.CommonCallBack callBack) {
        bleComm.generateRSAKeyPair(callBack);
    }

    public void tmfGetRSAPublicKey(int keyNum, BleComm.CommonCallBack callBack) {
        bleComm.getRSAPublicKey(keyNum, callBack);
    }
    
    public void tmfGenerateDigest() {
        // TODO: 2018/3/13  
    }

    public void tmfGenerateSessionKey(BleComm.CommonCallBack callBack) {
        bleComm.getEncryptMode(callBack);
    }

    public void tmfAesGcmNoPaddingEncryption() {
        // TODO: 2018/3/13
    }

    public void tmfAesGcmNoPaddingDecryption() {
        // TODO: 2018/3/13
    }

    public void tmfDigitalSignatureRSA2048withSHA256() {
        // TODO: 2018/3/13
    }

    /**
     * Asymmetric encryption using RSA-2048 when x509 certificate data is provided as an input from host.
     * @param algorithmType         0 for RSA/ECB/PKCS1Padding
     * @param x509CertificateData   x509 Certificate Data
     * @param x509Len               x509 Certificate Data length
     * @param inputContent          input data
     * @param inputContentLen       input data length
     * @param outputContent         output data
     * @param outputContentLen      output data length
     * @return 0 for Success, otherwise errorCode.
     */
    public int tmfAsymmetricEncryptionX509(byte algorithmType,
                                           byte[] x509CertificateData,
                                           int x509Len,
                                           byte[] inputContent,
                                           int inputContentLen,
                                           byte[] outputContent,
                                           int[] outputContentLen) {
        return RSAEncrypt.encryptX509(x509CertificateData,x509Len,
                inputContent,inputContentLen,outputContent,outputContentLen);
    }

    public void tmfWriteFileToDevice() {
        // TODO: 2018/3/13
    }

    public void tmfReadFileFromDevice() {
        // TODO: 2018/3/13  
    }

    public void tmfSignData() {
        // TODO: 2018/3/13
    }

    public void tmfGetAlgoVersion() {
        // TODO: 2018/3/13  
    }

    public void tmfGetTzFMR() {
        // TODO: 2018/3/13  
    }

    public void tmfFingerMatchFMR() {
        // TODO: 2018/3/13
    }

    public void tmfGetSecureProcessorUUID(BleComm.CommonCallBack callBack) {
        bleComm.getTEEUUID(callBack);
    }

    public void tmfGetSecureProcessorVersion(BleComm.CommonCallBack callBack) {
        bleComm.getTEEVersion(callBack);
    }

    public void tmfDeleteFileFromDevice() {
        // TODO: 2018/3/13  
    }

    public void tmfSetUSBEncryption() {
        // TODO: 2018/3/13
    }

    public void getFinger(BleComm.GetFingerCallBack callBack) {
        bleComm.getFinger(callBack);
    }

    public void cancelDevice(BleComm.CancelFingerCallBack callBack) {
        bleComm.cancelDevice(callBack);
    }

}
