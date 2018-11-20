package com.example.pbose.exceptions;

/**
 * Created by pbose on 3/19/16.
 */
public class ParsingException extends Exception {
    public ParsingException(String msg){
        super(msg);
    }

    public ParsingException(String msg,Throwable e){
        super(msg,e);
    }
}
