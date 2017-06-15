package com.analizator.translator.dijkstra;

/**
 * Created by alexandr on 3/21/17.
 */
public class PolisException extends Exception{
    public PolisException(){
        super("Brackets do not closed");
    }
}
