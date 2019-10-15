package com.tgc.researchchat;

import java.util.Date;

public class MyFiles {
    private String Path;
    private int type;
    private Date sentAt;
    MyFiles(String Path, int type, Date sentAt){
        this.Path=Path;
        this.type=type;
        this.sentAt = sentAt;
    }

    boolean isSent(){
        return type == 0;
    }

    String getFilePath(){
        return Path;
    }
    Date getTime() {
        return sentAt;
    }
}
