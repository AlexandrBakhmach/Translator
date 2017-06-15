package com.analizator.syntax_analizator;

/**
 * Created by alexandr on 5/20/17.
 */
public class Jump {
    private String jumpName;
    private int jumpStage;
    private int jumpStack;

    public Jump(String jumpName, int jumpStage, int jumpStack){
        this.jumpName = jumpName;
        this.jumpStage = jumpStage;
        this.jumpStack = jumpStack;
    }

    public String getJumpName() {
        return jumpName;
    }


    public int getJumpStage() {
        return jumpStage;
    }


    public int getJumpStack() {
        return jumpStack;
    }

    public void setJumpStage(int stage){
        this.jumpStage = stage;
    }

    @Override
    public String toString(){
        String str = "{ " + jumpName + " -> " + jumpStage;
        if(jumpStack != -1)
            str += " stack <- " + jumpStack + " }";
        else
            str += " }";
        return str;
    }

}
