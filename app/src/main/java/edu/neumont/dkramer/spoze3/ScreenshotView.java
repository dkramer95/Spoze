package edu.neumont.dkramer.spoze3;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by dkramer on 12/3/17.
 */

public class ScreenshotView extends View {
    private Bitmap mBitmap;

    public ScreenshotView(Context context) {
        super(context);
        init();
    }

    public ScreenshotView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScreenshotView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected void init() {
        setWillNotDraw(false);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#901CF4"));
        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
    }
}
