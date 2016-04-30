package kr.ac.snu.boncoeur2016;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.ImageView;

import kr.ac.snu.boncoeur2016.utils.Define;

/**
 * Created by hyes on 2016. 3. 17..
 */
public class DragListener implements View.OnDragListener {
    String dragData, name;
    int id;
    Context context;
    float x, y;

    public DragListener(Context context, String name, int id) {
        super();
        this.context = context;
        this.name = name;
        this.id = id;
    }

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

                float size = (int) (v.getResources().getDimension(R.dimen.position_button_size) / 2);
                if(dragData.equals(Define.POS_TAG_T)) {
                    x = event.getX();
                    y = event.getY();
                    ImageView test = (ImageView)v.findViewById(R.id.pos_t);
                    test.layout((int) (x - size), (int) (y - size), (int) (x + size), (int) (y + size));
                    updatePosition(dragData);
                }else if(dragData.equals(Define.POS_TAG_M)) {
                    x = event.getX();
                    y = event.getY();
                    ImageView test = (ImageView)v.findViewById(R.id.pos_m);
                    test.layout((int) (x - size), (int) (y - size), (int) (x + size), (int) (y + size));
                    updatePosition(dragData);
                }else if(dragData.equals(Define.POS_TAG_A)) {
                    x = event.getX();
                    y = event.getY();
                    ImageView test = (ImageView)v.findViewById(R.id.pos_a);
                    test.layout((int) (x - size), (int) (y - size), (int) (x + size), (int) (y + size));
                    updatePosition(dragData);
                }else if(dragData.equals(Define.POS_TAG_P)) {
                    x = event.getX();
                    y = event.getY();
                    ImageView test = (ImageView)v.findViewById(R.id.pos_p);
                    test.layout((int) (x - size), (int) (y - size), (int) (x + size), (int) (y + size));
                    updatePosition(dragData);
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

    private void updatePosition(String dragData) {
        Dao dao = new Dao(context);
        reCalc(x, y);
        dao.updatePosition(dragData, x, y, name, id);
    }

    private void reCalc(float x, float y) {
        
    }
}
