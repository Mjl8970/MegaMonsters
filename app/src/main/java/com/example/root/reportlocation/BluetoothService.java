package com.example.root.reportlocation;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by qemu on 2/21/2015.
 */
public class BluetoothService
{
	// app name and identifier id
	private static final String mAppName = "Pokemon";
	private static final UUID mUUID = UUID.fromString("7ff29267-b894-486b-8eb3-403f682c3fb7");

	// bluetooth
	private BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter();

	// handlers
	private Handler readHandle;

	// threads
	private ListenThread listener;
	private JoinThread joiner;
	private ConnectedThread connected;
	private boolean isConnect = false;

	public BluetoothService(Handler rHandle)
	{
		readHandle = rHandle;
	}

	private void connectionChange(boolean connect)
	{
		if (connect == isConnect)
		{
			return;
		}

		int message = connect ? Constants.CONNECTED : Constants.DISCONNECTED;
		readHandle.obtainMessage(message).sendToTarget();
		isConnect = connect;
	}

	public void listen()
	{
		listener = new ListenThread();
		listener.start();
	}

	public void join(BluetoothDevice host)
	{
		joiner = new JoinThread(host);
		joiner.start();
	}

	public void connect(BluetoothSocket socket)
	{
		connectionChange(true);
		connected = new ConnectedThread(socket);
		connected.start();
	}

	public void killAll()
	{
		if (listener != null)
		{
			listener.cancel();
			listener = null;
		}

		if (joiner != null)
		{
			joiner.cancel();
			joiner = null;
		}

		if (connected != null)
		{
			connected.cancel();
			connected = null;
		}
	}

	public void write(char type, float val)
	{
		if (connected == null)
		{
			return;
		}

		connected.write(type, val);
	}


	/**
	 * This thread will make the app listen and wait for a connection
	 * will remain in this thread until connection is established
	 */
	private class ListenThread extends Thread
	{
		private final BluetoothServerSocket srvSocket;

		public ListenThread()
		{
			BluetoothServerSocket tmp = null;
			try
			{
				tmp = mBtAdapter.listenUsingRfcommWithServiceRecord(mAppName, mUUID);
			}
			catch (IOException e)
			{
				connectionChange(false);
			}
			srvSocket = tmp;
		}

		public void run()
		{
			BluetoothSocket socket = null;
			while (true)
			{
				try
				{
					socket = srvSocket.accept();
				}
				catch (IOException e)
				{
					connectionChange(false);
				}
				if (socket != null)
				{
					connect(socket);
					cancel();
					break;
				}
			}
		}

		public void cancel()
		{
			try
			{
				srvSocket.close();
			}
			catch (IOException e)
			{
			}
		}
	}

	private class JoinThread extends Thread
	{
		private final BluetoothSocket socket;
		private final BluetoothDevice srvDevice;

		public JoinThread(BluetoothDevice device)
		{
			BluetoothSocket tmp = null;
			srvDevice = device;
			try
			{
				tmp = srvDevice.createRfcommSocketToServiceRecord(mUUID);
			}
			catch (IOException e)
			{
				connectionChange(false);
			}
			socket = tmp;
		}

		public void run()
		{
			mBtAdapter.cancelDiscovery();
			try
			{
				socket.connect();
			}
			catch (IOException e)
			{
				connectionChange(false);
			}
			connect(socket);
		}

		public void cancel()
		{
			try
			{
				socket.close();
			}
			catch (IOException e)
			{
			}
		}
	}

	private class ConnectedThread extends Thread
	{
		private final BluetoothSocket connection;
		private final DataInputStream in;
		private final DataOutputStream out;

		public ConnectedThread(BluetoothSocket socket)
		{
			connection = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;
			try
			{
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			}
			catch (IOException e)
			{
				connectionChange(false);
			}
			in = new DataInputStream(tmpIn);
			out = new DataOutputStream(tmpOut);
		}

		public void run()
		{
			char type;
			float value;

			while (true)
			{
				try
				{
					type = in.readChar();
					value = in.readFloat();
					readHandle.obtainMessage(type, 0, 0, value).sendToTarget();
					// try to send bytes to activity using a message
				}
				catch (IOException e)
				{
					Log.e("exception", e.getMessage());
					connectionChange(false);
					break;
                }
			}
		}

		public void write(char type, float value)
		{
			try
			{
				out.writeChar(type);
				out.writeFloat(value);
			}
			catch (IOException e)
			{
				Log.e("exception2", e.getMessage());
			}
		}

		public void cancel()
		{
			try
			{
				connection.close();
				in.close();
				out.close();
			}
			catch (IOException e)
			{
			}
		}
	}
}
