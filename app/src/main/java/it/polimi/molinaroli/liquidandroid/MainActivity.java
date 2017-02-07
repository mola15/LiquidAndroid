package it.polimi.molinaroli.liquidandroid;

import android.content.Context;
import android.content.Intent;
import android.net.nsd.NsdServiceInfo;
import android.os.AsyncTask;
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

    Server server;
    Client client;
    Context c;
    Button discover;
    Button display;
    NsdHelper helper;
    ListView serviceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Intent myIntent = new Intent(this, LiquidAndroidService.class);
        //startService(myIntent);
        c = this;
        server = new Server(c);
        new Thread(new Runnable() {
            public void run() {

                Log.d("service","starting server");
                server.startServer();
            }
        }).start();

        helper = new NsdHelper(c);
        helper.initializeNsd();
        helper.registerService(server.getmLocalPort());
        Log.d("local port"," " + server.getmLocalPort());
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
}
