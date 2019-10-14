package com.tgc.researchchat;

class Message {
    private String message;
    private int type;
    Message(String message, int type){
        this.message=message;
        this.type=type;
    }
    boolean isSent(){
        return type == 0;
    }

    String getMessage(){
        return message;
    }
}