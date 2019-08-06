package robot.com.myapplication.login;

import android.util.Log;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by Administrator on 2019/8/4.
 */

class PostWith {
    public static String TAG = "Test";

    //OkHttp的post网络请求函数
    public static void  sendPostWithOkhttp( String url,String JsonStr,okhttp3.Callback callback){
        //创建实例对象
        OkHttpClient okHttpClient = new OkHttpClient();
        //包装方法体
        Log.i( TAG, "reqJson is " +JsonStr);
        RequestBody requestBody = new  FormBody.Builder()
                .add( "reqJson", JsonStr )
                .build();

        final Request request = new Request.Builder()
                .post( requestBody )
                .url( url )
                .build();
        okHttpClient.newCall( request ).enqueue( callback );//异步请求
    }
}
