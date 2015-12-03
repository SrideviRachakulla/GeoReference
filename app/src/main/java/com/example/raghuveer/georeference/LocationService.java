package com.example.raghuveer.georeference;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.SyncStateContract;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.vision.barcode.Barcode;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,LocationListener {
    String KEY="AIzaSyAqYcUqLaqZ5PYMyDm19salHbIOlT-V4-k";
    List<Task> lstTasks;
    List<ParseObject> Notifications;
     GoogleApiClient mGoogleApiClient;
     Location mCurrentLocation;
    Context context;
    LocationManager locationManager;
    double mLatitudeText,mLongitudeText ;
    Geocoder geocoder;
    List<Address> addresses = null;
     String NotificationID,PlaceID;
    List<Task> lstTask;
    boolean sent=false;
    int count = 0;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this,"service created",Toast.LENGTH_SHORT).show();
        context=this;

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApiIfAvailable(LocationServices.API)
                .build();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this,"service started",Toast.LENGTH_SHORT).show();

        mGoogleApiClient.connect();

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        try{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, TimeUnit.MINUTES.toMillis(1), 100, this);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        geocoder = new Geocoder(this, Locale.getDefault());

        if (mCurrentLocation != null) {
            // Determine whether a Geocoder is available.
            Log.d("service", "current location not null");

        }

            return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
        try {
            locationManager.removeUpdates(this);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        locationManager = null;
        Toast.makeText(this,"service destroyed",Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.d("service", "about to get current location ");
        Location mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mCurrentLocation != null) {
            Log.d("service", "got it finally");
            mLatitudeText=mCurrentLocation.getLatitude();
            mLongitudeText=mCurrentLocation.getLongitude();
        }
            if (!Geocoder.isPresent()) {
                Toast.makeText(this, "no_geocoder_available",
                        Toast.LENGTH_LONG).show();
            } else {
                try {
                    addresses = geocoder.getFromLocation(mLatitudeText,mLongitudeText,1);


                } catch (IOException ioException) {
                    // Catch network or other I/O problems.

                    Log.d("service", "service not available"+ ioException);
                } catch (IllegalArgumentException illegalArgumentException) {
                    // Catch invalid latitude or longitude values.
                    Log.d("service", "invalid lat and lon" + ". " +
                            "Latitude = " + mLatitudeText +
                            ", Longitude = " +
                            mLongitudeText+ illegalArgumentException);
                }

                // Handle case where no address was found.
                if (addresses == null || addresses.size()  == 0) {
                    Log.d("service", "no address found");
                } else {
                    Address address = addresses.get(0);
                    ArrayList<String> addressFragments = new ArrayList<String>(); //do something with this retrned addres

                    // Fetch the address lines using getAddressLine,
                    // join them, and send them to the thread.
                    for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                        addressFragments.add(address.getAddressLine(i));
                    }
                    Toast.makeText(this, "address found"+addressFragments.toString(),
                            Toast.LENGTH_LONG).show();
                    Log.d("service", "address found"+addressFragments.toString());


                }
            }
        }

    private List<Task> getActiveTasks() {
        final List<Task> tasks=new ArrayList<Task>();
        lstTask=new ArrayList<Task>();
        try{
        ParseQuery<Task> query = ParseQuery.getQuery("Task");
            query.whereNotEqualTo("task_type", "");
            query.whereGreaterThanOrEqualTo("ExpiryDate", Calendar.getInstance().getTime());
            lstTask=query.find();
            return lstTask;

         }
        catch(Exception ex){
            ex.printStackTrace();
        }


        return null;
    }

    public class Dowork extends AsyncTask<Void,Void,ArrayList<Place>>{
        String TaskID,TaskType;

        public Dowork(String taskID,String TaskType) {
            this.TaskID = taskID;
            this.TaskType=TaskType;
        }

        @Override
        protected void onPostExecute(final ArrayList<Place> places) {
            super.onPostExecute(places);

            final ParseObject msg;
            if(places!=null){
                Notifications=new ArrayList<>();
                msg= new ParseObject("Notifications");
                msg.put("TaskID", TaskID);
                final ParseObject finalMsg = msg;
                msg.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            NotificationID = finalMsg.getObjectId();
                            ParseQuery<Task> task_query = ParseQuery.getQuery(Task.class);
                            task_query.whereEqualTo("objectId", TaskID);
                            task_query.findInBackground(new FindCallback<Task>() {
                                    @Override
                                    public void done(List<Task> objects, ParseException e) {
                                        new SetNotification().execute(TaskID, objects.get(0).getTaskName());
                                    }
                                });
                            loadPlaces(places);

                        }
                    }
                });
            }
        }

        private void loadPlaces(final ArrayList<Place> places) {
            int count = 0;
            for (Place pl : places) {
                if (count++ > 1) break;
                ParseObject msg = new ParseObject("Places");
                msg.put("latitude", pl.getLatitude().toString());
                msg.put("longitude", pl.getLongitude().toString());
                msg.put("name", pl.getName().toString());
                msg.put("vicinity", pl.getVicinity().toString());
                msg.put("icon", pl.getIcon().toString());
                final ParseObject finalMsg1 = msg;
                msg.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            PlaceID = finalMsg1.getObjectId();
                            ParseObject msg=new ParseObject("Notification_Places_Mapping");
                            msg.put("NotificationID", ParseObject.createWithoutData("Notifications",NotificationID));
                            msg.put("PlaceID", ParseObject.createWithoutData("Places",PlaceID));
                            Notifications.add(msg);
                            if(Notifications.size()==1){
                                try {
                                    for(ParseObject obj:Notifications) {
                                       obj.save();
                                    }
                                } catch (ParseException e1) {
                                    e1.printStackTrace();
                                }
                                Notifications.clear();
                                return;
                            }
                        }
                    }
                });

            }
        }

        @Override
        protected ArrayList<Place> doInBackground(Void... params) {
            PlaceService service = new PlaceService(KEY);
            ArrayList<Place> findPlaces = service.findPlaces(mLatitudeText, // 28.632808
                    mLongitudeText, TaskType); // 77.218276
                     return findPlaces;
        }

    }

    public class SetNotification extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(String... params) {
            Intent intent = new Intent(LocationService.this, PushReceiver.class);
            intent.putExtra("task_id", params[0]);
            intent.putExtra("task_name", params[1]);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), PendingIntent.getBroadcast(LocationService.this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT));
            return null;
        }
    }

    private void removeLinking() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Notification_Places_Mapping");
        List<ParseObject> lstLinks=new ArrayList<ParseObject>();
        try {

            lstLinks=query.find();
            ParseObject.deleteAll(lstLinks);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    private void removeExisitingNotifications() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Notifications");
        List<ParseObject> lstLinks=new ArrayList<ParseObject>();
        try {

            lstLinks=query.find();
            ParseObject.deleteAll(lstLinks);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    private void removeExistingPlaces() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Places");
        List<ParseObject> lstLinks=new ArrayList<ParseObject>();
        try {

            lstLinks=query.find();
            ParseObject.deleteAll(lstLinks);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onConnectionSuspended(int i) {
        Log.d("service", "connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("service", "connection failed");
    }

    @Override
    public void onLocationChanged(Location location) {
        lstTasks=new ArrayList<Task>();
        lstTasks=getActiveTasks();
        this.mCurrentLocation = location;
        mLatitudeText=mCurrentLocation.getLatitude();
        mLongitudeText=mCurrentLocation.getLongitude();
        removeLinking();
        removeExisitingNotifications();
        removeExistingPlaces();
        for(Task task : lstTasks) {
            new Dowork(task.getTaskID(),task.getTaskType()).execute();
        }
        //sendNotification();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
