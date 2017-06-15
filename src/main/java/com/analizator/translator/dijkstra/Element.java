package com.analizator.translator.dijkstra;

import com.analizator.lexycal_analizator.entity.ProgramUnit;
import org.springframework.stereotype.Component;

/**
 * Created by alexandr on 3/19/17.
 */
@Component("element")
public abstract class Element<E> extends ProgramUnit{
    protected E sign;

    public E getSign() {
        return sign;
    }
    public void setSign(E sign) {
        this.sign = sign;
    }

    @Override
    public abstract String toString();

}
