package robot.com.myapplication.login;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import robot.com.myapplication.ChatActivity;
import robot.com.myapplication.R;
import robot.com.myapplication.dialog.DialogUIUtils;

import static robot.com.myapplication.dialog.DialogUIUtils.dismiss;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private CheckBox cb_mima;
    private EditText et_username, et_password;
    private Button meLogin, register;

    private String url = "http://47.105.185.251:8081/Proj31/login";
    private int opType = 90002;
    private String TAG = "Test";

    //判断是否是登录过
    private boolean login = false;
    //进度条一
    Dialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.login );
        SharedPreferences User = getSharedPreferences( "data", Context.MODE_PRIVATE );
        //如果未找到该值，则使用get方法中传入的默认值false代替
        boolean login = User.getBoolean( "login", false );
        Log.i( TAG, "login is "+login );
        if(login){
            Intent intent= new Intent( LoginActivity.this, ChatActivity.class );
            startActivity( intent );
        }else{
            initView();
        }
    }

    /**
     * 初始化界面
     */
    private void initView() {
        cb_mima = (CheckBox) findViewById( R.id.cb_mima );
        cb_mima.setOnClickListener( this );

        et_username = (EditText) findViewById( R.id.in_username );
        et_password = (EditText) findViewById( R.id.in_password );

        meLogin = (Button) findViewById( R.id.login_in );
        meLogin.setOnClickListener( this );

        register = (Button) findViewById( R.id.register_in );
        register.setOnClickListener( this );
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_in:
                Login();
                break;
            case R.id.register_in:
                registerUser();
                break;
            case R.id.cb_mima:
                autoLogin();
                break;
        }
    }

    /**
     * 自动登录
     */
    private void autoLogin() {

    }

    /**
     * 注册用户
     */
    private void registerUser() {
        Intent intent_reg = new Intent( LoginActivity.this, RegisterInActivity.class );
        startActivity( intent_reg );
    }

    /**
     * 登录
     */
    private void Login() {
        String username = et_username.getText().toString().trim();
        String password = et_password.getText().toString().trim();

        //检查数据格式是否正确
        if (TextUtils.isEmpty( username ) | TextUtils.isEmpty( password )) {
            Toast.makeText( LoginActivity.this, "用户名和密码不可为空！", Toast.LENGTH_SHORT ).show();
        } else {
            //创建实例对象
            UserVO userVO = new UserVO();
            //设置属性值
            userVO.setOpType( opType );
            userVO.setUname( username );
            userVO.setUpassword( MD5Utils.getMD5( password ) );
            //封装为json串
            Gson gson = new Gson();
            String userJsonStr = gson.toJson( userVO, UserVO.class );
            Log.i( TAG, "Login: userJsonStr  is " + userJsonStr );

            //进度框显示方法一
            progressDialog = DialogUIUtils.showLoadingDialog( LoginActivity.this, "正在登录" );
            progressDialog.show();
            //发送请求
            postDataLogin( username, userJsonStr );
        }
    }

    private void postDataLogin(final String username, String userJsonStr) {
        PostWith.sendPostWithOkhttp( url, userJsonStr, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //这里是子线程
                Log.d( TAG, "获取数据失败了" + e.toString() );
                runOnUiThread( new Runnable() {
                    @Override
                    public void run() {
                        dismiss( progressDialog );
                        String errorData = TestAndVerify.judgeError( LoginActivity.this );
                        Toast.makeText( LoginActivity.this, errorData, Toast.LENGTH_SHORT ).show();
                    }
                } );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {//回调的方法执行在子线程。
                    Log.d( TAG, "获取数据成功了" );
                    final String str = response.body().string();
                    Log.d( TAG, "response.body().string() is " + str );
                    Gson gson = new Gson();
                    UserQuery user = gson.fromJson( str, UserQuery.class );
                    UserVO userVO = user.getUser();
                    int flag = user.getFlag();
                    if (flag == 200) {
                        //保存Token
                        SharedPreferences.Editor editor = getSharedPreferences( "data", MODE_PRIVATE ).edit();
                        login = true;
                        editor.putBoolean( "login", login );
                        editor.putString( "uname", username );
                        editor.putString( "token", user.getToken() );
                        editor.putString( "picDir", userVO.getPicDir() );
                        editor.putString( "ps", userVO.getPs() );
                        editor.putString( "uid", userVO.getUid() );
                        editor.commit();
                        runOnUiThread( new Runnable() {
                            @Override
                            public void run() {
                                dismiss( progressDialog );
                                Toast.makeText( LoginActivity.this, "登录成功!", Toast.LENGTH_SHORT ).show();
                                Intent intent = new Intent( LoginActivity.this, ChatActivity.class );
                                startActivity( intent );
                                finish();
                            }
                        } );
                    } else if (flag == 20005) {
                        runOnUiThread( new Runnable() {
                            @Override
                            public void run() {
                                dismiss( progressDialog );
                                Toast.makeText( LoginActivity.this, "用户名或密码不正确，登录失败！", Toast.LENGTH_SHORT ).show();
                            }
                        } );
                    } else {
                        runOnUiThread( new Runnable() {
                            @Override
                            public void run() {
                                dismiss( progressDialog );
                                Toast.makeText( LoginActivity.this, "登录失败！", Toast.LENGTH_SHORT ).show();
                            }
                        } );
                    }
                }
            }
        } );
    }
}
