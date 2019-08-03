package robot.com.myapplication.ImageUtils;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.text.format.DateFormat;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;


public class ImageUtil {
    private static String TAG = "TAG";
    public static File tempFile;
    private static String name;

    public static String getName() {
        return name;
    }

    public static Uri getImageUri(Context content) {
        File file = setTempFile(content);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= 24) {
            //将File对象转换成封装过的Uri对象，这个Uri对象标志着照片的真实路径
            Uri imageUri = FileProvider.getUriForFile(content, "chat.pic.fileprovider", file);
            return imageUri;
        } else {
            //将File对象转换成Uri对象，这个Uri对象标志着照片的真实路径
            Uri imageUri = Uri.fromFile(file);
            return imageUri;
        }
    }
    public static File setTempFile(Context content) {
        //自定义图片名称
        name = DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".png";
        Log.i(TAG, " name : " + name);
        //定义图片存放的位置
        tempFile = new File(content.getExternalCacheDir(), name);//将图片存放在关联缓存目录下
        Log.i(TAG, " tempFile : " + tempFile);//即：/sdcard/Android/data/<package name>/cache
        return tempFile;
    }

    //获取图片路径
    public static File getTempFile() {
        return tempFile;
    }
}
