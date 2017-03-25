package com.example.yousheng.photogallery;

/**
 * Created by yousheng on 17/3/25.
 */
//模型层
public class GalleryItem {
    private String mdate;
    private String mId;
    private String mUrl;

    @Override
    public String toString() {
        return mdate;
    }

    public String getMdate() {
        return mdate;
    }

    public void setMdate(String mdate) {
        this.mdate = mdate;
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getmUrl() {
        return mUrl;
    }

    public void setmUrl(String mUrl) {
        this.mUrl = mUrl;
    }
}
