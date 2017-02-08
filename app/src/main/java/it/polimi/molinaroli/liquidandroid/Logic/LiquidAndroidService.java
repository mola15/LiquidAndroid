package it.polimi.molinaroli.liquidandroid.Logic;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import it.polimi.molinaroli.liquidandroid.R;

public class LiquidAndroidService extends Service {
    private NsdHelper helper;
    Server server;
    Context c;
    private int port;
    private final IBinder mBinder = new LocalBinder();

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public NsdHelper getHelper() {
        return helper;
    }

    public void setHelper(NsdHelper helper) {
        this.helper = helper;
    }

    public class LocalBinder extends Binder {
      public  LiquidAndroidService getService() {
            // Return this instance of LocalService so clients can call public methods
            return LiquidAndroidService.this;
        }
    }

    public LiquidAndroidService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //prova
        /*
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
        */
        Notification notification = new Notification.Builder(this)
                .setContentTitle("LiquidAnroid")
                .setContentText("running service")
                .setSmallIcon(R.mipmap.ic_launcher)
                //.setContentIntent(pendingIntent)
                //.setTicker(getText(R.string.ticker_text))
                .build();

        startForeground(1, notification);
        c = this;
        server = new Server();
        setPort(server.getmLocalPort());
        setHelper(new NsdHelper(c));
        getHelper().initializeNsd();
        getHelper().registerService(server.getmLocalPort());
        Log.d("activity","executed");
        /*
        Intent bm = new Intent("PORT");
        bm.putExtra("port",server.getmLocalPort());
        sendBroadcast(bm);
        */
        new Thread(new Runnable() {
            public void run() {

                Log.d("service","starting server");
                server.startServer();

            }
        }).start();

        Log.d("local port"," " + server.getmLocalPort());
        Log.d("activity","executed");
        return super.onStartCommand(intent, flags, startId);
    }

}
