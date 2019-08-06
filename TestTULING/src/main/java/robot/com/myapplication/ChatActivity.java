package robot.com.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.wihaohao.PageGridView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import robot.com.myapplication.ImageUtils.ImageUtil;
import robot.com.myapplication.adapter.FaceUtils;
import robot.com.myapplication.adapter.MyIconModel;
import robot.com.myapplication.app.AppStr;
import robot.com.myapplication.mqtt.Constants;
import robot.com.myapplication.mqtt.RePublishClient;
import robot.com.myapplication.mqtt.SubscriptClient;
import robot.com.myapplication.recorder.AudioRecorderButton;
import robot.com.myapplication.recorder.MediaManager;
import robot.com.myapplication.tengxunyun.NTest;
import robot.com.myapplication.tengxunyun.PostObj;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {
    private RePublishClient rePublishClient = new RePublishClient();

    private String fromWho = "HZH";
    private String toUser = "HZH";
    private int infType = ListData.TEXT;

    private List<ListData> lists; //消息列表
    private ListView lv;    //列表控件
    private EditText et_sendText; //消息输入框
    private Button  btn_send;  //发送
    private ImageView bt_voice,bt_keyboard,bt_emoji,pop_plus,camera_img,pictures_img;
    private LinearLayout others;
    private String content_str;
    private TextAdapter adapter;
    private double currentTime, oldTime = 0;

    //本地广播
    private IntentFilter intentFilter;
    private LocalReceiver localReceiver;
    private LocalBroadcastManager localBroadcastManager;

    //自定义button
    private AudioRecorderButton mAudioRecorderButton;
    public boolean isPop;

    private boolean showFaceFlag = true;
    List <MyIconModel> mList;
    private PageGridView<MyIconModel> mPageGridView;

    private View mAnimView_left,mAnimView_right;

    private String TAG = "Test";
    private String message; //接收的消息

    private Uri imageUri;
    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;

    private static final int TYPE_RECORDER_MESSAGE = 1;
    private static final int TYPE_PHOTO_MESSAGE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        // 启动activity时不自动弹出软键盘
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        initView(); //初始化界面
        setDefaultState();
        rePublishClient.connectMQTTServer(); // 连接MQTT服务

        //订阅
        new Thread( new Runnable() {
            @Override
            public void run() {
                Log.i( TAG, "==============The client begin to start ...." );
                SubscriptClient client = new SubscriptClient( ChatActivity.this );
                client.start();
                Log.i( TAG, "==============The client is running...." );
            }
        } ).start();

        intentFilter = new IntentFilter();
        intentFilter.addAction( Constants.MY_MQTT_BROADCAST_NAME );
        localReceiver = new LocalReceiver();
        localBroadcastManager = LocalBroadcastManager.getInstance( this );
        //注册本地接收器
        localBroadcastManager.registerReceiver( localReceiver, intentFilter );

        //list点击
        setListViewAdapter();
    }

    /**
     * 广播接收器
     */
    private class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            message = intent.getStringExtra( "message" );
            runOnUiThread( new Runnable() {
                @Override
                public void run() {
                    Log.i( TAG, "ChatActivity message is:" + message );
                    ListData listData;
                    Gson gson = new Gson();
                    listData = gson.fromJson( message, ListData.class );
                    lists.add( listData );
                    adapter.notifyDataSetChanged();
                    lv.setAdapter( adapter );
                    lv.setSelection( adapter.getCount()-1 );
                }
            } );
        }
    }

    /**
     *初始化界面
     */
    private void initView() {
        lists = new ArrayList<ListData>();
        lv = (ListView) findViewById( R.id.lv );
        bt_voice = (ImageView) findViewById( R.id.bt_voice );
        bt_voice.setOnClickListener( this );
        bt_keyboard = (ImageView) findViewById( R.id.bt_keyboard );
        bt_keyboard.setOnClickListener( this );
        bt_emoji = (ImageView) findViewById( R.id.bt_emoji );
        bt_emoji.setOnClickListener( this );
        et_sendText = (EditText) findViewById( R.id.et_sendText );
        et_sendText.setOnClickListener( this );
        et_sendText.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(s.length() != 0){
                    btn_send.setVisibility( View.VISIBLE );
                    pop_plus.setVisibility( View.GONE );
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() == 0){
                    btn_send.setVisibility( View.GONE );
                    pop_plus.setVisibility( View.VISIBLE );
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() != 0){
                    //结束输入时，发送显示
                    btn_send.setVisibility( View.VISIBLE );
                    pop_plus.setVisibility( View.GONE );
                }
            }
        } );
        mAudioRecorderButton = (AudioRecorderButton) findViewById( R.id.id_recorder_button );
        mAudioRecorderButton.setAudioFinishRecorderListener( new AudioRecorderButton.AudioFinishRecorderListener() {
            @Override
            public void onFinish(float seconds, String filePath) {
                Log.i( TAG, "onFinish: seconds is "+seconds );
                Log.i( TAG, "onFinish: filePath is "+filePath );
                if (!showFaceFlag){//将表情栏隐藏
                    mPageGridView.setVisibility(View.GONE);
                    showFaceFlag = true;
                }
                ListData recorderList = new ListData( seconds,null,fromWho, toUser, ListData.SEND, getTime(), ListData.RECORDER );
                recorderList.setAmrFilePath( filePath );
                lists.add( recorderList );
                //更新adapter
                adapter.notifyDataSetChanged();
                lv.setAdapter( adapter );
                lv.setSelection( adapter.getCount()-1 );
                //测试网络状况
                if(NTest.getConnectedType( ChatActivity.this ) == 1){
                    PostObj postAmr = new PostObj();
                    AppStr appStr = (AppStr)getApplication();
                    appStr.setIsCompleted( false );
                    Log.i( TAG, "onFinish: 即将上传amr文件" );
                    postAmr.PostObject( ChatActivity.this,filePath,ListData.SEND,TYPE_RECORDER_MESSAGE );
                    sendAmr(appStr,postAmr,recorderList);
                }else{
                    Toast.makeText( ChatActivity.this, "网络连接不可用，请稍后重试！", Toast.LENGTH_SHORT ).show();
                }
            }
        } );
        btn_send = (Button) findViewById( R.id.bt_send );
        btn_send.setOnClickListener( this );
        pop_plus = (ImageView) findViewById( R.id.pop_plus );
        pop_plus.setOnClickListener( this );
        others = (LinearLayout)findViewById( R.id.others );
        camera_img = (ImageView)findViewById( R.id.camera_img );
        camera_img.setOnClickListener( this );
        pictures_img = (ImageView) findViewById( R.id.pictures_img );
        pictures_img.setOnClickListener( this );
        mPageGridView = findViewById(R.id.vp_grid_view);
        initData();
        //加载表情
        mPageGridView.setData(mList);
        mPageGridView.setOnItemClickListener(new PageGridView.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {//点击将表情显示在编辑框中
                infType = ListData.TEXT;
                if (position==20||position==41||position==62||position==83||position==104){
                    et_sendText.setText("");
                }else {
                    Html.ImageGetter imageGetter = new Html.ImageGetter() {
                        public Drawable getDrawable(String source) {
                            int id = Integer.parseInt(source);
                            Drawable d = getResources().getDrawable(id);
                            d.setBounds(0, 0, 100, 100);
                            return d;
                        }
                    };
                    CharSequence cs = Html.fromHtml("<img src='" + FaceUtils.gifFaceInfo.get(position) + "'/>", imageGetter, null);
                    et_sendText.getText().append(cs);
                }
            }
        });
        adapter = new TextAdapter( this, lists );
        lv.setAdapter( adapter );
    }

    /**
     * 获取表情数据
     */
    private void initData() {
        mList=new ArrayList<>();
        mList = FaceUtils.initFaceData();
    }

    /**
     * 发布语音信息
     */
    private void sendAmr(final AppStr appStr, final PostObj postAmr, final ListData recorderList) {
        if(appStr.IsCompleted() == true){
            String amrFilePath = postAmr.getAccessUrl();
            Log.i( TAG, "onFinish: amrFilPath is "+amrFilePath );
            String httpMessage = postAmr.getHttpMessage();
            Log.i( TAG, "onFinish: httpMessage is "+httpMessage );
            if(httpMessage.equals( "OK" ) && amrFilePath != null){
                recorderList.setFilePath( amrFilePath );
                Gson gson = new Gson();
                final String jsonStr = gson.toJson( recorderList,ListData.class );
                Log.i( TAG, "onFinish: jsonStr is "+jsonStr );
                new Thread( new Runnable() {
                    @Override
                    public void run() {
                        rePublishClient.myRepublish( jsonStr );
                    }
                } ).start();
            } else{
                Toast.makeText( this, "消息发送失败！", Toast.LENGTH_SHORT ).show();
                Log.i( TAG, "onFinish: 语音上传失败！" );
            }
        }else{
            new Thread( new Runnable() {
                @Override
                public void run() {
                    sendAmr(appStr,postAmr,recorderList );
                }
            } ).start();
        }
    }

    /**
     * 语音消息的读取
     */
    private void setListViewAdapter() {
        adapter = new TextAdapter(this,lists );
        lv.setAdapter( adapter );
        lv.setSelection( adapter.getCount()-1 );
        lv.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final ListData finalData = lists.get( position );
                if(finalData.getInfType() == ListData.RECORDER){
                    //如果第一个动画正在运行， 停止第一个播放其他的
                    if(mAnimView_right != null) {
                        mAnimView_right.setBackgroundResource( R.drawable.adj_right );
                    }
                    if(mAnimView_left != null) {
                        mAnimView_left.setBackgroundResource( R.drawable.adj_left );
                    }
                    mAnimView_right = null;
                    mAnimView_left =  null;

                    //播放动画
                    mAnimView_left = view.findViewById( R.id.id_recorder_anim_left );
                    mAnimView_right = view.findViewById( R.id.id_recorder_anim_right );
                    AnimationDrawable animation = null;
                    AppStr appStr = (AppStr)getApplicationContext();
                    if(finalData.getFlag() == ListData.SEND){
                        //检测文件
                        if(lists.get( position ).getAmrFilePath() == null){
                            Toast.makeText( ChatActivity.this, "录音文件地址为空，无法播放！", Toast.LENGTH_SHORT ).show();
                            return;
                        }

                        if(!NTest.fileIsExists( lists.get( position ).getAmrFilePath() )){
                            Toast.makeText( ChatActivity.this, "录音文件不存在，无法播放！", Toast.LENGTH_SHORT ).show();
                            return;
                        }
                        mAnimView_right.setBackgroundResource( R.drawable.play_anim_right );
                        animation = (AnimationDrawable) mAnimView_right.getBackground();
                        appStr.setDownLoad( true );
                    }else{
                        if(lists.get( position ).getAmrFilePath() == null){
                            //判断网络状态，要进行网络加载工作
                            if(NTest.getConnectedType( ChatActivity.this ) == 1){
                                appStr.setIsCompleted( false );
                                PostObj downLoad  = new PostObj();
                                Log.i( TAG, "onItemClick: 即将下载录音文件" );
                                downLoad.PostObject( ChatActivity.this,finalData.getFilePath(),ListData.RECEIVE,TYPE_RECORDER_MESSAGE );
                                appStr.setDownLoad( false );
                                downLoadRecorder(appStr,downLoad,position);
//                                mAnimView_left.setBackgroundResource( R.drawable.play_anim_left );
//                                animation = (AnimationDrawable) mAnimView_left.getBackground();
                            }else {
                                Toast.makeText( ChatActivity.this, "亲，当前网络断开了哦！无法播放", Toast.LENGTH_SHORT ).show();
                            }
                        }else{
                            if(!NTest.fileIsExists( lists.get( position ).getAmrFilePath() )){
                                Toast.makeText( ChatActivity.this, "录音文件不存在，无法播放！", Toast.LENGTH_SHORT ).show();
                                return;
                            }
//                            mAnimView_left.setBackgroundResource( R.drawable.play_anim_left );
//                            animation = (AnimationDrawable) mAnimView_left.getBackground();
                        }
                        mAnimView_left.setBackgroundResource( R.drawable.play_anim_left );
                        animation = (AnimationDrawable) mAnimView_left.getBackground();
                    }
                    animation.start();
                    //播放录音
                    playRecorder(appStr,finalData,position,mAnimView_left,mAnimView_right);
                } else {
                    if (!showFaceFlag){//将表情栏隐藏
                        mPageGridView.setVisibility(View.GONE);
                        showFaceFlag = true;
                    }
                    if(isPop){
                        others.setVisibility( View.GONE );
                        isPop = false;
                        pop_plus.setImageResource( R.drawable.plus_normal );
                    }
                }
            }
        } );
    }

    /**
     * 播放语音
     */
    private void playRecorder(final AppStr appStr, final ListData finalData, final int position, final View mAnimView_left, final View mAnimView_right) {
        if(appStr.isDownLoad() == true){
//            //检测文件
//            if(lists.get( position ).getAmrFilePath() == null){
//                Toast.makeText( ChatActivity.this, "录音文件地址为空，无法播放！", Toast.LENGTH_SHORT ).show();
//                return;
//            }
//
//            if(NTest.fileIsExists( lists.get( position ).getAmrFilePath() ) == false){
//                Toast.makeText( ChatActivity.this, "录音文件不存在，无法播放！", Toast.LENGTH_SHORT ).show();
//                return;
//            }

            //播放音频  完成后改回原来的background
            MediaManager.playSound( lists.get( position ).getAmrFilePath(), new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    //取消动画
                    if(finalData.getFlag() == ListData.SEND){
                        mAnimView_right.setBackgroundResource( R.drawable.adj_right );
                    }else{
                        mAnimView_left.setBackgroundResource( R.drawable.adj_left );
                    }
                }
            } );
        }else{
            new Thread( new Runnable() {
                @Override
                public void run() {
                    playRecorder(appStr,finalData,position,mAnimView_left,mAnimView_right);
                }
            } ).start();
        }
    }

    /**
     * 下载录音文件
     */
    @Nullable
    private void downLoadRecorder(final AppStr appStr, final PostObj downLoad, final int position) {
        //网络下载需要时间，需要等待下载成功结束拿到本地地址
        if(appStr.IsCompleted() == true){
            String httpMessage = downLoad.getHttpMessage();
            String amrPath = downLoad.getAccessUrl();
            if(httpMessage.equals( "OK" )){
                Log.i( TAG, "downLoadRecorder: amrPath is "+amrPath );
                lists.get( position ).setAmrFilePath( amrPath );
                appStr.setDownLoad( true );
            }else {
                Toast.makeText( ChatActivity.this, "读取消息失败！", Toast.LENGTH_SHORT ).show();
                appStr.setDownLoad( true );
            }
        }else {
            new Thread( new Runnable() {
                @Override
                public void run() {
                    downLoadRecorder( appStr,downLoad,position );
                }
            } ).start();
        }
    }

    /**
     * 根据生命周期 管理播放录音
     */
    @Override
    protected void onPause() {
        super.onPause();
        MediaManager.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MediaManager.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaManager.release();
    }

    /**
     *设置时间
     */
    private String getTime() {
        currentTime = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        Date curDate = new Date();
        String str = format.format( curDate );
        if (currentTime - oldTime >= 5 * 60 * 1000) {
            oldTime = currentTime;
            return str;
        } else {
            return "";
        }
    }

    /**
     *点击事件的处理
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_voice:
                if (!showFaceFlag){//将表情栏隐藏
                    mPageGridView.setVisibility(View.GONE);
                    showFaceFlag = true;
                }
                bt_voice.setVisibility( View.GONE );
                bt_keyboard.setVisibility( View.VISIBLE );
                et_sendText.setVisibility( View.GONE );
                mAudioRecorderButton.setVisibility( View.VISIBLE );
                btn_send.setVisibility( View.GONE );
                bt_emoji.setVisibility( View.VISIBLE );
                others.setVisibility( View.GONE );
                isPop = false;
                pop_plus.setImageResource( R.drawable.plus_normal );
                break;
            case R.id.bt_keyboard:
                if (!showFaceFlag){//将表情栏隐藏
                    mPageGridView.setVisibility(View.GONE);
                    showFaceFlag = true;
                }
                bt_keyboard.setVisibility( View.GONE );
                bt_voice.setVisibility( View.VISIBLE );
                et_sendText.setVisibility( View.VISIBLE );
                mAudioRecorderButton.setVisibility( View.GONE );
                bt_emoji.setVisibility( View.VISIBLE );
                pop_plus.setVisibility( View.VISIBLE );
                others.setVisibility( View.GONE );
                isPop = false;
                pop_plus.setImageResource( R.drawable.plus_normal );
                break;
            case R.id.bt_emoji:
                if (showFaceFlag){
                    mPageGridView.setVisibility(View.VISIBLE);
                    showFaceFlag = false;
                }else {
                    mPageGridView.setVisibility(View.GONE);
                    showFaceFlag = true;
                }
                bt_keyboard.setVisibility( View.GONE );
                bt_voice.setVisibility( View.VISIBLE );
                et_sendText.setVisibility( View.VISIBLE );
                mAudioRecorderButton.setVisibility( View.GONE );
                bt_emoji.setVisibility( View.VISIBLE );
                pop_plus.setVisibility( View.VISIBLE );
                others.setVisibility( View.GONE );
                isPop = false;
                pop_plus.setImageResource( R.drawable.plus_normal );
                break;
            case R.id.bt_send:
                sentAndRepublish();
                break;
            case R.id.et_sendText://点击编辑框将表情栏隐藏
                if (!showFaceFlag){
                    mPageGridView.setVisibility(View.GONE);
                    showFaceFlag = true;
                }
                others.setVisibility( View.GONE );
                isPop = false;
                pop_plus.setImageResource( R.drawable.plus_normal );
                break;
            case R.id.pop_plus:
                if (!showFaceFlag){//将表情栏隐藏
                    mPageGridView.setVisibility(View.GONE);
                    showFaceFlag = true;
                }
                if (isPop == true) {
                    pop_plus.setImageResource( R.drawable.plus_normal );
                    isPop = false;
                    others.setVisibility( View.GONE );
                } else {
                    isPop = true;
                    pop_plus.setImageResource( R.drawable.plus_picked );
                    others.setVisibility( View.VISIBLE );
                }
                break;
            case R.id.camera_img:
                infType = ListData.IMAGE;
                Log.i( TAG, "setOnItemClick: 你点击了拍照" );
                takePhoto();
                break;
            case R.id.pictures_img:
                infType = ListData.IMAGE;
                Log.i( TAG, "setOnItemClick: 你点击了相册" );
                pickPhoto();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    File picPath = ImageUtil.getTempFile();
                    showAndPostPic(picPath.toString());
                }
                break;
            case CHOOSE_PHOTO://相册
                if (resultCode == RESULT_OK) {
                    try {
                        imageUri = data.getData(); //获取系统返回的照片的Uri
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getContentResolver().query(imageUri,
                                filePathColumn, null, null, null);//从系统表中查询指定Uri对应的照片
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String path = cursor.getString(columnIndex);  //获取照片路径
                        cursor.close();
                        ImageUtil.setTempFile( ChatActivity.this );
                        showAndPostPic( path );
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    /**
     * 显示图片
     */
    private void showAndPostPic(String path) {
        ListData data = new ListData( fromWho,toUser, ListData.SEND,getTime(),infType );
        data.setLocalPicPath( path );
        lists.add(data);//将数据内容加入lists中
        adapter.notifyDataSetChanged();
        lv.setAdapter(adapter);
        if(NTest.getConnectedType( ChatActivity.this ) == 1){
            //上传图片
            AppStr appStr = (AppStr)getApplication();//全局变量
            appStr.setIsCompleted(false);
            PostObj postPic = new PostObj();
            postPic.PostObject( ChatActivity.this,path ,ListData.SEND,TYPE_PHOTO_MESSAGE);
            publishPic(appStr,postPic,data);//检查图片是否上传成功
        }else {
            Toast.makeText( this, "网络连接不可用，请稍后重试！", Toast.LENGTH_SHORT ).show();
        }
    }

    /**
     * 上传图片
     */
    private void publishPic(final AppStr appStr, final PostObj postPic, final ListData data) {
        if(appStr.IsCompleted()== true){
            String httpMessage = postPic.getHttpMessage();
            String picPath  = postPic.getAccessUrl();
            if(httpMessage.equals("OK") && picPath != null){
                Gson gson = new Gson();
                data.setPicPath( picPath );
                final String jsonStr = gson.toJson(data, ListData.class);
                Log.i( TAG, "myRepublish: jsonStr is "+jsonStr );
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        rePublishClient.myRepublish(jsonStr);
                    }
                }).start();
            }else{
                Toast.makeText(ChatActivity.this, "发送消息失败！", Toast.LENGTH_SHORT).show();
            }
        }else{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    publishPic(appStr,postPic,data);
                }
            }).start();
        }
    }

    /**
     * 选择相片
     */
    private void pickPhoto() {
        Intent picsIn = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(picsIn, CHOOSE_PHOTO);
    }

    /**
     * 拍照
     */
    private void takePhoto() {
        Intent photo = new Intent( MediaStore.ACTION_IMAGE_CAPTURE);
        imageUri = ImageUtil.getImageUri(ChatActivity.this);
        photo.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); //putExtra()指定图片的输出地址，填入之前获得的Uri对象
        startActivityForResult(photo, TAKE_PHOTO);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideKeyboard(v, ev)) {
                hideKeyboard(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
     */
    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0],
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        //如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditText上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * 获取InputMethodManager，隐藏软键盘
     */
    private void hideKeyboard(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 底部输入的初始状态
     */
    public void setDefaultState( ){
        bt_keyboard.setVisibility( View.GONE );
        bt_voice.setVisibility( View.VISIBLE );
        et_sendText.setVisibility( View.VISIBLE );
        mAudioRecorderButton.setVisibility( View.GONE );
        bt_emoji.setVisibility( View.VISIBLE );
        pop_plus.setVisibility( View.VISIBLE );
        btn_send.setVisibility( View.GONE );
        others.setVisibility( View.GONE );
    }

    /**
     * 至发布
     */
    private void sentAndRepublish() {
        content_str = FaceUtils.FilterHtml(Html.toHtml(et_sendText.getText()));//获取发送的聊天文字和表情内容
        et_sendText.setText( "" );
        //        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //        String time = sdf.format(new Date());
        ListData listData;
        listData = new ListData( fromWho, toUser, content_str, ListData.SEND, getTime(), infType );
        lists.add( listData );

        Log.i( TAG, "----------content_str=" + content_str );
        runOnUiThread( new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        } );

        Log.i( TAG, "----------content_str=" + content_str );
        Gson gson = new Gson();
        final String jsonStr = gson.toJson( listData, ListData.class );
        Log.i( TAG, "myRepublish: jsonStr is " + jsonStr );

//        if (lists.size() > 30) {
//            for (int i = 0; i < lists.size(); i++) {
//                lists.remove( i );
//            }
//        }

        new Thread( new Runnable() {
            @Override
            public void run() {
                rePublishClient.myRepublish( jsonStr );
            }
        } ).start();
    }
}
