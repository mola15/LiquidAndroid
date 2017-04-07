package it.polimi.molinaroli.liquidandroid;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.nsd.NsdServiceInfo;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import it.polimi.molinaroli.liquidandroid.Logic.Client;
import it.polimi.molinaroli.liquidandroid.Logic.IntentConverter;
import it.polimi.molinaroli.liquidandroid.Logic.LiquidAndroidService;
import it.polimi.molinaroli.liquidandroid.Logic.NsdHelper;
import xdroid.toaster.Toaster;

public class MainActivity extends AppCompatActivity {

    int myServerPort;
    Client client;
    Context c;
    Button discover;
    Button display;
    Button start;
    Button forward;
    NsdHelper helper;
    ListView serviceList;
    LiquidAndroidService mService;
    boolean mBound = false;
    Intent arrivalIntent;

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            LiquidAndroidService.LocalBinder binder = (LiquidAndroidService.LocalBinder) service;
            mService = binder.getService();
            Log.d("Activity","service connected");
            mBound = true;
            myServerPort = mService.getPort();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.d("Activity","service disconnected");
            mBound = false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        arrivalIntent = getIntent();
        if(getIntent().getAction().equals("OPEN")){
            //qui è stato avviato dalla notifica e quindi sicuramente il service sta andando
            final Intent intent = new Intent(this, LiquidAndroidService.class);
            bindService(intent, mConnection, 0);
            Log.d("bound", "" + mBound);
            Log.d("helperinit",""+mService.getHelper().getInit());
        } else{
            try {
                final Intent intent = new Intent(this, LiquidAndroidService.class);
                bindService(intent, mConnection, 0);
                Log.d("bound", "" + mBound);
            }catch(Exception e){
                mBound = false;
            }
        }

        setContentView(R.layout.activity_main);
        Log.e("azione intent",getIntent().getAction());
        try {
            Log.e("intento arrivato", IntentConverter.intentToJSON(getIntent()).toString());
        }catch(Exception e){
            e.printStackTrace();
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {


        final Intent intent = new Intent(this, LiquidAndroidService.class);
        c = this;
        start = (Button) findViewById(R.id.startservice) ;
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("bindstate",""+ mBound);
                try {
                    Intent myIntent = new Intent(getApplicationContext(), LiquidAndroidService.class);
                    startService(myIntent);
                    bindService(intent, mConnection, 0);
                    Log.d("bound", "" + mBound);
                  } catch (Exception e){
                Toaster.toast("Service not Started");
            }
            }
        });

            serviceList = (ListView) findViewById(R.id.servicelist);

        forward = (Button) findViewById(R.id.forward);
        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("bindstate",""+ mBound);
               try {
                    //eseguo solo se ho il bind attivo
                    helper = mService.getHelper();
                    helper.showDialog(c,arrivalIntent,myServerPort);
               }catch (Exception e){
                   Toaster.toast("Service not Started");
               }
            }
        });

        super.onStart();

    }

    //innerclass adapter
    public class CustomAdapterActivity extends ArrayAdapter<NsdServiceInfo> {

        public CustomAdapterActivity(Context context, int textViewResourceId,
                                     ArrayList<NsdServiceInfo> objects) {
            super(context, textViewResourceId, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.serviceitem, null);
            TextView nome = (TextView)convertView.findViewById(R.id.name);
            TextView port = (TextView)convertView.findViewById(R.id.porta);
            TextView ip = (TextView)convertView.findViewById(R.id.ip);

            NsdServiceInfo c = getItem(position);
            nome.setText(c.getServiceName());
            port.setText("" + c.getPort());
            ip.setText(c.getHost().toString());
            return convertView;
        }

    }

    @Override
    protected void onDestroy() {
        /*
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
        */
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
        super.onStop();
    }

}
