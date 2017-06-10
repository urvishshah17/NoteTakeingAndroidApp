package com.example.jd.notetake;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import com.example.jd.notetake.databinding.ViewNoteBinding;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ViewDataNote extends AppCompatActivity implements LocationListener {
    ViewNoteBinding Databind;
    LocationListener listener;
    ImageButton iAdd;
    DataNote Dmodel;
    Toolbar tb;
    LocationManager location_manager;

    String provider;
    boolean gps_enabled = false;
    boolean netwrk_enabled = false;
    Location location;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Databind = DataBindingUtil.setContentView(ViewDataNote.this, R.layout.view_note);
        Bundle extras = getIntent().getExtras();
        Dmodel = new Gson().fromJson(extras.getString("Data"), DataNote.class);

        Log.d("DataNoteViewClass",new Gson().toJson(Dmodel));
        tb = (Toolbar) findViewById(R.id.tool);

        setSupportActionBar(tb);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        iAdd = (ImageButton) findViewById(R.id.btnAdd);
        iAdd.setVisibility(View.VISIBLE);

        UpdateViewData();
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dmodel.setNoteName(Databind.editTextData.getText().toString() + "");
                SqlHelper db = new SqlHelper(ViewDataNote.this);
                db.updateNote(Dmodel);
                Intent intent = new Intent();
                intent.putExtra("data", new Gson().toJson(Dmodel));
                setResult(100, intent);
                finish();

            }
        });


        iAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }

        });

        LocationSet();

        Databind.btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ViewDataNote.this,ImageMapActivity.class);
                intent.putExtra("latitude", Dmodel.getLatitude());
                intent.putExtra("longitude", Dmodel.getLongitude());
                startActivity(intent);
            }
        });

    }

    private void selectImage() {
        final String[] userChoosenTask = {""};
        final CharSequence[] items = {"Take a New Photo"/*, "Choose from Library"*/,
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewDataNote.this);
        builder.setTitle("Add a New Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("Take a New Photo")) {
                    userChoosenTask[0] = "Take a New Photo";


                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, 1);
                    }
                } else if (items[item].equals("Choose from Existing Library")) {
                    userChoosenTask[0] = "Choose from Existing Library";
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);//
                    startActivityForResult(Intent.createChooser(intent, "Select File"), 2);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    public void UpdateViewData() {
        try {
            Databind.editTextData.setText(Dmodel.getNoteName());
            if (Dmodel.getImg().length!=0) {
                Databind.btnImage.setImageBitmap(BitArray.getImage(Dmodel.getImg()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void LocationSet()
    {
        location_manager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
        listener = this;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (location_manager.getAllProviders().contains(LocationManager.GPS_PROVIDER))
                    location_manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, listener);
                setup();

            }
            requestPermission();
        } else {
            location_manager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
            if (location_manager.getAllProviders().contains(LocationManager.GPS_PROVIDER))
                location_manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, listener);
            setup();

        }
    }


    @Override
    public void onBackPressed() {

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bm = null;
        if (requestCode == 1) {
            bm = (Bitmap) data.getExtras().get("data");
        } else if (requestCode == 2) {
        }
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        Databind.btnImage.setImageBitmap(bm);

        Dmodel.setImg(BitArray.getBytes(bm));
    }



    void setup() {
        gps_enabled = location_manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        netwrk_enabled = location_manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps_enabled)
            provider = LocationManager.GPS_PROVIDER;
        else
            provider = LocationManager.NETWORK_PROVIDER;
        Criteria criteria = new Criteria();
        provider = location_manager.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        location = location_manager.getLastKnownLocation(provider);
    }


    @Override
    public void onLocationChanged(Location location) {

        Log.d("ModelNoteView",new Gson().toJson(Dmodel));
       Dmodel.setLatitude(location.getLatitude()+"");
        Dmodel.setLongitude(location.getLongitude()+"");
        Log.e("Longitude:", "" + location.getLongitude());
        Log.e("Latitude:", "" + location.getLatitude());
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (checkLocationService()) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                                if (location_manager.getAllProviders().contains(LocationManager.GPS_PROVIDER))
                                    location_manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, listener);
                                setup();

                            }
                        }
                    }
                } else {
                    Log.e("permission", "Denied");
                }
                break;
        }
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }


    Boolean checkLocationService() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            // notify user
            android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(getApplicationContext());
            dialog.setMessage("GPS Not Available");
            dialog.setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub

                }
            });
            dialog.show();
        }
        return gps_enabled;
    }
    @Override
    public void onProviderDisabled(String provider) {

    }
    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

            Toast.makeText(this, "GPS permission allows us to access location of the picture. Please allow in Application Settings for more functionality.", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }


}
