package project1920-t1g07.message;

import java.io.*;

public class InvalidMessage extends Exception {
    private String message;

    public InvalidMessage(String message) {
        this.setMessage(message);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
   

}