package wei.imageexplore.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import wei.imageexplore.Adapter.ImageAdapter;
import wei.imageexplore.DataBase.Base;
import wei.imageexplore.DataBase.CollectionBase;
import wei.imageexplore.R;
import wei.imageexplore.Table.ImageInfo;

public class BrowserActivity extends AppCompatActivity {
    private ImageAdapter imageAdapter;  //图像列表适配器
    private GridView gridView;          //图像组件
    private ArrayList<String> list;     //图像路径表
    private int _case=2;
    private boolean isFirst=true;
    /*
    * 程序初始化
    * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
        initData();
        initView();
    }

    /*
    * 初始化数据
    * */
    private void initData(){
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        _case= (int) bundle.get("case");
        list=new ArrayList<>();
    }


    /*
    * 初始化界面*/
    private void initView(){
        gridView= (GridView) findViewById(R.id.imageExplore);//实例化控件
        LayoutAnimationController controller=new LayoutAnimationController(AnimationUtils.loadAnimation(this, R.anim.wave));
        controller.setOrder(LayoutAnimationController.ORDER_NORMAL);
        gridView.setLayoutAnimation(controller);
        gridView.setOnItemClickListener(imageExploreClick);//设置点击事件
        gridView.setOnCreateContextMenuListener(contextMenuListener);
        gridView.setOnItemLongClickListener(longClickListener);
        imageAdapter=new ImageAdapter(this,list,gridView);  //实例化适配器
        gridView.setAdapter(imageAdapter);
        new Thread(getImgListTask).start();//扫描图像
    }


    /*
    * 长按图像监听
    * */
    private AdapterView.OnItemLongClickListener longClickListener=new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            if(isFirst){
                isFirst=false;
                gridView.showContextMenu();
            }
            return false;
        }
    };


    /*
    * 点击进入全屏浏览
    *
    * */
    private AdapterView.OnItemClickListener imageExploreClick=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent=new Intent();
            intent.setClass(BrowserActivity.this,ViewPagerActivity.class);
            intent.putStringArrayListExtra("list",list);
            intent.putExtra("position",position);
            startActivity(intent);
        }
    };


    /*
    *系统菜单
    * */
    private View.OnCreateContextMenuListener contextMenuListener=new View.OnCreateContextMenuListener() {
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            v.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                    menu.setHeaderTitle("选项");
                    menu.add(0, 0, 0, "收藏");
                    if(_case==0){
                        menu.add(0,1,0,"移除");
                    }
                    menu.add(0, 2, 0, "删除");
                    menu.add(0,3,0,"重命名");
                    menu.add(0,4,0,"详细信息");
                }
            });
        }
    };


    /*
    * 菜单选项实现
    * */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info= (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int itemid=(int)info.id;
        final CollectionBase base=new CollectionBase(BrowserActivity.this,"collection", null,1);
        switch (item.getItemId())
        {
            case 0:
                Log.i("itemid",list.get(itemid));
                base.insert(list.get(itemid));
                Toast.makeText(this,"添加收藏成功",Toast.LENGTH_SHORT).show();
                base.close();
                return true;
            case 1:
                String remove=list.get(itemid);
                list.remove(itemid);
                imageAdapter.notifyDataSetChanged();
                base.delete(remove);
                Toast.makeText(this,"移除成功",Toast.LENGTH_SHORT).show();
                base.close();
                return true;
            case 2:
                String remove2=list.remove(itemid);
                imageAdapter.notifyDataSetChanged();
                Toast.makeText(this,"删除成功",Toast.LENGTH_SHORT).show();
                final String remove3=remove2;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        base.delete(remove3);
                        base.close();
                        File file=new File(remove3);
                        file.delete();
                        Base.fileUpdate(BrowserActivity.this,remove3);
                    }
                }).start();

                return true;
            case 3:
                reName(itemid,base);
                return true;
            case 4:
                showInfo(list.get(itemid));
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private void reName(final int index,final CollectionBase base){
        final String path=list.get(index);
        final File file=new File(path);
        try {
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
//            builder.setCustomTitle(View.inflate(context, R.layout.renametitle,null));
            View view=View.inflate(this, R.layout.rename,null);
            builder.setView(view);
            final Dialog dialog=builder.create();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.show();
            final Button ok= (Button) view.findViewById(R.id.ok);
            final Button cancel= (Button) view.findViewById(R.id.cancel);
            final EditText text= (EditText) view.findViewById(R.id.renameText);
            text.setText(file.getName());
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final File newFile=new File(file.getParent()+File.separator+text.getText().toString());
                    file.renameTo(newFile);
                    dialog.dismiss();
                    list.set(index,newFile.getPath());
                    imageAdapter.notifyDataSetChanged();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            base.delete(path);
                            base.insert(newFile.getPath());
                            base.close();
                            Base.fileUpdate(BrowserActivity.this,path);
                            Base.fileUpdate(BrowserActivity.this,newFile.getPath());
                        }
                    }).start();

                    Toast.makeText(BrowserActivity.this,"重命名成功",Toast.LENGTH_SHORT).show();
                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }catch (Exception e){
            Toast.makeText(this,"重命名失败!",Toast.LENGTH_SHORT).show();
        }
    }



    private void showInfo(final String path){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        View view=View.inflate(this, R.layout.info,null);
        builder.setView(view);
        final Dialog dialog=builder.create();
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        final Button ok= (Button) view.findViewById(R.id.ok);
        final Button cancel= (Button) view.findViewById(R.id.cancel);
        final TextView text= (TextView) view.findViewById(R.id.infoText);
        dialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                ImageInfo info=Base.getImageInfo(BrowserActivity.this,path);
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

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


    }





    /*
    * 扫描图像任务
    * */
    private Handler handler=new Handler();
    private Runnable getImgListTask=new Runnable() {
        @Override
        public void run() {
            String path;
            switch (_case){
                case 0:
                    CollectionBase base=new CollectionBase(BrowserActivity.this,"collection", null,1);
                    base.getCollections(list);
                    base.close();
                    break;
                case 1:
                    path= Environment.getExternalStorageDirectory()+ File.separator+"DCIM";
                    Base.getImgPathList(BrowserActivity.this,list,path);
                    break;
                case 3:
                    Base.getImgPathList(BrowserActivity.this,list);
                    break;
                case 2:
                    path= Environment.getExternalStorageDirectory()+ File.separator+"Pictures";
                    Base.getImgPathList(BrowserActivity.this,list,path);
                    break;
            }

            handler.post(new Runnable() {
                @Override
                public void run() {
                    imageAdapter.notifyDataSetChanged();
                }
            });
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
