package com.mvopo.floatingsms.Helper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mvopo.floatingsms.Model.Message;
import com.mvopo.floatingsms.R;

import java.util.List;

/**
 * Created by mvopo on 5/8/2018.
 */

public class ListAdapter extends ArrayAdapter {
    private Context mContext;
    private int layoutID;
    private List<Message> messages;

    public ListAdapter(@NonNull Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);

        mContext = context;
        layoutID = resource;
        messages = objects;
    }

    @Override
    public int getCount() {
        int size = 0;

        if (messages != null) size = messages.size();

        return size;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (layoutID == R.layout.message_in_layout) {
            if (messages.get(position).getType().equals("in"))
                convertView = LayoutInflater.from(mContext).inflate(R.layout.message_in_layout, parent, false);
            else
                convertView = LayoutInflater.from(mContext).inflate(R.layout.message_out_layout, parent, false);

            TextView messageBody = convertView.findViewById(R.id.message_body);
            messageBody.setText(messages.get(position).getBody());
        }else if (layoutID == R.layout.message_thread_layout){
            convertView = LayoutInflater.from(mContext).inflate(layoutID, parent, false);

            TextView image = convertView.findViewById(R.id.thread_image);
            TextView name = convertView.findViewById(R.id.thread_name);
            TextView body = convertView.findViewById(R.id.thread_body);

            Message message = messages.get(position);

            if(!message.getPerson().isEmpty()){
                image.setText(message.getPerson().substring(0, 1));
                name.setText(message.getPerson());
            }else{
                name.setText(message.getAddress());
            }

            if(message.getBody().length() > 30) body.setText(message.getBody().substring(0, 30) + "...");
            else body.setText(message.getBody());

        }

        return convertView;
    }
}
