package de.badgersburrow.sciman.utilities;


import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class VScroll extends ScrollView {

    public VScroll(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public VScroll(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VScroll(Context context) {
        super(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }
}




/**import android.widget.ScrollView;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class VScroll extends ScrollView {
private float xDistance, yDistance, lastX, lastY;

public VScroll(Context context, AttributeSet attrs) {
    super(context, attrs);
}

@Override
public boolean onInterceptTouchEvent(MotionEvent ev) {
    switch (ev.getAction()) {
        case MotionEvent.ACTION_DOWN:
            xDistance = yDistance = 0f;
            lastX = ev.getX();
            lastY = ev.getY();
            break;
        case MotionEvent.ACTION_MOVE:
            final float curX = ev.getX();
            final float curY = ev.getY();
            xDistance += Math.abs(curX - lastX);
            yDistance += Math.abs(curY - lastY);
            lastX = curX;
            lastY = curY;
            if(xDistance > yDistance)
                return false;
    }

    return super.onInterceptTouchEvent(ev);
}
}**/

