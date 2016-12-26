package org.dcxz.designdigger.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.felipecsl.gifimageview.library.GifImageView;


/**
 * <br/>
 * Created by OvO on 2016/12/26.<br/>
 * ChangeLog :
 * <pre>
 * </pre>
 */

public class AutoHeightGifImageView extends GifImageView {

    private Drawable mDrawable = null;
    private static int mWidth = 0;

    public AutoHeightGifImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoHeightGifImageView(Context context) {
        super(context);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        mDrawable = getDrawable();
        if (mWidth != 0) {
            setAutoHeight();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mWidth == 0) {
            mWidth = getMeasuredWidth();
            if (mDrawable != null) {
                setAutoHeight();
            }
        }
    }

    private void setAutoHeight() {
        float scale = mDrawable.getMinimumHeight() / (float) mDrawable.getMinimumWidth();
        float height = mWidth * scale;
        if (getParent() instanceof RelativeLayout) {
            setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int) height));
        } else if (getParent() instanceof LinearLayout) {
            setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) height));
        }
    }
}