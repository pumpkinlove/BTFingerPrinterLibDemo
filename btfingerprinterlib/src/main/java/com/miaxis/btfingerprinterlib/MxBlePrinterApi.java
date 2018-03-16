package com.miaxis.btfingerprinterlib;

import android.app.Application;

import com.miaxis.btfingerprinterlib.utils.CodeUtil;
import com.miaxis.btfingerprinterlib.utils.ProtocolUtil;
import com.miaxis.btfingerprinterlib.utils.cipher.RSAEncrypt;

/**
 * Miaxis ble finger printer api
 * Created by xu.nan on 2018/3/7.
 */

public class MxBlePrinterApi {

    private static final String TAG = "MxBlePrinterApi";
    private BleComm bleComm;
    private static volatile MxBlePrinterApi api;

    public static MxBlePrinterApi getInstance(Application app) {
        if (api == null) {
            api = new MxBlePrinterApi(app);
        }
        return api;
    }

    private MxBlePrinterApi(Application app) {
        bleComm = new BleComm(app);
    }

    public int initBle() {
        return bleComm.initBle();
    }

    public void connect(String mac, BleComm.ConnectCallBack callBack) {
        bleComm.connect(mac, callBack);
    }

    public void print(String content, BleComm.CommonCallBack callBack) {
        bleComm.print(content, callBack);
    }

    public void scrollPaper(int num, BleComm.CommonCallBack callBack) {
        bleComm.scrollPaper(num, callBack);
    }

    public void getDeviceSN(BleComm.CommonCallBack callBack) {
        bleComm.getDeviceSN(callBack);
    }

    public void getFingerImage(final BleComm.CommonCallBack callBack) {
        bleComm.getDeviceMode(new BleComm.CommonCallBack() {
            @Override
            public void onSuccess(byte[] responseData) {
                int mode = responseData[0];
                if (mode != 0) {
                    callBack.onFailure("mode error");
                } else {
                    bleComm.getEncryptMode(new BleComm.CommonCallBack() {
                        @Override
                        public void onSuccess(byte[] responseData) {
                            int mode = responseData[0];
                            bleComm.setEncryptMode(mode);
                            if (mode == 1) {
                                bleComm.getPublicKey(new BleComm.CommonCallBack() {
                                    @Override
                                    public void onSuccess(byte[] responseData) {
                                        byte[] sessionKey = ProtocolUtil.getRandomNum(16);
                                        bleComm.setSessionKey(sessionKey);
                                        bleComm.downEncSessionKey(responseData, new BleComm.CommonCallBack() {
                                            @Override
                                            public void onSuccess(byte[] responseData) {
                                                bleComm.getFingerImage(callBack);
                                            }

                                            @Override
                                            public void onFailure(String errorMessage) {
                                                callBack.onFailure(errorMessage);
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailure(String errorMessage) {
                                        callBack.onFailure(errorMessage);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            callBack.onFailure(errorMessage);
                        }
                    });
                }
            }

            @Override
            public void onFailure(String errorMessage) {

            }
        });
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

    public void tmfGenerateRSAKeyPair(byte keyNum, BleComm.CommonCallBack callBack) {
        bleComm.generateRSAKeyPair(keyNum, callBack);
    }

    public void tmfGetRSAPublicKey(int keyNum, BleComm.CommonCallBack callBack) {
        bleComm.getRSAPublicKey(keyNum, callBack);
    }
    
    public void tmfGenerateDigest(byte bAlgType, byte[] inputData, BleComm.CommonCallBack callBack) {
        bleComm.tmfGenerateDigest(bAlgType, inputData, callBack);
    }

    public void tmfGenerateSessionKey(BleComm.CommonCallBack callBack) {
        bleComm.getEncryptMode(callBack);
    }

    public void tmfAesGcmNoPaddingEncryption(byte[] key,
                                             byte[] inputData,
                                             byte[] ivData,
                                             byte[] aadData,
                                             BleComm.CommonCallBack callBack) {
        bleComm.tmfAESGCMEncrypt(key, inputData, ivData, aadData, callBack);
    }

    public void tmfAesGcmNoPaddingDecryption(byte[] key,
                                             byte[] inputData,
                                             byte[] ivData,
                                             byte[] aadData,
                                             BleComm.CommonCallBack callBack) {
        bleComm.tmfAESGCMDecrypt(key, inputData, ivData, aadData, callBack);
    }

    public void tmfDigitalSignatureRSA2048withSHA256(byte keyNum,
                                                     byte[] inputData,
                                                     BleComm.CommonCallBack callBack) {
        bleComm.tmfDigitalSignatureRSA2048withSHA256(keyNum, inputData, callBack);
    }

    /**
     * Asymmetric encryption using RSA-2048 when x509 certificate data is provided as an input from host.
     * @param x509CertificateData   x509 Certificate Data
     * @param inputContent          input data
     * @param outputContent         output data
     * @param outputContentLen      output data length
     * @return 0 for Success, otherwise errorCode.
     */
    public int tmfAsymmetricEncryptionX509(byte[] x509CertificateData,
                                           byte[] inputContent,
                                           byte[] outputContent,
                                           int[] outputContentLen) {
        return RSAEncrypt.encryptX509(x509CertificateData,
                inputContent,outputContent,outputContentLen);
    }

    public void tmfWriteFileToDevice(byte iFileType, byte[] fileContent, int fileContentLen, BleComm.CommonCallBack callBack) {
        bleComm.tmfWriteFileToDevice(iFileType, fileContent, fileContentLen, callBack);
    }

    public void tmfReadFileFromDevice(byte iFileType, BleComm.CommonCallBack callBack) {
        bleComm.tmfReadFileFromDevice(iFileType, callBack);
    }

    public void tmfSignData(byte keyNum, byte[] inputData, BleComm.CommonCallBack callBack) {
        bleComm.signData(keyNum, inputData, callBack);
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

    public void tmfDeleteFileFromDevice(byte iFileType, BleComm.CommonCallBack callBack) {
        bleComm.tmfWriteFileToDevice(iFileType, null, 0, callBack);
    }

    public void tmfSetUSBEncryption(final int encryptFlag, final BleComm.CommonCallBack callBack) {
        bleComm.getPublicKey(new BleComm.CommonCallBack() {
            @Override
            public void onSuccess(byte[] publicKey) {       //514 bytes RSA public key
                byte[] sessionKey = ProtocolUtil.getRandomNum(16);
                bleComm.setSessionKey(sessionKey);
                bleComm.downEncSessionKey(publicKey, new BleComm.CommonCallBack() {
                    @Override
                    public void onSuccess(byte[] responseData) {
                        bleComm.setEncryptMode(encryptFlag, new BleComm.CommonCallBack() {
                            @Override
                            public void onSuccess(byte[] responseData) {
                                callBack.onSuccess(responseData);
                            }

                            @Override
                            public void onFailure(String errorMessage) {
                                callBack.onFailure(errorMessage);
                            }
                        });
                    }
                    @Override
                    public void onFailure(String errorMessage) {
                        callBack.onFailure(errorMessage);
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                callBack.onFailure(errorMessage);
            }
        });
    }

    public void getFinger(BleComm.CommonCallBack callBack) {
        bleComm.getFinger(callBack);
    }

    public void cancelDevice(BleComm.CommonCallBack callBack) {
        bleComm.cancelDevice(callBack);
    }

}