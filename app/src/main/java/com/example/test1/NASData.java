package com.example.test1;

public class NASData {

    private String name;
    private String zoneName;
    private String tarSize;
    private String curSize;
    private String protocol;
    private String id;
    private String file; // 현재 파일 수
    private String date; // 생성일자
    private int resId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public String getTarSize() {
        return tarSize;
    }

    public void setTarSize(String tarSize) {
        this.tarSize = tarSize;
    }

    public String getCurSize() {
        return curSize;
    }

    public void setCurSize(String curSize) {
        this.curSize = curSize;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
