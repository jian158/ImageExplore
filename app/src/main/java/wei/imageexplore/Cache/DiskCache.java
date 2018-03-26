package wei.imageexplore.Cache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by wei on 2017/8/1.
 */

public class DiskCache {//本地缓存
    private File cacheDir;
    public final static String DEFAULTPATH="ImageCache";//缓存文件夹
    public DiskCache(){
        cacheDir=new File(Environment.getExternalStorageDirectory().getPath()+ File.separator+DEFAULTPATH);
        if (!cacheDir.exists())//创建缓存文件夹
            cacheDir.mkdirs();
    }

    public synchronized boolean put(Bitmap bitmap,String path){
        try {
            File file=new File(path);
            File dest=new File(cacheDir+File.separator+trimEndName(file.getName()));
            if (dest.exists())
                return true;
            FileOutputStream os=new FileOutputStream(dest);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,os);
            os.close();
        } catch (Exception e) {
            Log.e("error", "保存obj失败");
            return false;
        }
        return true;
    }

    public Bitmap get(String path){
        File file=new File(path);
        File dest=new File(cacheDir+File.separator+trimEndName(file.getName()));
        if (!dest.exists())
            return null;
        return getScaleBitmap(dest.getPath());
    }

    private Bitmap getScaleBitmap(String path){
        Bitmap bitmap =null;
//        BitmapFactory.Options opts = null;
        try {
//            opts = new BitmapFactory.Options();
//            opts.inJustDecodeBounds = true;      //设置inJustDecodeBounds为true后，decodeFile并不分配空间，此时计算原始图片的长度和宽度
//            opts.inPreferredConfig = Bitmap.Config.RGB_565;
//            BitmapFactory.decodeFile(path,opts);
//            opts.inSampleSize = 1;
//            opts.inJustDecodeBounds = false;
            return BitmapFactory.decodeFile(path);
        }catch (Exception e){
            Log.i("Tag","too Fast! Memory boom");
            return null;
        }
    }


    private String trimEndName(String Name){
        return Name+".cache";
    }
}
