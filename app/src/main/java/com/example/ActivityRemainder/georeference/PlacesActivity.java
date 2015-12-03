package com.example.ActivityRemainder.georeference;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class PlacesActivity extends AppCompatActivity {
    List<Place> placeList;
    PlacesAdapter placesAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

        placesAdapter = new PlacesAdapter(this, new ArrayList<Place>());
        final ListView place_list_view = (ListView) findViewById(R.id.listView);
        setTitle(getIntent().getStringExtra("task_name") + "-Reminder");
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Notifications");
        query.whereEqualTo("TaskID", getIntent().getStringExtra("task_id"));
        Log.d("task_id", getIntent().getStringExtra("task_id"));
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(final ParseObject object, ParseException e) {
                if (object != null) {
                    String notification_id = object.getObjectId();
                    Log.d("not_id", notification_id);
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Notification_Places_Mapping");
                    query.whereEqualTo("NotificationID", object);
                    query.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {
                            if (objects.size() > 0) {
                                Log.d("place_size", objects.size() + "");
                                placeList = new ArrayList<>();
                                if(placeList.size() > 0){
                                    placeList.clear();
                                }
                                for (int i = 0; i < objects.size(); i++) {
                                    ParseObject parseObject = (ParseObject) objects.get(i).get("PlaceID");
                                    Log.d("placeID", parseObject.getObjectId());
                                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Places");
                                    query.whereEqualTo("objectId", parseObject.getObjectId());
                                    query.findInBackground(new FindCallback<ParseObject>() {
                                        @Override
                                        public void done(List<ParseObject> objects, ParseException e) {
                                            if (e == null) {
                                                Place place = new Place();
                                                place.setId(objects.get(0).getObjectId());
                                                place.setIcon(objects.get(0).getString("icon"));
                                                place.setLatitude(Double.parseDouble(objects.get(0).getString("latitude")));
                                                place.setLongitude(Double.parseDouble(objects.get(0).getString("longitude")));
                                                place.setName(objects.get(0).getString("name"));
                                                placesAdapter.add(place);
                                                placeList.add(place);
                                                place_list_view.setAdapter(placesAdapter);
                                            } else {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }

                            }
                        }
                    });
                }
            }
        });

        place_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?daddr=" + placeList.get(position).getLatitude() + "," + placeList.get(position).getLongitude()));
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_places, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
