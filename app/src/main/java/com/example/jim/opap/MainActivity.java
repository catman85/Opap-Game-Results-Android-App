package com.example.jim.opap;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.support.v7.widget.LinearLayoutManager;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ArrayList<ExampleItem> mExampleList = new ArrayList<>();
    private RequestQueue mRequestQueue;
    private ExampleAdapter mExampleAdapter = new ExampleAdapter(MainActivity.this, mExampleList);
    private String game;

    private static String dayOfMonth;
    private static String month;
    private static String year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Ανανέωση Λίστας", Snackbar.LENGTH_SHORT)
//                        .setAction("Action", null).show();
                Toast.makeText(MainActivity.this, "Ανανέωση Λίστας...", Toast.LENGTH_SHORT).show();
                refresh();
            }
        });

        FloatingActionButton logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent back;
                back = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(back);
            }
        });

        FloatingActionButton date = findViewById(R.id.date);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("date", "Date FAB clicked");
                showDatePicker(view);
            }
        });


        DatePicker dp = findViewById(R.id.datePicker);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dp.setOnDateChangedListener(dateChangedListener);
        }


        //welcome message
        Bundle extras = getIntent().getExtras();
        TextView userName = (TextView) findViewById(R.id.textView);
        if (extras != null) {
            String welcome = "Καλωσήρθες " + extras.getString("userName");
            userName.setText(welcome);
        }


        //Game spinner drowpdown
        Spinner staticSpinner = (Spinner) findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner
        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter.createFromResource(this, R.array.game_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        staticAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        staticSpinner.setAdapter(staticAdapter);
        staticSpinner.setOnTouchListener(spinnerOnTouch);
        staticSpinner.setOnKeyListener(spinnerOnKey);
        //--Listeners
        staticSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Here we change the selected game
                game = (String) parent.getItemAtPosition(position);
                Log.v("selected", game);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
                refresh();

            }
        });


    }

    private View.OnTouchListener spinnerOnTouch = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                //Your code
                Log.v("touch", "Touch"+ game);
            }
            return false;
        }
    };
    private static View.OnKeyListener spinnerOnKey = new View.OnKeyListener() {
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                //your code
                return true;
            } else {
                return false;
            }
        }
    };
    //--Listeners-END

        private void parseJSON(String game,final ArrayList<ExampleItem> mList) {
        Log.v("parse", game);

        String url = "http://applications.opap.gr/DrawsRestServices/" + game + "/last.json";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //Object <-- Object
                            JSONObject draw = response.getJSONObject("draw");

                            //Array <-- Object
                            JSONArray results = draw.getJSONArray("results");

                            for (int i = 0; i < results.length(); i++) {

                                //value at Index of Array
                                try {
                                    int hit = results.getInt(i);
                                    mExampleList.add(new ExampleItem(i, hit));
                                }catch(JSONException e){
                                    //We don't know if that thing we are getting is an int or a String
                                    String hit = results.getString(i);
                                    mExampleList.add(new ExampleItem(i, hit));
                                }

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        mRequestQueue.add(request);
        }

    private void parseJsonDate(String game,final ArrayList<ExampleItem> mList,String dateFormat) {
        Log.v("parse", game);

        String url = "http://applications.opap.gr/DrawsRestServices/" + game + "/drawDate/"+ dateFormat +".json";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //Object <-- Object
                            JSONObject draws = response.getJSONObject("draws");

                            JSONArray draw = draws.getJSONArray("draw");

                            Log.v("date","Draw Size:" + String.valueOf(draw.length()));

                            for (int i = 0; i < draw.length(); i++) {

                                JSONObject num = draw.getJSONObject(i);

                                //this makes a list item with the timestamp of the game (one day has multiple games inside)
                                String drawTime = num.getString("drawTime");
                                String[] parts = drawTime.split("T");
                                String part0 = parts[0]; // 004
                                String part1 = parts[1]; // 034556
                                mExampleList.add(new ExampleItem(part0,part1));

                                JSONArray results = num.getJSONArray("results");

                                Log.v("date","results Size:" + String.valueOf(results.length()));

                                for (int j = 0; j < results.length(); j++) {


                                    //value at Index of Array
                                    try {
                                        int hit = results.getInt(j);
                                        mExampleList.add(new ExampleItem(j, hit));
                                    } catch (JSONException e) {
                                        //We don't know if that thing we are getting is an int or a String
                                        String hit = results.getString(j);
                                        mExampleList.add(new ExampleItem(j, hit));
                                    }

                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        mRequestQueue.add(request);
    }



    public void onTextClick(View v) {
//        refresh current Activity
//        Intent intent = getIntent();
//        finish();
//        startActivity(intent);
    }

    public static String getDateFormat(){
            return dayOfMonth+"-"+month+"-"+year;
    }



    private void refresh(){
        RecyclerView mRecyclerView =(RecyclerView) MainActivity.this.findViewById(R.id.recycler_view);
        mRecyclerView.setAdapter(new ExampleAdapter(MainActivity.this, mExampleList));
        mRecyclerView.setHasFixedSize(true);//???
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        mRequestQueue = Volley.newRequestQueue(MainActivity.this);


        //TODO if date is modified call parseJsonDate
        if( dayOfMonth == null || month == null || year == null) {
            parseJSON(game, mExampleList = new ArrayList<>());
        }else{
            parseJsonDate(game, mExampleList = new ArrayList<>(),getDateFormat());
            Log.v("date","parseJsonDate Launched");
        }

        //???
        mRecyclerView.invalidate();
        mExampleAdapter.notifyDataSetChanged();
        mExampleList.clear();//???
    }

    public void showDatePicker(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "date picker");
    }


    //TODO not working
    private DatePicker.OnDateChangedListener dateChangedListener =
            new DatePicker.OnDateChangedListener(){
                @Override
                public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {
                    Log.v("date",getDateFormat());

                    Toast.makeText(MainActivity.this, "picked date is " + datePicker.getDayOfMonth() +
                            " / " + (datePicker.getMonth()+1) +
                            " / " + datePicker.getYear(), Toast.LENGTH_SHORT).show();
                }
            };

    public static void setDayOfMonth(String dayOfMonth) {
        MainActivity.dayOfMonth = dayOfMonth;
    }

    public static void setMonth(String month) {
        MainActivity.month = month;
    }

    public static void setYear(String year) {
        MainActivity.year = year;
    }
}
