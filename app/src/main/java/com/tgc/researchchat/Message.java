package com.tgc.researchchat;

class Message {
    String message;
    int type;
    Message(String message, int type){
        this.message=message;
        this.type=type;
    }
    boolean isSent(){
        if(type==0){
            return true;
        }
        return false;
    }
    String getMessage(){
        return message;
    }
}