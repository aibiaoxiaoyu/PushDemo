package com.xiaoyutech.push.getui.net;


import com.xiaoyutech.push.getui.net.request.AuthRequest;
import com.xiaoyutech.push.getui.net.request.LinkNotificationRequest;
import com.xiaoyutech.push.getui.net.request.TransmissionRequest;
import com.xiaoyutech.push.getui.net.response.AuthResp;
import com.xiaoyutech.push.getui.net.response.NotificationResp;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Time：2020-03-09 on 15:36.
 * Decription:.
 * Author:jimlee.
 */
public interface NetApi {

    @POST("v1/{appid}/push_single")
    Observable<NotificationResp> sendNotification(@Header("authtoken") String authtoken, @Path("appid") String appId, @Body LinkNotificationRequest notificationRequest);

    @POST("v1/{appid}/push_single")
    Observable<NotificationResp> sendTransmission(@Header("authtoken") String authtoken, @Path("appid") String appId, @Body TransmissionRequest transmissionRequest);


    @POST("v1/{appid}/auth_sign")
    Observable<AuthResp> auth(@Path("appid") String appId, @Body AuthRequest authRequest);


}
