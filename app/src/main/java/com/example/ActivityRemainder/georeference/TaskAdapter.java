package com.example.ActivityRemainder.georeference;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

/**
 * Created by raghuveer on 11/24/2015.
 */
public class TaskAdapter extends ArrayAdapter<Task> {
    Context mContext;
    List<Task> mtaskList;
    Task task;
    Reload mreload;
    public TaskAdapter(MainActivity context, List<Task> taskList) {
        super(context, R.layout.task_layout, taskList);
        mContext = context;
        mtaskList = taskList;
        mreload=context;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        if(convertView == null){
            LayoutInflater mLayoutInflater = LayoutInflater.from(mContext);
            convertView = mLayoutInflater.inflate(R.layout.task_layout, null);
        }

        task = mtaskList.get(position);
        TextView descriptionView = (TextView) convertView.findViewById(R.id.text_view_task_name);
        descriptionView.setText(task.getTaskName());
        final View finalConvertView = convertView;
        convertView.findViewById(R.id.btnDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               task.deleteInBackground();
                mreload.refresh();
            }
        });
        return convertView;
    }
    public interface Reload{
        public void refresh();
    }
}
