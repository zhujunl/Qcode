package com.miaxis.bp990.event;

public class VerifyEvent {

    private String message;

    public VerifyEvent(String message){
        this.message=message;
    }
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
