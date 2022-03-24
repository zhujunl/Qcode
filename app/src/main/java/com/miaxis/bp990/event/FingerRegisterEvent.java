package com.miaxis.bp990.event;

public class FingerRegisterEvent {

    private Integer mark;
    private String feature;

    public FingerRegisterEvent(Integer mark, String feature) {
        this.mark = mark;
        this.feature = feature;
    }

    public Integer getMark() {
        return mark;
    }

    public void setMark(Integer mark) {
        this.mark = mark;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }
}
