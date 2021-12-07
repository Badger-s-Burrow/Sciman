package de.badgersburrow.sciman.utilities;

/**import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

public class HScroll extends HorizontalScrollView {

    public HScroll(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public HScroll(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HScroll(Context context) {
        super(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }
}

**/



import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;

public class PictureLink extends ImageView {
private int rownumber;

public PictureLink(Context context, AttributeSet attrs) {
    super(context, attrs);
}

public void setRownumber(int Rownumber){
	rownumber=Rownumber;
}

public int getRownumber(){
	return rownumber;
}
}

