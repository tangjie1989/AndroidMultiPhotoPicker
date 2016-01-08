package com.tj.library.model;

import java.util.ArrayList;

public class ImageGroup {
    /**
     * 文件夹名
     */
    private String dirName = "";

    /**
     * 文件夹下所有图片
     */
    private ArrayList<ImageThumbnails> images = new ArrayList<ImageThumbnails>();

    public String getDirName() {
        return dirName;
    }

    public void setDirName(String dirName) {
        this.dirName = dirName;
    }

    /**
     * 获取第一张图片的路径(作为封面)
     * 
     * @return
     */
    public String getFirstImgPath() {
        if (images.size() > 0) {
            return images.get(0).getSourceImgPath();
        }
        return "";
    }

    /**
     * 获取图片数量
     * 
     * @return
     */
    public int getImageCount() {
        return images.size();
    }

    public ArrayList<ImageThumbnails> getImages() {
        return images;
    }
    
    public void setImages(ArrayList<ImageThumbnails> images){
    	this.images = images;
    }

    /**
     * 添加一张图片
     * 
     * @param image
     */
    public void addImage(ImageThumbnails image) {
        if (images == null) {
            images = new ArrayList<ImageThumbnails>();
        }
        images.add(image);
    }

    @Override
    public String toString() {
        return "ImageGroup [firstImgPath=" + getFirstImgPath() + ", dirName=" + dirName
                + ", imageCount=" + getImageCount() + "]";
    }

    /**
     * <p>
     * 重写该方法
     * <p>
     * 使只要图片所在的文件夹名称(dirName)相同就属于同一个图片组
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ImageGroup)) {
            return false;
        }
        return dirName.equals(((ImageGroup)o).dirName);
    }
}
