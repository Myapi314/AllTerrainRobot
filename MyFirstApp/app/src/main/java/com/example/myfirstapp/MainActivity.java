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

    // MAC Address for HC-05
    public final static String MODULE_MAC = "20:19:07:00:56:C5";
    private static final UUID BT_MODULE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier

    // #defines for identifying shared types between calling functions
    public final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    private final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status

    private BluetoothAdapter mBTAdapter;
    private Set<BluetoothDevice> mPairedDevices;
    private ArrayAdapter<String> mBTArrayAdapter;

    private Handler mHandler; // Our main handler that will receive callback notifications
    private ConnectedThread mConnectedThread; // bluetooth background worker thread to send and receive data
    private BluetoothSocket mBTSocket = null; // bi-directional client-to-client data path

    private TextView mBluetoothStatus;
    private TextView mReadBuffer;

    private Button mButtonF;
    private Button mButtonB;
    private Button mButtonL;
    private Button mButtonR;
    private Button mButtonS;
    private Button mButtonU;
    private Button mButtonD;


    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup of main activity
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        // Assign components
        mBluetoothStatus = binding.getRoot().findViewById(R.id.textview_first);
        mBTArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        mBTAdapter = BluetoothAdapter.getDefaultAdapter(); // get a handle on the bluetooth radio

        mReadBuffer = findViewById(R.id.textViewRx);

        mButtonF = binding.getRoot().findViewById(R.id.button_forward);
        mButtonB = binding.getRoot().findViewById(R.id.button_back);
        mButtonL = binding.getRoot().findViewById(R.id.button_left);
        mButtonR = binding.getRoot().findViewById(R.id.button_right);
        mButtonS = binding.getRoot().findViewById(R.id.button_stop);
        mButtonU = binding.getRoot().findViewById(R.id.button_up);
        mButtonD = binding.getRoot().findViewById(R.id.button_down);

        // Permissions check
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
        }

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {

                // Read in serial messages from Arduino and display in read buffer textView
                if (msg.what == MESSAGE_READ) {
                    String readMessage;
                    readMessage = new String((byte[]) msg.obj, StandardCharsets.UTF_8);
                    mReadBuffer.setText(readMessage);
                }

                // If received a connecting status, handle
                if (msg.what == CONNECTING_STATUS) {

                    // Default formatting
                    String buttonText = getString(R.string.ConnectBT);
                    boolean moveButtonStatus = false;
                    int color = getResources().getColor(R.color.red, getTheme());

                    if (msg.arg1 == 1) {
                        // BT connected
                        mBluetoothStatus.setText(getString(R.string.BTConnected));
                        color = getResources().getColor(R.color.green, getTheme());
                        moveButtonStatus = true;
                        buttonText = getString(R.string.DisconnectBT);
                    } else if (msg.arg1 == -1 && msg.arg2 == 1) {
                        // BT has been disconnected
                        mBluetoothStatus.setText(getString(R.string.BTDisconnected));
                        mReadBuffer.setText(getString(R.string.readBuffer));
                    } else {
                        // BT failed to connect
                        mBluetoothStatus.setText(getString(R.string.BTconnFail));
                        mReadBuffer.setText(getString(R.string.readBuffer));
                    }
                    // Update formatting
                    mBluetoothStatus.setTextColor(color);
                    binding.buttonFirst.setText(buttonText);
                    mButtonF.setEnabled(moveButtonStatus);
                    mButtonB.setEnabled(moveButtonStatus);
                    mButtonL.setEnabled(moveButtonStatus);
                    mButtonR.setEnabled(moveButtonStatus);
                    mButtonS.setEnabled(moveButtonStatus);
                    mButtonU.setEnabled(moveButtonStatus);
                    mButtonD.setEnabled(moveButtonStatus);
                }

            }
        };

        if (mBTArrayAdapter == null) {
            // Device does not support Bluetooth
            mBluetoothStatus.setText(getString(R.string.sBTstaNF));
        }
        else {
            binding.buttonFirst.setOnClickListener(this::connectToArduino);
            mButtonF.setOnClickListener(this::moveForward);
            mButtonR.setOnClickListener(this::moveRight);
            mButtonB.setOnClickListener(this::moveBackward);
            mButtonL.setOnClickListener(this::moveLeft);
            mButtonS.setOnClickListener(this::stopMoving);
            mButtonU.setOnClickListener(view -> mConnectedThread.write("u"));
            mButtonD.setOnClickListener(view -> mConnectedThread.write("d"));
        }

        // icon button in bottom left of screen
        binding.fab.setOnClickListener(view -> Snackbar.make(view, "Someday this may have an important message", Snackbar.LENGTH_LONG)
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

    @RequiresApi(api = Build.VERSION_CODES.S)
    private void connectToArduino(View view) {

        // Check if there is already a connection to the Arduino
        if(mBTSocket != null && mBTSocket.isConnected()) {
            // Stop motors from moving
            stopMoving(view);

            // Close the connection
            mConnectedThread.cancel();

            // Handle closing the connection formatting
            mHandler.obtainMessage(CONNECTING_STATUS, -1, 1)
                    .sendToTarget();
            return;
        }

        // Clear the array of paired BT devices
        mBTArrayAdapter.clear();

        // Permissions check
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
        }

        // Get the paired devices
        mPairedDevices = mBTAdapter.getBondedDevices();

        // Check if BT is enabled
        if (mBTAdapter.isEnabled()) {

            // Cycle through paired devices to find HC-05 module and attempt to connect
            for (BluetoothDevice device : mPairedDevices) {
                String address = device.getAddress();
                String name = device.getName();
                if(address.equals(MODULE_MAC)) {
                    // Spawn a new thread to avoid blocking the GUI one
                    new Thread()
                    {
                        @RequiresApi(api = Build.VERSION_CODES.S)
                        @SuppressLint("MissingPermission")
                        @Override
                        public void run() {
                            boolean fail = false;

                            // Get device from MAC address
                            BluetoothDevice device = mBTAdapter.getRemoteDevice(address);

                            try {
                                // Attempt to create a socket with the device
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
                                    // Close the socket if it failed to connect
                                    fail = true;
                                    mBTSocket.close();
                                    mHandler.obtainMessage(CONNECTING_STATUS, -1, -1)
                                            .sendToTarget();
                                } catch (IOException e2) {
                                    Toast.makeText(getBaseContext(), getString(R.string.ErrSockCrea), Toast.LENGTH_SHORT).show();
                                }
                            }
                            if(!fail) {
                                // If the connection did not fail we have a connected thread and the handler sends the connecting status
                                mConnectedThread = new ConnectedThread(mBTSocket, mHandler);
                                mConnectedThread.start();

                                mHandler.obtainMessage(CONNECTING_STATUS, 1, -1, name)
                                        .sendToTarget();
                            }
                        }
                    }.start();
                    return;
                }
            }
        } else
            Toast.makeText(getApplicationContext(), getString(R.string.BTnotOn), Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        // Permissions check
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
        }

        // Create RFComm connection
        try {
            final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
            return (BluetoothSocket) m.invoke(device, BT_MODULE_UUID);
        } catch (Exception e) {
            Log.e(TAG, "Could not create Insecure RFComm Connection",e);
        }
        return  device.createRfcommSocketToServiceRecord(BT_MODULE_UUID);
    }

    private void moveForward(View view) {
        // Send command to move forward
        mConnectedThread.write("f");

        // Set only forward button as hovered
        mButtonF.setHovered(true);
        mButtonB.setHovered(false);
        mButtonL.setHovered(false);
        mButtonR.setHovered(false);
    }

    private void moveRight(View view) {
        // Send command to move right
        mConnectedThread.write("r");

        // Set only right button as hovered
        mButtonF.setHovered(false);
        mButtonB.setHovered(false);
        mButtonL.setHovered(false);
        mButtonR.setHovered(true);
    }

    private void moveBackward(View view) {
        // Send command to move backward
        mConnectedThread.write("b");

        // Set only Back button as hovered
        mButtonF.setHovered(false);
        mButtonB.setHovered(true);
        mButtonL.setHovered(false);
        mButtonR.setHovered(false);
    }

    private void moveLeft(View view) {
        // Send command to move left
        mConnectedThread.write("l");

        // Set only left button as hovered
        mButtonF.setHovered(false);
        mButtonB.setHovered(false);
        mButtonL.setHovered(true);
        mButtonR.setHovered(false);
    }

    private void stopMoving(View view) {
        // Send command to stop
        mConnectedThread.write("s");

        // Remove hover from all buttons
        mButtonF.setHovered(false);
        mButtonB.setHovered(false);
        mButtonL.setHovered(false);
        mButtonR.setHovered(false);
    }

}

