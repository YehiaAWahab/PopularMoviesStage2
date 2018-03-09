package io.github.yahia_hassan.popularmoviesstage2;



public class Video {
    private String mName;
    private String mKey;


    public Video(String name, String key) {
        mName = name;
        mKey = key;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getKey() {
        return mKey;
    }

    public void setKey(String key) {
        mKey = key;
    }

}
