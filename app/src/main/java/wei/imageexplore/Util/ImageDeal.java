package wei.imageexplore.Util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class ImageDeal {

    public ImageDeal() {
        // TODO Auto-generated constructor stub
    }

    public  static Bitmap ImageCrop(Bitmap bitmap) {
        int w = bitmap.getWidth(); // 得到图片的宽，高
        int h = bitmap.getHeight();

        int wh = w > h ? h/130: w/65;// 裁切后所取的正方形区域边长

        int retX = w > h ? (w - h) / 2 : 0;//基于原图，取正方形左上角x坐标
        int retY = w > h ? 0 : (h - w) / 2;

        //下面这句是关键
        return Bitmap.createBitmap(bitmap, retX, retY, wh, wh, null, false);
    }

    public static Bitmap getScaleBitmap(String path, int width, int height){
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
//            bitmap=BitmapFactory.decodeFile(path) ;
//            int bmpWidth = bitmap.getWidth();
//            int bmpHeight = bitmap.getHeight();
//            if(width != 0 && height !=0){
//                Matrix matrix = new Matrix();
//                float scaleWidth = ((float) width / bmpWidth);
//                float scaleHeight = ((float) height / bmpHeight);
//                matrix.postScale(scaleWidth, scaleHeight);
//                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bmpWidth, bmpHeight, matrix, true);
//            }
//            else
//                return null;
        }catch (Exception e){
            Log.i("Tag","too Fast! Memory boom");
            return null;
        }
    }



}
