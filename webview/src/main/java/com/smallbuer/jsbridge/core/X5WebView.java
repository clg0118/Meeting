package com.smallbuer.jsbridge.core;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ProgressBar;


import androidx.annotation.Nullable;

import com.tencent.smtt.export.external.interfaces.JsPromptResult;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import java.util.HashMap;
import java.util.Map;

/**
 * Created By XuQian
 * on 2021/4/14
 * describe:
 */
public class X5WebView extends WebView implements IWebView {
    private static long lastTime = 0;
    private static long diffTime = 1000;
    private String TAG = "X5WebView";
    private BridgeTiny bridgeTiny;
    private Map<String, BridgeHandler> mLocalMessageHandlers = new HashMap<>();

    private String mCustomHost;
    private boolean isIgnoreSslError = false;
    private onSslErrorInterface mSslErrorInterface;
    private onPageProgressInterface mPageProgressInterface;

    private Context mContext;

    private boolean isShowProgress;

    private ProgressBar mProgressBar;

    private boolean isStart = false;


    @SuppressLint("SetJavaScriptEnabled")
    public X5WebView(final Context context, AttributeSet arg1) {
        super(context, arg1);
        mContext = context;
        this.setWebViewClient(client);
        this.setWebChromeClient(chromeClient);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            android.webkit.WebView.enableSlowWholeDocumentDraw();
        }
        iniProgressBar(context);
        initWebViewSettings();
        this.getView().setClickable(true);
        bridgeTiny = new BridgeTiny(this);


    }

    private void iniProgressBar(Context context){
        mProgressBar = new ProgressBar(context,null, android.R.attr.progressBarStyleHorizontal);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 10);
        mProgressBar.setLayoutParams(layoutParams);
        mProgressBar.setProgress(0);
        addView(mProgressBar);
        mProgressBar.setVisibility(GONE);

    }

    private void initWebViewSettings() {
        WebSettings webSetting = this.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setSupportZoom(true);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(true);
        // webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true);
        // webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        // webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSetting.setCacheMode(WebSettings.LOAD_DEFAULT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSetting.setMixedContentMode(WebSettings.LOAD_NORMAL);
        }

        setDrawingCacheEnabled(true);
        buildDrawingCache();

        // this.getSettingsExtension().setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);//extension
        // settings 的设计
    }

    private WebViewClient client = new WebViewClient() {
        /**
         * prevent system browser from launching when web page loads
         */
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.i(TAG, "shouldOverrideUrlLoading: "+url+"\n"+mCustomHost);
            if (!(TextUtils.isEmpty(url))&&url.startsWith(mCustomHost)){
                Log.i(TAG, "shouldOverrideUrlLoading: 浏览器处理");
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                getContext().startActivity(intent);
                return true;
            }
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView webView, String s) {
            super.onPageFinished(webView, s);
            bridgeTiny.webViewLoadJs((IWebView) webView);
        }

        @Override
        public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
            if (isIgnoreSslError){
                sslErrorHandler.proceed();
            }else {
                if (mSslErrorInterface!=null){
                    mSslErrorInterface.onError(webView,sslErrorHandler,sslError);
                }
                super.onReceivedSslError(webView, sslErrorHandler, sslError);
            }
        }

        @Override
        public void onPageStarted(WebView webView, String s, Bitmap bitmap) {
            super.onPageStarted(webView, s, bitmap);
            X5WebView.this.getSettings().setBlockNetworkImage(true);

        }

    };

    private WebChromeClient chromeClient = new WebChromeClient(){
        @Override
        public boolean onJsPrompt(WebView webView, String url,String message, String defaultValue, JsPromptResult jsPromptResult) {
            BridgeLog.d(TAG,"message->"+message);
            bridgeTiny.onJsPrompt(X5WebView.this,message);
            //don't delete this line
            jsPromptResult.confirm("do");
            return true;
        }

        @Override
        public void onProgressChanged(WebView webView, int i) {
            super.onProgressChanged(webView, i);
            if (i>=0&&!isStart){
                isStart = true;
                if (mPageProgressInterface!=null){

                    mPageProgressInterface.start();
                }
            }
            if (i==100){
                isStart = false;
                if (mPageProgressInterface!=null&&!isFastComplete()){
                    mPageProgressInterface.complete();
                }
                X5WebView.this.getSettings().setBlockNetworkImage(false);
                mProgressBar.setVisibility(GONE);
            }else {
                if (mProgressBar.getVisibility()==GONE&&isShowProgress){
                    mProgressBar.setVisibility(VISIBLE);
                }
                mProgressBar.setProgress(i);
            }
        }
    };





    @Override
    public void destroy() {
        super.destroy();
        bridgeTiny.freeMemory();
    }

    @Override
    public void addHandlerLocal(String handlerName, BridgeHandler bridgeHandler) {
        mLocalMessageHandlers.put(handlerName,bridgeHandler);
    }

    @Override
    public Map<String, BridgeHandler> getLocalMessageHandlers() {
        return mLocalMessageHandlers;
    }

    @Override
    public void evaluateJavascript(String var1,@Nullable Object object) {
        if(object == null){
            super.evaluateJavascript(var1, null);
            return;
        }
        super.evaluateJavascript(var1, (ValueCallback<String>) object);
    }

    @Override
    public void callHandler(String handlerName, Object data, OnBridgeCallback responseCallback) {
        bridgeTiny.callHandler(handlerName,data,responseCallback);
    }

    public void setProgressBar(ProgressBar progressBar) {
        mProgressBar = progressBar;
    }

    public ProgressBar getProgressBar() {
        return mProgressBar;
    }

    public void setShowProgress(boolean showProgress) {
        isShowProgress = showProgress;
        if (mProgressBar!=null){
            mProgressBar.setVisibility(showProgress?VISIBLE:GONE);
        }
    }

    public void setCustomHost(String customHost) {
        mCustomHost = customHost;
    }

    public void setIgnoreSslError(boolean ignoreSslError) {
        isIgnoreSslError = ignoreSslError;
    }

    public void setSslErrorInterface(onSslErrorInterface sslErrorInterface) {
        mSslErrorInterface = sslErrorInterface;
    }

    public void setPageProgressInterface(onPageProgressInterface pageProgressInterface) {
        mPageProgressInterface = pageProgressInterface;
    }

    private boolean isFastComplete() {
        long time = System.currentTimeMillis();
        long timeD = time - lastTime;
        if (timeD < diffTime) {
            return true;
        }
        lastTime = time;
        return false;
    }

    public interface onSslErrorInterface{
        void onError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError);
    }

    public interface onPageProgressInterface{
        void start();
        void complete();
    }



}
