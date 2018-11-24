package reader.bluetooth.br.pucminas.bluetoothreader;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private ListView lstvw;
    private ArrayAdapter aAdapter;
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private TextView dataSent;
    private Bluetooth bluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lstvw = (ListView) findViewById(R.id.deviceList);
        dataSent = (TextView) findViewById(R.id.txt_view_result);

        //Define metodo para selecao de item pareado
        lstvw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView <?> parent, View view, int position, long id) {
                String bluetoothDevice = (String) aAdapter.getItem(position);
                String address = bluetoothDevice.substring( bluetoothDevice.indexOf("MAC Address: ")+13 );
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                Toast.makeText(MainActivity.this, "Address: "+address, Toast.LENGTH_SHORT).show();

                try {
                    bluetooth = new Bluetooth();
                    bluetooth.openDeviceConnection(device);
                    bluetooth.beginListenForData(dataSent);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetooth.close();
    }

    public void getListaDispositivosPareados(View v) {
        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Bluetooth Not Supported", Toast.LENGTH_SHORT).show();
        } else {
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            ArrayList list = new ArrayList();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    String devicename = device.getName();
                    String macAddress = device.getAddress();
                    list.add("Name: " + devicename + "MAC Address: " + macAddress);
                }
                aAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, list);
                lstvw.setAdapter(aAdapter);
            }
        }
    }

}