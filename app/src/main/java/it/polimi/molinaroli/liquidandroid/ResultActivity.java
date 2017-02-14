package it.polimi.molinaroli.liquidandroid;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.net.InetAddress;
import java.net.UnknownHostException;

import it.polimi.molinaroli.liquidandroid.Logic.Client;
import it.polimi.molinaroli.liquidandroid.Logic.LiquidAndroidService;

/**
 * activity che viene lanciata solo per poter lanciare lo
 * startactivity for result e poi passare indietro al service i dati
 */
public class ResultActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    int port;
    String address;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        context = this;
        port = getIntent().getIntExtra("PORTA",0);
        address = getIntent().getStringExtra("INDIRIZZO");


        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            //qui apro un client nuovo che parla col server dell altro dispotivo e gli rimanda l'immagine
            try {
                Client client = new Client(InetAddress.getByName(address),port,context,1);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
    }


}
