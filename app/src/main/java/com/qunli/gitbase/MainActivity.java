package com.qunli.gitbase;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bcq.protobuf.MideaCmd;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.kit.GsonUtil;
import com.kit.Logger;
import com.oklib.OkApi;
import com.oklib.callback.StringCallBack;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Request;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private String TAG = "TestActivity";
    private String MEET_ID = "5073239663496983776";//1-5号
    String url = "http://mindoc.qunlivideo.com/uploads/201906/flutter/attach_15a8ee63790b1e01.png";
    private int count = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.join).setOnClickListener(this);
        findViewById(R.id.sdp_req).setOnClickListener(this);
        findViewById(R.id.s2c_broadcast).setOnClickListener(this);
        findViewById(R.id.get_broadcast).setOnClickListener(this);
        findViewById(R.id.hello_req).setOnClickListener(this);
        findViewById(R.id.create).setOnClickListener(this);
        applyJoin(MEET_ID);
    }

    private void applyJoin(final String meetId) {
        final String uri = "/v1/meetings/" + meetId + "/join";
        String url = Signaturer.HOST + uri;
        final String timestamp = String.valueOf(new Date().getTime() / 1000);
        final String nonce = String.valueOf(count++);
        final Map<String, Object> params = new HashMap<>();
        params.put("userid", "test001");
        params.put("instanceid", 3);
        params.put("display_name", "test001");
        String body = GsonUtil.obj2Json(params);
        final String signature = Signaturer.signForPost(nonce, timestamp, uri, body);
        Log.e(TAG, "timestamp = " + timestamp);
        Log.e(TAG, "uri = " + uri);
        Log.e(TAG, "signature = " + signature);
        OkApi.post(url, params, new StringCallBack() {
            @Override
            public void onBefore(Request.Builder request) {
                request.addHeader("X-TC-Signature", signature);
                request.addHeader("X-TC-Nonce", nonce);
                request.addHeader("AppId", Signaturer.APP_ID);
                request.addHeader("X-TC-Key", Signaturer.SECRET_ID);
                request.addHeader("X-TC-Timestamp", timestamp);
            }

            @Override
            public void onResponse(String result) {
                Logger.e("TestActivity", "applyJoin：result = " + result);
                MediaInfo media = GsonUtil.json2Obj(result, MediaInfo.class);
                if ((null != media) && (null != media.getMedia_platform_info_meeting())) {
                    MediaInfo.MediaPlatformInfoMeetingBean plat = media.getMedia_platform_info_meeting();
                    String address = plat.getMedia_gw_ip();
                    String ports = plat.getMedia_gw_port();
                    mediaToken = plat.getMedia_gw_token();
                    int port = TextUtils.isEmpty(ports) ? 0 : Integer.valueOf(ports);
                    sender = new UDPSender(address, port);
                }
            }

            @Override
            public void onError(Exception e) {
                Logger.e("TestActivity", "applyJoin：error = " + e.getMessage());
            }
        });
    }

    private void CreateMeet() {
        final String uri = "/v1/meetings";
        String url = Signaturer.HOST + uri;
        final String timestamp = String.valueOf(new Date().getTime() / 1000);
        final String nonce = String.valueOf(count++);
        final Map<String, Object> params = new HashMap<>();
        params.put("userid", "test001");
        params.put("instanceid", 3);
        params.put("display_name", "test001");
        String body = GsonUtil.obj2Json(params);
        final String signature = Signaturer.signForPost(nonce, timestamp, uri, body);
        Log.e(TAG, "timestamp = " + timestamp);
        Log.e(TAG, "uri = " + uri);
        Log.e(TAG, "signature = " + signature);
        OkApi.post(url, params, new StringCallBack() {
            @Override
            public void onBefore(Request.Builder request) {
                request.addHeader("X-TC-Signature", signature);
                request.addHeader("X-TC-Nonce", nonce);
                request.addHeader("AppId", Signaturer.APP_ID);
                request.addHeader("X-TC-Key", Signaturer.SECRET_ID);
                request.addHeader("X-TC-Timestamp", timestamp);
            }

            @Override
            public void onResponse(String result) {
                Logger.e("TestActivity", "applyJoin：result = " + result);
                MediaInfo media = GsonUtil.json2Obj(result, MediaInfo.class);
                if ((null != media) && (null != media.getMedia_platform_info_meeting())) {
                    MediaInfo.MediaPlatformInfoMeetingBean plat = media.getMedia_platform_info_meeting();
                    String address = plat.getMedia_gw_ip();
                    String ports = plat.getMedia_gw_port();
                    mediaToken = plat.getMedia_gw_token();
                    int port = TextUtils.isEmpty(ports) ? 0 : Integer.valueOf(ports);
                    mediaId = media.getMedia_platform_tiny_id();
                    sender = new UDPSender(address, port);
                }
            }

            @Override
            public void onError(Exception e) {
                Logger.e("TestActivity", "applyJoin：error = " + e.getMessage());
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.join:
                joinMediaReq();
                break;
            case R.id.sdp_req:
                sdpReq();
                break;
            case R.id.s2c_broadcast:
                s2cBroadcastReq();
                break;
            case R.id.get_broadcast:
                getBroadcastReq();
                break;
            case R.id.hello_req:
                helloReq();
                break;
            case R.id.create:
                CreateMeet();
                break;
        }
    }

    private String mediaToken = "";
    private String mediaId = "";
    private UDPSender sender;
    private boolean listerenFlag = false;

    private void send(MideaCmd.MediaGatewayHead header, MideaCmd.MediaGatewayBodyReq bodyReq) {
        if (null == sender || null == bodyReq || null == header) {
            Logger.e(TAG, "send package data error for sender header or bodyReq is null !");
            return;
        }
        if (!listerenFlag) {
            listerenFlag = true;
            sender.setOnReceiveListener(new UDPSender.OnReceiveListener() {
                @Override
                public void onReceive(byte[] header, byte[] body) {
                    Logger.e("reveice : head = " + header.length + " data:" + Signaturer.bytesToHex(header));
                    Logger.e("reveice : body = " + body.length + " data:" + Signaturer.bytesToHex(body));
                    MideaCmd.MediaGatewayBodyRes result = null;
                    MideaCmd.MediaGatewayHead head = null;
                    try {
                        head = MideaCmd.MediaGatewayHead.parseFrom(header);
                        if (head.isInitialized()) {
                            if (head.hasUint32Sdkid())
                                Logger.e(TAG, "head: sdkid = " + head.getUint32Sdkid());
                            if (head.hasUint32SdkVer())
                                Logger.e(TAG, "head: sdkver= " + head.getUint32SdkVer());
                            if (head.hasUint32Seq())
                                Logger.e(TAG, "head: uint32_seq= " + head.getUint32Seq());
                            if (head.hasUint32Cmd())
                                Logger.e(TAG, "head: uint32_cmd= " + head.getUint32Cmd());
                            if (head.hasUint32CropId())
                                Logger.e(TAG, "head: uint32_crop_id= " + head.getUint32CropId());
                            if (head.hasStrUserId())
                                Logger.e(TAG, "head: str_user_id= " + head.getStrUserId());
                            if (head.hasUint64MeetingId())
                                Logger.e(TAG, "head: uint64_meeting_id= " + head.getUint64MeetingId());
                            if (head.hasUint32ErrorCode())
                                Logger.e(TAG, "head: uint32_error_code= " + head.getUint32ErrorCode());
                            if (head.hasBytesMediaGatewayToken())
                                Logger.e(TAG, "head: gateToekn = " + head.getBytesMediaGatewayToken().toStringUtf8());
                        }
                        //78 00000007 00000002 18372002c80c00 0a00 79
                        result = MideaCmd.MediaGatewayBodyRes.parseFrom(body);
                        MideaCmd.MediaGatewayBodyReq broacast = MideaCmd.MediaGatewayBodyReq.parseFrom(body);
                        Logger.e(TAG, "result:");
                        if (result.getMsgJoinMeetingMediaRes().isInitialized()) {
                            Logger.e(TAG, "Type:MsgJoinMeetingMediaRes");
                        }

                        if (result.getMsgSdpRes().isInitialized()) {
                            Logger.e(TAG, "Type:SdpRes");
                            MideaCmd.SdpRes sdpRes = result.getMsgSdpRes();
                            if (sdpRes.hasStrSdpAnswer())
                                Logger.e(TAG, "sdp_answer = " + sdpRes.getStrSdpAnswer());
                        }
                        if (result.getMsgGetMediaBroadcastRes().isInitialized()) {
                            Logger.e(TAG, "Type:GetMediaBroadcastRes");
                            MideaCmd.GetMediaBroadcastRes gb = result.getMsgGetMediaBroadcastRes();
                            if (gb.hasStrSdp())
                                Logger.e(TAG, "str_sdp = " + gb.getStrSdp());
                        }
                        if (result.getMsgHelloRes().isInitialized()) {
                            Logger.e(TAG, "Type:HelloRes");
                            MideaCmd.HelloRes helloRes = result.getMsgHelloRes();
                            if (helloRes.hasUint32ClientIp())
                                Logger.e(TAG, "client_ip = " + helloRes.getUint32ClientIp());
                            if (helloRes.hasUint32ClientPort())
                                Logger.e(TAG, "client_port = " + helloRes.getUint32ClientPort());
                            if (helloRes.hasUint32Interval())
                                Logger.e(TAG, "client_interval = " + helloRes.getUint32Interval());
                        }

                        if (result.getMsgS2CMediaBroadcastRes().isInitialized()) {
                            Logger.e(TAG, "Type:S2CMediaBroadcastRes");
                        }
                        if (broacast.getMsgS2CMediaBroadcastReq().isInitialized()) {
                            MideaCmd.S2CMediaBroadcastReq broa = broacast.getMsgS2CMediaBroadcastReq();
                            if (broa.hasStrSdp())
                                Logger.e(TAG, "str_sdp = " + broa.getStrSdp());
                        }
                    } catch (InvalidProtocolBufferException e) {
                        Logger.e(TAG, "解析数据异常，e = " + e.toString());
                    }
                }
            });
        }
        sender.send(header.toByteArray(), bodyReq.toByteArray());
    }

    private MideaCmd.MediaGatewayHead buildHeader(int cmd) {
        return MideaCmd.MediaGatewayHead.newBuilder()
                .setUint32Sdkid(Integer.valueOf(Signaturer.APP_ID))
                .setUint32SdkVer(0)
                .setUint32Seq(count)
                //添加meetId 和 绑定的token
                .setUint64MeetingId(Long.valueOf(MEET_ID))
                .setStrUserId(mediaId)
                .setBytesMediaGatewayToken(ByteString.copyFromUtf8(mediaToken))
                .setUint32Cmd(cmd)
                .build();
    }

    /**
     * 请求加入媒体
     */
    private void joinMediaReq() {
        MideaCmd.MediaGatewayHead header = buildHeader(MideaCmd.C2S_MEDIA_GATAWAY_CMD.CMD_C2S_JOIN_MEETING_MEDIA_REQ_VALUE);
        MideaCmd.JoinMeetingMediaReq req = MideaCmd.JoinMeetingMediaReq.newBuilder()
                .setStrOsVerion("android 7.0")
                .setStrScreenName("系统系统屏幕")
                .setUint32ScreenWidth(1080)
                .setUint32CameraHeight(1920)
                .setStrCameraName("camera_系统相机")
                .setUint32CameraWidth(1080)
                .setUint32CameraHeight(1920)
                .setStrSpeakerName("系统自带")
                .setStrMicName("系统mic")
                .build();
        MideaCmd.MediaGatewayBodyReq reqBody = MideaCmd.MediaGatewayBodyReq.newBuilder()
                .setMsgJoinMeetingMediaReq(req)
                .build();
        send(header, reqBody);
    }

    private void sdpReq() {
        MideaCmd.MediaGatewayHead header = buildHeader(MideaCmd.C2S_MEDIA_GATAWAY_CMD.CMD_C2S_SDP_REQ_VALUE);
        MideaCmd.SdpReq req = MideaCmd.SdpReq.newBuilder()
                .setStrSdpOffer("m=audio 60141 UDP 111^M\n" +
                        "a=rtpmap:111 OPUS/48000/2^M\n" +
                        "c=IN IP4 193.112.191.96^M\n" +
                        "a=rtcp-mux^M\n" +
                        "a=sendonly^M\n" +
                        "a=ssrc:2911882753 cname:5c2965f1-652c-7f79-8bea-1e0d5c770137^M\n" +
                        "m=video 60141 UDP 122^M\n" +
                        "a=rtpmap:122 H264/90000^M")
                .build();
        MideaCmd.MediaGatewayBodyReq reqBody = MideaCmd.MediaGatewayBodyReq.newBuilder()
                .setMsgSdpReq(req)
                .build();
        send(header, reqBody);
    }


    private void s2cBroadcastReq() {
        MideaCmd.MediaGatewayHead header = buildHeader(MideaCmd.C2S_MEDIA_GATAWAY_CMD.CMD_S2C_MEDIA_BROADCAST_REQ_VALUE);
        MideaCmd.S2CMediaBroadcastReq req = MideaCmd.S2CMediaBroadcastReq.newBuilder()
                .setStrSdp("m=audio 60141 UDP 111^M\n" +
                        "a=rtpmap:111 OPUS/48000/2^M\n" +
                        "c=IN IP4 193.112.191.96^M\n" +
                        "a=rtcp-mux^M\n" +
                        "a=sendonly^M\n" +
                        "a=ssrc:2911882753 cname:5c2965f1-652c-7f79-8bea-1e0d5c770137^M\n" +
                        "m=video 60141 UDP 122^M\n" +
                        "a=rtpmap:122 H264/90000^M")
                .build();
        MideaCmd.MediaGatewayBodyReq reqBody = MideaCmd.MediaGatewayBodyReq.newBuilder()
                .setMsgS2CMediaBroadcastReq(req)
                .build();
        send(header, reqBody);
    }

    private void getBroadcastReq() {
        MideaCmd.MediaGatewayHead header = buildHeader(MideaCmd.C2S_MEDIA_GATAWAY_CMD.CMD_C2S_GET_MEDIA_BROADCAST_REQ_VALUE);
        MideaCmd.GetMediaBroadcastReq req = MideaCmd.GetMediaBroadcastReq.newBuilder()
                .build();
        MideaCmd.MediaGatewayBodyReq reqBody = MideaCmd.MediaGatewayBodyReq.newBuilder()
                .setMsgGetMediaBroadcastReq(req)
                .build();
        send(header, reqBody);
    }

    private void helloReq() {
        MideaCmd.MediaGatewayHead header = buildHeader(MideaCmd.C2S_MEDIA_GATAWAY_CMD.CMD_S2C_MEDIA_BROADCAST_REQ_VALUE);
        MideaCmd.HelloReq req = MideaCmd.HelloReq.newBuilder()
                .build();
        MideaCmd.MediaGatewayBodyReq reqBody = MideaCmd.MediaGatewayBodyReq.newBuilder()
                .setMsgHelloReq(req)
                .build();
        send(header, reqBody);
    }
}
