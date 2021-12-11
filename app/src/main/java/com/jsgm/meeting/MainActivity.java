package com.jsgm.meeting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.smallbuer.jsbridge.core.BridgeHandler;
import com.smallbuer.jsbridge.core.CallBackFunction;
import com.smallbuer.jsbridge.core.X5WebView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private X5WebView x5WebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUi();
    }

    private void initUi(){
        x5WebView = findViewById(R.id.webview);
        x5WebView.getSettings().setUseWideViewPort(true);
        x5WebView.getSettings().setLoadWithOverviewMode(true);
        String userAgent = x5WebView.getSettings().getUserAgentString();
        x5WebView.getSettings().setUserAgent(userAgent + "; meeting");
        x5WebView.getSettings().setAllowContentAccess(true);
        x5WebView.getSettings().setBuiltInZoomControls(false);
        x5WebView.loadUrl("http://yunweigmwinlead.cn/meetingWX/do/openeqthome?roomId=1");

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
                adbcommand("echo w 0x06 > ./sys/devices/platform/led_con_h/zigbee_reset");
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
                adbcommand("echo w " + data + "> ./sys/devices/platform/led_con_h/zigbee_reset");
            }
        });

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
            Log.d("Jessica2 " , excresult);


            os.close();
            // System.out.println(excresult);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return excresult;
    }

}