package com.example.root.reportlocation;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ImageTextListBaseAdapterActivity extends Activity implements
        OnItemClickListener {

    ImageView monsterOne;
    ImageView monsterTwo;
    ImageView monsterThree;
    int[] selectedMonsterIDs = {0,0,0};
    int selectedMonster = 1;
    String url;

    public static final String[] titles = new String[] { "Vulcan",
            "Vulcan", "Snake", "Vulcan" };

    public static final String[] descriptions = new String[] {
            "BA Birdy. Monster 003",
            "BA Birdy. Monster 003",
            "Snakey. Monster 002",
            "BA Birdy. Monster 003" };

    public static final int[] monsterIds = new int[] {3, 3, 2, 3};

    public static final Integer[] images = { R.mipmap.bird,
            R.mipmap.bird, R.mipmap.snake, R.mipmap.bird };

    ListView listView;
    List<RowItem> rowItems;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monster_select);

        rowItems = new ArrayList<RowItem>();
        for (int i = 0; i < titles.length; i++) {
            RowItem item = new RowItem(images[i], titles[i], descriptions[i], monsterIds[i]);
            rowItems.add(item);
        }

        monsterOne = (ImageView) findViewById(R.id.imageView);
        monsterTwo = (ImageView) findViewById(R.id.imageView2);
        monsterThree = (ImageView) findViewById(R.id.imageView3);

        listView = (ListView) findViewById(R.id.list);
        CustomBaseAdapter adapter = new CustomBaseAdapter(this, rowItems);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {

        if(selectedMonster == 1) {
            monsterOne.setImageResource(rowItems.get(position).getImageId());
            selectedMonsterIDs[0] = rowItems.get(position).getMonsterId();
            selectedMonster++;
        }else if(selectedMonster == 2){
            monsterTwo.setImageResource(rowItems.get(position).getImageId());
            selectedMonsterIDs[1] = rowItems.get(position).getMonsterId();
            selectedMonster++;
        }else if(selectedMonster == 3){
            monsterThree.setImageResource(rowItems.get(position).getImageId());
            selectedMonsterIDs[2] = rowItems.get(position).getMonsterId();
            selectedMonster++;
            url = "http://ec2-52-1-68-245.compute-1.amazonaws.com/add_pokemon.php?pn=" + Player.getId() +
                    "&one=" + selectedMonsterIDs[0] + "&two=" + selectedMonsterIDs[1] + "&three=" + selectedMonsterIDs[2];
            try {
                final String s = new RequestTaskMonsterAdding().execute(url).get(8000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                e.printStackTrace();
            }

            Intent intent = new Intent(getBaseContext(), MainGameActivity.class);
            startActivityForResult(intent, 0);
        }

    }
}

class RequestTaskMonsterAdding extends AsyncTask<String, String, String> {

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