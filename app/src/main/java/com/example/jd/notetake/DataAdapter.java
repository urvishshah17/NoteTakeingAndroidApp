package com.example.jd.notetake;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.MyHolder>
{

    List<DataNote> Datalist;
    Context context;

    public DataAdapter(List<DataNote> list, Context context)
    {
        this.Datalist = list;
        this.context = context;

    }

    @Override
    public void onBindViewHolder(MyHolder holder, final int position)
    {
        holder.tv.setText(Datalist.get(position).getNoteName());
        holder.rl.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(context, ViewDataNote.class);
                intent.putExtra("Data", new Gson().toJson(Datalist.get(position)));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return Datalist.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder
    {
        RelativeLayout rl;
        TextView tv;

        public MyHolder(View itemView)
        {
            super(itemView);
            rl = (RelativeLayout) itemView.findViewById(R.id.listlayout);
            tv = (TextView) itemView.findViewById(R.id.viewTextData);
        }
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_note, parent, false);
        return new MyHolder(itemView);
    }



}
