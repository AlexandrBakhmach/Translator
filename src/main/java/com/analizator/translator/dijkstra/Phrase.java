package com.analizator.translator.dijkstra;

/**
 * Created by alexandr on 4/15/17.
 */
public class Phrase extends Element<String> {

    public Phrase(String name){
        super.sign = name;
    }

    @Override
    public String toString() {
        return super.getSign();
    }
}
