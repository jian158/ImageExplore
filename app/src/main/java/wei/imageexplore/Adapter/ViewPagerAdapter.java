package wei.imageexplore.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import wei.imageexplore.R;
import wei.imageexplore.Util.ThreadPool;
import wei.imageexplore.View.ZooImageView;

/**
 * Created by Administrator on 2016/10/22.
 */

public class ViewPagerAdapter extends PagerAdapter {//图像显示适配器

    private Context mContext;
    private List<String> mDrawableResIdList;//图像列表
    private int window_width, window_height;// 控件宽度
    private ThreadPool threadPool;          //线程池用于加载图像
    private Handler handler=new Handler(); //用于子线程显示图像
    public ViewPagerAdapter(Context context, List<String> resIdList, Activity activity) {
        super();
        mContext = context;
        mDrawableResIdList = resIdList;
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        window_width = manager.getDefaultDisplay().getWidth();
        window_height = manager.getDefaultDisplay().getHeight();
        threadPool=new ThreadPool(2);
    }



    @Override
    public int getCount() {//获取图像数目
        if (mDrawableResIdList != null) {
            return mDrawableResIdList.size();
        }
        return 0;
    }

    @Override
    public int getItemPosition(Object object) {//获取图像位置
        if (object != null && mDrawableResIdList != null) {
            String resId = (String) ((ImageView)object).getTag();
            if (resId != null) {
                for (int i = 0; i < mDrawableResIdList.size(); i++) {
                    if (resId.equals(mDrawableResIdList.get(i))) {
                        return i;
                    }
                }
            }
        }
        return POSITION_NONE;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {//加载图像
        if (mDrawableResIdList != null && position < mDrawableResIdList.size()) {
            final String resId = mDrawableResIdList.get(position);
            if (resId != null) {
                final ZooImageView itemView = new ZooImageView(mContext);
                threadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        final Bitmap bmp=getBitmap(resId);
                        itemView.setDrawingCacheEnabled(true);//true可手动释放内存
                        itemView.setTag(resId);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                itemView.setScaleType(ImageView.ScaleType.MATRIX);//缩放方式
                                itemView.setImageBitmap(bmp);
                            }
                        });
                    }
                });
                container.addView(itemView);
                return itemView;
            }
        }
        return null;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {//销毁资源
        ZooImageView ImageView= (ZooImageView) object;
        container.removeView(ImageView);
        Bitmap bmp=ImageView.getDrawingCache();
        ImageView.setDrawingCacheEnabled(false);
        if (bmp!=null)bmp.recycle();
        bmp=null;
        ImageView.setImageBitmap(null);
        ImageView=null;
        System.gc();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public void startUpdate(ViewGroup container) {
    }
    @Override
    public void finishUpdate(ViewGroup container) {
    }

    public void updateData(List<String> itemsResId) {
        if (itemsResId == null) {
            return;
        }
        mDrawableResIdList = itemsResId;
        this.notifyDataSetChanged();
    }
    public Bitmap getBitmap(String path){//缩放图像
        Bitmap bitmap = null;
        try {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;//避免重复加载图像进内存
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        BitmapFactory.decodeStream(new FileInputStream(path),null,opts);
            if (opts.outWidth>window_width||opts.outHeight>window_height)//缩放图像到窗口大小
            opts.inSampleSize= Math.max((int)(opts.outHeight / (float) window_height), (int)(opts.outWidth / (float) window_width));
        opts.inJustDecodeBounds=false;
            bitmap = BitmapFactory.decodeStream(new FileInputStream(path),null,opts);
            if (bitmap!=null)
            return bitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.fail);
    }
}
