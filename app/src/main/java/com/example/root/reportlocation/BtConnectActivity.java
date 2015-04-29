package com.example.root.reportlocation;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Set;


public class BtConnectActivity extends ActionBarActivity implements AdapterView.OnItemClickListener
{
	private ArrayAdapter<String> mDeviceList;
	private ListView mListView;
	private BluetoothAdapter mBtAdapter;

	@Override
	protected void onCreate(Bundle savedInstance)
	{
		super.onCreate(savedInstance);
		setContentView(R.layout.activity_bt_connect);
		setResult(Activity.RESULT_CANCELED);

		mDeviceList = new ArrayAdapter<String>(this, R.layout.bt_text);
		mListView = (ListView) findViewById(R.id.device_view);

		mListView.setAdapter(mDeviceList);
		mListView.setOnItemClickListener(this);

		// get paired devices
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		Set<BluetoothDevice> mDeviceSet = mBtAdapter.getBondedDevices();

		if (mDeviceSet.size() > 0)
		{
			findViewById(R.id.device_text).setVisibility(View.VISIBLE);

			for (BluetoothDevice device : mDeviceSet)
			{
				mDeviceList.add(device.getName() + '\n' + device.getAddress());
			}
		}
		else
		{
			mDeviceList.add("Please pair at least one bluetooth device\n");
		}
	}

	public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3)
	{
		mBtAdapter.cancelDiscovery();

		// string from clicked menu choice
		String value = ((TextView) v).getText().toString();

		// extract mac address only, 17 chars after
		String address = value.substring(value.length() - 17);

		Intent intent = new Intent();
		intent.putExtra(Constants.DEVICE_MAC, address);

		setResult(Activity.RESULT_OK, intent);
		finish();
	}
}
