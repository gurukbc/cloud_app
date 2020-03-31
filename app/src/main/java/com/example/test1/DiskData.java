package com.example.test1;

class DiskData {
    private String State;
    private String Created;
    private String Name;
    private String Zonename;
    private String Server;
    private String Size;
    private String type; // 구분명
    private int resId;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public DiskData() {

    }

    public String getState() {
        return State;
    }

    public String getCreated() {
        return Created;
    }

    public String getName() {
        return Name;
    }

    public int getResId() { return resId; }

    public String getZonename() {
        return Zonename;
    }

    public String getServer() {
        return Server;
    }

    public String getSize() {
        return Size;
    }

    public void setState(String state) {
        this.State = state;
    }

    public void setResId(int resi) { this.resId = resi; }

    public void setCreated(String created) {
        this.Created = created;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public void setZonename(String zonename) {
        this.Zonename = zonename;
    }

    public void setServer(String server) {
        this.Server = server;
    }

    public void setSize(String size) {
        this.Size = size;
    }
}
