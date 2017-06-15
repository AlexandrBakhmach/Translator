package com.analizator.translator.dijkstra;


import com.analizator.lexycal_analizator.entity.Type;

/**
 * Created by alexandr on 3/21/17.
 */

public class Number extends Element<String> implements ExecutionUnit {

    private Type type;


    public Number(String number, Type type){
        this.type = type;
        super.setSign(number);
    }

    @Override
    public Type getType(){
        return this.type;
    }

    @Override
    public String getValue() {
        return super.sign;
    }

    @Override
    public void setValue(String value) {
        super.setSign(value);
    }


    @Override
    public String toString() {
        return String.valueOf(super.getSign());
    }
}
