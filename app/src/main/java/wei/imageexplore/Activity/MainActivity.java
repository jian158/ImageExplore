package wei.imageexplore.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import wei.imageexplore.Adapter.CaseAdapter;
import wei.imageexplore.R;
import wei.imageexplore.Table.CaseTable;

public class MainActivity extends AppCompatActivity {

    private ListView caseListview;
    private CaseAdapter caseAdapter;
    private ArrayList<CaseTable> caseList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initCase();

    }

    private void initView(){
        caseListview= (ListView) findViewById(R.id.caseview);
    }

    private void initCase(){
        caseList=new ArrayList<>();
        caseList.add(new CaseTable("收藏",R.drawable.collection));
        caseList.add(new CaseTable("相机",R.drawable.camera));
        caseList.add(new CaseTable("截图",R.drawable.screenshot));
        caseList.add(new CaseTable("所有图片",R.drawable.pic));
        caseAdapter=new CaseAdapter(this,caseList);
        caseListview.setAdapter(caseAdapter);
        caseListview.setOnItemClickListener(itemClickListener);
    }


    private AdapterView.OnItemClickListener itemClickListener=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent=new Intent(MainActivity.this, BrowserActivity.class);
            switch (position){
                case 0:
                    intent.putExtra("case",0);
                    break;
                case 1:
                    intent.putExtra("case",1);
                    break;
                case 2:
                    intent.putExtra("case",2);
                    break;
                case 3:
                    intent.putExtra("case",3);
                    break;
            }
            startActivity(intent);
        }
    };


}
