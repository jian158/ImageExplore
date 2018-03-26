package wei.imageexplore.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import wei.imageexplore.Util.ImageLoader;
import wei.imageexplore.R;

/**
 * Created by wei on 2017/7/21.
 */

public class ImageAdapter extends BaseAdapter implements AbsListView.OnScrollListener{
    private Context context;
    private ArrayList<String> imgList;
    private GridView gridView;
    private ImageLoader imageLoader;
    private boolean isScrolling=false;
    private int firstItem,lastItem;
    public ImageAdapter(Context context,ArrayList<String> imgList,GridView gridView){
        this.gridView=gridView;
        this.context=context;
        this.imgList=imgList;
        imageLoader=new ImageLoader(context,gridView);
        gridView.setOnScrollListener(this);//设置滑动事件监听
    }


    @Override
    public int getCount() {
        return imgList.size();
    }

    @Override
    public Object getItem(int position) {
        return imgList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private Holder holder;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String path=imgList.get(position);
        if (convertView==null){//复用组件
            holder=new Holder();
            convertView=View.inflate(context, R.layout.imageitem,null);
//            convertView.setAnimation(AnimationUtils.loadAnimation(context, R.anim.slide));
            holder.imageView= (ImageView) convertView.findViewById(R.id.imgid);
            holder.textView= (TextView) convertView.findViewById(R.id.imgname);
            imageLoader.Load(holder.imageView,path);
            convertView.setTag(holder);//设置标识以便复用
        }
        else {
            holder= (Holder) convertView.getTag();
        }

//        holder.imageView.setTag(path);
        int index=path.lastIndexOf("/");//获取文件名
        holder.textView.setText(path.substring(index==-1?0:index+1));
//        if (isScrolling)
//            holder.imageView.setImageBitmap(null);
        imageLoader.Load(holder.imageView,path);
        return convertView;
    }

    private void LoadImage(int begin,int end){//加载序号begin~end的图像
        String path;
        for (int i = begin; i < end&&i<imgList.size(); i++) {
            path=imgList.get(i);
            ImageView iv= (ImageView) gridView.findViewWithTag(path);
            if (iv!=null){
                Log.i("ImageAdater",path);
                imageLoader.Load(iv,path);
            }
        }
    }



    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState==SCROLL_STATE_IDLE){
//            LoadImage(firstItem,lastItem);
            isScrolling=false;
        }
        else isScrolling=true;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        firstItem=firstVisibleItem;
        lastItem=firstVisibleItem+visibleItemCount;
    }

    private class Holder{
        public ImageView imageView;
        public TextView  textView;
    }


}
