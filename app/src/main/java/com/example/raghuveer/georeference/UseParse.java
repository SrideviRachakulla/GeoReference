package com.example.raghuveer.georeference;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;

/**
 * Created by raghuveer on 11/24/2015.
 */
public class UseParse extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "8MsDvrc2bdq2fBNT2Y6nBMZyF8OAll27A6uCevAA", "IHuU0YLS7jkk8jlv5kb9nMqMCCqwAxPbdVyQ2wsh");
        ParseInstallation.getCurrentInstallation().saveInBackground();
        ParsePush.subscribeInBackground(ParseInstallation.getCurrentInstallation().getInstallationId());
        ParseObject.registerSubclass(Task.class);
    }
}
