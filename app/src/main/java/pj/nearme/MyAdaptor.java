package pj.nearme;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Priyanshu on 30-09-2017.
 */

public class MyAdaptor extends RecyclerView.Adapter<MyAdaptor.MyViewHolder> {
    String name[], dist[], lat[], lng[],sex[];
    String type;
    public MyAdaptor(String type,String name[],String dist[],String lat[],String lng[],String sex[]) {
       this.name=name;
       this.dist=dist;
       this.lat=lat;
       this.lng=lng;
       this.sex=sex;
       this.type=type;
    }



    @Override
    public int getItemCount() {
        return name.length;
    }

    @Override
    public MyAdaptor.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_row,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tv_name.setText(name[position]);
        holder.tv_dist.setText("Distance: "+Math.round(Double.valueOf(dist[position]))+" mtrs");
        holder.tv_lat.setText("Lat:"+lat[position]);
        holder.tv_lng.setText("Long:"+lng[position]);

        if(type.equals("inside"))
        {
            if(sex[position].equals("M"))
            {
                holder.iv_sex.setImageResource(R.drawable.ic_male_img);

            }
            else if(sex[position].equals("F"))
            {
                holder.iv_sex.setImageResource(R.drawable.ic_female_img);
            }
        }
        else if(type.equals("outside"))
        {
            if(sex[position].equals("M"))
            {
                holder.iv_sex.setImageResource(R.drawable.ic_male_out_img);
            }
            else if(sex[position].equals("F"))
            {
                holder.iv_sex.setImageResource(R.drawable.ic_female_out_img);
            }
        }




    }




    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name,tv_dist,tv_lat,tv_lng;
        ImageView iv_sex;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_dist = (TextView)itemView.findViewById(R.id.tv_dist);
            tv_lat = (TextView)itemView.findViewById(R.id.tv_lat);
            tv_lng = (TextView)itemView.findViewById(R.id.tv_lng);
            iv_sex = (ImageView)itemView.findViewById(R.id.iv_sex);


        }

    }



}