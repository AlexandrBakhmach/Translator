package com.analizator.lexycal_analizator.entity;

/**
 * Created by alexandr on 5/10/17.
 */
public class Constant extends ProgramUnit {

    private static int count = 0;
    private Type type;

    public Constant(String name, Type type){
        super.id = count++;
        super.name = name;
        this.type = type;
    }
    public Type getType() {
        return type;
    }
    @Override
    public String toString(){
        return "NUMBER:\t" + id + "  NAME:\t" + name + "  TYPE:\t" + type;
    }
}
