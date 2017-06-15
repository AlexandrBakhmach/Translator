package com.analizator.lexycal_analizator.entity;

/**
 * Created by alexandr on 5/10/17.
 */
public enum Lexeme {

    NOT(1,"not"),PROGRAM(2,"program"),BEGIN(3,"begin"),
    END(4,"end"),INTEGER(5,"integer"), REAL(6,"real"),
    VAR(7,"var"),READ(8,"read"),WRITE(9,"write"),
    WHILE(10,"while"),DO(11,"do"),IF(12,"if"),
    THEN(13,"then"),ELSE(14,"else"),OR(15,"or"),
    AND(16,"and"),END_SIGN(17,";"),DOUBLE_DOT(17,":"),
    OPEN_BRACKETS(18,"("),CLOSE_BRACKETS(19,")"),OPEN_FIGURE_BRACKETS(20,"{"),
    CLOSE_FIGURE_BRACKETS(21,"}"),PLUS(22,"+"),MINUS(23,"-"),
    MULTIPLY(24,"*"),DIVISION(25,"/"),DOT(26,"."),
    APPROPRIATE(27,":="),MORE(28,">"),LESS(29,"<"),
    MORE_EQUAL(30,">="),LESS_EQUAL(31,"<="),EQUAL(32,"=="),
    NOT_EQUAL(33,"!="),ID(34,"id"),CONST(35,"const"),COMA(36,","),
    OPEN_SQUARE_BRACKET(37,"["), CLOSE_SQUARE_BRACKET(38,"]");

    private int id;
    private String name;

    Lexeme(int id, String name){
        this.id = id;
        this.name = name;
    }

    public int getId(){
        return this.id;
    }

    public String getName(){
        return  this.name;
    }

    public static Lexeme getLexeme(String substring){
        for(Lexeme lexeme : values())
            if(lexeme.getName().equals(substring)) return lexeme;
        return null;
    }
}

