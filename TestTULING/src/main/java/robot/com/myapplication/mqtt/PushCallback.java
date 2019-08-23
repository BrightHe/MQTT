package robot.com.myapplication.mqtt;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.Gson;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import robot.com.myapplication.ListData;
import robot.com.myapplication.pal.FriendsData;
import robot.com.myapplication.pal.NewFriend;

/**
 * Created by Administrator on 2019/7/25.
 */

public class PushCallback implements MqttCallback {
    private String TAG = "PushCallback";
    Context ctx;

    public PushCallback(Context ctx){
        this.ctx = ctx;
    }

    public void connectionLost(Throwable cause) {
        // 连接丢失后，一般在这里面进行重连
        Log.i( TAG, "connectionLost: PushCallback 连接断开，可以做重连" );
    }

    public void deliveryComplete(IMqttDeliveryToken token) {
        Log.i( TAG, "deliveryComplete: PushCallback "+token.isComplete() );
    }

    public void messageArrived(String topic, MqttMessage message) throws Exception {
        // subscribe后得到的消息会执行到这里面
        Log.i( TAG, "messageArrived: PushCallback 接收消息主题:" +topic);
        Log.i( TAG, "messageArrived: PushCallback 接收消息Qos:" +message.getQos());
        Log.i( TAG, "messageArrived: PushCallback 接收消息内容:"+new String( message.getPayload() ) );
        Log.i(TAG,"pushCallBack messages is : "+ message.toString());

        //解析Json串时，应该try和catch,防止解析崩溃
        Gson gson = new Gson();
        ListData listData = gson.fromJson( message.toString(),ListData.class );
        NewFriend palReq = gson.fromJson( message.toString(),NewFriend.class );
//        Log.i( TAG, "PushCallback messageArrived: palReq is "+palReq.toString() );
//        Log.i( TAG, "PushCallback messageArrived: listData is "+listData.toString() );
        Intent intent = new Intent(Constants.MY_MQTT_BROADCAST_NAME);
        Log.i( TAG, "PushCallback messageArrived: toUser is "+listData.getToUser() );
        if(listData.getToUser() != null && listData.getToUser().equals( FriendsData.UserInfo.getUserName() )){
            if(listData.getRequest_type() == Constants.CHAT_TYPE){
                listData.setFlag( ListData.RECEIVE );
                listData.setAmrFilePath( null );
                String chat_message = gson.toJson( listData,ListData.class );
                Log.i( TAG, "PushCallback messageArrived: chat message is "+chat_message );
                intent.putExtra("chatMessage",chat_message);
            }else if(listData.getRequest_type() == Constants.PAL_TYPE){
                String pal_message = gson.toJson( palReq,NewFriend.class );
                Log.i( TAG, "PushCallback messageArrived: pal is "+pal_message );
                intent.putExtra( "palMessage", pal_message);
            }
        }
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(ctx);
        //发送本地广播
        localBroadcastManager.sendBroadcast(intent);
    }
}
