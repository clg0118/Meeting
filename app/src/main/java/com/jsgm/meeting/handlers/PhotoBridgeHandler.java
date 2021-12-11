package com.jsgm.meeting.handlers;

import android.content.Context;
import android.widget.Toast;

import com.smallbuer.jsbridge.core.BridgeHandler;
import com.smallbuer.jsbridge.core.CallBackFunction;

public class PhotoBridgeHandler extends BridgeHandler {
    @Override
    public void handler(Context context,String data, CallBackFunction function) {

        Toast.makeText(context,"data:"+data,Toast.LENGTH_SHORT).show();

        function.onCallBack("{\"status\":\"0\"}");

    }
}
