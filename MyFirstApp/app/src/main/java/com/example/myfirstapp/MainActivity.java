package com.example.myfirstapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.myfirstapp.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private final String TAG = MainActivity.class.getSimpleName();

    public final static String MODULE_MAC = "20:19:07:00:56:C5";
    private static final UUID BT_MODULE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier

    // #defines for identifying shared types between calling functions
    private final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names
    public final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    private final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status

    private BluetoothAdapter mBTAdapter;
    private Set<BluetoothDevice> mPairedDevices;
    private ArrayAdapter<String> mBTArrayAdapter;

    private Handler mHandler; // Our main handler that will receive callback notifications
    private ConnectedThread mConnectedThread; // bluetooth background worker thread to send and receive data
    private BluetoothSocket mBTSocket = null; // bi-directional client-to-client data path

    private TextView mBluetoothStatus;
    private ListView mDevicesListView;
    private TextView mReadBuffer;

    private Button mButtonF;
    private Button mButtonB;
    private Button mButtonL;
    private Button mButtonR;
    private Button mButtonS;

    BluetoothManager bluetoothManager;
    BluetoothAdapter bluetoothAdapter;


    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        mBluetoothStatus = binding.getRoot().findViewById(R.id.textview_first);
        mBTArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        mBTAdapter = BluetoothAdapter.getDefaultAdapter(); // get a handle on the bluetooth radio

        mDevicesListView = binding.getRoot().findViewById(R.id.devices_list_view);
        mDevicesListView.setAdapter(mBTArrayAdapter); // assign model to view
        mDevicesListView.setOnItemClickListener(mDeviceClickListener);

        mButtonF = binding.getRoot().findViewById(R.id.button_forward);
        mButtonB = binding.getRoot().findViewById(R.id.button_back);
        mButtonL = binding.getRoot().findViewById(R.id.button_left);
        mButtonR = binding.getRoot().findViewById(R.id.button_right);
        mButtonS = binding.getRoot().findViewById(R.id.button_stop);

//        bluetoothManager = getSystemService(BluetoothManager.class);
//        bluetoothAdapter = bluetoothManager.getAdapter();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        mHandler = new Handler(Looper.getMainLooper()) {

            String buttonText = "Connect to HC-05";
            boolean moveButtonStatus = false;
            int color = getResources().getColor(R.color.red, getTheme());
            @Override
            public void handleMessage(Message msg) {

//                if (msg.what == MESSAGE_READ) {
//                    String readMessage = null;
//                    readMessage = new String((byte[]) msg.obj, StandardCharsets.UTF_8);
//                    mReadBuffer.setText(readMessage);
//                }

                if (msg.what == CONNECTING_STATUS) {

                    String buttonText = getString(R.string.ConnectBT);
                    boolean moveButtonStatus = false;
                    int color = getResources().getColor(R.color.red, getTheme());

                    char[] sConnected;
                    if (msg.arg1 == 1) {
                        mBluetoothStatus.setText(getString(R.string.BTConnected));
                        color = getResources().getColor(R.color.green, getTheme());
                        moveButtonStatus = true;
                        buttonText = getString(R.string.DisconnectBT);
                    }
                    else
                        mBluetoothStatus.setText(getString(R.string.BTconnFail));

                    mBluetoothStatus.setTextColor(color);
                    binding.buttonFirst.setText(buttonText);
                    binding.getRoot().findViewById(R.id.button_forward).setEnabled(moveButtonStatus);
                    binding.getRoot().findViewById(R.id.button_back).setEnabled(moveButtonStatus);
                    binding.getRoot().findViewById(R.id.button_left).setEnabled(moveButtonStatus);
                    binding.getRoot().findViewById(R.id.button_right).setEnabled(moveButtonStatus);
                    binding.getRoot().findViewById(R.id.button_stop).setEnabled(moveButtonStatus);
                }

            }
        };

        if (mBTArrayAdapter == null) {
            // Device does not support Bluetooth
            mBluetoothStatus.setText(getString(R.string.sBTstaNF));
            Toast.makeText(getApplicationContext(),getString(R.string.sBTdevNF),Toast.LENGTH_SHORT).show();
        }
        else {
            binding.buttonFirst.setOnClickListener(this::connectToArduino);
            mButtonF.setOnClickListener(this::moveForward);
            mButtonR.setOnClickListener(this::moveRight);
            mButtonB.setOnClickListener(this::moveBackward);
            mButtonL.setOnClickListener(this::moveLeft);
            mButtonS.setOnClickListener(this::stopMoving);
        }
        binding.fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAnchorView(R.id.fab)
                .setAction("Action", null).show());
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    private void connectToArduino(View view) {

        mBTArrayAdapter.clear();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
        }

        mPairedDevices = mBTAdapter.getBondedDevices();

        if (mBTAdapter.isEnabled()) {
            // put it's one to the adapter
            for (BluetoothDevice device : mPairedDevices) {
                String address = device.getAddress();
                String name = device.getName();
                if(device.getAddress().equals(MODULE_MAC)) {
                    mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }
            }
        } else
            Toast.makeText(getApplicationContext(), getString(R.string.BTnotOn), Toast.LENGTH_SHORT).show();
    }

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            if(!mBTAdapter.isEnabled()) {
                Toast.makeText(getBaseContext(), getString(R.string.BTnotOn), Toast.LENGTH_SHORT).show();
                return;
            }

            mBluetoothStatus.setText(getString(R.string.cConnect));
            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) view).getText().toString();
            final String address = info.substring(info.length() - 17);
            final String name = info.substring(0,info.length() - 17);

           mBTArrayAdapter.clear();

            // Spawn a new thread to avoid blocking the GUI one
            new Thread()
            {
                @RequiresApi(api = Build.VERSION_CODES.S)
                @SuppressLint("MissingPermission")
                @Override
                public void run() {
                    boolean fail = false;

                    BluetoothDevice device = mBTAdapter.getRemoteDevice(address);

                    try {
                        mBTSocket = createBluetoothSocket(device);
                    } catch (IOException e) {
                        fail = true;
                        Toast.makeText(getBaseContext(), getString(R.string.ErrSockCrea), Toast.LENGTH_SHORT).show();
                    }
                    // Establish the Bluetooth socket connection.
                    try {
                        mBTSocket.connect();
                    } catch (IOException e) {
                        try {
                            fail = true;
                            mBTSocket.close();
                            mHandler.obtainMessage(CONNECTING_STATUS, -1, -1)
                                    .sendToTarget();
                        } catch (IOException e2) {
                            //insert code to deal with this
                            Toast.makeText(getBaseContext(), getString(R.string.ErrSockCrea), Toast.LENGTH_SHORT).show();
                        }
                    }
                    if(!fail) {
                        mConnectedThread = new ConnectedThread(mBTSocket, mHandler);
                        mConnectedThread.start();

                        mHandler.obtainMessage(CONNECTING_STATUS, 1, -1, name)
                                .sendToTarget();
                    }
                }
            }.start();
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.S)
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
        }
        try {
            final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
            return (BluetoothSocket) m.invoke(device, BT_MODULE_UUID);
        } catch (Exception e) {
            Log.e(TAG, "Could not create Insecure RFComm Connection",e);
        }
        return  device.createRfcommSocketToServiceRecord(BT_MODULE_UUID);
    }

    private void moveForward(View view) {
        mConnectedThread.write("f");
        mButtonF.setHovered(true);
        mButtonB.setHovered(false);
        mButtonL.setHovered(false);
        mButtonR.setHovered(false);
    }

    private void moveRight(View view) {
        mConnectedThread.write("r");
        mButtonF.setHovered(false);
        mButtonB.setHovered(false);
        mButtonL.setHovered(false);
        mButtonR.setHovered(true);
    }

    private void moveBackward(View view) {
        mConnectedThread.write("b");
        mButtonF.setHovered(false);
        mButtonB.setHovered(true);
        mButtonL.setHovered(false);
        mButtonR.setHovered(false);
    }

    private void moveLeft(View view) {
        mConnectedThread.write("l");
        mButtonF.setHovered(false);
        mButtonB.setHovered(false);
        mButtonL.setHovered(true);
        mButtonR.setHovered(false);
    }

    private void stopMoving(View view) {
        mConnectedThread.write("s");
        mButtonF.setHovered(false);
        mButtonB.setHovered(false);
        mButtonL.setHovered(false);
        mButtonR.setHovered(false);
    }
}

