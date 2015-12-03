package com.example.ActivityRemainder.georeference;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TaskAdapter.Reload{
    EditText task_name;
    TaskAdapter taskAdapter;
    ListView listView;
    boolean started;
    ArrayList<Task> global_list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent i=new Intent(MainActivity.this,LocationService.class);
        startService(i);
        started = true;
        listView = (ListView) findViewById(R.id.list_view_tasks);
        taskAdapter = new TaskAdapter(this, new ArrayList<Task>());

        setTitle("To-Do");

        refreshList();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, PlacesActivity.class);
                intent.putExtra("task_id", global_list.get(position).getObjectId());
                startActivity(intent);
            }
        });

        task_name = (EditText) findViewById(R.id.edit_text_task_name);

        findViewById(R.id.plus_image_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (task_name.getText().length() == 0) {
                    Toast.makeText(MainActivity.this, "Please enter a task!", Toast.LENGTH_SHORT).show();
                } else {
                    final LinearLayout layout = new LinearLayout(MainActivity.this);
                    layout.setOrientation(LinearLayout.VERTICAL);
                    layout.setId(100+0);
                    final Button button_pic_cat = new Button(getApplicationContext());
                    button_pic_cat.setId(100 + 1);
                    button_pic_cat.setText(R.string.pick_category);
                    button_pic_cat.setGravity(Gravity.CENTER_HORIZONTAL);
                    final DatePicker datePicker = new DatePicker(getApplicationContext());
                    datePicker.setId(100 + 2);
                    datePicker.setForegroundGravity(Gravity.CENTER_HORIZONTAL);
                    layout.addView(button_pic_cat);
                    layout.addView(datePicker);
                    button_pic_cat.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder listBuilder = new AlertDialog.Builder(layout.getContext());
                            listBuilder.setTitle(R.string.pick_category).setItems(Categories.categories, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    button_pic_cat.setText(Categories.categories[which]);
                                }
                            }).show();
                        }
                    });
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle(task_name.getText().toString()).setView(layout).setPositiveButton("Add", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Task task = new Task();
                            //task.setUser(ParseUser.getCurrentUser());
                            task.setTaskName(task_name.getText().toString());
                            task.setTaskType(button_pic_cat.getText().toString());
                            task.setExpiryDate(getDateFromDatePicker(datePicker));
                            task.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(com.parse.ParseException e) {
                                    refreshList();
                                    task_name.setText("");
                                }
                            });
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // Do nothing.
                        }
                    }).show();
                }
            }
        });
    }

    public static java.util.Date getDateFromDatePicker(DatePicker datePicker){
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year =  datePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        return calendar.getTime();
    }

    public void refreshList(){
        ParseQuery<Task> query = ParseQuery.getQuery(Task.class);
        //query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<Task>() {

            @Override
            public void done(List<Task> tasks, com.parse.ParseException error) {
                if (tasks != null) {
                    taskAdapter.clear();
                    taskAdapter.addAll(tasks);
                    global_list = new ArrayList<>(tasks);
                }
            }
        });
        listView.setAdapter(taskAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_turn_on) {
            if(!started) {
                Intent i = new Intent(MainActivity.this, LocationService.class);
                startService(i);
                started = true;
            }
            else{
                Toast.makeText(MainActivity.this, "Notifications are already turned on!", Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        if(id == R.id.action_turn_off){
            if(started) {
                Intent i = new Intent(MainActivity.this, LocationService.class);
                stopService(i);
                started = false;
            }
            else{
                Toast.makeText(MainActivity.this, "Notifications are already turned off!", Toast.LENGTH_SHORT).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void refresh() {
        refreshList();
    }
}
