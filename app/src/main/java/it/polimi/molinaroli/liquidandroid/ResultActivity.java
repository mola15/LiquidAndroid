package it.polimi.molinaroli.liquidandroid;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

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
    ImageView iv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        context = this;
        port = getIntent().getIntExtra("PORTA",0);
        address = getIntent().getStringExtra("INDIRIZZO");

        if (port != 0) {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
            }
        }else{
            //qui apro la bitmap
            byte[] byteArray = getIntent().getByteArrayExtra("IMAGE");
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray , 0, byteArray .length);
            iv = (ImageView) findViewById(R.id.image);
            iv.setImageBitmap(bitmap);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            final Bitmap imageBitmap = (Bitmap) extras.get("data");
            //qui apro un client nuovo che parla col server dell altro dispotivo e gli rimanda l'immagine

                //gestisce l'invio della bitmap indietro


                new Thread(new Runnable() {
                    public void run() {
                        try {
                        Log.d("Activity", "starting client");
                        Client client = new Client(InetAddress.getByName(address.replaceAll("/","")),port,context,imageBitmap);
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                finish();
        }
    }


}
