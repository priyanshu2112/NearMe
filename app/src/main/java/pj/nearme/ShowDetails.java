package pj.nearme;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class ShowDetails extends AppCompatActivity {
RecyclerView rv1,rv2;Intent intent;
    String[] name_500,sex_500,dist_500,lat_500,lng_500;
    String[] name_a,sex_a,dist_a,lat_a,lng_a;
    ArrayList data500;
    ArrayList data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_details);
        rv1 = (RecyclerView) findViewById(R.id.recycle1);
        rv2 = (RecyclerView) findViewById(R.id.recycle2);
        rv1.setLayoutManager(new LinearLayoutManager(this));
        rv2.setLayoutManager(new LinearLayoutManager(this));
        intent = getIntent();

        data500=intent.getStringArrayListExtra("data500");
        name_500 = (String[]) data500.get(0);
        dist_500 = (String[]) data500.get(1);
        lat_500 = (String[]) data500.get(2);
        lng_500 = (String[]) data500.get(3);
        sex_500 = (String[]) data500.get(4);


        data=intent.getStringArrayListExtra("data");
        name_a = (String[]) data.get(0);
        dist_a = (String[]) data.get(1);
        lat_a = (String[]) data.get(2);
        lng_a = (String[]) data.get(3);
        sex_a = (String[]) data.get(4);
        RecyclerView.Adapter adapter1 = new MyAdaptor("inside",name_500,dist_500,lat_500,lng_500,sex_500);
        RecyclerView.Adapter adapter2 = new MyAdaptor("outside",name_a,dist_a,lat_a,lng_a,sex_a);

        rv1.setAdapter(adapter1);
        rv2.setAdapter(adapter2);

    }
}
