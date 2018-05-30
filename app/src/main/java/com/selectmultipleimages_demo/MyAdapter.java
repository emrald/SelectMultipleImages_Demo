package com.selectmultipleimages_demo;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by TI A1 on 29-05-2018.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolde>{
    Context context;
    DataClass data;
    static ArrayList<String> arrayList = new ArrayList<String>();
    private ArrayList<DataClass> imageData = new ArrayList<DataClass>();

    public MyAdapter(ArrayList<DataClass> imageData, Activity activity) {
        this.imageData = imageData;
        this.context = activity;
    }

    @Override
    public MyAdapter.MyViewHolde onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row, parent, false);

        return new MyViewHolde(itemView);
    }

    @Override
    public void onBindViewHolder(final MyAdapter.MyViewHolde holder, final int position) {

        data = imageData.get(position);
        holder.bind(position);
        //   Log.e("data",data+"");
        // pos = position;
        if (data != null) {
            Glide.with(context).load(data.getUri()).into(holder.singleImageView);

           holder.singleImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int adapterPosition = holder.getAdapterPosition();

                    if (data.isChecked()) {
                        holder.checkbox.setChecked(false);
                        data.setChecked(false);
                        arrayList.remove(data.getUri()+"");
                    }
                    else {
                        holder.checkbox.setChecked(true);
                        data.setChecked(true);
                        Log.e("data",data.getUri()+"");
                        arrayList.add(data.getUri()+"");
                    }
                }
            });
            holder.checkbox.setClickable(false);
           /* holder.checkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int adapterPosition = holder.getAdapterPosition();

                    if (data.isChecked()) {
                        holder.checkbox.setChecked(false);
                        data.setChecked(false);
                    }
                    else {
                        holder.checkbox.setChecked(true);
                        data.setChecked(true);
                        Log.e("data",data.getUri()+"");
                        arrayList.add(data.getUri()+"");
                    }
                }
            });*/
        } else {
            Toast.makeText(context, "Images Empty", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return imageData.size();
    }

  /*  @Override
    public void onClick(View v) {

        if (data.isChecked()) {
            holder.checkbox.setChecked(false);
            data.setChecked(false);
            arrayList.remove(data.getUri()+"");
        }
        else {
            holder.checkbox.setChecked(true);
            data.setChecked(true);
            Log.e("data",data.getUri()+"");
            arrayList.add(data.getUri()+"");
        }
    }*/

    public class MyViewHolde extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView singleImageView;
        CheckBox checkbox;

        public MyViewHolde(View itemView) {
            super(itemView);
            singleImageView = (ImageView) itemView.findViewById(R.id.img);
            checkbox = (CheckBox)itemView.findViewById(R.id.checkbox);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
        void bind(int position) {
            if (data.isChecked()) {
                checkbox.setChecked(true);
            }
            else {
                checkbox.setChecked(false);
            }
        }
    }
}
