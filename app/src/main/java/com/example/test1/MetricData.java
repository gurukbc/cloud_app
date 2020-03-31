package com.example.test1;

import android.widget.CheckBox;

public class MetricData extends Data {

    public boolean checked;
    private String opt;
    private CheckBox cbtn;

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }


    public boolean setSelected() {
        if (!this.checked) {
            this.checked = true;
            return true;
        }
        else {
            this.checked = false;
            return false;
        }
    }

    public String getOpt() {
        return opt;
    }

    public void setOpt(String opt) {
        this.opt = opt;
    }

    public CheckBox getCbtn() {
        return cbtn;
    }

    public void setCbtn(CheckBox cbtn) {
        this.cbtn = cbtn;
    }

}
