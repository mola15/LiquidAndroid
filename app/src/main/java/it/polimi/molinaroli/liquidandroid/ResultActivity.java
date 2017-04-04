package it.polimi.molinaroli.liquidandroid;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import it.polimi.molinaroli.liquidandroid.Logic.Client;
import it.polimi.molinaroli.liquidandroid.Logic.LiquidAndroidService;
import xdroid.toaster.Toaster;

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
    String mCurrentPhotoPath;



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

                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File

                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    Log.d("Actvityresult","file creato con successo");
                    Uri photoURI = FileProvider.getUriForFile(this,
                            "com.example.android.fileprovider",
                            photoFile);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        }else{
            Toaster.toast("image received");
            Bitmap bitmap = BitmapFactory.decodeFile(getIntent().getStringExtra("IMAGE"));
            iv = (ImageView) findViewById(R.id.image);
            iv.setImageBitmap(bitmap);
            galleryAddPic(getIntent().getStringExtra("IMAGE"));
        }
    }

    /**
     * automatically starts the return client to send data back
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //Bundle extras = data.getExtras();
            //final Bitmap imageBitmap = (Bitmap) extras.get("data");
            final Bitmap imageBitmap = decodeBitmap();
            //qui apro un client nuovo che parla col server dell altro dispotivo e gli rimanda l'immagine

                //gestisce l'invio della bitmap indietro


                new Thread(new Runnable() {
                    public void run() {
                        try {
                        Log.d("Activity", "starting client");
                        Client client = new Client(InetAddress.getByName(address.replaceAll("/","")),port,context,mCurrentPhotoPath);
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                finish();
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    public Bitmap decodeBitmap() {
        // Get the dimensions of the View
        int targetW = 300;
        int targetH = 400;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        return BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

    }
    /**
    ads the image to the gallery
     */
    private void galleryAddPic(String mCurrentPhotoPath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }


}
