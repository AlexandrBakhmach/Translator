package com.analizator.lexycal_analizator.entity;

/**
 * Created by alexandr on 5/10/17.
 */
public class ProgramLine extends ProgramUnit{
    private static int count = 0;
    private StringBuilder line;

    public ProgramLine(String line){
        super.id = ++count;
        super.name = "line";
        this.line = new StringBuilder(line);
    }

    public StringBuilder getLine(){
        return this.line;
    }

    @Override
    public String toString(){
        return "Number:\t" + super.id + "  Line:\t" + this.line;
    }
}
