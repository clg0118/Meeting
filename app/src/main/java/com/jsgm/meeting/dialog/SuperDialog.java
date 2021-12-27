package com.jsgm.meeting.dialog;


import android.os.Bundle;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;


public class SuperDialog extends BaseSuperDialog {

    private ViewConvertListener convertListener;

    public static SuperDialog init() {
        return new SuperDialog();
    }

    @Override
    public int intLayoutId() {
        return layoutId;
    }

    @Override
    public void convertView(ViewHolder holder, BaseSuperDialog dialog) {
        if (convertListener != null) {
            convertListener.convertView(holder, dialog);
        }
    }

    public SuperDialog setLayoutId(@LayoutRes int layoutId) {
        this.layoutId = layoutId;
        return this;
    }

    public SuperDialog setConvertListener(ViewConvertListener convertListener) {
        this.convertListener = convertListener;
        return this;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (savedInstanceState != null) {
//            convertListener = (ViewConvertListener) savedInstanceState.getSerializable("listener");
//        }
    }

    /**
     * 保存接口
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putSerializable("listener", convertListener);
    }
}
