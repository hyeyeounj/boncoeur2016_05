package kr.ac.snu.boncoeur2016;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Created by hyes on 2016. 3. 19..
 */
public class DragShadow extends View.DragShadowBuilder {

    private static Drawable shadow;


    public DragShadow(View v) {
        super(v);
        shadow = new ColorDrawable(Color.LTGRAY);
    }


    @Override
    public void onProvideShadowMetrics (Point size, Point touch){
        // Defines local variables
        int width, height;

        // Sets the width of the shadow to half the width of the original View
        width = getView().getWidth();

        // Sets the height of the shadow to half the height of the original View
        height = getView().getHeight();

        // The drag shadow is a ColorDrawable. This sets its dimensions to be the same as the
        // Canvas that the system will provide. As a result, the drag shadow will fill the
        // Canvas.
        shadow.setBounds(0, 0, width, height);

        // Sets the size parameter's width and height values. These get back to the system
        // through the size parameter.
        size.set(width, height);

        // Sets the touch point's position to be in the middle of the drag shadow
        touch.set(width, height);
    }

    @Override
    public void onDrawShadow(Canvas canvas) {
        shadow.draw(canvas);
    }
}
