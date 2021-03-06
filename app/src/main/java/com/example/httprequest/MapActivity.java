package com.example.httprequest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.String;
import cz.msebera.android.httpclient.Header;

import static com.example.httprequest.MainActivity.APP_PREFER;

public class MapActivity extends AppCompatActivity implements LocationListener {
    private ArrayList<String> report = new ArrayList<String>();
    private TextView textView5;
    private TextView txt;
//    EditText lat;
//    EditText lon;
//    EditText idr;
//    EditText detail;
    private LocationManager locationManager;
    public static final String USERNAME_PREFER = "usernamePref";
    String locationlat = "";
    String locationlon = "";
    String oo ;
    String bb ;
    String ID = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Intent intent = getIntent();
        ID = intent.getExtras().getString("idRental");
//        TextView txt6 = (TextView) findViewById(R.id.textView6);
//        txt6.setVisibility(View.VISIBLE);

//        txt = (TextView) findViewById(R.id.textView10);
//        txt.setText(ID);

        Spinner spin = (Spinner) findViewById(R.id.spinner2);
        ArrayAdapter<String> Adap = new ArrayAdapter<String>(MapActivity.this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.names));
        Adap.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spin.setAdapter(Adap);

        BottomNavigationView btm2 = findViewById(R.id.btm);
        btm2.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        int id = menuItem.getItemId();
                        if(id == R.id.action_rent){
                            Intent intent = new Intent(getApplicationContext(), FirstActivity.class);
                            startActivity(intent);
                        }else if(id == R.id.action_problem){
                            Intent intent = new Intent(getApplicationContext(), NextActivity.class);
                            startActivity(intent);
                        }else if(id == R.id.action_pro){
//                            SharedPreferences sharePrefer = getSharedPreferences(MainActivity.APP_PREFER,
//                                    Context.MODE_PRIVATE);
//                            SharedPreferences.Editor editor = sharePrefer.edit();
//                            editor.clear();  // ทำการลบข้อมูลทั้งหมดจาก preferences
//                            editor.commit();  // ยืนยันการแก้ไข preferences
                            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                            startActivity(intent);
                            finish();
                            return true;
                        }
                        return true;
                    }
                }
        );

        textView5 = (TextView) findViewById(R.id.textView5);
        SharedPreferences sharedPrefer = getSharedPreferences(APP_PREFER, Context.MODE_PRIVATE);
        String sharePrefUsername = sharedPrefer.getString("usernamePref",null);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        final Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);

        onLocationChanged(location);


    }


    @Override
    public void onLocationChanged(Location location) {

        final double longtitude = location.getLongitude();
        final double lattitude = location.getLatitude();
//        textView5.setText("Latitude: " + lattitude + "\n" + "Longtitude: " + longtitude);
        locationlat = location.getLatitude() + "";
        locationlon = location.getLongitude() + "";
        final EditText lat = (EditText) findViewById(R.id.lat);
        final EditText lon = (EditText) findViewById(R.id.lon);
        final EditText detail = (EditText) findViewById(R.id.detail);
        final Spinner sp = (Spinner) findViewById(R.id.spinner2);
        final EditText idr = (EditText) findViewById(R.id.idrent);

        idr.setEnabled(false);

        lat.setEnabled(false);
        lon.setEnabled(false);
//        lat.setVisibility(View.VISIBLE);
//        lon.setVisibility(View.VISIBLE);
        idr.setText(ID);
        Log.d(ID, "onLocationChanged: ");
        //params.add("lon",lon.getText(locationlat).toString());
        lat.setText(locationlat);
        lon.setText(locationlon);
        Button btn = (Button) findViewById(R.id.button4);
        btn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Boolean cancel = false;
                String prefix = "กรุณาระบุ";
                detail.setError(null);
                if (TextUtils.isEmpty(detail.getText().toString())){
                    cancel = true;
                    detail.setError(prefix + detail.getHint().toString());
                    detail.requestFocus();
                }

                if (cancel==false) {
                    RequestParams params = new RequestParams();
                    params.add("lon", oo = lon.getText().toString());
                    params.add("lat", bb = lat.getText().toString());
                    params.add("detail", detail.getText().toString());
                    params.add("Pro", sp.getSelectedItem().toString());
                    params.add("idrent", idr.getText().toString());
                    AsyncHttpClient http = new AsyncHttpClient();

                    http.post("http://gg.harmonicmix.xyz/Map_api/", params, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            JSONObject obj = null;
                            try {
                                obj = new JSONObject(response.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            String status = null;
                            try {
                                status = (String) obj.get("status");
                            } catch (JSONException e) {
                                e.printStackTrace();


                            }
                            if (status.equals("insert")) {
                                Toast.makeText(getApplicationContext(), "บันทึกข้อมูลเรียบร้อย", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), NextActivity.class);
                                startActivity(intent);


                            } else {
                                Toast.makeText(getApplicationContext(), "กรุณากรอกข้อมูลให้ครบ", Toast.LENGTH_LONG).show();
                            }

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            super.onFailure(statusCode, headers, responseString, throwable);
                            Log.d("onFailure", Integer.toString(statusCode));
                        }
                    });
                }
//        Toast.makeText(this, "Clicked on Button", Toast.LENGTH_LONG).show();
            }
        });

// Uri uri = Uri.parse(String.valueOf(lattitude+longtitude)+"?z=18");
// Intent intent = new Intent(Intent.ACTION_VIEW, uri);
// startActivity(Intent.createChooser(intent
// , "View map with"));

        Button buttonIntent = (Button)findViewById(R.id.button2);
        buttonIntent.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
// onLocationChanged(location);
                Uri uri = Uri.parse("geo:"+String.valueOf(longtitude+lattitude)+"?q="+String.valueOf(lattitude)+","+String.valueOf(longtitude));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(Intent.createChooser(intent
                        , "View map with"));

            }
        });
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
//    public void onInsert(View v)
//    {
//
//        RequestParams params = new RequestParams();
//        params.add("lat", lat.getText().toString());
//        params.add("lon",lon.getText().toString());
//        //params.add("password", ok2.getText().toString());
//
//        AsyncHttpClient http = new AsyncHttpClient();
//        http.post("http://gg.harmonicmix.xyz/Map_api/",params, new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                JSONObject obj = null;
//                try {
//                    obj = new JSONObject(response.toString());
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                String status = null;
//                try {
//                    status = (String) obj.get("status");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                if (status.equals("insert")) {
//                    Toast.makeText(getApplicationContext(), "ลงเบส", Toast.LENGTH_LONG).show();
////                    Intent intent = new Intent(getApplicationContext(), ShowActivity.class);
////                    startActivity(intent);
////                    isSuccess = true;
//
//                }
//                else {
//                    Toast.makeText(getApplicationContext(), "ไม่ลง", Toast.LENGTH_LONG).show();
//                }
//
//            }
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                super.onFailure(statusCode, headers, responseString, throwable);
//                Log.d("onFailure", Integer.toString(statusCode));
//            }
//        });
////        Toast.makeText(this, "Clicked on Button", Toast.LENGTH_LONG).show();
//    }
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_first, menu);
            return true;
        }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            SharedPreferences sharePrefer = getSharedPreferences(MainActivity.APP_PREFER,
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharePrefer.edit();
            editor.clear();  // ทำการลบข้อมูลทั้งหมดจาก preferences
            editor.commit();  // ยืนยันการแก้ไข preferences
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
