package com.jsgm.meeting.dialog;

import java.io.Serializable;

public interface ViewConvertListener extends Serializable {
    void convertView(ViewHolder holder, BaseSuperDialog dialog);
}
