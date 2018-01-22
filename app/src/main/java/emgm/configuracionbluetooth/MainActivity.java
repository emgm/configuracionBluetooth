package emgm.configuracionbluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnConnect, btnDisconecct, btnEncenderLeds, btnApagarLeds;

    BluetoothAdapter mBluetoothAdapter = null; /// Adaptador
    BluetoothDevice  mBTDevice = null;
    BluetoothSocket  mBTSocket = null;

    private static final int SOLICITA_ACTIVACION = 1;
    private static final int SOLICITA_CONEXION = 2;

    ConnectedThread connectedThread;

    boolean conexionBluetooth = false;

    private static String MAC = null;

    UUID MEU_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnConnect = findViewById(R.id.btnConnect);
        btnDisconecct = findViewById(R.id.btnDisconnect);

        btnEncenderLeds = findViewById(R.id.btnEncenderLeds);
        btnApagarLeds = findViewById(R.id.btnApagarLeds);

        btnConnect.setOnClickListener(this);
        btnDisconecct.setOnClickListener(this);

        btnEncenderLeds.setOnClickListener(this);
        btnApagarLeds.setOnClickListener(this);


        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Verifica si el bluetooth esta disponible

        if(mBluetoothAdapter == null) {

            Toast.makeText(getApplicationContext(), "El Bluetooth no esta disponible", Toast.LENGTH_SHORT).show();

        }else if(!mBluetoothAdapter.isEnabled()){

            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE); // Activa el bluetooth
            startActivityForResult(enableBtIntent, SOLICITA_ACTIVACION);

            Toast.makeText(getApplicationContext(), "Disponible", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){

            case SOLICITA_ACTIVACION:

                if(resultCode == Activity.RESULT_OK){

                    Toast.makeText(getApplicationContext(), "El Bluetooth fue activado", Toast.LENGTH_SHORT).show();

                }else{

                    Toast.makeText(getApplicationContext(), "El Bluetooth no fue activado", Toast.LENGTH_SHORT).show();
                    finish();

                }

                break;

            case SOLICITA_CONEXION:

                if(resultCode == Activity.RESULT_OK){

                    MAC = data.getExtras().getString(ListDevices.DIRECCION_MAC);

                    Toast.makeText(getApplicationContext(), "MAC Obtenida: " + MAC, Toast.LENGTH_SHORT).show();
                    mBTDevice = mBluetoothAdapter.getRemoteDevice(MAC);

                    try {

                        mBTSocket = mBTDevice.createRfcommSocketToServiceRecord(MEU_UUID);

                        mBTSocket.connect();

                        conexionBluetooth = true;

                        connectedThread = new ConnectedThread(mBTSocket);
                        connectedThread.start();

                        btnConnect.setText("Desconectar");

                        Toast.makeText(getApplicationContext(), "Dispositivo conectado con: " + MAC, Toast.LENGTH_SHORT).show();

                    }catch (IOException erro){

                        conexionBluetooth = false;

                        Toast.makeText(getApplicationContext(), "Ha ocurrido un error: " + erro, Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(getApplicationContext(), "Falla al obtener la MAC", Toast.LENGTH_SHORT).show();

                }

                break;

        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.btnConnect:

                if(conexionBluetooth){
                    // Desconectar

                    try {

                        mBTSocket.close();
                        Toast.makeText(getApplicationContext(), "Bluetooth fue desconectado", Toast.LENGTH_SHORT).show();

                        conexionBluetooth = false;

                        btnConnect.setText("Conectar");

                    }catch (IOException erro){
                        Toast.makeText(getApplicationContext(), "Ha ocurrido un error: " + erro, Toast.LENGTH_SHORT).show();
                    }

                }else{
                    // Conectar

                    Intent listDevices = new Intent(MainActivity.this, ListDevices.class);
                    startActivityForResult(listDevices, SOLICITA_CONEXION);
                }

                break;

            case R.id.btnDisconnect:

                break;

            case R.id.btnEncenderLeds:

                if(conexionBluetooth){

                    connectedThread.enviarDatosArduino("ledsOn");

                }else{

                    Toast.makeText(getApplicationContext(), "Bluetooth no esta conectado", Toast.LENGTH_SHORT).show();

                }

                break;

            case R.id.btnApagarLeds:

                break;
        }
    }

    private class ConnectedThread extends Thread {

        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {

            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            /*
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    // Send the obtained bytes to the UI activity
                    //mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                          //  .sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }*/
        }

        /* Call this from the main activity to send data to the remote device */
        public void enviarDatosArduino(String datosParaEnviar) {

            byte[] msgBuffer = datosParaEnviar.getBytes();

            try {
                mmOutStream.write(msgBuffer);
            } catch (IOException e) {

            }
        }
    }
}