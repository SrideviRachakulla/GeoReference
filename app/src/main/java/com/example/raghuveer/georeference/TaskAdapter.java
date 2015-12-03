package com.example.raghuveer.georeference;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by raghuveer on 11/24/2015.
 */
public class TaskAdapter extends ArrayAdapter<Task> {
    Context mContext;
    List<Task> mtaskList;
    Task task;
    public TaskAdapter(Context context, List<Task> taskList) {
        super(context, R.layout.task_layout, taskList);
        mContext = context;
        mtaskList = taskList;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        if(convertView == null){
            LayoutInflater mLayoutInflater = LayoutInflater.from(mContext);
            convertView = mLayoutInflater.inflate(R.layout.task_layout, null);
        }

        task = mtaskList.get(position);
        TextView descriptionView = (TextView) convertView.findViewById(R.id.text_view_task_name);
        descriptionView.setText(task.getTaskName());
        return convertView;
    }
}
