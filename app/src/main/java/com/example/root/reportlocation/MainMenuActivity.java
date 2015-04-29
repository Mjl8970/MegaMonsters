package com.example.root.reportlocation;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainMenuActivity extends ActionBarActivity implements View.OnClickListener
{
	public final String button1Txt = "Listen";
	public final String button2Txt = "Connect";
	public final String button3Txt = "Test";
	public String debug = "";

	private TextView mDebugText;

	private BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter();


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);

		mDebugText = (TextView) findViewById(R.id.textView);

		Button button1 = (Button) findViewById(R.id.button);
		Button button2 = (Button) findViewById(R.id.button2);
		Button button3 = (Button) findViewById(R.id.button3);

		button1.setOnClickListener(this);
		button2.setOnClickListener(this);
		button3.setOnClickListener(this);
	}

	@Override
	public void onClick(View v)
	{
		if (v.getId() == R.id.button)
		{
			Intent intent = new Intent(this, GameActivity.class);
			debug = "Listening";
			intent.putExtra(Constants.CONNECT_TYPE, Constants.LISTEN_FOR_CONNECT);
			mDebugText.setText(debug);
			startActivity(intent);
		}
		else if (v.getId() == R.id.button2)
		{
			debug = "Connecting";
			Intent intent = new Intent(this, BtConnectActivity.class);
			startActivityForResult(intent, Constants.GET_MAC_ADDR);

			mDebugText.setText(debug);
			return;
		}
		else
		{
			Intent intent = new Intent(this, GameActivity.class);
			debug = "Test";
			intent.putExtra(Constants.CONNECT_TYPE, Constants.DO_NOT_CONNECT);
			mDebugText.setText(debug);
			startActivity(intent);
		}

	}

	public void onActivityResult(int req_code, int result_code, Intent data)
	{
		if (result_code == Activity.RESULT_OK && req_code == Constants.GET_MAC_ADDR)
		{
			Intent intent = new Intent(this, GameActivity.class);
			String MAC_ADDR = data.getStringExtra(Constants.DEVICE_MAC);
			intent.putExtra(Constants.DEVICE_MAC, MAC_ADDR);
			intent.putExtra(Constants.CONNECT_TYPE, Constants.CONNECT_TO_HOST);
			startActivity(intent);

		}
	}
	//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main_menu, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
