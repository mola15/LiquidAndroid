package it.polimi.molinaroli.liquidandroid.Logic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {
    public BootReceiver() {
    }

    /**
     * Method to start automatically Android distributed intent receiver service at boot time
     *
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent myIntent = new Intent(context, LiquidAndroidService.class);
        context.startService(myIntent);

    }
}
