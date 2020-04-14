package com.xiaoyutech.push.getui.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.xiaoyutech.push.getui.DemoApplication;
import com.xiaoyutech.push.getui.config.Config;
import com.xiaoyutech.push.getui.net.interceptor.NetInterceptor;
import com.xiaoyutech.push.getui.ui.presenter.AuthInteractor;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;
import static com.xiaoyutech.push.getui.config.Config.AUTH_ACTION;

/**
 * Time：2020-03-11 on 09:47.
 * Decription:.
 * Author:jimlee.
 */
public class AlarmReceiver extends BroadcastReceiver {

    private final String TAG = this.getClass().getSimpleName();

    private AuthInteractor authInteractor = new AuthInteractor();
    private AlarmManager manager = (AlarmManager) DemoApplication.appContext.getSystemService(ALARM_SERVICE);
    public static final String IS_TRY_ONCEN = "is_try_once";

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (intent == null || intent.getAction() == null) {
            return;
        }
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION) ||
                intent.getAction().equals(Config.AUTH_ACTION)) {
            Log.i("AlarmReceiver", "receive action " + intent.getAction());
            if (NetInterceptor.isNetworkConnected() && TextUtils.isEmpty(Config.authToken)) {
                authInteractor.fetchAuthToken(new AuthInteractor.IAuthFinished() {

                    @Override
                    public void onAuthFinished(String token) {
                        Log.i(TAG, "fetch token successed token = " + token);
                        Config.authToken = token;
                    }

                    @Override
                    public void onAuthFailed(String msg) {
                        if (!intent.getBooleanExtra(IS_TRY_ONCEN,false)){
                            Log.i(TAG,"try again in 10 seconds");
                            startAuthAlram();
                        }
                    }
                });
            }
        }
    }

    private void startAuthAlram() {
        Intent intent = new Intent(AUTH_ACTION);
        intent.putExtra(IS_TRY_ONCEN,true);
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.add(Calendar.SECOND, 10);
        PendingIntent pendingintent =
                PendingIntent.getBroadcast(DemoApplication.appContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingintent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            manager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingintent);
        } else {
            manager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingintent);
        }
    }


}
