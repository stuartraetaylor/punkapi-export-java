package com.github.stuartraetaylor.punkapiexport;

public class PunkException extends Exception {

    public PunkException(String message) {
        super(message);
    }

    public PunkException(String message, Throwable cause) {
        super(message, cause);
    }

    public PunkException(Throwable cause) {
        super(cause);
    }

}
