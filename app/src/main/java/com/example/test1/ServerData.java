package com.example.test1;

class ServerData {
    private String State;
    private String Created;
    private String Name;
    private String Zonename;
    private String Osname;
    private String id;
    private String addr; // 내부주소
    private String cpu; // CPU/메모리
    private String disk; // 디스크 용량
    private int resId;

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }
    public String getCpu() {
        return cpu;
    }

    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    public String getDisk() {
        return disk;
    }

    public void setDisk(String disk) {
        this.disk = disk;
    }


    public ServerData() {

    }

    public ServerData(String State, String Created, String Name, String Templatename, String Osname) {
        this.State = State;
        this.Created = Created;
        this.Name = Name;
        this.Osname = Osname;
        this.Zonename = Zonename;
    }

    public String getState() {
        return State;
    }

    public String getId() {
        return id;
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

    public String getOsname() {
        return Osname;
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

    public void setId(String id) {
        this.id = id;
    }

    public void setZonename(String zonename) {
        this.Zonename = zonename;
    }

    public void setOsname(String osname) {
        this.Osname = osname;
    }
}