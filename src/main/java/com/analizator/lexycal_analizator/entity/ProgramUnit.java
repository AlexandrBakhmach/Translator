package com.analizator.lexycal_analizator.entity;

/**
 * Created by alexandr on 5/10/17.
 */
public abstract class ProgramUnit {
    protected int id;
    protected String name;

    public int getId(){
        return id;
    }

    public String getName(){
        return name;
    }
}
