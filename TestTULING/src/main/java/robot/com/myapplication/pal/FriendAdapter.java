package robot.com.myapplication.pal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import robot.com.myapplication.R;

/**
 * Created by admin on 2019/7/31.
 */

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {
    private List<NewFriend> mFriendList = new ArrayList<NewFriend>();//数据源
    private NewFriend friend;
    private static final String TAG = "FriendAdapter";

    public FriendAdapter(List<NewFriend> mFriendList) {
        this.mFriendList = mFriendList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        View mFriendView;
        ImageView userImage;
        TextView userName1;
        TextView userWrite1;

        public ViewHolder(View itemView) {
            super(itemView);
            mFriendView = itemView;
            userImage = itemView.findViewById( R.id.image_userhead);
            userName1 = itemView.findViewById(R.id.username1);
            userWrite1 = itemView.findViewById(R.id.qianming1);
        }
    }

    //创建ViewHolder相当于ListView Adapter的getView方法
    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.frienditem,parent,false);
       final ViewHolder viewHolder1 = new ViewHolder(view1);
        viewHolder1.mFriendView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int positon = viewHolder1.getAdapterPosition();
                friend = mFriendList.get(positon);
                Bundle data1 = new Bundle();
                data1.putSerializable("friend_data",friend);
                Intent intent = new Intent(parent.getContext(),FriendDataActivity.class);
                intent.putExtras(data1);
                parent.getContext().startActivity(intent);
            }
        });
        return  viewHolder1;
    }
    //数据绑定
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.i(TAG, "FriendAdapter的onBindViewHolder: position"+position);
            NewFriend friend = mFriendList.get(position);
            holder.userName1.setText(mFriendList.get(position).getUserName());
            holder.userWrite1.setText(mFriendList.get(position).getSigature());
            if (mFriendList.get(position).getUserImage() != null){
                Bitmap bitmap = BitmapFactory.decodeByteArray(mFriendList.get(position).getUserImage(),0,mFriendList.get(position).getUserImage().length,null);
                holder.userImage.setImageBitmap(bitmap);
            }else {
                holder.userImage.setImageResource(R.mipmap.ic_launcher_round);
            }
            holder.userImage.setImageResource(mFriendList.get(position).getFriendImage());
    }

    //数据的长度
    @Override
    public int getItemCount() {
        return mFriendList.size();
    }
}
