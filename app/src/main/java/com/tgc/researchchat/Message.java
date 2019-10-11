package com.tgc.researchchat;

public class Message {
    String message;
    int type;
    public Message(String message,int type){
        this.message=message;
        this.type=type;
    }
    public boolean isSent(){
        if(type==0){
            return true;
        }
        return false;
    }
    public String getMessage(){
        return message;
    }
}