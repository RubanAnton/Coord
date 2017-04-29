package com.example.antonruban.gpstracker;


import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import static com.example.antonruban.gpstracker.GPSTracker.latitude;
import static com.example.antonruban.gpstracker.GPSTracker.longitude;

public class MainActivity extends AppCompatActivity {

    File directory;
    final int REQUEST_CODE_VIDEO = 2;
    final String TAG_ = "myLogs";
    public static Uri uri;
    public  static GPSTracker gps;
    TextView hashView;
    public  static boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createDirectory();
        hashView = (TextView) findViewById(R.id.textView);

    }
    public void Location(View view) {
        gps = new GPSTracker(MainActivity.this);
        if (gps.canGetLocation()) {
            latitude =  gps.getLatitude();
            longitude =  gps.getLongitude();
            Toast.makeText(getApplicationContext(), "Location - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        } else {
            gps.showSettingsAlert();
        }
    }
    public void ClickVideo(View view) {
        uri = generateFileUri();
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, REQUEST_CODE_VIDEO);
    }
    public void ClickHash(View view) {
        getHash(uri);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        if (requestCode == REQUEST_CODE_VIDEO) {
            if (resultCode == RESULT_OK) {
                if (intent == null) {
                    Toast.makeText(this, "Intetnt is null", Toast.LENGTH_SHORT).show();
                } else {

                    Log.d(TAG_, "Video uri: " + intent.getData());
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Cancle", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private Uri generateFileUri() {
        File file = new File(directory.getPath() + "/" + "video_" + System.currentTimeMillis() + ".mp4");
        Log.d(TAG_, "fileName = " + file);
        return Uri.fromFile(file);
    }
    private void createDirectory() {
        directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), "Lab2");
        if (!directory.exists())
            directory.mkdirs();
    }
    private void getHash(Uri uri){

        try {
            Log.d("Hash", "start");
            Date d = new Date();
            long startTime = d.getTime();

            File file = new File(uri.getPath());
            byte[] data = Hash.getHashFromFile(file);
            String value = new String(data, "UTF-16");
            hashView.setText(value);

            d = new Date();
            long time = d.getTime() - startTime;
            Log.d("Hash", "end " + time + " s");
        }catch (Exception e){
        }
    }
    public  void HashString (View vie){
        try{
            Date d = new Date();
            long startTime = d.getTime();
            File file  = new File("/Users/antonruban/Desktop/hash.txt");
            byte[] data = Hash.getHashFromFile(file);
            String value = new String(data,"UTF-16");
//           String HexStr = Hex.encodeHexString(value.getBytes(String.valueOf(data)));
            hashView.setText(value);

            d = new Date();
            long time = d.getTime() - startTime;
            Log.d("Hash", "end " + time + " s");
        }catch (Exception e){e.printStackTrace();}

    }

    //stop
    public void onClickPost(View view) throws InterruptedIOException {
        SaveCoordToServer cord_to_server=new SaveCoordToServer();
        cord_to_server.execute();
    }

    //export coord on server with asynctask
    public void onStopClick(View view){
        flag = false;
    }
        class SaveCoordToServer extends AsyncTask<Void,Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            flag = true;
            while (flag) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.accumulate("device_id", android_id);
                    jsonObject.accumulate("coords", gps.getLatitude());
                    jsonObject.accumulate("coords", gps.getLongitude());
                    jsonObject.accumulate("is_hash", false);

                    String data = jsonObject.toString();
                    Log.d("json data", data);
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost("http://openlab.hopto.org:3000/coords");
                    StringEntity se = new StringEntity(data);
                    httpPost.setEntity(se);
                    httpPost.setHeader("Accept", "application/json");
                    httpPost.setHeader("Content-type", "application/json");
                    HttpResponse httpResponse = httpclient.execute(httpPost);
                    Log.d("RESPONSE FROM SERVER", httpResponse.getEntity().getContent().toString());


                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            return null;
        }

    }
}

