package wei.imageexplore.DataBase;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import wei.imageexplore.Table.ImageInfo;

/**
 * Created by wei on 2017/11/10.
 */

public class Base {
    public static void getImgPathList(Context context, List<String> imglist, String imagepath) {
        String[] selectionargs={imagepath+"%"};
        String selection= MediaStore.Images.Media.DATA+" like ?";
        Cursor cursor =context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,null,selection,selectionargs,null);
        while (cursor.moveToNext()) {
            imglist.add(cursor.getString(1));// 将图片路径添加到list中
        }
        if (imglist.size()>0)
            Log.i("path",imglist.get(0));
        cursor.close();
    }




    public static void getImgPathList(Context context, List<String> imglist) {
        Cursor cursor =MediaStore.Images.Media.query(context.getContentResolver(),
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null);
        while (cursor.moveToNext()) {
            imglist.add(cursor.getString(1));// 将图片路径添加到list中
        }
        cursor.close();
        Log.i("Size",String.valueOf(imglist.size()));
//        return imglist;
    }

    public static ArrayList<String> getImgPathList(Context context, String imagepath ) {
        ArrayList<String> imglist=new ArrayList<String>();
        String[] selectionargs={imagepath+"%"};
        String selection= MediaStore.Images.Media.DATA+" like ?";
        //new String[] { "_id", "_data" }
        //EXTERNAL_CONTENT_URI
        Cursor cursor =context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,null,selection,selectionargs,null);
        while (cursor.moveToNext()) {
            imglist.add(cursor.getString(1));// 将图片路径添加到list中
        }
        cursor.close();
        return imglist;
    }


    public static void fileUpdate(Context context,String file){
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file)));
    }

    public static ImageInfo getImageInfo(Context context,String path){
        ImageInfo info=null;
        //可以手动指定获取的列
        String[] columns = new String[]{
                MediaStore.Images.Media.DATA, MediaStore.Images.Media.SIZE,MediaStore.Images.Media.DATE_TAKEN,MediaStore.Images.Media.WIDTH,MediaStore.Images.Media.HEIGHT};

        String[] selectionargs={path};
        String selection= MediaStore.Images.Media.DATA+" = ?";
        Cursor cursor =context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,columns,selection,selectionargs,null);
        if (cursor!=null){
            cursor.moveToFirst();
            info=new ImageInfo();
            info.path=cursor.getString(0);
            info.size=cursor.getString(1);
            info.date=cursor.getString(2);
            info.w=cursor.getString(3);
            info.h=cursor.getString(4);
            cursor.close();
        }
        return info;
    }

}
