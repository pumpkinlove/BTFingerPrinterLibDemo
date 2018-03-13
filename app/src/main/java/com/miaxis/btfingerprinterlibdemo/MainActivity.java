package com.miaxis.btfingerprinterlibdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.miaxis.btfingerprinterlib.BleComm;
import com.miaxis.btfingerprinterlib.MxBlePrinterApi;

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
    private Button btnGetEncryptMode;


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
        btnGetEncryptMode = findViewById(R.id.btn_get_encrypt_mode);
        btnGetEncryptMode.setOnClickListener(this);

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
            case R.id.btn_get_encrypt_mode:
                getEncryptMode();
                break;
        }
    }

    private void init() {
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
        api.getFinger(new BleComm.GetFingerCallBack() {
            @Override
            public void onGetFingerSuccess(byte[] bytes) {
                if (bytes != null) {
                    Log.e(TAG, "onGetFingerSuccess length :" + bytes.length);
                    tvFinger.setText(Base64.encodeToString(bytes, Base64.DEFAULT));
                } else {
                    Log.e(TAG, "onGetFingerSuccess length : 0");
                }
            }

            @Override
            public void onGetFingerFailure(String s) {
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
        api.print(content, new BleComm.PrintCallBack() {
            @Override
            public void onPrintSuccess() {
                Log.e(TAG, "onPrintSuccess");
            }

            @Override
            public void onPrintFailure(String s) {
                Log.e(TAG, "onPrintFailure " + s);
            }
        });
    }

    private void scrollPaper(int num) {
        api.scrollPaper(num, new BleComm.ScrollPaperCallBack() {
            @Override
            public void onScrollSuccess() {
                Log.e(TAG, "onScrollSuccess");
            }

            @Override
            public void onScrollFailure(String s) {
                Log.e(TAG, "onScrollFailure " + s);
            }
        });
    }

    private void cancelGetFinger() {
        api.cancelDevice(new BleComm.CancelFingerCallBack() {
            @Override
            public void onCancelSuccess() {
                Log.e(TAG, "onCancelSuccess");
            }

            @Override
            public void onCancelFailure(String s) {
                Log.e(TAG, "onCancelFailure " + s);
            }
        });
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

    private void getEncryptMode() {
        api.getEncryptMode(new BleComm.CommonCallBack() {
            @Override
            public void onSuccess(byte[] responseData) {
                Toast.makeText(MainActivity.this, "EncryptMode" + responseData[0], Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String s) {
                Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
