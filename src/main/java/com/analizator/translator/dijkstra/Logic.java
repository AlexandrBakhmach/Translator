package com.analizator.translator.dijkstra;


import com.analizator.lexycal_analizator.entity.ProgramUnit;

import java.util.List;

/**
 * Created by alexandr on 4/4/17.
 */
public interface Logic {
    List<ProgramUnit> getPolis();
    void made() throws PolisException;
}
