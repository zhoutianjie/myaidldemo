package com.czy.client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.czy.myapplication.IOnOperationCompletedListener;
import com.czy.myapplication.IOperationManager;
import com.czy.myapplication.Parameter;

public class MainActivity extends AppCompatActivity {

    private Button mBtnRegister;
    private Button mBtnUnregister;
    private Button mBtnOperation;
    private EditText mEditTextParam1;
    private EditText mEditTextParam2;
    private EditText mEditTextResult;


    private IOperationManager iOperationManager;
    private IOnOperationCompletedListener listener = new IOnOperationCompletedListener.Stub() {
        @Override
        public void onOperationCompleted(final Parameter result) throws RemoteException {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mEditTextResult.setText(""+result.getParam());
                }
            });
        }

    };

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            iOperationManager = IOperationManager.Stub.asInterface(iBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            iOperationManager = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        bindService();
    }

    private void bindService() {
        Intent intent = new Intent();
        intent.setClassName("com.czy.myapplication", "com.czy.myapplication.AIDLService");
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void initView() {
        mBtnRegister = findViewById(R.id.btn_registerListener);
        mBtnUnregister = findViewById(R.id.btn_unregisterListener);
        mBtnOperation = findViewById(R.id.btn_operation);
        mEditTextParam1 = findViewById(R.id.et_param1);
        mEditTextParam2 = findViewById(R.id.et_param2);
        mEditTextResult = findViewById(R.id.et_result);

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(iOperationManager!=null){
                    try {
                        iOperationManager.registerListener(listener);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        mBtnUnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(iOperationManager!=null){
                    try {
                        iOperationManager.unregisterListener(listener);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        mBtnOperation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(mEditTextParam1.getText()) || TextUtils.isEmpty(mEditTextParam2.getText())) {
                    return;
                }
                final int param1 = Integer.valueOf(mEditTextParam1.getText().toString());
                final int param2 = Integer.valueOf(mEditTextParam2.getText().toString());

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Parameter parameter1 = new Parameter(param1);
                        Parameter parameter2 = new Parameter(param2);
                        if (iOperationManager != null) {
                            try {
                                iOperationManager.operation(parameter1, parameter2);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(serviceConnection!=null){
            unbindService(serviceConnection);
        }
    }
}
