package robot.com.myapplication.pal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.List;

import robot.com.myapplication.R;
import robot.com.myapplication.app.AppStr;
import robot.com.myapplication.dialog.MyDialog;
import robot.com.myapplication.login.LoginActivity;
import robot.com.myapplication.mqtt.Constants;
import robot.com.myapplication.mqtt.SubscriptClient;

public class PalActivity extends AppCompatActivity {
    private RelativeLayout rv_recom,rv_new;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager layoutManager;
    private static final String TAG = "PalActivity";
    private FriendAdapter friendAdapter;
    private ImageView setting;

    private boolean login;

    //订阅消息
    private IntentFilter intentFilter;
    private LocalReceiver localReceiver;
    private LocalBroadcastManager localBroadcastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_pal );

        SharedPreferences User = getSharedPreferences( "data", Context.MODE_PRIVATE );
        //如果未找到该值，则使用get方法中传入的默认值false代替
        login = User.getBoolean( "login", false );
        Log.i( TAG, "PalActivity: login is "+login );

        intentFilter = new IntentFilter();
        intentFilter.addAction( Constants.MY_MQTT_BROADCAST_NAME);
        localReceiver = new LocalReceiver();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        //注册本地接收器
        localBroadcastManager.registerReceiver(localReceiver,intentFilter);

        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //订阅好友申请以及好友申请回复
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    while(true) {
                        Log.i(TAG, "==============The client begin to start ....");
                        SubscriptClient client = new SubscriptClient(PalActivity.this,
                                Constants.MQTT_LIGHT_PUBLIC_TOPIC,
                                AppStr.getClientId( Constants.MQTT_LIGHT_SUBSCRIPT_AGREE_clientid ),
                                Constants.MQTT_LIGHT_SUBSCRIPT_HOST);
                        client.start();
                        Log.i(TAG, "==============The client is running....");
                        Thread.sleep(100000);
                        if (client != null){
                            client = null;
                        }
                    }
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        Log.i( TAG, "onStart: override" );
        addAgreeFriend(); //添加同意后的好友
    }

    /**
     * 添加同意好友
     */
    private void addAgreeFriend() {
        if(FriendsData.tempNewFriendList != null && FriendsData.tempNewFriendList.size() >= 1){
            for(int i = 0;i<FriendsData.tempNewFriendList.size();i++){
                Log.i( TAG, "addAgreeFriend: FriendsData.tempNewFriendList.get("+ i +") is" +FriendsData.tempNewFriendList.get( i ));
                if(FriendsData.tempNewFriendList.get( i ).getAgreeOrRefuse() == Constants.ADD_FRIEND_AGREE){
                    if(!checkListContainFriend( FriendsData.myFriendList,FriendsData.tempNewFriendList.get( i ).getUserName() )){ //重复检测
                        FriendsData.myFriendList.add(FriendsData.tempNewFriendList.get( i ));
                        friendAdapter.notifyDataSetChanged();
                    }
                }
            }
        }
        FriendsData.tempNewFriendList.clear();
    }

    /**初始化界面
     *
     */
    private void initView() {
        setting = (ImageView)findViewById( R.id.setting );
        setting.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkOut();
            }
        } );
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        rv_new = (RelativeLayout) findViewById(R.id.rv_new);
        //好友列表
        rv_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PalActivity.this,AddActivity.class);
                startActivity(intent);
            }
        });
        rv_recom = (RelativeLayout) findViewById(R.id.rv_recom);
        //推荐好友列表
        rv_recom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PalActivity.this,RecActivity.class);
                startActivity(intent);
            }
        });
        friendAdapter = new FriendAdapter(FriendsData.myFriendList);
        mRecyclerView.setAdapter(friendAdapter);
    }

    private void checkOut() {
        if(login == true){
            //弹出一个弹框，询问是否退出
            MyDialog.show(this, "确定退出登录吗?", new MyDialog.OnConfirmListener() {
                @Override
                public void onConfirmClick() {
                    //这里写点击确认后的逻辑
                    SharedPreferences.Editor set_sp = getSharedPreferences( "data", Context.MODE_PRIVATE ).edit();
                    set_sp.putBoolean( "login",false );
                    set_sp.commit();
                    Intent intent_back = new Intent( PalActivity.this, LoginActivity.class );
                    startActivity( intent_back );
                }
            });
        }
        else{
            Toast.makeText( this, "您处于未登录状态，无法退出登录！", Toast.LENGTH_SHORT ).show();
        }
    }

    /*
     *广播接收器，接收好友申请以及好友申请的结果
     */
    private class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String message = intent.getStringExtra("palMessage");
            runOnUiThread(new Runnable() {
                @Override
                public void run(){
                    Log.i(TAG,"PalActivity message is:"+ message);
                    if(message != null){
                        adapterFriend(message);
                    }
                }
            });
        }
    }

    /**
     * 处理好友请求
     */
    private void adapterFriend(String message) {
        Gson gson = new Gson();
        Log.i( TAG, "adapterFriend: message is "+message );
        NewFriend myFriend = gson.fromJson( message,NewFriend.class );
        String name = myFriend.getUserName();
        Log.i( TAG, "adapterFriend: name is "+ name );
        Log.i(TAG, "MainActivity myFriend is: "+myFriend.toString());
        //首先判断是否是发给自己的消息
        Log.i(TAG, "run: toUser is "+myFriend.getToUser());
        Log.i(TAG, "run: FriendsData.UserInfo.getUserName() is "+FriendsData.UserInfo.getUserName());
        Log.i(TAG, "run: myFriend.getAgreeOrRefuse() is "+myFriend.getAgreeOrRefuse());
        if (myFriend.getToUser().equals(FriendsData.UserInfo.getUserName())){
            //其次判断是否是是好友申请的结果，是申请结果且同意则将其显示在好友列表中
            if (myFriend.getAgreeOrRefuse() == 1){
                Log.i(TAG, "run: myFriend 是好友申请的结果");
                if (!checkListContainFriend(FriendsData.myFriendList,name)) {
                    FriendsData.myFriendList.add(myFriend);
                    friendAdapter = new FriendAdapter(FriendsData.myFriendList);
                    mRecyclerView.setAdapter(friendAdapter);
                    Log.i(TAG, "run: myFriendList is:" + FriendsData.myFriendList);
                }
            }else {//最后判断是否是是好友申请，是则将其保存在好友申请列表中
                if (!checkListContainFriend(FriendsData.newFriendList, name)) {
                    FriendsData.newFriendList.add(myFriend);
                    Log.i(TAG, "run: newFriendList is:" + FriendsData.newFriendList.toString());
                }
            }
        }
    }

    /**
     * 判断重复的好友
     */
    public boolean checkListContainFriend(List<NewFriend> list, String name) {
        if (list != null && list.size() >= 1) {
            for (int i = 0; i < list.size(); i++) {
                String tempName = list.get(i).getUserName();
                if (tempName.equals(name)) {
                    return true;
                }
            }
        }
        return false;
    }
}
