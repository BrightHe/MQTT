package robot.com.myapplication.pal;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.List;

import robot.com.myapplication.R;
import robot.com.myapplication.mqtt.Constants;
import robot.com.myapplication.mqtt.RePublishClient;

/**
 * Created by admin on 2019/8/2.
 */

public class RecomFriendAdpater extends RecyclerView.Adapter<RecomFriendAdpater.ViewHolder> {
    private List<NewFriend> recomFriendList = new ArrayList<>();
    private NewFriend addFriend;
    private RePublishClient publishClient;
    private MqttMessage message;
    private Context context;
    private static final String TAG = "RecomFriendAdpater";

    public RecomFriendAdpater(List<NewFriend> mnewFriendList, Context context) {
        this.recomFriendList = mnewFriendList;
        this.context = context;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View addFriendView;
        ImageView friendImage;
        ImageView userImage;
        TextView userName3;
        TextView userWrite3;
        Button btn_add;//添加

        public ViewHolder(View itemView) {
            super(itemView);
            addFriendView = itemView;
            userImage = addFriendView.findViewById( R.id.image_userhead);
            friendImage = addFriendView.findViewById(R.id.image_head);
            userName3 = addFriendView.findViewById(R.id.username3);
            userWrite3 = addFriendView.findViewById(R.id.qianming3);
            btn_add = addFriendView.findViewById(R.id.btn_add);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recomfrienditem, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);

        //点击添加好友按钮，发送添加好友申请，将自己的用户信息发送给要添加的好友
        viewHolder.btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                addFriend = recomFriendList.get(position);

                //点击添加好友将当前的用户信息以及要添加的对象发布
                String userName = FriendsData.UserInfo.getUserName();   //用户名
                String sex = FriendsData.UserInfo.getSex();             //性别
                String sigature = FriendsData.UserInfo.getSigature();   //签名
                String whereFrom = FriendsData.UserInfo.getWherefrom(); //地区
                int friendImage = FriendsData.UserInfo.getFriendImage();//用户头像
                NewFriend newFriendData = new NewFriend(userName,sex,sigature,whereFrom,friendImage,userName,addFriend.getUserName(),Constants.PAL_TYPE);
                try {
                    //创建发布类对象
                    publishClient = new RePublishClient(context,
                            Constants.MQTT_LIGHT_PUBLISH_HOST,
                            Constants.MQTT_LIGHT_PUBLIC_TOPIC,
                            Constants.MQTT_LIGHT_PUBLISH_APPLY_clientid,
                            Constants.MQTT_LIGHT_PUBLISH_userName,
                            Constants.MQTT_LIGHT_PUBLISH_passWord);
                } catch (MqttException e) {
                    e.printStackTrace();
                }

                Gson gson = new Gson();
                String jsonStr = gson.toJson(newFriendData, NewFriend.class);
                Log.i(TAG, "添加好友请求中jsonStr--->" + jsonStr);
                message = new MqttMessage();
                message.setQos(2); // 可以有三种值（0,1,2）
                message.setRetained(false);//
                Log.i(TAG, message.isRetained() + "------retained状态");
                //设置负载，即消息内容
                message.setPayload(jsonStr.getBytes());
                new Thread( new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //发送添加好友申请，将自己的用户信息发送给要添加的好友
                            publishClient.publish(publishClient.getTopic(), message);
                        } catch (MqttException e) {
                            e.printStackTrace();
                            Log.i(TAG, "run: " + "发布失败");
                        }
                    }
                }).start();

                viewHolder.btn_add.setText("已发送");
                viewHolder.btn_add.setEnabled(false);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        addFriend = recomFriendList.get(position);
        holder.userName3.setText(recomFriendList.get(position).getUserName());
        holder.userWrite3.setText(recomFriendList.get(position).getUserWrite());

        if (recomFriendList.get(position).getUserImage() != null){
            Bitmap bitmap = BitmapFactory.decodeByteArray(recomFriendList.get(position).getUserImage(),0,recomFriendList.get(position).getUserImage().length,null);
            holder.userImage.setImageBitmap(bitmap);
        }else {
            holder.userImage.setImageResource(R.mipmap.ic_launcher_round);
        }
        holder.userImage.setImageResource(recomFriendList.get(position).getFriendImage());

        Log.i(TAG, "onBindViewHolder: recomFriendList.get("+position+").getAgreeOrRefuse() is :"+recomFriendList.get(position).getAgreeOrRefuse());

        if (recomFriendList.get(position).getAgreeOrRefuse()==Constants.ADD_FRIEND_AGREE){
            holder.btn_add.setText("已同意");
            holder.btn_add.setEnabled(false);
        }
    }

    @Override
    public int getItemCount() {
        return recomFriendList.size();
    }
}
