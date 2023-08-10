package model;

import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Expose;

public class ElementInfo {
    @SerializedName("key")
    @Expose
    private String key;

    @SerializedName("androidValue")
    @Expose
    private String androidValue;

    @SerializedName("androidType")
    @Expose
    private String androidType;

    @SerializedName("androidIndex")
    @Expose
    private int androidIndex;




    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getAndroidValue() {
        return androidValue;
    }


    public String getAndroidType() {
        return androidType;
    }


    public int getAndroidIndex() {
        return androidIndex;
    }





    @Override
    public String toString() {
        return "ElementInfo{" +
                "key='" + key + '\'' +
                ", androidValue='" + androidValue + '\'' +
                ", androidType='" + androidType + '\'' +
                ", androidIndex=" + androidIndex +
                '}';
    }
}
