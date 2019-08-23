package robot.com.myapplication.pal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import robot.com.myapplication.ChatActivity;
import robot.com.myapplication.R;

public class FriendDataActivity extends AppCompatActivity {
    private ImageView iv_back;
    private Button btn_send;
    private TextView username1;
    private TextView qianming1;
    private TextView from1;
    private TextView sex1;
    private ImageView image_head;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_friend_data);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        btn_send = (Button) findViewById(R.id.btn_send);

        username1 = (TextView) findViewById(R.id.username1);
        qianming1 = (TextView) findViewById(R.id.qianming1);
        sex1 = (TextView) findViewById(R.id.sex1);
        from1 = (TextView) findViewById(R.id.from1);
        image_head = (ImageView) findViewById(R.id.image_head);

        Intent intent1 = getIntent();
        final NewFriend friend = (NewFriend) intent1.getSerializableExtra("friend_data");
        username1.setText(friend.getUserName());
        qianming1.setText(friend.getSigature());
        sex1.setText(friend.getSex());
        from1.setText(friend.getWherefrom());
        image_head.setImageResource(friend.getFriendImage());

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FriendDataActivity.this,"进入消息界面", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent( FriendDataActivity.this, ChatActivity.class );
                intent.putExtra( "pal_name",friend.getUserName() );
                startActivity( intent );
            }
        });

    }
}
