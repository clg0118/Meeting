package com.smallbuer.jsbridge.core;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

import com.tencent.smtt.sdk.WebView;

/**
 * Created By XuQian
 * on 2021/4/16
 * describe:
 */
public class X5WebViewUtil {
    /**
     * webView 截图
     * @param mWebView
     * @return
     */
    public static Bitmap webViewShot(WebView mWebView) {
        try {
            mWebView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            mWebView.layout(0, 0, mWebView.getMeasuredWidth(), mWebView.getMeasuredHeight());

            //&emsp;开启WebView的缓存(当开启这个开关后下次调用getDrawingCache()方法的时候会把view绘制到一个bitmap上)
            mWebView.setDrawingCacheEnabled(true);
            //&emsp;强制绘制缓存（必须在setDrawingCacheEnabled(true)之后才能调用，否者需要手动调用destroyDrawingCache()清楚缓存）
            mWebView.buildDrawingCache();
            Bitmap longImage = Bitmap.createBitmap(mWebView.getMeasuredWidth(),
                    mWebView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            // 画布的宽高和 WebView 的网页保持一致
            Canvas canvas = new Canvas(longImage);
            Paint paint = new Paint();
            canvas.drawBitmap(longImage, 0, mWebView.getMeasuredHeight(), paint);

            float scaleX =(float) mWebView.computeHorizontalScrollRange()/(float) mWebView.getContentWidth();
            float scaleY = (float) mWebView.computeVerticalScrollRange()/(float) mWebView.getContentHeight();
            float scale = Math.min(scaleX,scaleY);

            Bitmap x5Bitmap = Bitmap.createBitmap(mWebView.computeHorizontalScrollRange(), mWebView.computeVerticalScrollRange(), Bitmap.Config.ARGB_8888);
            Canvas x5Canvas = new Canvas(x5Bitmap);
            x5Canvas.drawColor(Color.WHITE);
            // 少了这行代码就无法正常生成长图
            if(mWebView.getX5WebViewExtension() != null) {
                mWebView.getX5WebViewExtension().snapshotWholePage(x5Canvas, false, false);
                Matrix matrix = new Matrix();
                matrix.setScale(scale, scale);
                canvas.drawBitmap(x5Bitmap, matrix, paint);
                return longImage;
            }else {
                //&emsp;将WebView绘制在刚才创建的画板上
                mWebView.draw(canvas);
                mWebView.setDrawingCacheEnabled(false);
                mWebView.destroyDrawingCache();
                return longImage;
            }
        } catch (Exception e) {
            Log.e("TAG", "webViewShot: ", e);
        }
        return null;
    }
}
