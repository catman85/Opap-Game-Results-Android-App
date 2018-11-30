package com.example.jim.opap;

public class ExampleItem {
    private String mNum;
    private String mResS;

    public ExampleItem(int mNum, int mRes) {
        this.mNum = String.valueOf(mNum);
        this.mResS = String.valueOf(mRes);
    }

    public ExampleItem(int mNum, String mResS) {
        this.mNum = String.valueOf(mNum);
        this.mResS = mResS;
    }

    public ExampleItem(String empty, String mResS) {
        this.mNum = empty;
        this.mResS = mResS;
    }

    public String getmNum() {
        return mNum;
    }

    public String getmRes() {
        return mResS;
    }
}
