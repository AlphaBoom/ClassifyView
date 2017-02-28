package com.anarchy.classifyview.sample.ireader.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.anarchy.classify.ClassifyView;
import com.anarchy.classifyview.R;

/**
 * Version 2.1.1
 * <p>
 * Date: 17/1/10 15:46
 * Author: rsshinide38@163.com
 */

public class IReaderClassifyView extends ClassifyView {
    public IReaderClassifyView(Context context) {
        super(context);
    }

    public IReaderClassifyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IReaderClassifyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected Dialog createSubDialog() {
        Dialog dialog = new Dialog(getContext(), R.style.SubDialogStyle);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.height = getHeight();
        layoutParams.dimAmount = 0.6f;
        layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;
        layoutParams.format = PixelFormat.TRANSPARENT;
        layoutParams.windowAnimations = R.style.BottomDialogAnimation;
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return dialog;
    }

    @Override
    protected View getSubContent() {
        return inflate(getContext(), R.layout.extra_ireader_sub_content, null);
    }
}
