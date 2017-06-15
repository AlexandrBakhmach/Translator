package com.analizator.translator.dijkstra;


import com.analizator.lexycal_analizator.entity.Identifier;
import com.analizator.lexycal_analizator.entity.Type;

/**
 * Created by alexandr on 3/21/17.
 */
public class Variable extends Element<String> implements ExecutionUnit {
    private Type type;
    private Identifier identifier;

    public Variable(String name, Type type, Identifier identifier){
        super.setSign(name);
        this.identifier = identifier;
        this.type = type;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public String getValue() {
        return identifier.getValue();
    }

    @Override
    public void setValue(String value) {
       this.identifier.setValue(value);
    }

    @Override
    public String toString() {
        return super.getSign();
    }
}
