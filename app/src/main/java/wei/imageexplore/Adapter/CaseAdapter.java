package wei.imageexplore.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import wei.imageexplore.R;
import wei.imageexplore.Table.CaseTable;
import wei.imageexplore.View.CircleImageView;

/**
 * Created by wei on 2017/11/10.
 */

public class CaseAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<CaseTable> titles;
    public CaseAdapter(Context context,ArrayList<CaseTable> titles){
        this.context=context;
        this.titles=titles;
    }
    @Override
    public int getCount() {
        return titles.size();
    }

    @Override
    public Object getItem(int position) {
        return titles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private Holder holder;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null){//复用组件
            holder=new Holder();
            convertView=View.inflate(context, R.layout.caseitem,null);
            holder.icon= (CircleImageView) convertView.findViewById(R.id.caseicon);
            holder.title= (TextView) convertView.findViewById(R.id.casetitle);
            convertView.setTag(holder);
        }
        else {
            holder= (Holder) convertView.getTag();
        }
        holder.title.setText(titles.get(position).title);
        holder.icon.setImageResource(titles.get(position).icon);
        return convertView;
    }


    private class Holder{
        public CircleImageView icon;
        public TextView title;
    }

}
