package emgm.configuracionbluetooth;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

/**
 * Created by DESARROLLO-07 on 22/01/2018.
 */

public class ListDevices extends ListActivity {

    BluetoothAdapter mBluetoothAdapter2 = null;

    static String DIRECCION_MAC = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ArrayAdapter<String> ArrayBluetooth = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        mBluetoothAdapter2 = BluetoothAdapter.getDefaultAdapter();

        Set<BluetoothDevice> dispositivosDisponibles = mBluetoothAdapter2.getBondedDevices();

        if(dispositivosDisponibles.size() > 0){

            for(BluetoothDevice dispositivo: dispositivosDisponibles){

                String nameBt = dispositivo.getName();
                String macBt = dispositivo.getAddress();

                ArrayBluetooth.add(nameBt + "\n" + macBt);

            }
        }

        setListAdapter(ArrayBluetooth);
    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        String información  = ((TextView) v).getText().toString();

        String direccionMac = información.substring(información.length() - 17);

        //Toast.makeText(this, "Mac: " + direccionMac, Toast.LENGTH_SHORT).show();

        Intent returMac = new Intent();
        returMac.putExtra(DIRECCION_MAC, direccionMac);
        setResult(RESULT_OK, returMac);
        finish();


    }
}
