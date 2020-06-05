package com.qunli.gitbase;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.bcq.protobuf.AddressBookProtos;
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
    private String MEET_ID = "7554318394910745345";//1-5号
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
//        params.put("password","");
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
        }
    }

    private String mediaToken = "";
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
                            Logger.e(TAG, "sdkid = " + head.getUint32Sdkid());
                            Logger.e(TAG, "sdkver= " + head.getUint32SdkVer());
                            Logger.e(TAG, "uint32_seq= " + head.getUint32Seq());
                            Logger.e(TAG, "uint32_cmd= " + head.getUint32Cmd());
                            Logger.e(TAG, "uint32_crop_id= " + head.getUint32CropId());
                            Logger.e(TAG, "str_user_id= " + head.getStrUserId());
                            Logger.e(TAG, "uint64_meeting_id= " + head.getUint64MeetingId());
                            Logger.e(TAG, "uint32_error_code= " + head.getUint32ErrorCode());
                            Logger.e(TAG, "gateToekn = " + head.getBytesMediaGatewayToken().toStringUtf8());
                        }
                        //78 00000007 00000002 18372002c80c00 0a00 79
                        result = MideaCmd.MediaGatewayBodyRes.parseFrom(body);
                        Logger.e(TAG, "result");
                        if (result.getMsgJoinMeetingMediaRes().isInitialized()) {
                            Logger.e(TAG, "MsgJoinMeetingMediaRes");
                        }
                        if (result.getMsgGetMediaBroadcastRes().isInitialized()) {
                            Logger.e(TAG, "GetMediaBroadcastRes");
                            MideaCmd.GetMediaBroadcastRes gb = result.getMsgGetMediaBroadcastRes();
                            Logger.e(TAG, "str_sdp = " + gb.getStrSdp());
                        }
                        if (result.getMsgHelloRes().isInitialized()) {
                            Logger.e(TAG, "HelloRes");
                            MideaCmd.HelloRes helloRes = result.getMsgHelloRes();
                            Logger.e(TAG, "client_ip = " + helloRes.getUint32ClientIp());
                            Logger.e(TAG, "client_port = " + helloRes.getUint32ClientPort());
                            Logger.e(TAG, "client_interval = " + helloRes.getUint32Interval());
                        }
                        if (result.getMsgSdpRes().isInitialized()) {
                            Logger.e(TAG, "SdpRes");
                            MideaCmd.SdpRes sdpRes = result.getMsgSdpRes();
                            Logger.e(TAG, "sdt_answer = " + sdpRes.getStrSdpAnswer());
                        }
                        if (result.getMsgS2CMediaBroadcastRes().isInitialized()) {
                            Logger.e(TAG, "S2CMediaBroadcastRes");
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
                .setStrUserId("test001")
                //添加meetId 和 绑定的token
                .setUint64MeetingId(Long.valueOf(MEET_ID))
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
                .setStrSdpOffer("sdpoffer")
                .build();
        MideaCmd.MediaGatewayBodyReq reqBody = MideaCmd.MediaGatewayBodyReq.newBuilder()
                .setMsgSdpReq(req)
                .build();
        send(header, reqBody);
    }


    private void s2cBroadcastReq() {
        MideaCmd.MediaGatewayHead header = buildHeader(MideaCmd.C2S_MEDIA_GATAWAY_CMD.CMD_S2C_MEDIA_BROADCAST_REQ_VALUE);
        MideaCmd.S2CMediaBroadcastReq req = MideaCmd.S2CMediaBroadcastReq.newBuilder()
                .setStrSdp("sdp")
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
