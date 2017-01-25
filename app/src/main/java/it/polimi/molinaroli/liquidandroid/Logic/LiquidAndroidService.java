package it.polimi.molinaroli.liquidandroid.Logic;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

public class LiquidAndroidService extends Service {

    Server server;
    Client client;
    Context c;
    NsdHelper helper;

    public LiquidAndroidService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //prova
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
        Log.d("activity","executed");

        return super.onStartCommand(intent, flags, startId);
    }
}
