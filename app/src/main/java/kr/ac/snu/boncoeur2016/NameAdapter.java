package kr.ac.snu.boncoeur2016;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by hyes on 2016. 4. 25..
 */
public class NameAdapter extends ArrayAdapter<NameItem> {
    private Context context;
    private int layoutResourceId;
    private ArrayList<NameItem> nameList;


    public NameAdapter(Context context, int resource, ArrayList<NameItem> nameList) {
        super(context, resource, nameList);

        this.context = context;
        this.layoutResourceId = resource;
        this.nameList = nameList;
    }

    public View getView(int position, View convertView, ViewGroup parent){

        View row = convertView;

        if(row == null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

        }

        TextView id_tv = (TextView) row.findViewById(R.id.id_tv);
        TextView name_tv = (TextView) row.findViewById(R.id.name_tv);


        id_tv.setText("" + nameList.get(position).getId());
        name_tv.setText(nameList.get(position).getName());




        return row;

    }


}
