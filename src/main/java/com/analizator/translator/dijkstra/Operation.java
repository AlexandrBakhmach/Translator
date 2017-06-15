package com.analizator.translator.dijkstra;

import org.springframework.stereotype.Component;

/**
 * Created by alexandr on 3/21/17.
 */
@Component("operation")
public class Operation extends Element<String> {
    private int priority;
    public Operation(){}
    public Operation(String sign, int priority){
        super.setSign(sign);
        this.priority = priority;
    }
    public int getPriority() {
        return priority;
    }
    @Override
    public String toString() {
        return super.getSign();
    }
}
