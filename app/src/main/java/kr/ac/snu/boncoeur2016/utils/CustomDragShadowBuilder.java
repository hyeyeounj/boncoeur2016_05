package kr.ac.snu.boncoeur2016.utils;

import android.graphics.Point;
import android.view.View;

public class CustomDragShadowBuilder extends View.DragShadowBuilder {

    // ------------------------------------------------------------------------------------------
// Private attributes :
    private Point _offset;
// ------------------------------------------------------------------------------------------


    // ------------------------------------------------------------------------------------------
// Constructor :
    public CustomDragShadowBuilder(View view, Point offset) {

        // Stores the View parameter passed to myDragShadowBuilder.
        super(view);

        // Save the offset :
        _offset = offset;
    }
// ------------------------------------------------------------------------------------------


    // ------------------------------------------------------------------------------------------
// Defines a callback that sends the drag shadow dimensions and touch point back to the system.
    @Override
    public void onProvideShadowMetrics(Point size, Point touch) {

        // Set the shadow size :
        size.set(getView().getWidth() * 2, getView().getHeight() * 2);
//        size.set( ( int )( getView().getWidth() + getView().getResources().getDimension( R.dimen.position_button_size ) ), ( int )( getView().getHeight() + getView().getResources().getDimension( R.dimen.position_button_size ) ) );

        // Sets the touch point's position to be in the middle of the drag shadow
        touch.set(_offset.x, _offset.y);
    }
// ------------------------------------------------------------------------------------------
}