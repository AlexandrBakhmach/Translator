package com.analizator.lexycal_analizator.entity;

/**
 * Created by alexandr on 5/10/17.
 */
public class OutputUnit extends ProgramUnit {
    private int lineNumber;
    private Lexeme lexeme;
    private int code;
    private int specNum;
    private static int count = 0;

    public OutputUnit(int lineNumber, Lexeme lexeme, int code, int specNum){
        super.id = count++;
        super.name = "lexUnit";
        this.lineNumber = lineNumber;
        this.lexeme = lexeme;
        this.code = code;
        this.specNum = specNum;
    }


    public int getLineNumber() {
        return lineNumber;
    }

    public Lexeme getLexeme() {
        return lexeme;
    }

    public int getSpecNum() {
        return specNum;
    }


    @Override
    public String toString(){
        String str = "LINE NUMBER:\t" + lineNumber +
                "  LEXEME:\t" + lexeme.getName() +
                "  CODE:\t" + code;
        if(specNum >= 0){
            str += "  SPECIAL NUMBER:\t" + specNum;
        }else{
            str += "  SPECIAL NUMBER:\tno";
        }
        return  str;
    }

}
