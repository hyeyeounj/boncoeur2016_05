package kr.ac.snu.boncoeur2016;

import android.content.ClipData;
import android.content.ClipDescription;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.ImageView;

import kr.ac.snu.boncoeur2016.utils.Define;

/**
 * Created by hyes on 2016. 3. 17..
 */
public class DragListener implements View.OnDragListener {

    String dragData;

    @Override
    public boolean onDrag(View v, DragEvent event) {
        final int action = event.getAction();

        switch(action){
            case DragEvent.ACTION_DRAG_STARTED:
                if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                    return true;
                }
                return false;
            case DragEvent.ACTION_DRAG_ENTERED:
                return true;
            case DragEvent.ACTION_DROP:
                ClipData.Item item = event.getClipData().getItemAt(0);
                dragData = item.getText().toString();
                Log.d("test", "Dragged data is " + dragData);
                v.setVisibility(View.VISIBLE);
                v.invalidate();

                if(dragData.equals(Define.POS_TAG_T)) {
                    float x = event.getX();
                    float y = event.getY();
                    ImageView test = (ImageView)v.findViewById(R.id.pos_t);
                    test.layout((int)x-55, (int)y-55, (int)x+5, (int)y+5);

                }else if(dragData.equals(Define.POS_TAG_M)) {
                    float x = event.getX();
                    float y = event.getY();
                    ImageView test = (ImageView)v.findViewById(R.id.pos_m);
                    test.layout((int)x-55, (int)y-55, (int)x+5, (int)y+5);
                }else if(dragData.equals(Define.POS_TAG_A)) {
                    float x = event.getX();
                    float y = event.getY();
                    ImageView test = (ImageView)v.findViewById(R.id.pos_a);
                    test.layout((int)x-55, (int)y-55, (int)x+5, (int)y+5);
                }else if(dragData.equals(Define.POS_TAG_P)) {
                    float x = event.getX();
                    float y = event.getY();
                    ImageView test = (ImageView)v.findViewById(R.id.pos_p);
                    test.layout((int)x-55, (int)y-55, (int)x+5, (int)y+5);
                }


                return true;
            case DragEvent.ACTION_DRAG_ENDED:

                v.invalidate();

                if (event.getResult()) {
                    Log.d("test",  "The drop was handled.");

                } else {
                    Log.d("test", "The drop was failed.");
                }
                return true;
            default:
                Log.e("DragDrop Example","Unknown action type received by OnDragListener.");
                break;
        }
        return false;
    }
}
