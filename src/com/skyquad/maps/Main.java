package com.skyquad.maps;

import java.nio.ByteBuffer;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.skyquad.maps.BluetoothSerialService;

/**
 * This class provide the start screen. In this Activity the user load maps or
 * create new ones
 */
public class Main extends Activity {
	// Debugging
	public static final String TAG = "Main";
	public static final Boolean D = true;

	// Message types sent from the BluetoothReadService Handler
	public static final int MSG_STATE_CHANGE = 1;
	public static final int MSG_READ = 2;
	public static final int MSG_WRITE = 3;
	public static final int MSG_DEVICE_NAME = 4;
	public static final int MSG_TOAST = 5;

	// Key names received from the BluetoothChatService Handler
	public static final String DEVICE_NAME = "deviceName";
	public static final String TOAST = "toast";

	private static final int REQUEST_BT_ENABLE = 1;
	private static final int REQUEST_CONNECT_DEVICE = 2;

	private Button bConnect, bNewMap, bLoadMap;
	private TextView tvSelectedDevice, tvConnectionStatus;

	private String sRemoteDeviceMac;
	private BluetoothDevice btDevice;

	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothSerialService mSerialService;
	private Boolean fBluetoothAvailable;
	private ByteBuffer mMsgBuffer;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Init UI
		setContentView(R.layout.main);
		bConnect = (Button) findViewById(R.id.bConnect);
		bNewMap = (Button) findViewById(R.id.newMap);
		bLoadMap = (Button) findViewById(R.id.loadMap);
		tvSelectedDevice = (TextView) findViewById(R.id.tvSelectedDevice);
		tvConnectionStatus = (TextView) findViewById(R.id.tvConnectionStatus);

		bConnect.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View arg0) {
				Intent serverIntent = new Intent(Main.this,
						DeviceListActivity.class);
				startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
			}
		});

		bNewMap.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View arg0) {
				Intent intent = new Intent(Main.this, LocationChooser.class);
				startActivity(intent);
			}
		});
		bLoadMap.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
			}
		});

		mSerialService = new BluetoothSerialService(this, mHandlerBT,
				mMsgBuffer);

		// Test if Bluetooth is available
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			tvSelectedDevice.setText(R.string.bluetoothNotAvailable);
			fBluetoothAvailable = false;
		} else {
			tvSelectedDevice.setText(R.string.bluetoothNotSelected);
			fBluetoothAvailable = true;
		}

		requestEnableBT(true);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (D) Log.e(TAG, "--- ON RESUME ---");

		requestEnableBT(false);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		if (D) Log.e(TAG, "--- ON RESTART ---");

		requestEnableBT(true);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (D) Log.e(TAG, "--- ON DESTROY ---");

		if (mSerialService != null) mSerialService.stop();
	}

	// Updates BT-stat and requests the User to enable BT when askUser==true
	private void requestEnableBT(boolean askUser) {
		if (fBluetoothAvailable) {
			// Test if Bluetooth adapter is enabled
			if (!mBluetoothAdapter.isEnabled()) {
				if (askUser == true) {
					// adapter is disabled, ask user to enable it.
					Intent enableBtIntent = new Intent(
							BluetoothAdapter.ACTION_REQUEST_ENABLE);
					startActivityForResult(enableBtIntent, REQUEST_BT_ENABLE);
				} else {
					// adapter is disabled but request to enable is not set.
					tvConnectionStatus
							.setText(R.string.conStatBluetoothDisabled);
				}
			} else { // adapter is already enabled
				tvConnectionStatus.setText(R.string.conStatNotConnected);
			}
		} else { // no adapter available
			tvConnectionStatus.setText(R.string.conStatUnavailable);
		}
	}

	// Resulthandler for BT-Enable-Request.
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// Returned from "request bt enable" Activity
		switch (requestCode) {
		case REQUEST_BT_ENABLE:
			if (resultCode == RESULT_OK) {
				tvConnectionStatus.setText(R.string.conStatNotConnected);
			} else {
				tvConnectionStatus.setText(R.string.conStatBluetoothDisabled);
			}
			break;

		case REQUEST_CONNECT_DEVICE:
			requestEnableBT(true); // make sure BT is active

			// check if already connected, then disconnect
			if (mSerialService.getState() == BluetoothSerialService.STATE_CONNECTED) {
				mSerialService.stop();
			} else { // otherwise connect
				sRemoteDeviceMac = data.getExtras().getString(
						DeviceListActivity.EXTRA_DEVICE_ADDRESS);
				btDevice = mBluetoothAdapter.getRemoteDevice(sRemoteDeviceMac);
				tvSelectedDevice.setText(mBluetoothAdapter.getRemoteDevice(
						sRemoteDeviceMac).getName()
						+ " (" + sRemoteDeviceMac + ")");
				mSerialService.connect(btDevice); // connect to device.
			}
			break;
		}
	}

	// The Handler that gets information back from the BluetoothService
	private final Handler mHandlerBT = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_STATE_CHANGE:
				tvConnectionStatus.setText(msg.arg1 + ":" + msg.arg2);
				switch (msg.arg1) {
				case BluetoothSerialService.STATE_CONNECTED:
					tvConnectionStatus.setText(R.string.conStatConnected);
					break;
				case BluetoothSerialService.STATE_CONNECTING:
					tvConnectionStatus.setText(R.string.conStatConnecting);
					break;
				case BluetoothSerialService.STATE_LISTEN:
				case BluetoothSerialService.STATE_NONE:
					tvConnectionStatus.setText(R.string.conStatNotConnected);
					break;
				}
				break;
			case MSG_WRITE:
				//Toast.makeText(getApplicationContext(), msg.getData(), Toast.LENGTH_SHORT);
				break;
			case MSG_READ:
				if (D) Log.d(TAG, "Bluetooth message recieved");
				break;
			case MSG_TOAST:
				Toast.makeText(getApplicationContext(),
						msg.getData().getString(TOAST), Toast.LENGTH_SHORT)
						.show();
				break;
			}
		}
	};

}
