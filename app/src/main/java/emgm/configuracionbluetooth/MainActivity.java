package emgm.configuracionbluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnConnect, btnDisconecct, btnEncenderLeds, btnApagarLeds;

    private BluetoothAdapter mBluetoothAdapter = null; /// Adaptador

    private static final int SOLICITA_ACTIVACION = 1;
    private static final int SOLICITA_CONEXION = 2;

    boolean conexionBluetooth = false;

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

        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.btnConnect:

                if(conexionBluetooth){

                    // Desconectar

                }else{
                    // Conectar

                    Intent listDevices = new Intent(MainActivity.this, ListDevices.class);
                    startActivityForResult(listDevices, SOLICITA_CONEXION);
                }

                break;

            case R.id.btnDisconnect:

                break;

            case R.id.btnEncenderLeds:

                break;

            case R.id.btnApagarLeds:

                break;
        }
    }
}