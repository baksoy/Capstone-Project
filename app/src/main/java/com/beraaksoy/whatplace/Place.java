package com.beraaksoy.whatplace;

import java.io.Serializable;
import java.util.Random;

/**
 * Created by beraaksoy on 5/16/16.
 */
public class Place implements Serializable {

    private int _id;
    private String name;
    private String address;
    private String phone;
    private String website;
    private byte[] photoId;
    private String memo;
    private int bgColorIndex;
    public static final int COLOR_COUNT = 40;

    public Place() {
        this.bgColorIndex = new Random().nextInt(COLOR_COUNT);
    }

    public Place(int _id, String name, String address, String phone, String website, byte[] photoId, String memo, int bgColorIndex) {
        this._id = _id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.website = website;
        this.photoId = photoId;
        this.memo = memo;
    }

    public int getBgColorIndex() {
        return bgColorIndex;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int get_id() {
        return _id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getWebsite() {
        return website;
    }

    public void setPhotoId(byte[] photoId) {
        this.photoId = photoId;
    }

    public byte[] getPhotoId() {
        return photoId;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getMemo() {
        return memo;
    }

    public String getPhotoFileName() {
        return "IMG_" + getPhotoId() + ".jpg";
    }

    // public ContentValues values() {
    //     ContentValues values = new ContentValues();
    //     values.put(WhatPlaceContract.PlaceColumns.PLACE_NAME, getName());
    //     values.put(WhatPlaceContract.PlaceColumns.PLACE_ADDRESS, getAddress());
    //     values.put(WhatPlaceContract.PlaceColumns.PLACE_PHONE, getPhone());
    //     values.put(WhatPlaceContract.PlaceColumns.PLACE_WEBSITE, getWebsite());
    //     values.put(WhatPlaceContract.PlaceColumns.PLACE_MEMO, getMemo());
    //     values.put(WhatPlaceContract.PlaceColumns.PLACE_PHOTO, getPhotoId());
    //     return values;
    // }
}
