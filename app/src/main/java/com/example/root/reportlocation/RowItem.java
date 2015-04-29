package com.example.root.reportlocation;
public class RowItem {
    private int imageId;
    private String title;
    private String desc;
    private int monsterId;

    public RowItem(int imageId, String title, String desc, int monsterId) {
        this.imageId = imageId;
        this.title = title;
        this.desc = desc;
        this.monsterId = monsterId;
    }
    public int getImageId() {
        return imageId;
    }
    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setMonsterId(int monsterId){ this.monsterId = monsterId; }
    public int getMonsterId(){ return monsterId; }
    @Override
    public String toString() {
        return title + "\n" + desc;
    }
}