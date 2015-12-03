package com.example.raghuveer.georeference;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;

/**
 * Created by raghuveer on 11/24/2015.
 */
@ParseClassName("Task")
public class Task extends ParseObject {
    public Task(){}

    public String getUser(){
        return getString("user");
    }

    public void setUser(ParseUser user){
        put("user", user);
    }

    public String getTaskId(){
        return getObjectId();
    }

    public void setTaskId(String task_id){
        put("task_id", task_id);
    }

    public String getTaskName(){
        return getString("task_name");
    }

    public void setTaskName(String task_name){
        put("task_name", task_name);
    }

    public String getTaskType(){
        return getString("task_type");
    }

    public void setTaskType(String task_type){
        put("task_type", task_type);
    }

    public Date getExpiryDate(){
        return getDate("expiry_date");
    }

    public void setExpiryDate(Date expiry_date){
        put("ExpiryDate", expiry_date);
    }

    public String getTaskID(){
        return getObjectId();
    }

}
