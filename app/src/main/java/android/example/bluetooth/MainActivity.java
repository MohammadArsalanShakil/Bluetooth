package android.example.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private Button mTurnOn;
    private Button mTurnOff;
    private Button mVisible;
    private Button mPaired;
    private Button mScan;

    private BluetoothAdapter mBluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTurnOn = findViewById(R.id.button_turnon);
        mTurnOff = findViewById(R.id.button_turnoff);
        mVisible = findViewById(R.id.button_visible);
        mPaired = findViewById(R.id.button_list);
        mScan = findViewById(R.id.button_scan);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mListView = findViewById(R.id.listview);

        mTurnOn.setOnClickListener(v -> {
            if (!mBluetoothAdapter.isEnabled()) {
                startActivity(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));
                Toast.makeText(getApplicationContext(), "Turned On", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(getApplicationContext(), "Already Turned On", Toast.LENGTH_SHORT).show();
        });

        mTurnOff.setOnClickListener(v -> {
            if (mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.disable();
                Toast.makeText(getApplicationContext(), "Turned Off", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(getApplicationContext(), "Already Turned Off", Toast.LENGTH_SHORT).show();

        });

        mVisible.setOnClickListener(v -> {
            if (mBluetoothAdapter.isEnabled())
                startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE), 0);
            else
                Toast.makeText(getApplicationContext(), "Turn Bluetooth on", Toast.LENGTH_SHORT).show();
        });

        mPaired.setOnClickListener(v -> {
            pairedDevices = mBluetoothAdapter.getBondedDevices();

            ArrayList<String> list = new ArrayList<>();

            for (BluetoothDevice mBluetoothDevice : pairedDevices)
                list.add(mBluetoothDevice.getName());

            final ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_expandable_list_item_1, list);
            mListView.setAdapter(adapter);
        });

        mScan.setOnClickListener(v -> {
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            getApplicationContext().registerReceiver(mReceiver, filter);
            mBluetoothAdapter.startDiscovery();
        });
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            ArrayList<String> list = new ArrayList<>();
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address

                list.add(deviceName);
                list.add(deviceHardwareAddress);

                Log.i("Device Name: ", "device " + deviceName);
                Log.i("deviceHardwareAddress ", "hard" + deviceHardwareAddress);
            }
            final ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_expandable_list_item_1, list);
            mListView.setAdapter(adapter);
        }
    };
}