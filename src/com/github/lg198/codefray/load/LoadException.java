package com.github.lg198.codefray.load;

public class LoadException extends RuntimeException {

    public LoadException(String s) {
        super(s);
    }

    public LoadException(String s, Throwable t) {
        super(s, t);
    }
}
