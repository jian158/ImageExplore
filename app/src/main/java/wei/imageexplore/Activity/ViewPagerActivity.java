package wei.imageexplore.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import wei.imageexplore.DataBase.Base;
import wei.imageexplore.R;
import wei.imageexplore.Adapter.ViewPagerAdapter;
import wei.imageexplore.Table.ImageInfo;

public class ViewPagerActivity extends Activity {

    private int position;               //当前图像序号
    private ArrayList<String> piclist;  //图像路径表
    private ViewPager pager;            //显示图像页面
    private ViewPagerAdapter pagerAdapter;//显示图像适配器
    private Handler handler=new Handler();

    private Dialog infoDialog;
    private TextView text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpager);
        //隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        pager= (ViewPager) findViewById(R.id.imgpaper);
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();   //获取上个Activity传过来图像表
        piclist= (ArrayList<String>) bundle.get("list");
        position=(int)bundle.get("position");//获取图像序号
        pagerAdapter=new ViewPagerAdapter(this,piclist,this);
        pager.setAdapter(pagerAdapter);
        pager.setCurrentItem(position);
        initView();
    }

    private void initView(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        View view=View.inflate(this, R.layout.info,null);
        builder.setView(view);
        infoDialog=builder.create();
        infoDialog.setCancelable(false);
        infoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        final Button ok= (Button) view.findViewById(R.id.ok);
        final Button cancel= (Button) view.findViewById(R.id.cancel);
        text= (TextView) view.findViewById(R.id.infoText);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoDialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoDialog.dismiss();
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_MENU){
            showInfo(piclist.get(pager.getCurrentItem()));
            Log.i("Tag","show");
        }

        return super.onKeyDown(keyCode, event);
    }


    private void showInfo(final String path){

        infoDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                ImageInfo info= Base.getImageInfo(ViewPagerActivity.this,path);
                if (info!=null){
                    final StringBuilder sb=new StringBuilder();
                    sb.append("文件名：").append(path).append('\n');
                    sb.append("文件大小：").append(info.getSize()).append('\n');
                    sb.append("分辨率：").append(info.getPixel()).append('\n');
                    sb.append("时间：").append(info.getDate()).append('\n');
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            text.setText(sb);
                        }
                    });
                }

            }
        }).start();




    }

    @Override
    protected void onDestroy() {//释放资源
        pager.removeAllViews();
        piclist.clear();
        pagerAdapter=null;
        System.gc();
        super.onDestroy();
    }
}
