package wei.imageexplore.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;


/**
 * Created by wei on 2017/1/13.
 */

public class CollectionBase extends SQLiteOpenHelper {

    private final static int DATABASE_VERSION = 1;
    private final static String TABLE_NAME = "mytable";
    public final static String ID = "id";
    public final static String PATH = "path";
//    SQLiteDatabase db;

    public CollectionBase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);

    }


    //创建table
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME + " (" + ID
                + " INTEGER primary key, " + PATH + " text);";
//        autoincrement
        db.execSQL(sql);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(sql);
        onCreate(db);

    }

    public void getCollections(ArrayList<String> list){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db
                .query(TABLE_NAME, null, null, null, null, null, null);
        if(cursor.moveToFirst()){
            while (cursor.moveToNext()){
                list.add(cursor.getString(1));
            }
        }
        cursor.close();
    }

    public Cursor select() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db
                .query(TABLE_NAME, null, null, null, null, null, null);
        return cursor;
    }

    //增加操作
    public long insert(String path)
    {
        long row=-1;
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues cv = new ContentValues();
//        cv.put(ID,id);
        cv.put(PATH,path);
        row = db.insert(TABLE_NAME, null, cv);
        return row;
    }

    public void insertobject(int id,Object object)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ID,id);
        try {
            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(arrayOutputStream);
            objectOutputStream.writeObject(object);
            objectOutputStream.flush();
            byte data[] = arrayOutputStream.toByteArray();
            cv.put(PATH,data);
            db.insert(TABLE_NAME, null, cv);
            objectOutputStream.close();
            arrayOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //删除操作
    public void delete(String path)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        String where =PATH + "=?";
//        String[] whereValue ={ Integer.toString(id) };
        db.delete(TABLE_NAME,where,new String[]{path});
    }
    //修改操作
    public void update(int id,String path)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        String where =ID + "=?";
        String[] whereValue = { Integer.toString(id) };
        ContentValues cv = new ContentValues();
        cv.put(PATH, path);
        db.update(TABLE_NAME, cv, where, whereValue);
    }
}

