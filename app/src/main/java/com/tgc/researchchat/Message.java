package com.tgc.researchchat;

import java.util.Date;

class Message {
    private String message;
    private int type;
    private Date sentAt;

    Message(String message, int type, Date sentAt) {
        this.message = message;
        this.type = type;
        this.sentAt = sentAt;
    }

    boolean isSent() {
        return type == 0;
    }

    Date getTime() {
        return sentAt;
    }

    String getMessage() {
        return message;
    }
}