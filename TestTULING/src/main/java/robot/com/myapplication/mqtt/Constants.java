package robot.com.myapplication.mqtt;

/**
 * Created by Administrator on 2019/7/25.
 */

public class Constants {
    public static final String MY_MQTT_BROADCAST_NAME = "com.ghl.mqttsubscript";

    public static final String MY_MQTT_BROADCAST_APPLY = "MY_MQTT_BROADCAST_APPLY";//好友申请的广播
    public static final String MY_MQTT_BROADCAST_AGREE = "MY_MQTT_BROADCAST_AGREE";//好友申请回复的广播
    public static final String ADD_FRIEND_FLAG = "添加好友";
    public static final int ADD_FRIEND_AGREE = 1;
    public static final int ADD_FRIEND_REFUSE = 2;
    public static final String MQTT_LIGHT_PUBLIC_TOPIC = "PUBLIC_TOPIC";//公共的主题

    // MQTT 部分 publish
    public static final String MQTT_LIGHT_PUBLISH_HOST = "tcp://47.105.185.251:61613";
    public static final String MQTT_LIGHT_PUBLISH_AGREE_TOPIC = "AGREE_OR_REFUSE";//发布申请回复的主题
    public static final String MQTT_LIGHT_PUBLISH_APPLY_TOPIC = "APPLY_ME";        //发布好友申请的主题
    public static final String MQTT_LIGHT_PUBLISH_APPLY_clientid = "MQTT_LIGHT_PUBLISH_APPLY_ME";//发布申请回复的ID
    public static final String MQTT_LIGHT_PUBLISH_AGREE_clientid = "MQTT_LIGHT_PUBLISH_AGREE_OR_REFUSE";//发布好友申请的ID
    public static final String  MQTT_LIGHT_PUBLISH_userName = "admin";
    public static final String  MQTT_LIGHT_PUBLISH_passWord = "password";



    // MQTT 部分 subscript
    public static final String MQTT_LIGHT_SUBSCRIPT_HOST = "tcp://47.105.185.251:61613";
    public static final String MQTT_LIGHT_SUBSCRIPT_AGREE_TOPIC = "AGREE_OR_REFUSE";//订阅申请回复的主题
    public static final String MQTT_LIGHT_SUBSCRIPT_APPLY_TOPIC = "APPLY_ME";        //订阅好友申请的主题
    public static final String MQTT_LIGHT_SUBSCRIPT_APPLY_clientid = "MQTT_LIGHT_SUBSCRIPT_APPLY_ME";//订阅申请回复的ID
    public static final String MQTT_LIGHT_SUBSCRIPT_AGREE_clientid = "MQTT_LIGHT_SUBSCRIPT_AGREE_OR_REFUSE";//订阅好友申请的ID
    public static final String  MQTT_LIGHT_SUBSCRIPT_userName = "admin";
    public static final String  MQTT_LIGHT_SUBSCRIPT_passWord = "password";

    //聊天主题
    public static final String MQTT_LIGHT_SUBSCRIPT_CHAT_TOPIC = "CHAT";        //订阅好友申请的主题

    //订阅类型
    public static final int CHAT_TYPE = 1;
    public static final int PAL_TYPE = 2;
}
