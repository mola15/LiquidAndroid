package it.polimi.molinaroli.liquidandroid.Logic;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import it.polimi.molinaroli.liquidandroid.MainActivity;
import it.polimi.molinaroli.liquidandroid.R;

import static android.content.ContentValues.TAG;

public class LiquidAndroidService extends Service {
    private Bitmap resultBitmap;

    private NsdHelper helper;
    Server server;
    Context c;
    private int port;
    private final IBinder mBinder = new LocalBinder();

    private static final String ACTION_OPEN_ACTIVITY = "OPEN";
    private static final String ACTION_STOP_SERVICE = "STOP";

    private static final int NOTIFCATION_ID = 21422;

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

    public Bitmap getResultBitmap() {
        return resultBitmap;
    }

    public void setResultBitmap(Bitmap resultBitmap) {
        this.resultBitmap = resultBitmap;
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

        NotificationManager manager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

           if (ACTION_STOP_SERVICE.equals(intent.getAction())) {
               Log.d(TAG, "called to cancel service");
               helper.tearDown();
               stopForeground(true);
               stopSelf();

           } else {
               NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
               builder.setContentTitle("LiquidAndroid");
               builder.setContentText("Service running...Press below button to stop.");
               builder.setPriority(NotificationCompat.PRIORITY_HIGH);
               builder.setSmallIcon(R.mipmap.ic_launcher);

               Intent openIntent = new Intent(this, MainActivity.class);
               openIntent.setAction(ACTION_OPEN_ACTIVITY);
               PendingIntent piOpen = PendingIntent.getActivity(this, 0, openIntent, 0);
               builder.addAction(R.mipmap.ic_launcher, "OPEN", piOpen);
               Intent stopSelf = new Intent(this, LiquidAndroidService.class);
               stopSelf.setAction(this.ACTION_STOP_SERVICE);
               PendingIntent pStopSelf = PendingIntent.getService(this, 0, stopSelf, PendingIntent.FLAG_CANCEL_CURRENT);
               builder.addAction(R.mipmap.ic_launcher, "STOP", pStopSelf);
               //manager.notify(NOTIFCATION_ID, builder.build());


               startForeground(NOTIFCATION_ID, builder.build());
               c = this;
               server = new Server(c);
               setPort(server.getmLocalPort());
               setHelper(new NsdHelper(c));
               getHelper().initializeNsd();
               getHelper().registerService(server.getmLocalPort());
               Log.d("activity", "executed");

               new Thread(new Runnable() {
                   public void run() {

                       Log.d("service", "starting server");
                       server.startServer();

                   }
               }).start();

               Log.d("local port", " " + server.getmLocalPort());
               Log.d("activity", "executed");
           }//end else


        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        server.stopServer();
        helper.tearDown();
        stopForeground(true);
        super.onDestroy();
    }
}
