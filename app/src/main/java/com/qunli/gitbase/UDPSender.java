package com.qunli.gitbase;


import android.os.SystemClock;
import android.util.Log;

import com.kit.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.zip.Inflater;

public class UDPSender {
    private final static String TAG = "UDPSender";
    private DatagramSocket sendSocket;
    private DatagramPacket dpReceive;
    private LinkedBlockingQueue<byte[]> sendQueue;

    public static byte[] intToBytes(int n) {
        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            b[i] = (byte) (n >> (24 - i * 8));
        }
        return b;
    }

    public static byte[] unZipByte(byte[] data) {
        Inflater decompresser = new Inflater();
        decompresser.setInput(data);
        byte result[] = new byte[0];
        ByteArrayOutputStream o = new ByteArrayOutputStream(1);
        try {
            byte[] buf = new byte[1024];
            int got = 0;
            while (!decompresser.finished()) {
                got = decompresser.inflate(buf);
                o.write(buf, 0, got);
            }
            result = o.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                o.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            decompresser.end();
        }
        return result;
    }

    private String serviece_host;
    private int serviece_port;
    SocketAddress address;

    public UDPSender(String host, final int port) {
        this.serviece_host = host;
        this.serviece_port = port;
        address = new InetSocketAddress(serviece_host, serviece_port);
        sendQueue = new LinkedBlockingQueue<>();
        //循环处理接收数据
        new Thread(new UdpTask(address, new UdpTask.OnTaskRun() {
            @Override
            public void onReCreate(DatagramSocket newInstance) {
                sendSocket = newInstance;
            }

            @Override
            public void onRun() {
                byte[] rece_arr = new byte[1024 * 2];
                dpReceive = new DatagramPacket(rece_arr, rece_arr.length);
                try {
                    Log.e(TAG, "receive 前 : ");
                    sendSocket.receive(dpReceive);
                    byte[] data = dpReceive.getData();
                    int len = dpReceive.getLength();
                    byte[] result = new byte[len];
                    System.arraycopy(data, 0, result, 0, len);
                    // 请求包（字节数组）：  0x78（起始）+ headLen+ bodyLen（int）+ head + BodyReq/BodyRes  + 0x79（结束）
                    //cStx(0x80)+oriBodyLen+zipBodyLen+zipBody+cEtx(0x81)
                    //zipBody解开之后，就是0x78开头0x79结尾的原始pb消息
                    // 80 000002eb 00000173 789ccd903f4bc34018c683d02590c9c5f17012a4f5fee5ee927a4e2276295d9c43488ef6a0f943726dd351f01b3889a35fa050f07bf801045747671793a815b16e0edef41ccffb3eeffbfe2acbb20e2d6be77eefb10297e060f5fa70dbc1bb2430aa3410a200c3f6112e9e9eef6eaeae5fd667fbeb8ebdeacc2574ec4c262ad661300e8d5a844bb0a50fb890134c3cc608f5982708e70c0c866030a20021d14398f53cb7471128e5f13003469ba93a71ec486e2b726c2321a8278752472a986aa31c3b91731dab0c887a20b8381d0184715351983c0973bffe8173cce891d76cd418aa328d41c1ac487d32ce73bf0de8668556a9098dced2df176872a3bc9bccaa46972a8db374ba6c74a263bfcd2fcb22f2297405a78280280d13e5234a1172311290d5088887dc7e96ab54c712051f1e264cd41213ce3ef941f4336fa1633391080bd89f283d9e18c931ecb7170466992b89fe1792af135c4e18659ef757483679ef4808de10c1f41b91faf6e51bcc4ad0a6 81
                    //len ：361 80 0000029f 0000015f 789ccd90bb4ac440148683ac4d20958de5c14a905de7964926eb5889b8cdb28d7508c9b03bb2b9b099bda4147c032bb1f40504df43f00d2c04b1b4b63189b020aed83ad519ce7ffef37f676559d681656d5dec3e567009fbf71f4fb7db648786469506211c12d43eeaf9cf6f773757d7ef0fa77baf1dfba5b390c8b17399aa4447e138326a1955b0610e5ce4514205e794092e7cea791c0643188c1860ecf730e13de1f61886521e0d7330da4cd5b163c77293c8b18d44506f8ea48e5538d54639762a173a5139f8f542383f190126a451cc4c91464550ffe08c7076289a444d43ad4cd360309f65011d1745d01a74f3995699898cceb3df0334be71d14de7aba62e5596e4d9b46aea542741eb5f96b33860c8f53de65388b3285501660c6397601ff1fa045460f7a772a913339198f8a83f517a3c31d223a8df660b4d552889ff172c5e23b81ee58c0bf137ec5af9054bc99a95b06fac3555f509e2a8be4081
                    Log.e(TAG, "len ：" + result.length + " result : " + Signaturer.bytesToHex(result));
                    if ((result[0] & 0xff) == 0x80 && (result[len - 1] & 0xff) == 0x81) {
                        Logger.e("媒体广播");
                        int oriLen = getLength(result, 1, 4);
                        int zipLen = getLength(result, 5, 8);
                        byte[] zipBody = new byte[zipLen];
                        System.arraycopy(result, 9, zipBody, 0, zipLen);
                        byte[] oriBody = unZipByte(zipBody);
                        if (oriBody.length == oriLen) {
                            Log.e(TAG, "oriLen : " + oriLen + " zipLen ：" + zipLen + " oriBody : " + Signaturer.bytesToHex(oriBody));
                            result = oriBody;
                            len = result.length;
                        }else {
                            Logger.e("解压失败！");
                        }
                    }
                    Logger.e(TAG,"start = "+(result[0] & 0xff)+" end = "+(result[len - 1] & 0xff));
                    if ((result[0] & 0xff) == 0x78 && (result[len - 1] & 0xff) == 0x79) {
                        int headLen = getLength(result, 1, 4);
                        int bodyLen = getLength(result, 5, 8);
                        byte[] header = new byte[headLen];
                        byte[] body = new byte[bodyLen];
                        System.arraycopy(result, 9, header, 0, headLen);
                        System.arraycopy(result, 9 + headLen, body, 0, bodyLen);
                        Log.e(TAG, "headLen ：" + headLen + " bodyLen : " + bodyLen);
                        if (null != onReceiveListener) {
                            onReceiveListener.onReceive(header, body);
                        }
                    }
                } catch (IOException e) {
                    Log.e(TAG, "receive data fail : e:" + e.getMessage());
                }
            }
        })).start();
        //发送线程
        new Thread(new UdpTask(address, new UdpTask.OnTaskRun() {
            @Override
            public void onReCreate(DatagramSocket newInstance) {
                sendSocket = newInstance;
            }

            @Override
            public void onRun() {
                try {
                    byte[] data = sendQueue.take();
                    if (null != data) {
                        DatagramPacket dpSend = new DatagramPacket(data, data.length);
                        sendSocket.send(dpSend);
                        Log.e(TAG, "send success !");
                    } else {
                        //没有数据
                        Log.e(TAG, "no data !");
                        sendQueue.clear();
                        SystemClock.sleep(300);
                    }
                } catch (IOException e) {
                    Log.e(TAG, "send fail: e =  " + e.toString());
                } catch (InterruptedException e) {
                    Log.e(TAG, "send fail: e =  " + e.toString());
                }
            }
        })).start();
    }

/*
     header = 804  body = 95 data = 909
     headlenArrs : 00000324
     data : 780000032400000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000079
     bodyLenArrs : 0000005f
     data : 78000003240000005f000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000079
     header : 20013207746573743030313881c6ea86e49195eb68428c06776441486d392b784e7270636b32364f6a734d585554706371384c664d4c7757664a5a4232443979656659554f46383854704442654c644e30504a6b3874344a55773865336e67423553706449393564662f5152624e7956475a714e726a5a544b45476f754a6554626c38535150373472467563657545557439757638446a625158683651694e686f4176376d534a566c6962516f62456e2b464b665735393630786571504a6d436b4b5a46617930503130787a396e376b6873764c622b684c75724a56417a6545534e67723339413059342f6d71776634466a376b57443053772f5945372b3232444d304231387772476e7772454a6b30774d754f716b797259634d7042364e6f522b6e374f43374f4f364a4349757776347875544c4f566d737a45484d587a4b66346b2f614c62653563444e6d6276444e6b5842466c345376747a625a592b6c6d36466e716a75706933516d2f393032664e576b6e6444375636527a75754e41354941316a6e3674496a7a434f586f6b727a73584f59456d54796e776d68486c756367444c2b306466783751705634554d7a4864486a43632b34746c544870656c67456b6570733547484f62486f395847674372385279626d5644676b44735447535767727052456b494d436f7841364c6c71315167515a6c64673263346c6b78653441506d4163354b5a6b6d4c6e594353786751515a5758587557523374474631576d52554d61387a324e343679576a7176735944686b626d666b4f65706f577837776c49656143505637777a45665076485a6752766c50504735377665655a566d7a43465a376c7979457a705a30626f6f4c2f456c4e44786d446a7479623374362f4a754e6e4e6570367746384f46654a47537659767930536752306d3350434248625236793973354a592f44416e375a7a6d665373613277503074524e4c534e6b313457627a6375554b4135385333634631442f6e74796e62503248557a76377942354441345667796b56716747513731304d622b542f317665436250734e5050365053564b444e3432716e366b764b3233716a636c4246634237564d6f43553d
     data : 78000003240000005f20013207746573743030313881c6ea86e49195eb68428c06776441486d392b784e7270636b32364f6a734d585554706371384c664d4c7757664a5a4232443979656659554f46383854704442654c644e30504a6b3874344a55773865336e67423553706449393564662f5152624e7956475a714e726a5a544b45476f754a6554626c38535150373472467563657545557439757638446a625158683651694e686f4176376d534a566c6962516f62456e2b464b665735393630786571504a6d436b4b5a46617930503130787a396e376b6873764c622b684c75724a56417a6545534e67723339413059342f6d71776634466a376b57443053772f5945372b3232444d304231387772476e7772454a6b30774d754f716b797259634d7042364e6f522b6e374f43374f4f364a4349757776347875544c4f566d737a45484d587a4b66346b2f614c62653563444e6d6276444e6b5842466c345376747a625a592b6c6d36466e716a75706933516d2f393032664e576b6e6444375636527a75754e41354941316a6e3674496a7a434f586f6b727a73584f59456d54796e776d68486c756367444c2b306466783751705634554d7a4864486a43632b34746c544870656c67456b6570733547484f62486f395847674372385279626d5644676b44735447535767727052456b494d436f7841364c6c71315167515a6c64673263346c6b78653441506d4163354b5a6b6d4c6e594353786751515a5758587557523374474631576d52554d61387a324e343679576a7176735944686b626d666b4f65706f577837776c49656143505637777a45665076485a6752766c50504735377665655a566d7a43465a376c7979457a705a30626f6f4c2f456c4e44786d446a7479623374362f4a754e6e4e6570367746384f46654a47537659767930536752306d3350434248625236793973354a592f44416e375a7a6d665373613277503074524e4c534e6b313457627a6375554b4135385333634631442f6e74796e62503248557a76377942354441345667796b56716747513731304d622b542f317665436250734e5050365053564b444e3432716e366b764b3233716a636c4246634237564d6f43553d000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000079
     body : 0a5d0a0b616e64726f696420372e305a12e7b3bbe7bb9fe7b3bbe7bb9fe5b18fe5b99560b808aa011363616d6572615fe7b3bbe7bb9fe79bb8e69cbab001b808b801800ffa010ce7b3bbe7bb9fe887aae5b8a6ca0209e7b3bbe7bb9f6d6963
     data : 78000003240000005f20013207746573743030313881c6ea86e49195eb68428c06776441486d392b784e7270636b32364f6a734d585554706371384c664d4c7757664a5a4232443979656659554f46383854704442654c644e30504a6b3874344a55773865336e67423553706449393564662f5152624e7956475a714e726a5a544b45476f754a6554626c38535150373472467563657545557439757638446a625158683651694e686f4176376d534a566c6962516f62456e2b464b665735393630786571504a6d436b4b5a46617930503130787a396e376b6873764c622b684c75724a56417a6545534e67723339413059342f6d71776634466a376b57443053772f5945372b3232444d304231387772476e7772454a6b30774d754f716b797259634d7042364e6f522b6e374f43374f4f364a4349757776347875544c4f566d737a45484d587a4b66346b2f614c62653563444e6d6276444e6b5842466c345376747a625a592b6c6d36466e716a75706933516d2f393032664e576b6e6444375636527a75754e41354941316a6e3674496a7a434f586f6b727a73584f59456d54796e776d68486c756367444c2b306466783751705634554d7a4864486a43632b34746c544870656c67456b6570733547484f62486f395847674372385279626d5644676b44735447535767727052456b494d436f7841364c6c71315167515a6c64673263346c6b78653441506d4163354b5a6b6d4c6e594353786751515a5758587557523374474631576d52554d61387a324e343679576a7176735944686b626d666b4f65706f577837776c49656143505637777a45665076485a6752766c50504735377665655a566d7a43465a376c7979457a705a30626f6f4c2f456c4e44786d446a7479623374362f4a754e6e4e6570367746384f46654a47537659767930536752306d3350434248625236793973354a592f44416e375a7a6d665373613277503074524e4c534e6b313457627a6375554b4135385333634631442f6e74796e62503248557a76377942354441345667796b56716747513731304d622b542f317665436250734e5050365053564b444e3432716e366b764b3233716a636c4246634237564d6f43553d0a5d0a0b616e64726f696420372e305a12e7b3bbe7bb9fe7b3bbe7bb9fe5b18fe5b99560b808aa011363616d6572615fe7b3bbe7bb9fe79bb8e69cbab001b808b801800ffa010ce7b3bbe7bb9fe887aae5b8a6ca0209e7b3bbe7bb9f6d696379
*/

    /**
     * Protobuf组包规则：
     * cStx  +  dwHeadLen+dwBodyLen  +   MediaGatewayHead  +   MediaGatewayBodyReq/MediaGatewayBodyRes  +  cEtx
     * cStx=0x78、cEtx=0x79,各1字节
     * dwHeadLen、wBodyLen各4字节,网络序
     * 请求包（字节数组）：  0x78（起始）+ headLen+ bodyLen（int）+ head + BodyReq/BodyRes  + 0x79（结束）
     * 包的总长度（byte）：   1         +  4     +   4           + headlen + bodylen      + 1
     *
     * @param header
     * @param body
     * @return
     */
    private byte[] join(byte[] header, byte[] body) {
        int hl = header.length;
        int bl = body.length;
        int dl = 1 + 4 + 4 + hl + bl + 1;
        Log.e(TAG, "header = " + hl + "  body = " + bl + " data = " + dl);
        byte[] data = new byte[dl];
        data[0] = 0x78;//起始标识
        data[dl - 1] = 0x79;//结束标识
        //copy到data的起始位
        int desPos = 1;
        byte[] headLens = intToBytes(hl);
        byte[] bodyLens = intToBytes(bl);
        //copy headlen
        System.arraycopy(headLens, 0, data, desPos, headLens.length);
        desPos += headLens.length;
//        Log.e(TAG, "headlenArrs : " + Signaturer.bytesToHex(headLens));
//        Log.e(TAG, "data : " + Signaturer.bytesToHex(data));
        //copy bodylen
        System.arraycopy(bodyLens, 0, data, desPos, bodyLens.length);
        desPos += bodyLens.length;
//        Log.e(TAG, "bodyLenArrs : " + Signaturer.bytesToHex(bodyLens));
//        Log.e(TAG, "data : " + Signaturer.bytesToHex(data));
        //copy head
        System.arraycopy(header, 0, data, desPos, header.length);
//        Log.e(TAG, "header : " + Signaturer.bytesToHex(header));
//        Log.e(TAG, "data : " + Signaturer.bytesToHex(data));
        desPos += header.length;
        System.arraycopy(body, 0, data, desPos, body.length);
//        Log.e(TAG, "body : " + Signaturer.bytesToHex(body));
//        Log.e(TAG, "data : " + Signaturer.bytesToHex(data));
        return data;
    }

    public void send(final byte[] header, final byte[] body) {
        if (null == header || body == null) {
            Log.e(TAG, "send package fail for header or boyd is null !");
            return;
        }
        final byte[] data = join(header, body);
        if (null != data && sendQueue != null) {
            try {
                sendQueue.put(data);
            } catch (InterruptedException e) {
                Log.e(TAG, "send fail for exception： " + e.toString());
            }
        }
    }


    public static int getLength(byte[] data, int begin, int end) {
        int n = 0;
        if (begin >= data.length || end >= data.length) return 0;
        for (; begin <= end; begin++) {
            n <<= 8;
            n += data[begin] & 0xFF;
        }
        return n;
    }

    private OnReceiveListener onReceiveListener;

    public void setOnReceiveListener(OnReceiveListener onReceiveListener) {
        this.onReceiveListener = onReceiveListener;
    }

    public interface OnReceiveListener {
        void onReceive(byte[] header, byte[] body);
    }

    public static class UdpTask implements Runnable {
        private SocketAddress address;
        private DatagramSocket socket;
        private OnTaskRun tastRun;

        public UdpTask(SocketAddress address, OnTaskRun tastRun) {
            this.address = address;
            this.tastRun = tastRun;
        }

        @Override
        public void run() {
            while (true) {
                if (connected()) {
                    tastRun.onRun();
                } else {
                    Log.e(TAG, "socket disconnect !");
                }
            }
        }

        private boolean connected() {
            if (null == address) {
                return false;
            }
            if (null == socket || socket.isClosed()) {
                try {
                    socket = new DatagramSocket();
                    if (tastRun != null) tastRun.onReCreate(socket);
                } catch (SocketException e) {
                    Log.e(TAG, "create socket fail : e:" + e.getMessage());
                    return false;
                }
            }
            if (!socket.isConnected()) {
                try {
                    socket.connect(address);
                } catch (SocketException e) {
                    Log.e(TAG, "socket connect fail : e:" + e.getMessage());
                    return false;
                }
            }
            return true;
        }

        interface OnTaskRun {
            void onRun();

            void onReCreate(DatagramSocket newInstance);
        }
    }
}
