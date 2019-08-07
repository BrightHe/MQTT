package robot.com.myapplication.pal;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import robot.com.myapplication.R;


public class AddActivity extends AppCompatActivity {
    private ImageView image_back;
    private NewFriendAdapter newFriendAdapter;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager layoutManager;

    private static final String TAG = "AddActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_add);

        //初始化界面视图
        initView();

        //返回按钮
        image_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    //初始化界面视图
    private void initView(){
        image_back = (ImageView) findViewById(R.id.image_back);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        layoutManager = new LinearLayoutManager(this);//创建布局管理器
        mRecyclerView.setLayoutManager(layoutManager);//设置布局管理器，默认item方向为垂直
        //创建好友申请列表适配器
        Log.i(TAG, "initView: "+ FriendsData.newFriendList.toString());
        newFriendAdapter = new NewFriendAdapter(FriendsData.newFriendList,AddActivity.this);
        mRecyclerView.setAdapter(newFriendAdapter);//设置适配器
        if (FriendsData.newFriendList==null){
            Toast.makeText(this,"暂无好友申请", Toast.LENGTH_SHORT).show();
        }
    }

}
