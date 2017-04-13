package tvfan.tv.ui.andr.play.baseplay.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 *网络连接Receiver,如果网络中途断掉，进行相应提示
 * @author noah
 *
 */
public class ConnectionChangeReceiver extends BroadcastReceiver {
    private onNetworkChangedListener mOnNetworkChangedListener;

	public void setOnNetworkChangedListener(
			onNetworkChangedListener onNetworkChangedListener) {
		this.mOnNetworkChangedListener = onNetworkChangedListener;
	}

	@Override  
    public void onReceive(Context context, Intent intent) {
        if(mOnNetworkChangedListener != null){
        	mOnNetworkChangedListener.onNetworkChanged(isNetworkAvailable(context));
        }
    }
	
	public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);  
        if (connectivity == null) {  
            return false;  
        } else {  
            NetworkInfo[] info = connectivity.getAllNetworkInfo();  
            if (info != null) {  
                for (int i = 0; i < info.length; i++) {  
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {  
                        return true;  
                    }  
                }  
            }  
        }  
        return false;  
    }  
    
    public static interface onNetworkChangedListener{
    	public void onNetworkChanged(boolean isConnected);
    }
}
