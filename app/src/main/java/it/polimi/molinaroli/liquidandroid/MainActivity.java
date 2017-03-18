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
        } else if ((arrivalIntent.getAction().equals("android.media.action.IMAGE_CAPTURE")) || (arrivalIntent.getAction().equals(Intent.ACTION_VIEW)) || (arrivalIntent.getAction().equals(Intent.ACTION_SEND)) || (arrivalIntent.getAction().equals(Intent.ACTION_SENDTO))){/*
            Intent stop = new Intent(this,LiquidAndroidService.class);
            stopService(stop);
            per ore non gli faccio fare niente dovrebbe funzionare cmq
            */
            //voglio che sia partito il servizio e quindi dico che mbound è true;
            final Intent intent = new Intent(this, LiquidAndroidService.class);
            bindService(intent, mConnection, 0);
            Log.d("bound", "" + mBound);
        }

        setContentView(R.layout.activity_main);
        Log.e("azione intent",getIntent().getAction());

        //DA RIMUOVERE PROVE PER L'INTENT CONVERTER
        /*
        Intent in = new Intent(Intent.ACTION_SEND);
        Uri i;

        in.putExtra("nome","marco");
        int[] num = {1,2,3};
        in.putExtra("integers",num);
        double[] num2 = {1.2,2.2,3.3};
        in.putExtra("doubles",num2);
        byte b = -10;
        in.putExtra("byte",b);
        in.setData(Uri.parse("geo:37.7749,-122.4194"));
        Log.e("intent originale", in.toUri(0));
        Log.e("intent originale", "" + in.getDoubleArrayExtra("doubles")[0]);

        JSONObject job = IntentConverter.intentToJSON(in);
        Log.e("Json",job.toString());
        Intent i2 = IntentConverter.JSONToIntent(job);
        Log.e("intent rigenerato", i2.toUri(0));
        Log.e("intent rigenerato", "" + i2.getDoubleArrayExtra("doubles")[0]);
           */
        //FINE DA RIMUOVERE
        /*
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("*\/*");
        intent.putExtra(Intent.EXTRA_EMAIL, "example@example.com");
        intent.putExtra(Intent.EXTRA_SUBJECT, "example");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        JSONObject job = IntentConverter.intentToJSON(intent);
        Log.d("intento",job.toString());
        */
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
                if(!mBound) {
                    Intent myIntent = new Intent(getApplicationContext(), LiquidAndroidService.class);
                    startService(myIntent);
                    bindService(intent, mConnection, 0);
                    Log.d("bound", "" + mBound);
                } else{
                    Toaster.toast("Service already Started");
                }
            }
        });

            serviceList = (ListView) findViewById(R.id.servicelist);
            discover = (Button) findViewById(R.id.discover);
            discover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("bindstate",""+ mBound);
                    if(mBound) {
                        helper = mService.getHelper();
                        //reinizializzo il vettore di servizi
                        helper.setServices(new ArrayList<NsdServiceInfo>());
                        helper.discoverServices();
                        // faccio il display anche se non so se li ho risolti
                    }
                }
            });
            display = (Button) findViewById(R.id.display);
            display.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("bindstate",""+ mBound);
                    if(mBound){
                    helper = mService.getHelper();
                    helper.stopDiscovery();
                    // faccio il display anche se non so se li ho risolti
                    final CustomAdapterActivity adapter = new CustomAdapterActivity(c, R.layout.serviceitem, helper.getServices());
                    serviceList.setAdapter(adapter);
                    serviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            final NsdServiceInfo s = (NsdServiceInfo) adapter.getItem(position);
                            // quando ci clicco su lancio il client per parlare col server
                            // posso scegliere qui o dal client passando l'intento da decidere cosa è meglio
                            //penso di farlo di la mandare l'intento e poi gestire tutti i parametri
                            //per ora lo faccio qui perche ho solo pochi esempi
                            //qui leggo l'intento e in base al tipo lancio il client giusto
                            String action;
                            action = arrivalIntent.getAction();
                            Log.d("arrival intent",action);
                            switch (action) {
                                case "android.media.action.IMAGE_CAPTURE":  new Thread(new Runnable() {
                                        public void run() {

                                            Log.d("Activity", "starting client");
                                            Client client = new Client(s.getHost(), s.getPort(), c, myServerPort);
                                        }
                                    }).start();
                                    break;
                                case Intent.ACTION_SEND: Log.d("Activity", "intent not yet coded"); break;
                                case Intent.ACTION_VIEW: new Thread(new Runnable() {
                                    public void run() {

                                        Log.d("Activity", "starting client");
                                        Client client = new Client(s.getHost(), s.getPort(), c,arrivalIntent.getData().toString(),0);
                                    }
                                }).start();
                                    break;
                                default: Log.d("Activity", "intent not yet coded");
                                    //caso generale che impacchetta l'intento e lo spedisce
                                    Client client = new Client(s.getHost(), s.getPort(), c,arrivalIntent);
                                    break;
                            }
                        }
                    });
                    Log.d("Activity", "" + helper.getServices().size());
                }}

            });

        forward = (Button) findViewById(R.id.forward);
        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("bindstate",""+ mBound);
                if(mBound){
                    //eseguo solo se ho il bind attivo
                    helper = mService.getHelper();
                    helper.showDialog(c,arrivalIntent,myServerPort);
                } else{
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
