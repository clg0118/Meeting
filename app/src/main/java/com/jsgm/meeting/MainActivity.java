package com.jsgm.meeting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.jsgm.meeting.dialog.BaseSuperDialog;
import com.jsgm.meeting.dialog.SuperDialog;
import com.jsgm.meeting.dialog.ViewConvertListener;
import com.jsgm.meeting.dialog.ViewHolder;
import com.smallbuer.jsbridge.core.BridgeHandler;
import com.smallbuer.jsbridge.core.CallBackFunction;
import com.smallbuer.jsbridge.core.X5WebView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private X5WebView x5WebView;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        initUi();
    }

    private void initUi() {
        x5WebView = findViewById(R.id.webview);

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        x5WebView.getSettings().setUseWideViewPort(true);
        x5WebView.getSettings().setLoadWithOverviewMode(true);
        String userAgent = x5WebView.getSettings().getUserAgentString();
        x5WebView.getSettings().setUserAgent(userAgent + "; meeting");
        x5WebView.getSettings().setAllowContentAccess(true);
        x5WebView.getSettings().setBuiltInZoomControls(false);
        String url = PreferenceUtils.getPrefString(mContext, "gm_url", "");
        if (TextUtils.isEmpty(url)){
            x5WebView.loadUrl("http://yunweigmwinlead.cn/meetingWX/do/openeqthome?roomId=1");
        } else {
            x5WebView.loadUrl(url);
        }

        x5WebView.setCustomHost("meeting");
        x5WebView.setShowProgress(true);

        x5WebView.addHandlerLocal("turnToRed", new BridgeHandler() {
            @Override
            public void handler(Context context, String data, CallBackFunction function) {
                adbcommand("echo w 0x04 > ./sys/devices/platform/led_con_h/zigbee_reset");
            }
        });
        x5WebView.addHandlerLocal("turnToGreen", new BridgeHandler() {
            @Override
            public void handler(Context context, String data, CallBackFunction function) {
                adbcommand("echo w 06 > ./sys/devices/platform/led_con_h/zigbee_reset");
            }
        });
        x5WebView.addHandlerLocal("turnToBlue", new BridgeHandler() {
            @Override
            public void handler(Context context, String data, CallBackFunction function) {
                adbcommand("echo w 0x05 > ./sys/devices/platform/led_con_h/zigbee_reset");
            }
        });
        x5WebView.addHandlerLocal("turnToSevenColor", new BridgeHandler() {
            @Override
            public void handler(Context context, String data, CallBackFunction function) {
                adbcommand("echo w 0x05 > ./sys/devices/platform/led_con_h/zigbee_reset");
            }
        });
        x5WebView.addHandlerLocal("turnToColor", new BridgeHandler() {
            @Override
            public void handler(Context context, String data, CallBackFunction function) {
                adbcommand("echo w " + data.replaceAll("\"", "") + "> ./sys/devices/platform/led_con_h/zigbee_reset");
            }
        });
        x5WebView.addHandlerLocal("setUrl", new BridgeHandler() {
            @Override
            public void handler(Context context, String data, CallBackFunction function) {

            }
        });
    }

    private void showDialog() {
        SuperDialog.init().setLayoutId(R.layout.dialog_layout)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    public void convertView(ViewHolder holder, BaseSuperDialog dialog) {
                        String url = PreferenceUtils.getPrefString(mContext, "gm_url", "");
                        EditText editUrl = holder.getView(R.id.edit_url);
                        if (!TextUtils.isEmpty(url)) {
                            editUrl.setText(url);
                        }
                        holder.setOnClickListener(R.id.btn_cancel, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });
                        holder.setOnClickListener(R.id.btn_ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (!TextUtils.isEmpty(editUrl.getText().toString())) {
                                    PreferenceUtils.setPrefString(mContext, "gm_url", editUrl.getText().toString());
                                    x5WebView.loadUrl(editUrl.getText().toString());
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(mContext, "地址不能为空", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                })
                .setOutCancel(true)
                .setDimAmount(0.3f)
                .show(getSupportFragmentManager());
    }

    public String adbcommand(String command) {
        Process process = null;
        DataOutputStream os = null;
        String excresult = "";
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
            StringBuffer stringBuffer = new StringBuffer();
            String line = null;
            while ((line = in.readLine()) != null) {
                stringBuffer.append(line + " ");
            }
            excresult = stringBuffer.toString();
            Log.d("Jessica2 ", excresult);


            os.close();
            // System.out.println(excresult);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return excresult;
    }

}