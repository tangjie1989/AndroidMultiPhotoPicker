package com.tj.library.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.tj.library.utils.GenerateImageThumbnailsFileNameUtil;

public class ImageThumbnails implements Parcelable{

	private String sdcardCacheSavePath;
	private String sourceImgPath;
	private String bigImgFileName;
	private String smallImgFileName;
	private boolean isSelected;
	
	public ImageThumbnails(String sdcardCacheSavePath, String sourceImgPath){
		
		this.sdcardCacheSavePath = sdcardCacheSavePath;
		this.sourceImgPath = sourceImgPath;
		
		setBigImgFileName(GenerateImageThumbnailsFileNameUtil.generateBigThumbnailsFileName(sourceImgPath));
		setSmallImgFileName(GenerateImageThumbnailsFileNameUtil.generateSmallThumbnailsFileName(sourceImgPath));
	}

	public String getSdcardCacheSavePath() {
		return sdcardCacheSavePath;
	}

	public void setSdcardCacheSavePath(String sdcardCacheSavePath) {
		this.sdcardCacheSavePath = sdcardCacheSavePath;
	}

	public String getSourceImgPath() {
		return sourceImgPath;
	}

	public void setSourceImgPath(String sourceImgPath) {
		this.sourceImgPath = sourceImgPath;
	}

	public String getBigImgFileName() {
		return bigImgFileName;
	}

	public void setBigImgFileName(String bigImgFileName) {
		this.bigImgFileName = bigImgFileName;
	}

	public String getSmallImgFileName() {
		return smallImgFileName;
	}

	public void setSmallImgFileName(String smallImgFileName) {
		this.smallImgFileName = smallImgFileName;
	}
	
	public String getBigImgPath(){
		return getSdcardCacheSavePath() + getBigImgFileName();
	}
	
	public String getSmallImgPath(){
		return getSdcardCacheSavePath() + getSmallImgFileName();
		
	}
	
	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

		dest.writeString(sdcardCacheSavePath);
		dest.writeString(sourceImgPath);
		dest.writeString(bigImgFileName);
		dest.writeString(smallImgFileName);
		dest.writeByte((byte) (isSelected ? 1 : 0)); 
	}

	public ImageThumbnails() {
		super();
	}

	public ImageThumbnails(Parcel in) {
		sdcardCacheSavePath = in.readString();
		sourceImgPath = in.readString();
		bigImgFileName = in.readString();
		smallImgFileName = in.readString();
		isSelected = in.readByte() != 0; 
	}

	public static Creator<ImageThumbnails> getCreator() {
		return CREATOR;
	}

	public static void setCreator(Creator<ImageThumbnails> creator) {
		CREATOR = creator;
	}

	public static Creator<ImageThumbnails> CREATOR = new Creator<ImageThumbnails>() {
		public ImageThumbnails createFromParcel(Parcel in) {
			return new ImageThumbnails(in);
		}

		public ImageThumbnails[] newArray(int size) {
			return new ImageThumbnails[size];
		}
	};
	
	@Override
    public boolean equals(Object o) {
        if (!(o instanceof ImageThumbnails)) {
            return false;
        }
        return sourceImgPath.equals(((ImageThumbnails)o).sourceImgPath);
    }

}
