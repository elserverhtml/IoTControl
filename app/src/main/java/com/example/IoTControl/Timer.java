package com.example.IoTControl;

class Timer {
    final static int MODE_EVERYDAY = 127;
    final static int MODE_WEEKDAY = 62;
    final static int MODE_WEEKEND = 65;
    final static int MODE_SINGLY = 0;

    private int id;
    private int days;
    private int h;
    private int m;
    private boolean isOn;
    private boolean isEnable;

    Timer(int id, int days, int h, int m, boolean isOn, boolean isEnable) {
        this.id = id;
        this.days = days;
        this.h = h;
        this.m = m;
        this.isOn = isOn;
        this.isEnable = isEnable;
    }

    static int getConstStringDays(int days) {
        switch (days) {
            case Timer.MODE_EVERYDAY:
                return R.string.timer_everyday;
            case Timer.MODE_WEEKDAY:
                return R.string.timer_weekday;
            case Timer.MODE_WEEKEND:
                return R.string.timer_weekend;
            case Timer.MODE_SINGLY:
                return R.string.timer_singly;
            default:
                return -1;
        }
    }

    static String getStringDays(int days) {
        String str = "";
        if( (days >> 5 & 1) == 1) str += "ПН ";
        if( (days >> 4 & 1) == 1) str += "ВТ ";
        if( (days >> 3 & 1) == 1) str += "СР ";
        if( (days >> 2 & 1) == 1) str += "ЧТ ";
        if( (days >> 1 & 1) == 1) str += "ПТ ";
        if( (days & 1) == 1) str += "СБ ";
        if( (days >> 6 & 1) == 1) str += "ВС";
        return str;
    }

    int getId() {
        return id;
    }
    void setId(int id) {
        this.id = id;
    }

    int getDays() {
        return days;
    }
    void setDays(int days) {
        this.days = days;
    }

    int getH() {
        return h;
    }
    void setH(int h) {
        this.h = h;
    }

    int getM() {
        return m;
    }
    void setM(int m) {
        this.m = m;
    }

    boolean isOn() {
        return isOn;
    }
    void setOn(boolean on) {
        isOn = on;
    }

    boolean isEnable() {
        return isEnable;
    }
    void setEnable(boolean enable) {
        isEnable = enable;
    }
}
