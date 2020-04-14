package com.xiaoyutech.push.getui.ui.presenter;

import android.text.TextUtils;
import android.util.Log;

import com.xiaoyutech.push.getui.BuildConfig;
import com.xiaoyutech.push.getui.config.Config;
import com.xiaoyutech.push.getui.net.RetrofitManager;
import com.xiaoyutech.push.getui.net.interceptor.NetInterceptor;
import com.xiaoyutech.push.getui.net.request.AuthRequest;
import com.xiaoyutech.push.getui.net.response.AuthResp;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

/**
 * Time：2020-03-10 on 14:15.
 * Decription:.
 * Author:jimlee.
 */
public class AuthInteractor implements BaseInteractor {

    private final String TAG = this.getClass().getSimpleName();

    private CompositeDisposable disposables;

    public AuthInteractor() {
        disposables = new CompositeDisposable();
    }

    public static String getSHA256(String str) {
        MessageDigest messageDigest;
        String encodestr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes("UTF-8"));
            encodestr = byte2Hex(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodestr;
    }

    private static String byte2Hex(byte[] bytes) {
        StringBuffer stringBuffer = new StringBuffer();
        String temp = null;
        for (int i = 0; i < bytes.length; i++) {
            temp = Integer.toHexString(bytes[i] & 0xFF);
            if (temp.length() == 1) {
                // 1得到一位的进行补0操作
                stringBuffer.append("0");
            }
            stringBuffer.append(temp);
        }
        return stringBuffer.toString();
    }

    @Override
    public void onDestroy() {
        disposables.clear();
    }

    public void fetchAuthToken(final IAuthFinished authFinished) {
        if (TextUtils.isEmpty(Config.appkey) || TextUtils.isEmpty(BuildConfig.MASTERSECRET)) {
            Log.i(TAG, "appkey | mastersecret is empty, cancel fetchAuthtoken");
            return;
        }
        final AuthRequest authRequest = new AuthRequest();
        String currentTime = String.valueOf(System.currentTimeMillis());
        authRequest.setAppkey(Config.appkey);
        authRequest.setTimestamp(currentTime);
        authRequest.setSign(getSHA256(Config.appkey + currentTime + BuildConfig.MASTERSECRET));
        disposables.add(RetrofitManager
                .auth(authRequest)
                .subscribe(new Consumer<AuthResp>() {
                    @Override
                    public void accept(AuthResp authResp) {
                        if (authResp == null || TextUtils.isEmpty(authResp.result)) {
                            authFinished.onAuthFailed("鉴权失败");
                        } else if (authResp.result.equals("ok")) {
                            authFinished.onAuthFinished(authResp.auth_token);
                        } else {
                            authFinished.onAuthFailed(authResp.result);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        if (throwable instanceof NetInterceptor.NoNetException) {
                            authFinished.onAuthFailed(throwable.getMessage());
                        } else {
                            authFinished.onAuthFailed("鉴权失败");
                        }
                    }
                }));
    }


    public interface IAuthFinished {
        void onAuthFinished(String token);

        void onAuthFailed(String msg);
    }


}
