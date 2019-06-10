package com.example.stopsmoking.data;

import java.util.ArrayList;

public class MeasureLogData {
    String userId;
    int COValue;
    ArrayList temp = new ArrayList<Integer>();


    public String getUserId() {
        return userId;
    }

    public int getCOValue() {
        return COValue;
    }

    public void setTemp(ArrayList temp) {
        this.temp = temp;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setCOValue(int COValue) {
        this.COValue = COValue;
    }
}
