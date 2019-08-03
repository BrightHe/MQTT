package robot.com.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

import robot.com.myapplication.tengxunyun.NTest;

public class TextAdapter extends BaseAdapter{
	private List<ListData> lists; //消息列表
	private Context mContext; //上下文
	//item 最小最大值
	private int mMinItemWidth;
	private int mMaxIItemWidth;

	private RelativeLayout layout; //布局

	public TextAdapter(@NonNull Context context,List<ListData> lists) {
		this.lists = lists;
		this.mContext = context;

		//获取屏幕宽度
		WindowManager wm = (WindowManager) context.getSystemService( Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);

		//item 设定最小最大值
		mMaxIItemWidth = (int) (outMetrics.widthPixels * 0.70f);
		mMinItemWidth = (int) (outMetrics.widthPixels * 0.16f);
	}

	@Override
	public int getCount() {
		return lists.size();
	}

	@Override
	public Object getItem(int position) {
		return lists.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		if(lists.get(position).getFlag() == ListData.RECEIVE){
			layout = (RelativeLayout) inflater.inflate(R.layout.leftitem, null);
		}
		if(lists.get(position).getFlag() == ListData.SEND){
			layout = (RelativeLayout) inflater.inflate(R.layout.rightitem, null);
		}
		TextView tv = (TextView) layout.findViewById(R.id.tv);
		TextView tv_time = (TextView) layout.findViewById(R.id.tv_time);
		ImageView tv_img = (ImageView) layout.findViewById( R.id.tv_img );
		View length = (LinearLayout)layout.findViewById( R.id.id_recorder_length );
		TextView seconds = (TextView)layout.findViewById( R.id.id_recorder_time );

		tv.setVisibility( View.GONE );
		tv_time.setVisibility( View.VISIBLE );
		tv_img.setVisibility( View.GONE );
		length.setVisibility( View.GONE );
		seconds.setVisibility( View.GONE );
        tv_time.setText(lists.get(position).getPublishTime());

		//按照消息类型匹配布局
		if(lists.get( position ).getInfType() == ListData.TEXT){
			tv.setVisibility( View.VISIBLE );
			//实例化imageGetter对象
			Html.ImageGetter imageGetter = new Html.ImageGetter() {
				public Drawable getDrawable(String source) {
					int id = Integer.parseInt(source);
					Drawable d = mContext.getResources().getDrawable(id);
					d.setBounds(0, 0, 100, 100);
					return d;
				}
			};
			tv.append(Html.fromHtml(lists.get(position).getContent(),imageGetter,null));
		}
		if(lists.get( position ).getInfType() == ListData.IMAGE){
			tv_img.setVisibility( View.VISIBLE );
			if(NTest.getConnectedType( mContext ) == -1){
                tv_img.setBackgroundResource( R.drawable.net_cut );
			}else {
				if(lists.get( position ).getFlag() == ListData.SEND){
					if(lists.get( position ).getLocalPicPath() == null){
						Toast.makeText( mContext, "图片不存在！", Toast.LENGTH_SHORT ).show();
						return layout;
					}
					if(!NTest.fileIsExists( lists.get( position ).getLocalPicPath() )){
						tv_img.setImageResource( R.drawable.not_exist );
						Toast.makeText( mContext, "图片不存在！", Toast.LENGTH_SHORT ).show();
						return layout;
					}
					Bitmap bitmap = NTest.imageScale( BitmapFactory.decodeFile( lists.get( position ).getLocalPicPath() ),300,300 );
					tv_img.setImageBitmap( bitmap );
				}else{
					Glide.with( mContext ).load( lists.get( position ).getPicPath() ).centerCrop().into( tv_img );
				}
			}
		}
		if(lists.get( position ).getInfType() == ListData.RECORDER){
			seconds.setVisibility( View.VISIBLE );
            length.setVisibility( View.VISIBLE );
			if(NTest.getConnectedType( mContext ) == -1){
                seconds.setBackgroundResource( R.drawable.net_cut );
			}else{
				//设置时间  matt.round 四舍五入
				seconds.setText(Math.round(lists.get(position).getTime())+"\"");
				//设置背景的宽度
				ViewGroup.LayoutParams lp = length.getLayoutParams();
				lp.width = (int) (mMinItemWidth + (mMaxIItemWidth / 60f*(lists.get( position ).getTime())));
			}
		}
		return layout;
	}
}
