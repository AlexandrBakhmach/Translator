package com.analizator.lexycal_analizator.entity;

/**
 * Created by alexandr on 5/10/17.
 */
public class Identifier extends ProgramUnit{
    private static int count = 0;
    private Type type;
    private String value;

    public Identifier(String name, Type type){
        this.type = type;
        this.name = name;
        this.id = count++;
    }

    public void setValue(String value){
        this.value = value;
    }

    public String getValue(){
        return this.value;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String toString(){
        return "NUMBER:\t" + id + "  NAME:\t" + name + "  TYPE:\t" + type;
    }


}
