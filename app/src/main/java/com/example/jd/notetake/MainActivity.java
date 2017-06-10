package com.example.jd.notetake;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    RecyclerView rView;
    List<DataNote> Datalist;
    DataAdapter Dadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar tb = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(tb);

        Datalist = new ArrayList<>();
        rView = (RecyclerView) findViewById(R.id.Recyc_View);
        rView.setLayoutManager(new LinearLayoutManager(this));
        UpdateUI();
        ImageButton imb = (ImageButton) findViewById(R.id.btnAdd);
        imb.setVisibility(View.VISIBLE);
        imb.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                SqlHelper db = new SqlHelper(MainActivity.this);
                db.AddNote(new DataNote("Add New DataNote", "","", null));
                Datalist = db.getAllNotes();
                Dadapter = new DataAdapter(Datalist, MainActivity.this);
                rView.setAdapter(Dadapter);
            }
        });
    }

    public void UpdateUI()
    {
        try
        {
            SqlHelper db = new SqlHelper(MainActivity.this);
            Datalist = db.getAllNotes();
            Dadapter = new DataAdapter(Datalist, MainActivity.this);
            rView.setAdapter(Dadapter);

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        try
        {
            SqlHelper db = new SqlHelper(MainActivity.this);
            Datalist = db.getAllNotes();
            Dadapter = new DataAdapter(Datalist, MainActivity.this);
            rView.setAdapter(Dadapter);

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
