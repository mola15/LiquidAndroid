package it.polimi.molinaroli.liquidandroid;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import it.polimi.molinaroli.liquidandroid.Logic.Client;
import it.polimi.molinaroli.liquidandroid.Logic.LiquidAndroidService;
import it.polimi.molinaroli.liquidandroid.Logic.NsdHelper;
import it.polimi.molinaroli.liquidandroid.Logic.Server;

public class MainActivity extends AppCompatActivity {

    Server server;
    Client client;
    Context c;
    Button discover;
    Button resolve;
    NsdHelper helper;

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
        discover = (Button) findViewById(R.id.discover);
        discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.discoverServices();
            }
        });
    }
}
