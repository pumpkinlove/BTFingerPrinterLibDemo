package com.miaxis.btfingerprinterlib;

import android.app.Application;
import android.bluetooth.BluetoothGatt;
import android.util.Log;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleScanAndConnectCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.miaxis.btfingerprinterlib.utils.BluetoothUUID;
import com.miaxis.btfingerprinterlib.utils.CodeUtil;
import com.miaxis.btfingerprinterlib.utils.ProtocolOrderCode;
import com.miaxis.btfingerprinterlib.utils.ProtocolUtil;
import com.miaxis.btfingerprinterlib.utils.cipher.DES3;
import com.miaxis.btfingerprinterlib.utils.cipher.Digest;
import com.miaxis.btfingerprinterlib.utils.cipher.Encrypt;
import com.miaxis.btfingerprinterlib.utils.cipher.RSAEncrypt;

import org.bouncycastle.crypto.InvalidCipherTextException;

import java.security.interfaces.RSAPublicKey;


/**
 * bluetooth comm
 * Created by xu.nan on 2018/3/7.
 */

public class BleComm {

    public static final int BLE_INIT_SUCCESS = 0;
    public static final int BLE_NOT_SUPPORT = -1;
    public static final int BLE_NOT_ENABLE = -2;

    public static final int MAX_DATA_LEN    = 2048;

    private static final String TAG = BleComm.class.getSimpleName();

    private byte[] sessionKey;
    private int encryptMode;

    private Application app;
    private int curOrderCode;
    private static final byte PROTOCOL_HEAD = 0x02;
    private static final byte PROTOCOL_END = 0x03;
    private static final int PRINT_SIZE = 24;
    private BleManager bleManager;
    private BleDevice conDevice;

    private byte[] cacheData = new byte[0];

    private ConnectCallBack connectCallBack;
    private CommonCallBack commonCallBack;

    private static final byte GROUP_CMD = -1;

    public byte[] getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(byte[] sessionKey) {
        this.sessionKey = sessionKey;
    }

    BleComm(Application app) {
        this.app = app;
        bleManager = BleManager.getInstance();
    }

    int initBle() {
        bleManager.init(app);
        if (!bleManager.isSupportBle()) {
            return BLE_NOT_SUPPORT;
        }
        if (!bleManager.isBlueEnable()) {
            bleManager.enableBluetooth();
        }
        return BLE_INIT_SUCCESS;
    }

    void connect(String mac, ConnectCallBack callBack) {
        bleManager.disconnectAllDevice();
        connectCallBack = callBack;
        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
                .setDeviceMac(mac)                  // 只扫描指定mac的设备，可选
                .setScanTimeOut(10000)              // 扫描超时时间，可选，默认10秒；小于等于0表示不限制扫描时间
                .build();
        bleManager.initScanRule(scanRuleConfig);
        bleManager.scanAndConnect(new BleScanAndConnectCallback() {
            @Override
            public void onScanStarted(boolean success) {
                Log.i(TAG, "onScanStarted " + success);
            }

            @Override
            public void onScanFinished(BleDevice scanResult) {
                if (scanResult != null) {
                    Log.i(TAG, "onScanFinished " + scanResult.getName());
                } else {
                    Log.i(TAG, "onScanFinished no device");
                }
            }

            @Override
            public void onStartConnect() {
                Log.i(TAG, "onStartConnect ");
            }

            @Override
            public void onConnectFail(BleException exception) {
                Log.i(TAG, "onConnectFail " + exception.getDescription());
                connectCallBack.onConnectFailure(exception.getDescription());
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                Log.i(TAG, "onConnectSuccess " + bleDevice.getName());
                startNotify(bleDevice);
            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {
                Log.i(TAG, "onDisConnected " + device.getName());
                connectCallBack.onDisConnect();
            }
        });
    }

    private void analysisData(byte[] btData) {
        if (btData == null || btData.length == 0) {
            return;
        }
        boolean hasHead = false;
        for (int i = 0; i < btData.length; i++) {
            if (btData[i] == PROTOCOL_HEAD) {
                byte[] tempCacheData = new byte[btData.length - i];
                System.arraycopy(btData, i, tempCacheData, 0, tempCacheData.length);
                btData = tempCacheData;
                hasHead = true;
                break;
            }
        }
        boolean hasEnd = false;
        for (int i = 0; i < btData.length; i++) {
            if (btData[i] == PROTOCOL_END) {
                byte[] tempCacheData = new byte[i + 1];
                System.arraycopy(btData, 0, tempCacheData, 0, tempCacheData.length);
                btData = tempCacheData;
                hasEnd = true;
                break;
            }
        }

        if (hasHead) {
            cacheData = btData;
        } else {
            addToCache(btData);
        }

        if (hasEnd) {
            StringBuilder reMsgSb = new StringBuilder();
            byte[] mergeCacheData = CodeUtil.mergeRetBytes(cacheData);
            if (CodeUtil.getXorCheckCode(mergeCacheData) != mergeCacheData[mergeCacheData.length - 2]) {
                cacheData = null;
                return;
            }
            if (handleReturnSw1(mergeCacheData[3], reMsgSb)) {
                commonCallBack.onSuccess(CodeUtil.getData(mergeCacheData));
            } else {
                commonCallBack.onFailure(reMsgSb.toString());
            }
        }

    }

    private void addToCache(byte[] values) {
        byte[] temp = new byte[cacheData.length + values.length];
        System.arraycopy(cacheData, 0, temp, 0, cacheData.length);
        System.arraycopy(values, 0, temp, cacheData.length, values.length);
        cacheData = temp;
    }

    private void startNotify(final BleDevice bleDevice) {
        bleManager.notify(bleDevice,
                BluetoothUUID.SERVICE_UUID_STR,
                BluetoothUUID.CH_9601_UUID_STR,
                new BleNotifyCallback() {
                    @Override
                    public void onNotifySuccess() {
                        conDevice= bleDevice;
                        Log.i(TAG, "onNotifySuccess");
                        connectCallBack.onConnectSuccess();
                    }

                    @Override
                    public void onNotifyFailure(BleException exception) {
                        Log.i(TAG, "onNotifyFailure");
                        connectCallBack.onConnectFailure(exception.getDescription());
                    }

                    @Override
                    public void onCharacteristicChanged(byte[] data) {
                        Log.i(TAG, "onCharacteristicChanged data received");
                        analysisData(data);
                    }
                }

        );

    }

    private void sendOrder(byte orderCode, byte[] orderCodeData, int encryptMode, byte[] sessionKey) {
        curOrderCode = orderCode;
        if (encryptMode != 0) {
            orderCodeData = DES3.encryptMode(sessionKey, orderCodeData);
        }
        byte[] reqBytes = ProtocolUtil.getReqOrder(orderCode, orderCodeData);
        writeData(CodeUtil.splitReqBytes(reqBytes));
    }

    private void writeData(final byte[] orderCodeData) {
        bleManager.write(conDevice,
                BluetoothUUID.SERVICE_UUID_STR,
                BluetoothUUID.CH_9600_UUID_STR,
                orderCodeData,
                new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                        Log.e(TAG,"onWriteSuccess current:" + current + " total:" + total);
                    }

                    @Override
                    public void onWriteFailure(BleException exception) {
                        Log.e(TAG,"onWriteFailure " + exception.getDescription());
                    }
                }
        );
    }

    private boolean handleReturnSw1(byte sw1, StringBuilder reMsgSb) {
        switch (sw1) {
            case 0x00:
                reMsgSb.append("Operation Succeeded");
                return true;
            case 0x01:
                reMsgSb.append("Operation Failed");
                return false;
            case 0x04:
                reMsgSb.append("Finger DataBase Full");
                return false;
            case 0x05:
                reMsgSb.append("No user");
                return false;
            case 0x07:
                reMsgSb.append("User Exists");
                return false;
            case 0x08:
                reMsgSb.append("Out of time");
                return false;
            case 0x09:
                reMsgSb.append("Free");
                return false;
            case 0x0A:
                reMsgSb.append("Orders executing");
                return false;
            case 0x0B:
                reMsgSb.append("Has Finger");
                return false;
            case 0x0C:
                reMsgSb.append("No Finger");
                return false;
            case 0x0D:
                reMsgSb.append("Finger Pass");
                return true;
            case 0x0E:
                reMsgSb.append("Finger Not Pass");
                return false;
            case 0x0F:
                reMsgSb.append("Security Level 1");
                return false;
            case 0x10:
                reMsgSb.append("Security Level 2");
                return false;
            case 0x11:
                reMsgSb.append("Security Level 3");
                return false;
            case 0x12:
                reMsgSb.append("Security Level 4");
                return false;
            case 0x13:
                reMsgSb.append("Security Level 5");
                return false;
            case 0x60:
                reMsgSb.append("No Finger");
                return false;
            case 0x61:
                reMsgSb.append("Search Fail");
                return false;
            case 0x62:
                reMsgSb.append("Generate Plate Fail");
                return false;
            case 0x63:
                reMsgSb.append("Generate Feature Fail");
                return false;
            case 0x29:
                reMsgSb.append("Time out");
                return false;
        }
        return true;
    }

    public interface ConnectCallBack {
        void onConnectSuccess();
        void onConnectFailure(String errorMessage);
        void onDisConnect();
    }

    public interface CommonCallBack {
        void onSuccess(byte[] responseData);
        void onFailure(String errorMessage);
    }

    void scrollPaper(int num, CommonCallBack callBack) {
        commonCallBack = callBack;
        sendOrder(ProtocolOrderCode.SCROLL_PAPER, new byte[]{(byte) num}, 0, null);
    }

    void print(String content, CommonCallBack callBack) {
        commonCallBack = callBack;
        byte[] data = new byte[PRINT_SIZE];
        for (int i = 0; i < content.length(); i++) {
            data[i] = (byte) content.charAt(i);
        }
        for (int i = content.length(); i < data.length; i++) {
            data[i] = 0x20;
        }
        sendOrder(ProtocolOrderCode.PRINT, data, 0, null);
    }

    void cancelDevice(CommonCallBack callBack) {
        commonCallBack = callBack;
        sendOrder(ProtocolOrderCode.CANCEL_GET_FINGER, null, encryptMode, sessionKey);
    }

    void getFinger(CommonCallBack callBack) {
        commonCallBack = callBack;

        byte[] data = new byte[4];
        data[0] = 0x00;
        data[1] = 0x00;
        data[2] = 0x27;
        data[3] = 0x10;

        sendOrder(ProtocolOrderCode.GET_FINGER, data, encryptMode, sessionKey);
    }

    void lampControl(int lampOnOff, CommonCallBack callBack) {
        commonCallBack = callBack;
        sendOrder(ProtocolOrderCode.DEV_OPEN_CLOSE_LED, new byte[]{(byte) lampOnOff}, 0, sessionKey);
    }

    void getDeviceInfo(CommonCallBack callBack) {
        commonCallBack = callBack;
        sendOrder(ProtocolOrderCode.CMD_GET_DEVICE_INFO, null, 0, sessionKey);
    }

    void getDeviceSN(CommonCallBack callBack) {
        commonCallBack = callBack;
        sendOrder(ProtocolOrderCode.DEV_GET_DEVICE_SN, null, 0, sessionKey);
    }

    void getDeviceUUID(CommonCallBack callBack) {
        commonCallBack = callBack;
        sendOrder(ProtocolOrderCode.CMD_GET_DEVICE_UUID, null, 0, sessionKey);
    }

    void getFingerImage(CommonCallBack callBack) {
        commonCallBack = callBack;
        byte[] data = new byte[4];
        data[0] = 0x00;
        data[1] = 0x00;
        data[2] = 0x27;
        data[3] = 0x10;
        sendOrder(ProtocolOrderCode.DEV_GET_IMAGE, data, encryptMode, sessionKey);
    }

    void getRSAPublicKey(int keyNum, CommonCallBack callBack) {
        commonCallBack = callBack;
        sendOrder(ProtocolOrderCode.DEV_EXPORT_RSA_PUBKEY, new byte[]{(byte) keyNum}, encryptMode, sessionKey);
    }

    void getPublicKey(CommonCallBack callBack) {
        commonCallBack = callBack;
        sendOrder(ProtocolOrderCode.DEV_GET_PUBLICK_KEY, null, 0, null);    //no des encrypt
    }

    void base64Encode(byte[] data, CommonCallBack callBack) {
        commonCallBack = callBack;
        sendOrder(ProtocolOrderCode.DEV_GET_BASE64ENCODE, data, 0, sessionKey);
    }

    void generateRSAKeyPair(byte keyNum, CommonCallBack callBack) {
        commonCallBack = callBack;
        sendOrder(ProtocolOrderCode.DEV_GEN_RSAKEY_PAIR, new byte[]{keyNum}, 0, sessionKey);
    }

    void getEncryptMode(CommonCallBack callBack) {
        commonCallBack = callBack;
        sendOrder(ProtocolOrderCode.DEV_GET_ENCRYPT_MODE_L0, null, 0, null);
    }

    void downEncSessionKey(byte[] publicKey, CommonCallBack callBack) {     //no 3des encrypt
        commonCallBack = callBack;
        //转换为java公钥
        RSAPublicKey javaPubKey = RSAEncrypt.DeviceRSAPubKeyToJavaPubKey(publicKey);
        //公钥加密
        byte[] encSessionKey;
        try {
            encSessionKey = RSAEncrypt.encrypt(javaPubKey,sessionKey);
        } catch (Exception e) {
            e.printStackTrace();
            commonCallBack.onFailure("public key encrypt failed");
            return;
        }
        if (encSessionKey == null) {
            commonCallBack.onFailure("public key encrypt failed");
            return;
        }
        sendOrder(ProtocolOrderCode.DEV_SET_SESSION_KEY, encSessionKey, 0, null);
    }

    void setDeviceMode(int mode, CommonCallBack callBack) {
        commonCallBack = callBack;
        sendOrder(ProtocolOrderCode.DEV_SET_MODE, new byte[]{(byte) mode}, 0, sessionKey);
    }

    void tmfGenerateDigest(byte bAlgorithmType, byte[] inputData, CommonCallBack callBack) {
        commonCallBack = callBack;
        if (inputData == null) {
            commonCallBack.onFailure("input data can not be null");
            return;
        }
        if (inputData.length > MAX_DATA_LEN) {
            byte[] bDigest = Digest.JAVADigest(inputData);
            byte[] bDigestLen = CodeUtil.intToByteArray(bDigest.length);
            byte[] reData = new byte[bDigest.length + bDigestLen.length];
            System.arraycopy(bDigestLen, 0, reData, 0, bDigestLen.length);
            System.arraycopy(bDigest, 0, reData, 4, bDigest.length);
            commonCallBack.onSuccess(reData);
        } else {
            int iSendLen = 1 + 4 + inputData.length;        //1 byte bAlgorithmType + 4 byte inputData length + input data
            byte[] bSendData = new byte[iSendLen];
            bSendData[0] = bAlgorithmType;
            byte[] bDataLen = CodeUtil.intToByteArray(inputData.length);
            System.arraycopy(bDataLen, 0, bSendData, 1, 4);
            System.arraycopy(inputData, 0, bSendData, 5, inputData.length);
            sendOrder(ProtocolOrderCode.DEV_GEN_DIGEST, bSendData, 0, sessionKey);
        }
    }

    void getTEEUUID(CommonCallBack callBack) {
        commonCallBack = callBack;
        sendOrder(ProtocolOrderCode.DEV_GET_TEE_UUID, null, 0, sessionKey);
    }

    void getTEEVersion(CommonCallBack callBack) {
        commonCallBack = callBack;
        sendOrder(ProtocolOrderCode.DEV_GET_TEE_INFO, null, 0, sessionKey);
    }

    void tmfWriteFileToDevice(byte iFileType, byte[] fileData, int dataLen, CommonCallBack callBack) {
        commonCallBack = callBack;
        if(dataLen > MAX_DATA_LEN * 2) {
            commonCallBack.onFailure("data length error");
            return;
        }
        if (iFileType > 10) {
            iFileType -= 7;
        }
        byte[] data = new byte[1 + 4 + dataLen];
        data[0] = iFileType;
        byte[] lenBytes = CodeUtil.intToByteArray(dataLen);
        System.arraycopy(lenBytes, 0, data, 1, 4);
        System.arraycopy(fileData, 0, data, 5, dataLen);
        sendOrder(ProtocolOrderCode.DEV_IMPORT_CERTIFICATE, data, 0, sessionKey);
    }

    void tmfReadFileFromDevice(byte iFileType, CommonCallBack callBack) {
        commonCallBack = callBack;
        sendOrder(ProtocolOrderCode.DEV_EXPORT_CERTIFICATE, new byte[]{iFileType}, 0, sessionKey);

    }

    void setEncryptMode(int mode) {
        this.encryptMode = mode;
    }

    void setEncryptMode(int mode, CommonCallBack callBack) {
        commonCallBack = callBack;
        sendOrder(ProtocolOrderCode.DEV_SET_ENCRYPT_MODE_L0, new byte[]{(byte) mode}, 1, sessionKey);
    }

    void signData(byte keyNum, byte[] signData, CommonCallBack callBack) {
        commonCallBack = callBack;
        if (signData == null) {
            commonCallBack.onFailure("input data can not be null");
            return;
        }
        if (signData.length > MAX_DATA_LEN) {
            commonCallBack.onFailure("data length error");
            return;
        }
        byte[] data = new byte[1 + 4 + signData.length];
        data[0] = keyNum;
        byte[] len = CodeUtil.intToByteArray(signData.length);
        System.arraycopy(len, 0, data, 1, 4);
        System.arraycopy(signData, 0, data, 5, signData.length);
        sendOrder(ProtocolOrderCode.DEV_RSA_SIGN_DATA, data, 0, sessionKey);
    }

    void tmfAESGCMEncrypt(byte[] key,
                          byte[] inputData,
                          byte[] ivData,
                          byte[] aadData,
                          CommonCallBack callBack) {
        commonCallBack = callBack;
        if (inputData.length > MAX_DATA_LEN) {
            byte[] reData;
            try {
                byte[] encryptedData = Encrypt.encryptAES_GCM_NOPADDINGSessionKey(true,key,ivData,aadData,inputData);
                byte[] bEncryptedDataLen = CodeUtil.intToByteArray(encryptedData.length);
                reData = new byte[bEncryptedDataLen.length + encryptedData.length];
                System.arraycopy(bEncryptedDataLen, 0 , reData, 0, bEncryptedDataLen.length);
                System.arraycopy(encryptedData, 0, reData, 4, encryptedData.length);
                commonCallBack.onSuccess(reData);
            } catch (Exception e) {
                commonCallBack.onFailure("AESGCMEncrypt failed");
            }
        } else {
            int iSendLen = 4 + key.length + 4 + inputData.length + 4 + ivData.length + 4 + aadData.length;
            byte[] bSendData = new byte[iSendLen];
            byte[] bKeyLen = CodeUtil.intToByteArray(key.length);
            int iOffSize = 0;

            System.arraycopy(bKeyLen, 0, bSendData, iOffSize, 4);
            iOffSize += 4;
            System.arraycopy(key, 0, bSendData, iOffSize, key.length);
            iOffSize += key.length;

            byte[] binputDataLen = CodeUtil.intToByteArray(inputData.length);
            System.arraycopy(binputDataLen, 0, bSendData, iOffSize, 4);
            iOffSize += 4;
            System.arraycopy(inputData, 0, bSendData, iOffSize, inputData.length);
            iOffSize += inputData.length;

            byte[] bivDataLen = CodeUtil.intToByteArray(ivData.length);
            System.arraycopy(bivDataLen, 0, bSendData, iOffSize, 4);
            iOffSize += 4;
            System.arraycopy(ivData, 0, bSendData, iOffSize, ivData.length);
            iOffSize += ivData.length;

            byte[] bAadDataLen = CodeUtil.intToByteArray(aadData.length);
            System.arraycopy(bAadDataLen, 0, bSendData, iOffSize, 4);
            iOffSize += 4;
            System.arraycopy(aadData, 0, bSendData, iOffSize, aadData.length);

            sendOrder(ProtocolOrderCode.DEV_AESGCM_ENCRYPT, bSendData, encryptMode, sessionKey);
        }

    }

    void tmfAESGCMDecrypt(byte[] key,
                          byte[] inputData,
                          byte[] ivData,
                          byte[] aadData,
                          CommonCallBack callBack) {
        commonCallBack = callBack;
        if (inputData.length > MAX_DATA_LEN) {
            byte[] reData;
            try {
                byte[] encryptedData = Encrypt.encryptAES_GCM_NOPADDINGSessionKey(true,key,ivData,aadData,inputData);
                byte[] bEncryptedDataLen = CodeUtil.intToByteArray(encryptedData.length);
                reData = new byte[bEncryptedDataLen.length + encryptedData.length];
                System.arraycopy(bEncryptedDataLen, 0 , reData, 0, bEncryptedDataLen.length);
                System.arraycopy(encryptedData, 0, reData, 4, encryptedData.length);
                commonCallBack.onSuccess(reData);
            } catch (Exception e) {
                commonCallBack.onFailure("AESGCMDecrypt failed");
            }
        } else {
            int iSendLen = 4 + key.length + 4 + inputData.length + 4 + ivData.length + 4 + aadData.length;
            byte[] bSendData = new byte[iSendLen];
            byte[] bKeyLen = CodeUtil.intToByteArray(key.length);
            int iOffSize = 0;

            System.arraycopy(bKeyLen, 0, bSendData, iOffSize, 4);
            iOffSize += 4;
            System.arraycopy(key, 0, bSendData, iOffSize, key.length);
            iOffSize += key.length;

            byte[] binputDataLen = CodeUtil.intToByteArray(inputData.length);
            System.arraycopy(binputDataLen, 0, bSendData, iOffSize, 4);
            iOffSize += 4;
            System.arraycopy(inputData, 0, bSendData, iOffSize, inputData.length);
            iOffSize += inputData.length;

            byte[] bivDataLen = CodeUtil.intToByteArray(ivData.length);
            System.arraycopy(bivDataLen, 0, bSendData, iOffSize, 4);
            iOffSize += 4;
            System.arraycopy(ivData, 0, bSendData, iOffSize, ivData.length);
            iOffSize += ivData.length;

            byte[] bAadDataLen = CodeUtil.intToByteArray(aadData.length);
            System.arraycopy(bAadDataLen, 0, bSendData, iOffSize, 4);
            iOffSize += 4;
            System.arraycopy(aadData, 0, bSendData, iOffSize, aadData.length);

            sendOrder(ProtocolOrderCode.DEV_AESGCM_DECRYPT, bSendData, encryptMode, sessionKey);
        }

    }

    void tmfDigitalSignatureRSA2048withSHA256(byte bKeyNum,
                                              byte[] inputData,
                                              CommonCallBack callBack) {
        commonCallBack = callBack;
        byte[] bDigestData = Digest.JAVADigest(inputData);
        byte[] OIDSHA_256 = new byte[]{0x30,0x31,0x30,0x0d,0x06,0x09,0x60,(byte) 0x86,0x48,0x01,0x65,0x03,0x04,0x02,0x01,0x05,0x00,0x04,0x20};
        byte[] bToSignData = new byte[256];
        System.arraycopy(OIDSHA_256, 0, bToSignData, 0, OIDSHA_256.length);
        System.arraycopy(bDigestData, 0, bToSignData, OIDSHA_256.length, bDigestData.length);
        byte[] bSendData = new byte[1 + 4 + bToSignData.length];
        bSendData[0] = bKeyNum;
        byte[] bToSignDataLen = CodeUtil.intToByteArray(bToSignData.length);
        System.arraycopy(bToSignDataLen, 0, bSendData, 1, 4);
        System.arraycopy(bToSignData, 0, bSendData, 1 + 4, bToSignData.length);
        sendOrder(ProtocolOrderCode.DEV_RSA_SIGN_DATA, bSendData, encryptMode, sessionKey);
    }

    void getDeviceMode(CommonCallBack callBack) {
        commonCallBack = callBack;
        sendOrder(ProtocolOrderCode.DEV_GET_MODE, null, 0, null);
    }



}
