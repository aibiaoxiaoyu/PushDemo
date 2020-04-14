package com.xiaoyutech.push.getui.ui.presenter;

import android.util.Log;

import com.xiaoyutech.push.getui.config.Config;

/**
 * Time：2020-03-09 on 15:25.
 * Decription:.
 * Author:jimlee.
 */
public class HomePresenter implements HomeInteractor.NotificationListener, AuthInteractor.IAuthFinished {

    private static final String TAG = HomePresenter.class.getSimpleName();
    private HomeView homeView;
    private BaseInteractor notificationInteractor;
    private BaseInteractor authInteractor;

    public HomePresenter(HomeView homeView) {
        this.homeView = homeView;
        this.notificationInteractor = new HomeInteractor();
        this.authInteractor = new AuthInteractor();
    }

    public void sendNotification() {
        ((HomeInteractor) notificationInteractor).sendNotification(this);
    }

    public void sendTransmission() {
        ((HomeInteractor) notificationInteractor).sendTransmission(this);
    }

    public void fetchAuthToken() {
        ((AuthInteractor) authInteractor).fetchAuthToken(this);
    }

    @Override
    public void onSendNotificationSuccess(String msg) {
        if (homeView != null) {
            homeView.onNotificationSended(msg);
        }
    }

    @Override
    public void onSendNotificationFailed(String msg) {
        if (homeView != null) {
            homeView.onNotificationSendFailed(msg);
        }
    }

    @Override
    public void onAuthFinished(String token) {
        Config.authToken = token;
        Log.i(TAG, "fetch token successed token = " + token);
    }

    @Override
    public void onAuthFailed(String msg) {
        if (homeView != null) {
            homeView.onAuthFailed(msg);
        }
    }

    public void onDestroy() {
        notificationInteractor.onDestroy();
        authInteractor.onDestroy();
    }

    public interface HomeView {
        void onNotificationSended(String msg);

        void onNotificationSendFailed(String msg);

        void onAuthFailed(String msg);
    }
}
