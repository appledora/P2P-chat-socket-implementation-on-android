package com.tgc.researchchat;

public class MyFiles {
    private String Path;
    private int type;

    MyFiles(String Path, int type) {
        this.Path = Path;
        this.type = type;
    }

    boolean isSent() {
        return type == 0;
    }

    String getFilePath() {
        return Path;
    }
}
