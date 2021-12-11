package com.jsgm.meeting;

import android.app.Application;

import com.jsgm.meeting.handlers.HandlerName;
import com.jsgm.meeting.handlers.PhotoBridgeHandler;
import com.jsgm.meeting.handlers.RequestBridgeHandler;
import com.jsgm.meeting.handlers.ToastBridgeHandler;
import com.smallbuer.jsbridge.core.Bridge;
import com.smallbuer.jsbridge.core.BridgeHandler;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.smtt.export.external.TbsCoreSettings;
import com.tencent.smtt.sdk.QbSdk;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initX5();
        initJsBridgeHandler();
    }

    private void initX5(){
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(getApplicationContext());
        strategy.setCrashHandleCallback(new CrashReport.CrashHandleCallback() {
            public Map<String, String> onCrashHandleStart(
                    int crashType,
                    String errorType,
                    String errorMessage,
                    String errorStack) {

                LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
                String x5CrashInfo = com.tencent.smtt.sdk.WebView.getCrashExtraMessage(getApplicationContext());
                map.put("x5crashInfo", x5CrashInfo);
                return map;
            }
            @Override
            public byte[] onCrashHandleStart2GetExtraDatas(
                    int crashType,
                    String errorType,
                    String errorMessage,
                    String errorStack) {
                try {
                    return "Extra data.".getBytes("UTF-8");
                } catch (Exception e) {
                    return null;
                }
            }

        });
        //AppId 需要在bugly 申请
//        CrashReport.initCrashReport(getApplicationContext(), "APPID", true, strategy);
        // 在调用TBS初始化、创建WebView之前进行如下配置
        HashMap map = new HashMap();
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER, true);
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE, true);
        QbSdk.initTbsSettings(map);
    }
    private void initJsBridgeHandler(){
        HashMap<String, BridgeHandler> handlerMap = new  HashMap<String,BridgeHandler>();
        handlerMap.put(HandlerName.HANDLER_NAME_TOAST,new ToastBridgeHandler());
        handlerMap.put(HandlerName.HANDLER_NAME_PHOTO,new PhotoBridgeHandler());
        handlerMap.put(HandlerName.HANDLER_NAME_REQUEST,new RequestBridgeHandler());
        Bridge.INSTANCE.registerHandler(handlerMap);
    }

}
