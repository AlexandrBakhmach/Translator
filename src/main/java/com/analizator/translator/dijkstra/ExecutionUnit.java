package com.analizator.translator.dijkstra;

import com.analizator.lexycal_analizator.entity.Type;

/**
 * Created by alexandr on 5/23/17.
 */
public interface ExecutionUnit {
    public Type getType();
    public String getValue();
    public void setValue(String value);
}
