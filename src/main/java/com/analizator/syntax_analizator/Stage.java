package com.analizator.syntax_analizator;

import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;

/**
 * Created by alexandr on 5/20/17.
 */

public class Stage {
    private int currentStage;
    @Value("#{jumpArray}") private ArrayList<Jump> jumps;

    public Stage(int currentStage, ArrayList<Jump> jumps){
        this.currentStage = currentStage;
        this.jumps = jumps;

    }

    public int getCurrentStage() {
        return currentStage;
    }

    public ArrayList<Jump> getJumps() {
        return jumps;
    }

    @Override
    public String toString(){
        String str = currentStage + " ";
        for(Jump jump : jumps)
            str += jump;
        return str;
    }


}
