package com.example.jd.notetake;

public class DataNote {
    String NoteName;
    String latitude;
    String longitude;
    byte[] img;
    int id;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public DataNote()
    {

    }

    public DataNote(String NoteName, String lat, String lon, byte[] img)
    {
        this.NoteName = NoteName;
        this.latitude =lat;
        this.longitude =lon;
        this.img=img;
    }

    public byte[] getImg()
    {
        return img;
    }

    public void setImg(byte[] img)
    {
        this.img = img;
    }

    public String getLatitude()
    {
        return latitude;
    }

    public void setLatitude(String latitude)
    {
        this.latitude = latitude;
    }

    public String getNoteName()
    {

        return NoteName;
    }
    public void setNoteName(String NoteName)
    {
        this.NoteName = NoteName;
    }

    public String getLongitude()
    {
        return longitude;
    }

    public void setLongitude(String longitude)
    {
        this.longitude = longitude;
    }

   }
