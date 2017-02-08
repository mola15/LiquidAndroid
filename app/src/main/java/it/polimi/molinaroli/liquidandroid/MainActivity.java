package it.polimi.molinaroli.liquidandroid;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.nsd.NsdServiceInfo;
import android.os.AsyncTask;
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
import it.polimi.molinaroli.liquidandroid.Logic.LiquidAndroidService;
import it.polimi.molinaroli.liquidandroid.Logic.NsdHelper;
import it.polimi.molinaroli.liquidandroid.Logic.Server;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {

    Client client;
    Context c;
    Button discover;
    Button display;
    Button start;
    NsdHelper helper;
    ListView serviceList;
    BroadcastReceiver receiver;

    LiquidAndroidService mService;
    boolean mBound = false;


    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            LiquidAndroidService.LocalBinder binder = (LiquidAndroidService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /*

        server = new Server(c);
        new Thread(new Runnable() {
            public void run() {

                Log.d("service","starting server");
                server.startServer();
            }
        }).start();
*/
        /*
        IntentFilter filter = new IntentFilter();
        filter.addAction("PORT");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //do something based on the intent's action
                //QUANDO RICEVO LA PORTA REGISTRO TUTTO E CONTINUO
                helper = new NsdHelper(c);
                helper.initializeNsd();
                Log.d("local port"," " + intent.getIntExtra("port",0));
                helper.registerService(intent.getIntExtra("port",0));
                Log.d("activity","executed");

                serviceList = (ListView) findViewById(R.id.servicelist);
                discover = (Button) findViewById(R.id.discover);
                discover.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        helper.discoverServices();
                        // faccio il display anche se non so se li ho risolti

                    }
                });
                display = (Button) findViewById(R.id.display);
                display.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        helper.stopDiscovery();
                        // faccio il display anche se non so se li ho risolti
                        final CustomAdapter adapter = new CustomAdapter(c, R.layout.serviceitem, helper.getServices());
                        serviceList.setAdapter(adapter);
                        serviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                final NsdServiceInfo s = (NsdServiceInfo) adapter.getItem(position);
                                // quando ci clicco su lancio il client per parlare col server

                                new Thread(new Runnable() {
                                    public void run() {

                                        Log.d("Activiry","starting client");
                                        Client c = new Client(s.getHost(),s.getPort());
                                    }
                                }).start();

                            }
                        });
                        Log.d("Activity","" + helper.getServices().size());
                    }
                });
            }
        };
        registerReceiver(receiver, filter);
        */
    }

    @Override
    protected void onStart() {
        Intent intent = new Intent(this, LiquidAndroidService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        Log.d("bound","" + mBound);
        c = this;
        start = (Button) findViewById(R.id.startservice) ;
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getApplicationContext(), LiquidAndroidService.class);
                startService(myIntent);
            }
        });

            serviceList = (ListView) findViewById(R.id.servicelist);
            discover = (Button) findViewById(R.id.discover);
            discover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mBound) {
                        helper = mService.getHelper();
                        helper.discoverServices();
                        // faccio il display anche se non so se li ho risolti
                    }
                }
            });
            display = (Button) findViewById(R.id.display);
            display.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mBound){
                    helper = mService.getHelper();
                    helper.stopDiscovery();
                    // faccio il display anche se non so se li ho risolti
                    final CustomAdapter adapter = new CustomAdapter(c, R.layout.serviceitem, helper.getServices());
                    serviceList.setAdapter(adapter);
                    serviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            final NsdServiceInfo s = (NsdServiceInfo) adapter.getItem(position);
                            // quando ci clicco su lancio il client per parlare col server

                            new Thread(new Runnable() {
                                public void run() {

                                    Log.d("Activity", "starting client");
                                    Client c = new Client(s.getHost(), s.getPort());
                                }
                            }).start();

                        }
                    });
                    Log.d("Activity", "" + helper.getServices().size());
                }}

            });

        super.onStart();

    }

    //innerclass adapter
    public class CustomAdapter extends ArrayAdapter<NsdServiceInfo> {

        public CustomAdapter(Context context, int textViewResourceId,
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
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

}
