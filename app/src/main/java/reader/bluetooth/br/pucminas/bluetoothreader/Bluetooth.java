package reader.bluetooth.br.pucminas.bluetoothreader;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.UUID;

public class Bluetooth {

    private BluetoothSocket mSocket = null;
    private BufferedReader mBufferedReader = null;

    private static final String UUID_SERIAL_PORT_PROFILE = "00001101-0000-1000-8000-00805F9B34FB";
    private static final String TAG = "bluetooth1";

    private InputStream inStream = null;
    private Handler handler = new Handler();
    private byte delimiter = 10;
    private boolean stopWorker = false;
    private int readBufferPosition = 0;
    private byte[] readBuffer = new byte[1024];

    private TextView dataX;
    private TextView dataY;
    private TextView dataZ;

    private boolean simular;

    public Bluetooth(TextView referenceX, TextView referenceY, TextView referenceZ){
        dataX = referenceX;
        dataY = referenceY;
        dataZ = referenceZ;
    }

    public void openDeviceConnection(BluetoothDevice aDevice)
            throws IOException {
        InputStream aStream = null;
        InputStreamReader aReader = null;
        try {
            ParcelUuid list[] = aDevice.getUuids();

            mSocket = aDevice.createRfcommSocketToServiceRecord(list[0].getUuid());
            mSocket.connect();
            aStream = mSocket.getInputStream();
            aReader = new InputStreamReader(aStream);
            mBufferedReader = new BufferedReader(aReader);
        } catch (IOException e) {
            Log.e(TAG, "Could not connect to device", e);
            close(mBufferedReader);
            close(aReader);
            close(aStream);
            close(mSocket);

            Random rnd = new Random();
            sendData("AcX: "+ String.valueOf(rnd.nextFloat()));
            sendData("AcY: "+ String.valueOf(rnd.nextFloat()));
            sendData("AcZ: "+ String.valueOf(rnd.nextFloat()));
        }
    }

    private void close(Closeable aConnectedObject) {
        if (aConnectedObject == null) return;
        try {
            aConnectedObject.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private UUID getSerialPortUUID() {
        return UUID.fromString(UUID_SERIAL_PORT_PROFILE);
    }

    private void sendData(String data){
        if (data.contains("X")){
            dataX.setText(data);
        } else if (data.contains("Y")){
            dataY.setText(data);
        } else{
            dataZ.setText(data);
        }
    }

    public void close(){
        try {
            mSocket.close();
        } catch (IOException e) {
        }
    }

    public void beginListenForData() {
        try {
            inStream = mSocket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Thread workerThread = new Thread(new Runnable() {
            public void run() {
                System.out.println("++++++++++++++++++++++++++ Simular? "+simular);
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        int bytesAvailable = inStream.available();
                        if (bytesAvailable > 0) {
                            byte[] packetBytes = new byte[bytesAvailable];
                            inStream.read(packetBytes);
                            for (int i = 0; i < bytesAvailable; i++) {
                                byte b = packetBytes[i];
                                if (b == delimiter) {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;
                                    handler.post(new Runnable() {
                                        public void run() {
                                            sendData(data);
                                        }
                                    });
                                } else {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    } catch (IOException ex) {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }

    public void setSimular(boolean simular) {
        this.simular = simular;
    }
}
