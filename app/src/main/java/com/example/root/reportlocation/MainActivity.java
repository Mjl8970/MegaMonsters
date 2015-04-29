package com.example.root.reportlocation;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MainActivity extends ActionBarActivity {

    Button btnCreateUser;
    //Button btnUpdateLocation;
    Button btnLogin;
    EditText nameField;
    EditText pwField;
    String logonResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnCreateUser = (Button) findViewById(R.id.create_user);
        btnLogin = (Button) findViewById(R.id.login);
        nameField = (EditText) findViewById(R.id.nameField);
        pwField = (EditText) findViewById(R.id.pwField);

        btnCreateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RequestTask().execute("http://ec2-52-1-68-245.compute-1.amazonaws.com/create_user.php?pn="
                        +nameField.getText().toString()+"&pw="+pwField.getText().toString()+"&id=1234&dt=1&tid=1");
                Intent intent = new Intent(v.getContext(), ImageTextListBaseAdapterActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://ec2-52-1-68-245.compute-1.amazonaws.com/logon.php?id="+nameField.getText().toString() + "&pw=" + pwField.getText().toString();
                try {
                    final String s = new RequestTask2().execute(url).get(8000, TimeUnit.MILLISECONDS);
                } catch (InterruptedException | TimeoutException | ExecutionException e) {
                    e.printStackTrace();
                }
                if ((logonResults.charAt(0) == '[') && logonResults != null) {
                    System.out.println(logonResults);
                    Intent intent = new Intent(v.getContext(), MainGameActivity.class);
                    startActivityForResult(intent, 0);
                    Player.name = nameField.getText().toString();
                }
                System.out.println(logonResults);
                pwField.getText().clear();

            }
        });
    }

    class RequestTask2 extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... uri) {
            String result = "";
//the year data to send);

//http post
            try{
                HttpClient httpclient = new DefaultHttpClient();
                System.out.println("http://ec2-52-1-68-245.compute-1.amazonaws.com/logon.php?id="+nameField.getText().toString() + "&pw=" + pwField.getText().toString());
                HttpPost httppost = new HttpPost("http://ec2-52-1-68-245.compute-1.amazonaws.com/logon.php?id="+nameField.getText().toString() + "&pw=" + pwField.getText().toString());
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                InputStream is = entity.getContent();

                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    is.close();

                    result = sb.toString();
                } catch (IOException e) {
                    Log.e("log_tag", "Error converting result " + e.toString());
                }


            }catch(Exception e){
                Log.e("log_tag", "Error in http connection " + e.toString());
            }
//convert response to string

//parse json data
            logonResults = result;
            // just in case
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //Do anything with response..
        }

    }

}

class RequestTask extends AsyncTask<String, String, String> {

    @Override
    protected String doInBackground(String... uri) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        String responseString = null;
        try {
            response = httpclient.execute(new HttpGet(uri[0]));
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                responseString = out.toString();
                out.close();
            } else{
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (ClientProtocolException e) {
            //TODO Handle problems..
        } catch (IOException e) {
            //TODO Handle problems..
        }
        return responseString;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        //Do anything with response..
    }
}