package robot.com.myapplication.pal;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.List;

import robot.com.myapplication.R;
import robot.com.myapplication.app.AppStr;
import robot.com.myapplication.mqtt.Constants;
import robot.com.myapplication.mqtt.RePublishClient;

import static robot.com.myapplication.pal.FriendsData.tempNewFriendList;

public class NewFriendAdapter extends RecyclerView.Adapter<NewFriendAdapter.ViewHolder> {
    private List<NewFriend> newFriendList = new ArrayList<>();
    private Context context;
    private NewFriend newFriend;
    private RePublishClient publishClient;//创建发布类对象
    private MqttMessage message;
    private static final String TAG = "NewFriendAdapter";

    public NewFriendAdapter(List<NewFriend> newFriendList, Context context) {
        this.newFriendList = newFriendList;
        this.context = context;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View newFriendView;
        ImageView userImage;
        TextView userName;
        TextView signature;
        Button btn_add;//同意
        Button btn_no;//拒绝

        public ViewHolder(View itemView) {
            super(itemView);
            newFriendView = itemView;
            userImage = itemView.findViewById( R.id.image_userhead);
            userName = itemView.findViewById(R.id.username2);
            signature = itemView.findViewById(R.id.qianming2);
            btn_add = itemView.findViewById(R.id.btn_add);
            btn_no = itemView.findViewById(R.id.btn_no);
        }
    }

    //创建ViewHolder,相当于ListView Adapter的getView方法
    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.addnewitem,parent,false);
        final ViewHolder viewHolder = new ViewHolder(view2);

        viewHolder.newFriendView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                Log.i(TAG,"position is " +position);
                newFriend = newFriendList.get(position);
                Bundle data = new Bundle();
                data.putSerializable("newFriend", newFriend);
                Intent intent = new Intent(parent.getContext(),NewFriendDetailsActivity.class);//好友资料界面
                intent.putExtras(data);
                parent.getContext().startActivity(intent);
            }
        });

        //点击同意按钮
        viewHolder.btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                newFriend = newFriendList.get( position );
                FriendsData.newFriendList.get( position ).setAgreeOrRefuse( Constants.ADD_FRIEND_AGREE );
                newFriendList.get( position ).setAgreeOrRefuse( Constants.ADD_FRIEND_AGREE );
                //点击同意将用户信息以及同意标志和发送者接受者发布
                String userName = FriendsData.UserInfo.getUserName();//用户名
                String sex = FriendsData.UserInfo.getSex();         //性别
                String sigature = FriendsData.UserInfo.getSigature();//签名
                String wherefrom = FriendsData.UserInfo.getWherefrom();
                int friendImage = FriendsData.UserInfo.getFriendImage();//用户头像
                int AgreeOrRefuse = Constants.ADD_FRIEND_AGREE;
                NewFriend newFriendData = new NewFriend(userName,sex,sigature,wherefrom,friendImage, AgreeOrRefuse,userName,newFriend.getUserName(),Constants.PAL_TYPE);
                try {
                    publishClient = new RePublishClient(context, Constants.MQTT_LIGHT_PUBLISH_HOST,
                            Constants.MQTT_LIGHT_PUBLIC_TOPIC,
                            AppStr.getClientId( Constants.MQTT_LIGHT_PUBLISH_AGREE_clientid ),
                            Constants.MQTT_LIGHT_PUBLISH_userName,
                            Constants.MQTT_LIGHT_PUBLISH_passWord);//创建发布类对象
                } catch (MqttException e) {
                    e.printStackTrace();
                }

                Gson gson = new Gson();
                String jsonStr = gson.toJson(newFriendData, NewFriend.class);
                Log.i(TAG, "同意好友请求中jsonStr--->" + jsonStr);
                message = new MqttMessage();
                message.setQos(2); // 可以有三种值（0,1,2）
                message.setRetained(false);//
                Log.i(TAG, message.isRetained() + "------retained状态");
                //设置负载，即消息内容
                message.setPayload(jsonStr.getBytes());
                new Thread( new Runnable() {
                    @Override
                    public void run() {
                        //发送同意消息，将自己的用户信息和同意标志发送
                        try {
                            publishClient.publish(publishClient.getTopic(), message);
                        } catch (MqttException e) {
                            e.printStackTrace();
                            Log.i(TAG, "run: " + "发布失败");
                        }
                    }
                }).start();

                Toast.makeText(parent.getContext(), "同意", Toast.LENGTH_SHORT).show();
                viewHolder.btn_add.setText("已同意");
                viewHolder.btn_add.setEnabled(false);
                viewHolder.btn_no.setEnabled(false);

                newFriend.setAgreeOrRefuse( AgreeOrRefuse );
                tempNewFriendList.add( newFriend );
                Log.i( TAG, "onClick: tempNewFriendList is " +newFriend.toString());

                //同意后为其设置已同意标志，推荐好友列表显示已同意（也可将其删除）
                if(FriendsData.recomFriendList!=null&&FriendsData.recomFriendList.size()>=1){
                    for(int i=0;i<FriendsData.recomFriendList.size();i++){
                        if(newFriend.getUserName().equals(FriendsData.recomFriendList.get(i).getUserName())){//判断当前列表的好友与推荐列表的好友相同
                            FriendsData.recomFriendList.get(i).setAgreeOrRefuse(Constants.ADD_FRIEND_AGREE);
                            Log.i(TAG, "onClick: FriendsData.recomFriendList.get("+ i +").getUserName is :"+FriendsData.recomFriendList.get(i).getUserName());
                            Log.i(TAG, "onClick: FriendsData.recomFriendList.get("+ i +").getAgreeOrRefuse is :"+FriendsData.recomFriendList.get(i).getAgreeOrRefuse());
                        }
                    }
                }
                //删除好友申请
//                FriendsData.deleteNewFriend(position);
            }
        });

        viewHolder.btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                newFriend = newFriendList.get(position);
                FriendsData.newFriendList.get( position ).setAgreeOrRefuse( Constants.ADD_FRIEND_REFUSE );
                //if (viewHolder2.flag){
                //点击拒绝将用户信息以及拒绝标志和发送者接受者发布
                String userName = FriendsData.UserInfo.getUserName();//用户名
                String sex = FriendsData.UserInfo.getSex();         //性别
                String sigature = FriendsData.UserInfo.getSigature();//签名
                String wherefrom = FriendsData.UserInfo.getWherefrom();
                int friendImage = FriendsData.UserInfo.getFriendImage();//用户头像
                int AgreeOrRefuse = Constants.ADD_FRIEND_REFUSE;
                NewFriend newFriendData = new NewFriend(userName, sex, sigature, wherefrom, friendImage, AgreeOrRefuse, userName, newFriend.getUserName(),Constants.PAL_TYPE);
                try {
                    publishClient = new RePublishClient(context, Constants.MQTT_LIGHT_PUBLISH_HOST,
                            Constants.MQTT_LIGHT_PUBLIC_TOPIC,
                            AppStr.getClientId( Constants.MQTT_LIGHT_PUBLISH_AGREE_clientid ),
                            Constants.MQTT_LIGHT_PUBLISH_userName,
                            Constants.MQTT_LIGHT_PUBLISH_passWord);//创建发布类对象
                } catch (MqttException e) {
                    e.printStackTrace();
                }
                Gson gson = new Gson();
                String jsonStr = gson.toJson(newFriendData, NewFriend.class);
                Log.i(TAG, "拒绝好友请求中jsonStr--->" + jsonStr);
                message = new MqttMessage();
                message.setQos(2); // 可以有三种值（0,1,2），分别代表消息发送情况：至少发送一次，至少
                message.setRetained(false);//
                Log.i(TAG, message.isRetained() + "------retained状态");
                //设置负载，即消息内容
                message.setPayload(jsonStr.getBytes());
                new Thread( new Runnable() {
                    @Override
                    public void run() {
                        try {
                            publishClient.publish(publishClient.getTopic(), message);
                        } catch (MqttException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                Toast.makeText(parent.getContext(), "拒绝", Toast.LENGTH_SHORT).show();
                viewHolder.btn_no.setText("已拒绝");
                viewHolder.btn_no.setEnabled(false);
                viewHolder.btn_add.setEnabled(false);

                //删除好友申请
                FriendsData.deleteNewFriend(position);
            }
            // }
        });

        return  viewHolder;
    }
    //数据绑定
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
//        if (mnewFriendList.get(position).getContent().equals(Constants.ADD_FRIEND_FLAG) ){
        newFriend = newFriendList.get(position);
        holder.userName.setText(newFriend.getUserName());//名字
        holder.signature.setText(newFriend.getSigature());//签名
        if (newFriendList.get(position).getUserImage() != null){
            Bitmap bitmap = BitmapFactory.decodeByteArray(newFriend.getUserImage(),0,newFriend.getUserImage().length,null);
            holder.userImage.setImageBitmap(bitmap);
        }else {
            holder.userImage.setImageResource(R.mipmap.ic_launcher_round);
        }
        //加载申请列表的用户头像
        holder.userImage.setImageResource(newFriend.getFriendImage());
        freshList( newFriend,holder );
//        }else {
//            Log.d("TAG","暂时没有添加好友申请");
//        }
    }

    /**
     *刷新列表
     */
    private void freshList(NewFriend newFriend,ViewHolder viewHolder) {
        if(newFriend != null ){
            if(newFriend.getAgreeOrRefuse() == 1){
                viewHolder.btn_add.setText("已同意");
                viewHolder.btn_add.setEnabled(false);
                viewHolder.btn_no.setEnabled(false);
            }else if(newFriend.getAgreeOrRefuse() == 2){
                viewHolder.btn_no.setText("已拒绝");
                viewHolder.btn_add.setEnabled(false);
                viewHolder.btn_no.setEnabled(false);
            }
        }
    }

    //数据的长度
    @Override
    public int getItemCount() {
        return newFriendList.size();
    }
}
