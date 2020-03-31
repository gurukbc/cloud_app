package com.example.test1;

class AutoscalingData {
    private String name;
    private String state;
    private String zoneName;
    private String curVM;
    private String tarVM;
    private String minVm;
    private String maxVm;
    private String down; // 쿨다운
    private String time; // 헬스체크 유예시간
    private int resId;

    public String getDown() {
        return down;
    }

    public void setDown(String down) {
        this.down = down;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public String getCurVM() {
        return curVM;
    }

    public void setCurVM(String curVM) {
        this.curVM = curVM;
    }

    public String getTarVM() {
        return tarVM;
    }

    public void setTarVM(String tarVM) {
        this.tarVM = tarVM;
    }

    public String getMinVm() {
        return minVm;
    }

    public void setMinVm(String minVm) {
        this.minVm = minVm;
    }

    public String getMaxVm() {
        return maxVm;
    }

    public void setMaxVm(String maxVm) {
        this.maxVm = maxVm;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public AutoscalingData() {
    }
}
