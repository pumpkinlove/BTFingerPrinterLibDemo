package com.miaxis.btfingerprinterlibdemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.miaxis.btfingerprinterlib.BleComm;
import com.miaxis.btfingerprinterlib.MxBlePrinterApi;
import com.miaxis.btfingerprinterlib.utils.CodeUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private Button btnInit;
    private Button btnConnect;
    private Button btnPrint;
    private Button btnScrollPaper;
    private Button btnGetFinger;
    private Button btnCancelGetFinger;
    private Button btnLampControl;
    private Button btnGetDeviceInfo;
    private Button btnGetDeviceSN;
    private Button btnGetDeviceUUID;
    private Button btnGetFingerImage;
    private Button btnSetDeviceMode0;
    private Button btnSetDeviceMode1;
    private Button btnBase64Encode;
    private Button btnGenRSAKeyPair;
    private Button btnGetRSAPublicKey;
    private Button btnGenDigest;
    private Button btnGenSessionKey;
    private Button btnAesGcmNoPaddingEncryption;
    private Button btnAesGcmNoPaddingDecryption;
    private Button btnDigitalSignatureRSA2048WithSHA256;
    private Button btnAsymmetricEncryptionX509;
    private Button btnWriteFileToDevice;
    private Button btnReadFileFromDevice;
    private Button btnSignData;
    private Button btnGetSecureProcessorUuid;
    private Button btnGetSecureProcessorVersion;
    private Button btnDeleteFileFromDevice;
    private Button btnSetUsbEncryption0;
    private Button btnSetUsbEncryption1;
    private ImageView ivFinger;

    private EditText etMac;
    private TextView tvFinger;
    private EditText etContent;

    private MxBlePrinterApi api;

    private boolean isInited;
    private boolean isConnected;
    private boolean lampOnOff = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();
        initView();
        showButtons();
    }

    private void initData() {
        api = MxBlePrinterApi.getInstance(getApplication());
    }

    private void initView() {
        ivFinger = findViewById(R.id.iv_finger);
        btnInit = findViewById(R.id.btn_init);
        btnInit.setOnClickListener(this);

        btnConnect = findViewById(R.id.btn_connect);
        btnConnect.setOnClickListener(this);

        btnGetFinger = findViewById(R.id.btn_get_finger);
        btnGetFinger.setOnClickListener(this);

        btnPrint = findViewById(R.id.btn_print);
        btnPrint.setOnClickListener(this);

        btnCancelGetFinger = findViewById(R.id.btn_cancel_get_finger);
        btnCancelGetFinger.setOnClickListener(this);

        btnScrollPaper = findViewById(R.id.btn_scroll_paper);
        btnScrollPaper.setOnClickListener(this);

        btnLampControl = findViewById(R.id.btn_lamp_control);
        btnLampControl.setOnClickListener(this);

        btnGetDeviceInfo = findViewById(R.id.btn_get_device_info);
        btnGetDeviceInfo.setOnClickListener(this);

        btnGetDeviceSN = findViewById(R.id.btn_get_device_sn);
        btnGetDeviceSN.setOnClickListener(this);

        btnGetDeviceUUID = findViewById(R.id.btn_get_device_uuid);
        btnGetDeviceUUID.setOnClickListener(this);

        btnGetFingerImage = findViewById(R.id.btn_get_finger_image);
        btnGetFingerImage.setOnClickListener(this);

        btnSetDeviceMode0 = findViewById(R.id.btn_set_device_mode0);
        btnSetDeviceMode0.setOnClickListener(this);

        btnSetDeviceMode1 = findViewById(R.id.btn_set_device_mode1);
        btnSetDeviceMode1.setOnClickListener(this);

        btnBase64Encode = findViewById(R.id.btn_base64_encode);
        btnBase64Encode.setOnClickListener(this);

        btnGenRSAKeyPair = findViewById(R.id.btn_gen_RSA_key_pair);
        btnGenRSAKeyPair.setOnClickListener(this);

        btnGetRSAPublicKey = findViewById(R.id.btn_get_RSA_public_key);
        btnGetRSAPublicKey.setOnClickListener(this);

        btnGenDigest = findViewById(R.id.btn_gen_digest);
        btnGenDigest.setOnClickListener(this);

        btnGenSessionKey = findViewById(R.id.btn_get_session_key);
        btnGenSessionKey.setOnClickListener(this);

        btnAesGcmNoPaddingEncryption = findViewById(R.id.btn_AES_GCM_NOPADDING_Encryption);
        btnAesGcmNoPaddingEncryption.setOnClickListener(this);

        btnAesGcmNoPaddingDecryption = findViewById(R.id.btn_AES_GCM_NOPADDING_Decryption);
        btnAesGcmNoPaddingDecryption.setOnClickListener(this);

        btnDigitalSignatureRSA2048WithSHA256 = findViewById(R.id.btn_DigitalSignatureRSA2048withSHA256);
        btnDigitalSignatureRSA2048WithSHA256.setOnClickListener(this);

        btnAsymmetricEncryptionX509 = findViewById(R.id.btn_AsymmetricEncryptionX509);
        btnAsymmetricEncryptionX509.setOnClickListener(this);

        btnWriteFileToDevice = findViewById(R.id.btn_write_file_to_device);
        btnWriteFileToDevice.setOnClickListener(this);

        btnReadFileFromDevice = findViewById(R.id.btn_read_file_from_device);
        btnReadFileFromDevice.setOnClickListener(this);

        btnSignData = findViewById(R.id.btn_sign_data);
        btnSignData.setOnClickListener(this);

        btnGetSecureProcessorUuid = findViewById(R.id.btn_get_secure_processor_uuid);
        btnGetSecureProcessorUuid.setOnClickListener(this);

        btnGetSecureProcessorVersion = findViewById(R.id.btn_get_secure_processor_version);
        btnGetSecureProcessorVersion.setOnClickListener(this);

        btnDeleteFileFromDevice = findViewById(R.id.btn_delete_from_device);
        btnDeleteFileFromDevice.setOnClickListener(this);

        btnSetUsbEncryption0 = findViewById(R.id.btn_set_usb_encryption0);
        btnSetUsbEncryption0.setOnClickListener(this);

        btnSetUsbEncryption1 = findViewById(R.id.btn_set_usb_encryption1);
        btnSetUsbEncryption1.setOnClickListener(this);

        etMac = findViewById(R.id.et_mac);
        tvFinger = findViewById(R.id.tv_finger);
        etContent = findViewById(R.id.et_print_content);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_init:
                init();
                break;
            case R.id.btn_connect:
                connect();
                break;
            case R.id.btn_print:
                print();
                break;
            case R.id.btn_scroll_paper:
                scrollPaper(3);
                break;
            case R.id.btn_get_finger:
                getFinger();
                break;
            case R.id.btn_cancel_get_finger:
                cancelGetFinger();
                break;
            case R.id.btn_lamp_control:
                if (lampOnOff) {
                    lampControl(1);
                } else {
                    lampControl(0);
                }
                break;
            case R.id.btn_get_device_info:
                getDeviceInfo();
                break;
            case R.id.btn_get_device_sn:
                getDeviceSN();
                break;
            case R.id.btn_get_device_uuid:
                getDeviceUUID();
                break;
            case R.id.btn_get_finger_image:
                getFingerImage();
                break;
            case R.id.btn_set_device_mode0:
                setDeviceMode(0);
                break;
            case R.id.btn_set_device_mode1:
                setDeviceMode(1);
                break;
            case R.id.btn_base64_encode:
                base64Encode();
                break;
            case R.id.btn_gen_RSA_key_pair:
                genRSAKeyPair();
                break;
            case R.id.btn_get_RSA_public_key:
                getRSAPublicKey();
                break;
            case R.id.btn_gen_digest:
                genDigest();
                break;
            case R.id.btn_get_session_key:
                getSessionKey();
                break;
            case R.id.btn_AES_GCM_NOPADDING_Encryption:
                aesGcmNoPaddingEncryption();
                break;
            case R.id.btn_AES_GCM_NOPADDING_Decryption:
                aesGcmNoPaddingDecryption();
                break;
            case R.id.btn_DigitalSignatureRSA2048withSHA256:
                digitalSignatureRSA2048WithSHA256();
                break;
            case R.id.btn_AsymmetricEncryptionX509:
                asymmetricEncryptionX509();
                break;
            case R.id.btn_write_file_to_device:
                writeFileToDevice();
                break;
            case R.id.btn_read_file_from_device:
                readFileFromDevice();
                break;
            case R.id.btn_sign_data:
                signData();
                break;
            case R.id.btn_get_secure_processor_uuid:
                getSecureProcessorUUID();
                break;
            case R.id.btn_get_secure_processor_version:
                getSecureProcessorVersion();
                break;
            case R.id.btn_delete_from_device:
                deleteFileFromDevice();
                break;
            case R.id.btn_set_usb_encryption0:
                setUsbEncryption(0);
                break;
            case R.id.btn_set_usb_encryption1:
                setUsbEncryption(1);
                break;
        }
    }

    private void showButtons() {
        btnConnect.setEnabled(isInited);
        btnPrint.setEnabled(isConnected);
        etContent.setEnabled(isConnected);
        btnGetFinger.setEnabled(isConnected);
        btnCancelGetFinger.setEnabled(isConnected);
        btnScrollPaper.setEnabled(isConnected);
        btnLampControl.setEnabled(isConnected);
        btnGetDeviceUUID.setEnabled(isConnected);
        btnGetDeviceSN.setEnabled(isConnected);
        btnGetDeviceInfo.setEnabled(isConnected);
        btnGetFingerImage.setEnabled(isConnected);
        btnSetDeviceMode0.setEnabled(isConnected);
        btnSetDeviceMode1.setEnabled(isConnected);
        btnBase64Encode.setEnabled(isConnected);
        btnGenRSAKeyPair.setEnabled(isConnected);
        btnGetRSAPublicKey.setEnabled(isConnected);
        btnGenDigest.setEnabled(isConnected);
        btnGenSessionKey.setEnabled(isConnected);
        btnAesGcmNoPaddingEncryption.setEnabled(isConnected);
        btnAesGcmNoPaddingDecryption.setEnabled(isConnected);
        btnDigitalSignatureRSA2048WithSHA256.setEnabled(isConnected);
        btnAsymmetricEncryptionX509.setEnabled(isConnected);
        btnWriteFileToDevice.setEnabled(isConnected);
        btnReadFileFromDevice.setEnabled(isConnected);
        btnSignData.setEnabled(isConnected);
        btnGetSecureProcessorUuid.setEnabled(isConnected);
        btnGetSecureProcessorVersion.setEnabled(isConnected);
        btnDeleteFileFromDevice.setEnabled(isConnected);
        btnSetUsbEncryption0.setEnabled(isConnected);
        btnSetUsbEncryption1.setEnabled(isConnected);
    }

    private void init() {
        String str1 = "0230363F3F353430303030303030303030303130303030333033313330303D3036303936303836343830313635303330343032303130353030303432303B3C3E303A3F3F31393C3F353A3A363A373436393A33303D36313D30343E343337363E343B3B3F363338313035323E3E393E373F33333932353C3935343D35323030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303903";
        String str2 = "0230363F3F353430303030303030303030303130303030333033313330303D3036303936303836 34 38 30 31 36 35 30 33 30 34 30 32 30 31 30 35 30 30 30 34 32 30 3B 3C 3E 30 3A 3F 3F 31 39 3C 3F 35 3A 3A 36 3A 37 34 36 39 3A 33 30 3D 36 31 3D 30 34 3E 34 33 37 36 3E 34 3B 3B 3F 36 33 38 31 30 35 32 3E 3E 39 3E 37 3F 33 33 39 32 35 3C 39 35 34 3D 35 32 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 39 03 ";


        String str1Trim = str1.trim();
        String str2Trim = str2.replace(" ", "");

        Log.e("str1Trim", str1Trim);
        Log.e("str2Trim", str2Trim);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str2.length(); i++) {
            if (!" ".equals(str2.charAt(i))) {
                sb.append(str2.charAt(i));
            }
        }
        Log.e(TAG, sb.toString());
        Log.e(TAG, str1Trim.length() +" _ " + str2Trim.length());
        if (str1Trim.equals(str2Trim)) {
            Log.e(TAG, "== equal");
        } else {
            Log.e(TAG, "== not equal");
        }

        int re = api.initBle();
        switch (re) {
            case BleComm.BLE_NOT_SUPPORT:
                Toast.makeText(this, "BLE_NOT_SUPPORT", Toast.LENGTH_SHORT).show();
                isInited = false;
                break;
            case BleComm.BLE_NOT_ENABLE:
                Toast.makeText(this, "BLE_NOT_ENABLE", Toast.LENGTH_SHORT).show();
                isInited = false;
                break;
            case BleComm.BLE_INIT_SUCCESS:
                Toast.makeText(this, "BLE_INIT_SUCCESS", Toast.LENGTH_SHORT).show();
                isInited = true;
                break;
        }
        showButtons();
    }

    private void connect() {
        String mac = etMac.getText().toString();
        if (TextUtils.isEmpty(mac)) {
            Toast.makeText(this, "mac address can not be null", Toast.LENGTH_SHORT).show();
            return;
        }
        api.connect(mac, new BleComm.ConnectCallBack() {
            @Override
            public void onConnectSuccess() {
                Log.e(TAG, "onConnectSuccess");
                isConnected = true;
                showButtons();
            }

            @Override
            public void onConnectFailure(String s) {
                Log.e(TAG, "onConnectFailure " + s);
                isConnected = false;
                showButtons();
            }

            @Override
            public void onDisConnect() {
                Log.e(TAG, "onDisConnect");
                isConnected = false;
                showButtons();
            }
        });
    }

    private void getFinger() {
        api.getFinger(new BleComm.CommonCallBack() {
            @Override
            public void onSuccess(byte[] bytes) {
                Log.e(TAG, "onGetFingerSuccess");
                if (bytes != null) {
                    tvFinger.setText(Base64.encodeToString(bytes, Base64.DEFAULT));
                }
            }

            @Override
            public void onFailure(String s) {
                Log.e(TAG, "onGetFingerFailure " + s);
            }

        });
    }

    private void print() {
        String content = etContent.getText().toString();
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "Print content can not be null", Toast.LENGTH_SHORT).show();
            return;
        }
        api.print(content, new BleComm.CommonCallBack() {
            @Override
            public void onSuccess(byte[] bytes) {
                Log.e(TAG, "onPrintSuccess");
            }

            @Override
            public void onFailure(String s) {
                Log.e(TAG, "onPrintFailure " + s);
            }

        });
    }

    private void scrollPaper(int num) {
        api.scrollPaper(num, new BleComm.CommonCallBack() {
            @Override
            public void onSuccess(byte[] bytes) {
                Log.e(TAG, "onScrollSuccess");
            }

            @Override
            public void onFailure(String s) {
                Log.e(TAG, "onScrollFailure " + s);
            }

        });
    }

    private void cancelGetFinger() {
        api.cancelDevice(new BleComm.CommonCallBack() {
            @Override
            public void onSuccess(byte[] bytes) {
                Log.e(TAG, "onCancelSuccess");
            }

            @Override
            public void onFailure(String s) {
                Log.e(TAG, "onCancelFailure " + s);
            }

        });
    }

    private void lampControl(final int onOff) {
        api.lampControl(onOff, new BleComm.CommonCallBack() {
            @Override
            public void onSuccess(byte[] responseData) {
                Toast.makeText(MainActivity.this, "lampControl success", Toast.LENGTH_SHORT).show();
                lampOnOff = !lampOnOff;
            }

            @Override
            public void onFailure(String s) {
                Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getDeviceInfo() {
        api.getDeviceInfo(new BleComm.CommonCallBack() {
            @Override
            public void onSuccess(byte[] responseData) {
                if (responseData != null) {
                    Toast.makeText(MainActivity.this, new String(responseData), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String s) {
                Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getDeviceSN() {
        api.getDeviceSN(new BleComm.CommonCallBack() {
            @Override
            public void onSuccess(byte[] responseData) {
                if (responseData != null) {
                    Toast.makeText(MainActivity.this, new String(responseData), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String s) {
                Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getDeviceUUID() {
        api.getDeviceUUID(new BleComm.CommonCallBack() {
            @Override
            public void onSuccess(byte[] responseData) {
                if (responseData != null) {
                    Toast.makeText(MainActivity.this, new String(responseData), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String s) {
                Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getFingerImage() {
        api.getFingerImage(new BleComm.CommonCallBack() {
            @Override
            public void onSuccess(byte[] bytes) {
                Log.e(TAG, "getFingerImage onSuccess");

                int a,b;
                a = JUnsigned(bytes[0]);
                b = JUnsigned(bytes[1]);
                int iImageWidth = a*256+b;
                //图像高,原因：byte的取值范围-128~127
                a = JUnsigned(bytes[2]);
                b = JUnsigned(bytes[3]);
                int iImageHeight = a*256+b;

                byte[] bmpData = new byte[bytes.length - 4];
                System.arraycopy(bytes, 4, bmpData, 0, bmpData.length);

                Bitmap bFinger = BitmapFactory.decodeByteArray(bmpData, 0, bmpData.length);

                ivFinger.setImageBitmap(bFinger);


            }

            @Override
            public void onFailure(String s) {
                Log.e(TAG, "getFingerImage onFailure " + s);
            }
        });
    }
    public static int JUnsigned(int x) {
        if (x >= 0)
            return x;
        else
            return (x + 256);
    }
    private void setDeviceMode(int mode) {
        api.setDeviceMode(mode, new BleComm.CommonCallBack() {
            @Override
            public void onSuccess(byte[] bytes) {
                Log.e(TAG, "setDeviceMode onSuccess");
            }

            @Override
            public void onFailure(String s) {
                Log.e(TAG, "setDeviceMode onFailure " + s);
            }
        });
    }

    private void base64Encode() {
//        String test = "testData1234567890";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 251; i ++) {
            sb.append("x");
        }
        api.base64Encode(sb.toString().getBytes(), new BleComm.CommonCallBack() {
            @Override
            public void onSuccess(byte[] bytes) {
                Log.e(TAG, "base64Encode onSuccess");
                if (bytes != null) {
                    byte[] n = Base64.decode(bytes, Base64.DEFAULT);
                    Log.e(TAG, "Decode To String : " + new String(n));
                }
            }

            @Override
            public void onFailure(String s) {
                Log.e(TAG, "base64Encode onFailure " + s);
            }
        });
    }

    private void genRSAKeyPair() {
        byte bKeyIndex = 0;     // 0 or 1
        api.tmfGenerateRSAKeyPair(bKeyIndex, new BleComm.CommonCallBack() {
            @Override
            public void onSuccess(byte[] bytes) {
                Log.e(TAG, "tmfGenerateRSAKeyPair onSuccess");
            }

            @Override
            public void onFailure(String s) {
                Log.e(TAG, "tmfGenerateRSAKeyPair onFailure " + s);
            }
        });
    }

    private void getRSAPublicKey() {
        api.tmfGetRSAPublicKey(0, new BleComm.CommonCallBack() {
            @Override
            public void onSuccess(byte[] bytes) {
                Log.e(TAG, "getRSAPublicKey onSuccess");
                if (bytes != null) {
                    Log.e(TAG, "RSA public key " + new String(bytes));
                }
            }

            @Override
            public void onFailure(String s) {
                Log.e(TAG, "getRSAPublicKey onFailure " + s);
            }
        });
    }

    private void genDigest() {
        byte bAlgType = 0;
        byte[] inputData = new byte[500];
        for (int i = 0; i < 500; i ++) {
            inputData[i] = (byte) i;
        }
        api.tmfGenerateDigest(bAlgType, inputData, new BleComm.CommonCallBack() {
            @Override
            public void onSuccess(byte[] bytes) {
                Log.e(TAG, "tmfGenerateDigest onSuccess");
            }

            @Override
            public void onFailure(String s) {
                Log.e(TAG, "tmfGenerateDigest onFailure " + s);
            }
        });
    }

    private void getSessionKey() {
        api.tmfGenerateSessionKey(new BleComm.CommonCallBack() {
            @Override
            public void onSuccess(byte[] bytes) {
                Log.e(TAG, "tmfGenerateSessionKey onSuccess");
            }

            @Override
            public void onFailure(String s) {
                Log.e(TAG, "tmfGenerateSessionKey onFailure " + s);
            }
        });
    }

    private void aesGcmNoPaddingEncryption() {
        byte[] keyData = new byte[]{0};
        byte[] inputData = new byte[100];
        for (int i = 0; i < 100; i ++) {
            inputData[i] = (byte) i;
        }
        byte[] ivData = new byte[12];
        byte[] aadData = new byte[16];

        api.tmfAesGcmNoPaddingEncryption(keyData, inputData, ivData, aadData, new BleComm.CommonCallBack() {
            @Override
            public void onSuccess(byte[] bytes) {
                Log.e(TAG, "tmfAesGcmNoPaddingEncryption onSuccess");
            }

            @Override
            public void onFailure(String s) {
                Log.e(TAG, "tmfAesGcmNoPaddingEncryption onFailure " + s);
            }
        });
    }

    private void aesGcmNoPaddingDecryption() {
        byte[] keyData = new byte[]{0};
        byte[] inputData = new byte[100];
        for (int i = 0; i < 100; i ++) {
            inputData[i] = (byte) i;
        }
        byte[] ivData = new byte[12];
        byte[] aadData = new byte[16];

        api.tmfAesGcmNoPaddingDecryption(keyData, inputData, ivData, aadData, new BleComm.CommonCallBack() {
            @Override
            public void onSuccess(byte[] bytes) {
                Log.e(TAG, "tmfAesGcmNoPaddingDecryption onSuccess");
            }

            @Override
            public void onFailure(String s) {
                Log.e(TAG, "tmfAesGcmNoPaddingDecryption onFailure " + s);
            }
        });
    }

    private void digitalSignatureRSA2048WithSHA256() {
        byte keyData = 0;
        byte[] inputData = new byte[300];
        for (int i = 0; i < 300; i ++) {
            inputData[i] = (byte) i;
        }
        api.tmfDigitalSignatureRSA2048withSHA256(keyData, inputData, new BleComm.CommonCallBack() {
            @Override
            public void onSuccess(byte[] bytes) {
                Log.e(TAG, "tmfDigitalSignatureRSA2048withSHA256 onSuccess");
            }

            @Override
            public void onFailure(String s) {
                Log.e(TAG, "tmfDigitalSignatureRSA2048withSHA256 onFailure " + s);
            }
        });
    }

    private void asymmetricEncryptionX509() {
        byte[] x509CertificateData = null;
        InputStream is = null;
        ByteArrayOutputStream os = null;
        try {
            is = getAssets().open("X509.cer");
            os = new ByteArrayOutputStream(is.available());
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = is.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            x509CertificateData = os.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (x509CertificateData == null) {
            return;
        }
        byte[] inputContent = new byte[100];
        for (int i = 0; i < 100; i ++) {
            inputContent[i] = (byte) i;
        }
        byte[] outputContent = new byte[256];
        int[] outputContentLen = new int[1];

        int re = api.tmfAsymmetricEncryptionX509(x509CertificateData, inputContent, outputContent, outputContentLen);
        if (re == 0) {
            Log.e(TAG, "tmfAsymmetricEncryptionX509 onSuccess");
        } else {
            Log.e(TAG, "tmfAsymmetricEncryptionX509 onFailure re = " + re);
        }
    }

    private void writeFileToDevice() {
        byte fileType = 0;

        byte[] fileContent = new byte[2048];
        int fileContentLen = fileContent.length;
        for (int i=0;i<fileContentLen;i++)
        {
            fileContent[i]=(byte) (0x30+i);
        }
        for (int i = 0; i < 64; i++) {
            fileContent[128+i] = fileType;
        }

        api.tmfWriteFileToDevice(fileType, fileContent, fileContent.length, new BleComm.CommonCallBack() {
            @Override
            public void onSuccess(byte[] bytes) {
                Log.e(TAG, "tmfWriteFileToDevice onSuccess");
            }

            @Override
            public void onFailure(String s) {
                Log.e(TAG, "tmfWriteFileToDevice onFailure " + s);
            }
        });
    }

    private void readFileFromDevice() {
        byte fileType = 0;
        api.tmfReadFileFromDevice(fileType, new BleComm.CommonCallBack() {
            @Override
            public void onSuccess(byte[] bytes) {
                Log.e(TAG, "tmfReadFileFromDevice onSuccess");
            }

            @Override
            public void onFailure(String s) {
                Log.e(TAG, "tmfReadFileFromDevice onFailure " + s);
            }
        });
    }

    private void signData() {
        byte keyNum = 0;
        byte[] inputData = new byte[300];
        for (int i = 0; i < 300; i ++) {
            inputData[i] = (byte) i;
        }
        api.tmfSignData(keyNum, inputData, new BleComm.CommonCallBack() {
            @Override
            public void onSuccess(byte[] bytes) {
                Log.e(TAG, "tmfSignData onSuccess");
            }

            @Override
            public void onFailure(String s) {
                Log.e(TAG, "tmfSignData onFailure " + s);
            }
        });
    }

    private void getSecureProcessorUUID() {
        api.tmfGetSecureProcessorUUID(new BleComm.CommonCallBack() {
            @Override
            public void onSuccess(byte[] bytes) {
                Log.e(TAG, "tmfGetSecureProcessorUUID onSuccess");
                if (bytes != null) {
                    Log.e(TAG, "UUID : " + new String(bytes));
                }
            }

            @Override
            public void onFailure(String s) {
                Log.e(TAG, "tmfGetSecureProcessorUUID onFailure " + s);
            }
        });
    }

    private void getSecureProcessorVersion() {
        api.tmfGetSecureProcessorVersion(new BleComm.CommonCallBack() {
            @Override
            public void onSuccess(byte[] bytes) {
                Log.e(TAG, "tmfGetSecureProcessorVersion onSuccess");
                if (bytes != null) {
                    Log.e(TAG, "Version : " + new String(bytes));
                }
            }

            @Override
            public void onFailure(String s) {
                Log.e(TAG, "tmfGetSecureProcessorVersion onFailure " + s);
            }
        });
    }

    private void deleteFileFromDevice() {
        byte fileType = 0;
        api.tmfDeleteFileFromDevice(fileType, new BleComm.CommonCallBack() {
            @Override
            public void onSuccess(byte[] bytes) {
                Log.e(TAG, "tmfDeleteFileFromDevice onSuccess");
            }

            @Override
            public void onFailure(String s) {
                Log.e(TAG, "tmfDeleteFileFromDevice onFailure " + s);
            }
        });
    }

    private void setUsbEncryption(int mode) {
        api.tmfSetUSBEncryption(mode, new BleComm.CommonCallBack() {
            @Override
            public void onSuccess(byte[] bytes) {
                Log.e(TAG, "tmfSetUSBEncryption onSuccess");
            }

            @Override
            public void onFailure(String s) {
                Log.e(TAG, "tmfSetUSBEncryption onFailure " + s);
            }
        });
    }

}
