package com.example.test1;

class ServerData {
    private String State;
    private String Created;
    private String Name;
    private String Zonename;
    private String Osname;
    private int resId;

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

    public void setZonename(String zonename) {
        this.Zonename = zonename;
    }

    public void setOsname(String osname) {
        this.Osname = osname;
    }
}
