package com.example.test1;

class LBData {
    private String server;
    private String lbType;
    private String lbOpt;
    private String ip;
    private String port;
    private String name;
    private String zoneName;
    private int resId;
    private String id;

    public LBData() {

    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getServer() { return server; }

    public String getLBType() { return lbType; }

    public String getName() {
        return name;
    }

    public int getResId() { return resId; }

    public String getZoneName() { return zoneName; }

    public String getLBOpt() {
        return lbOpt;
    }

    public String getIp() {
        return ip;
    }

    public String getPort() {
        return port;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public void setResId(int resi) { this.resId = resi; }

    public void setLBType(String lbType) { this.lbType = lbType; }

    public void setName(String name) {
        this.name = name;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setLBOpt(String lbOpt) { this.lbOpt = lbOpt; }

    public void setPort(String port) {
        this.port = port;
    }
}
