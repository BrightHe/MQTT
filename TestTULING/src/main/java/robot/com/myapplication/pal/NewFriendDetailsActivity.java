package robot.com.myapplication.pal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import robot.com.myapplication.R;


public class NewFriendDetailsActivity extends AppCompatActivity {
    private TextView text_username;
    private TextView qianming;
    private TextView from2;
    private TextView sex2;
    private ImageView iv_back;
    private ImageView image_head;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_new_friend_details);

        text_username = (TextView) findViewById(R.id.username2);
        qianming = (TextView) findViewById(R.id.qianming2);
        image_head = (ImageView) findViewById(R.id.image_head);
        sex2 = (TextView) findViewById(R.id.sex2);
        from2 = (TextView) findViewById(R.id.from2);
        iv_back = (ImageView) findViewById(R.id.iv_back);

        Intent intent = getIntent();
        NewFriend newFriend = (NewFriend) intent.getSerializableExtra("newFriend");
        text_username.setText(newFriend.getUserName());
        qianming.setText(newFriend.getSigature());
        sex2.setText(newFriend.getSex());
        from2.setText(newFriend.getWherefrom());
        image_head.setImageResource(newFriend.getFriendImage());


        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
  }
}
