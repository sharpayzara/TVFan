package tvfan.tv.daemon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import tvfan.tv.AppGlobalConsts;
import tvfan.tv.EntryPoint;
import tvfan.tv.dal.LocalData;

public class PowerBooteceiver extends BroadcastReceiver {
    static final String ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        String pbset =  LocalData
                .sGetKV(AppGlobalConsts.PERSIST_NAMES.POWERBOOT
                        .name());
        Log.d("receiver", "pbset的值是" + pbset);
        if (intent.getAction().equals(ACTION)&&pbset.equals("1")){
            Intent entryActivityIntent = new Intent(context,EntryPoint.class);  // 要启动的Activity
            entryActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(entryActivityIntent);
        }
    }

}
