package robot.com.myapplication.mqtt;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * Created by Administrator on 2019/7/28.
 */

public class RePublishClient {
//    private String TAG = "Test";
//    private MqttClient client;
//    private String host = "tcp://47.105.185.251:61613"; //主机的ip(tcp连接)
//    private String userName = "admin";    // MQTT的server的用户名
//    private String passWord = "password"; // MQTT的server的密码
//    private MqttTopic topic;
//    private MqttMessage message;
//
//    private String myTopic = "HZH/HZH";     //   发布消息主题
//    private String myClientID = "13791156728@163.com"; //  发布消息的ID , 可以是任意唯一字符串 （比如：邮箱，手机号，UUID等）

    public MqttTopic topic;
    private MqttClient client;
    private MqttConnectOptions options;
    private String userName ;
    private String passWord ;
    private String HOST;
    private String TOPIC;
    private String clientid;
    public MqttMessage message;

    private Context ctx;
    private static final String TAG = "RePublishClient";
    public RePublishClient(Context ctx, String HOST,String TOPIC, String clientid, String userName, String passWord)throws MqttException {
        client = new MqttClient(HOST, clientid, new MemoryPersistence());
        connect(ctx,client,userName,passWord,TOPIC);
        Log.i(TAG, "PublishClient: topic is "+TOPIC);
    }

    public MqttTopic getTopic() {
        return topic;
    }

    public void setTopic(MqttTopic topic) {
        this.topic = topic;
    }

    public MqttClient getClient() {
        return client;
    }

    public void setClient(MqttClient client) {
        this.client = client;
    }

    public MqttConnectOptions getOptions() {
        return options;
    }

    public void setOptions(MqttConnectOptions options) {
        this.options = options;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getHOST() {
        return HOST;
    }

    public void setHOST(String HOST) {
        this.HOST = HOST;
    }

    public String getTOPIC() {
        return TOPIC;
    }

    public void setTOPIC(String TOPIC) {
        this.TOPIC = TOPIC;
    }

    public String getClientid() {
        return clientid;
    }

    public void setClientid(String clientid) {
        this.clientid = clientid;
    }

    public MqttMessage getMessage() {
        return message;
    }

    public void setMessage(MqttMessage message) {
        this.message = message;
    }

    public Context getCtx() {
        return ctx;
    }

    public void setCtx(Context ctx) {
        this.ctx = ctx;
    }

    private void connect(Context ctx, MqttClient client, String userName, String passWord, String TOPIC) {
        Log.i("HZH","userName:"+userName + "  password:"+passWord + " TOPIC:"+TOPIC);

        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(false);
        options.setUserName(userName);
        options.setPassword(passWord.toCharArray());
        // 设置超时时间
        options.setConnectionTimeout(10);
        // 设置会话心跳时间
        options.setKeepAliveInterval(20);
        try {
            client.setCallback(new PushCallback(ctx));
            client.connect(options);
            topic = client.getTopic(TOPIC);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void publish(MqttTopic topic , MqttMessage message) throws MqttPersistenceException,
            MqttException {
        MqttDeliveryToken token = topic.publish(message);
        token.waitForCompletion();
        Log.i(TAG, "publish: "+token.isComplete());
    }
}
