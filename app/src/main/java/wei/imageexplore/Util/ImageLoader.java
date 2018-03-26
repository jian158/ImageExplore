package wei.imageexplore.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.util.LruCache;
import android.widget.AbsListView;
import android.widget.ImageView;

import java.util.HashSet;

import wei.imageexplore.R;
import wei.imageexplore.Cache.DiskCache;


public class ImageLoader{
    private AbsListView absListView;
    private Context context;
    public ThreadPool pool;
    private LruCache<String,Bitmap> lruCache;//内存缓存
    private Handler handler=new Handler();
    private HashSet<String> cacheSet;
    private int width=180,height=240;
    private DiskCache diskCache;            //本地缓存
    public ImageLoader(Context context, AbsListView absListView){
        this.context=context;
        this.absListView=absListView;
        pool=new ThreadPool(5);
        cacheSet=new HashSet<>();
        diskCache=new DiskCache();
        int cacheSize= (int) (Runtime.getRuntime().maxMemory()/16);
        lruCache=new LruCache<String,Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {//测量图像字节数
                return value.getByteCount();
            }

            @Override       //缓存过多清除
            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                super.entryRemoved(evicted, key, oldValue, newValue);
                oldValue.recycle();
            }

        };
    }

    public void setSize(int w,int h){
        width=w;
        height=h;
    }

    public boolean isCache(String path){
        return lruCache.get(path)==null;
    }

    public void Load(ImageView imageView,String path){
        imageView.setTag(path);
        Bitmap bitmap=null;
        if ((bitmap=lruCache.get(path))!=null){//判断是否缓存
            imageView.setImageBitmap(bitmap);
        }
        else {
            imageView.setImageBitmap(null);
            if (!cacheSet.contains(path)){
                cacheSet.add(path);
                pool.execute(new LoadImageTask(path));
            }
        }
    }


    public static Bitmap getScaleBitmap(String path, int width, int height){//缩放图像
        Bitmap bitmap =null;
        BitmapFactory.Options opts = null;
        try {
            opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;      //设置inJustDecodeBounds为true后，decodeFile并不分配空间，此时计算原始图片的长度和宽度
            opts.inPreferredConfig = Bitmap.Config.RGB_565;
            BitmapFactory.decodeFile(path,opts);
            opts.inSampleSize = Math.max((int) (opts.outHeight / (float) height), (int) (opts.outWidth / (float) width));
            opts.inJustDecodeBounds = false;
            return BitmapFactory.decodeFile(path,opts);
        }catch (Exception e){
            Log.i("Tag","Memory boom");
            return null;
        }
    }


    private class setBitmapTask implements Runnable{
        public String path;
        public setBitmapTask(String path) {
            this.path = path;
        }

        @Override
        public void run() {
            ImageView iv = (ImageView) absListView.findViewWithTag(path);
            if (iv != null) {
                iv.setImageBitmap(lruCache.get(path));
            }
        }
    }


    private class LoadImageTask implements Runnable{
        private String path;
        public LoadImageTask(String path){
            this.path=path;
        }
        @Override
        public void run() {
            Bitmap bitmap=diskCache.get(path);//从硬盘获取缓存
            if (bitmap==null)//获取失败则缩放原图
                bitmap=getScaleBitmap(path,width,height);
            if (bitmap==null){
                Log.i("Tag","No Valid");
                bitmap= BitmapFactory.decodeResource(context.getResources(), R.drawable.load_fail);
            }
            lruCache.put(path,bitmap);
//            pool.execute(new DiskCacheRunable(bitmap,path));
            cacheSet.remove(path);
            handler.post(new setBitmapTask(path));
            diskCache.put(bitmap,path);
        }
    }

    public void ClearCache(){
        cacheSet.clear();
        lruCache.evictAll();
    }
}
