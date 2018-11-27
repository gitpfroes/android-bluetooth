package reader.bluetooth.br.pucminas.bluetoothreader;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.io.IOException;

public class CoordinatesActivity extends AppCompatActivity {

    private TextView dataX;
    private TextView dataY;
    private TextView dataZ;

    private String message;
    private Bluetooth bluetooth;

    private void connectBluetooth(String message){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(message);
        bluetooth = new Bluetooth(dataX, dataY, dataZ);

        try {
            bluetooth.openDeviceConnection(device);
            bluetooth.beginListenForData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinates);

        // Recebe o identificador do dispositivo via parametro
        Intent intent = getIntent();
        message = intent.getStringExtra(MainActivity.BLUETOOTH_MESSAGE);

        dataX = (TextView) findViewById(R.id.edt_x);
        dataY = (TextView) findViewById(R.id.edt_y);
        dataZ = (TextView) findViewById(R.id.edt_z);

        connectBluetooth(message);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bluetooth != null){
            bluetooth.close();
        }
    }

    public void atualizar(View view){
        connectBluetooth(message);
    }

}
