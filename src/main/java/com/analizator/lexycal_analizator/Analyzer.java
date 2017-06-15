package com.analizator.lexycal_analizator;

import com.analizator.lexycal_analizator.entity.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Created by alexandr on 5/10/17.
 */

/**
 * Main Lexical analyzer class
 * Lexical analyzer is responsible to check input program text
 * for different undeclared characters.
 * Lexical analyzer was made using state-diagram method
 */

@Service("lexAnalyzer")
public class Analyzer {

    private static final String LETTER_PATTERN = "[A-Za-z]";
    private static final String NUMBER_PATTERN = "[0-9]";
    private static final String DEVIDER_PATTERN = "[\\s\t]";
    private static final String DOUBLE_PATTERN = "^[0-9]*[.,]?[0-9]+$";
    private static final String INTEGER_PATTERN = "^-?\\d+$";
    private static final String TWO_SIGN_SYMBOL_PATTERN = "[<>=!:().,;]";
    private static final String ONE_SIGN_PATTERN = "[\\{\\}\\[\\]+-/*]";
    private static final char POINT = '.';
    private static final char EQUAL = '=';
    private static Character pointedChar;
    private boolean allowCharacterDeclaration;

    @Value("#{constantsLex}") private ArrayList<Constant> constants;
    @Value("#{identifiersLex}") private ArrayList<Identifier> identifiers;
    @Value("#{outputLex}")private ArrayList<OutputUnit> units;
    @Value("#{bufferedReader}")private BufferedReader bufferedReader;
    @Value("#{programLinesLex}")private ArrayList<ProgramLine> programLines;

    public void analyze() throws IOException, LexicalError {
        createProgramLines();
        analyzeLines();
    }

    private void analyzeLines() throws LexicalError {
        allowCharacterDeclaration = true;
        for (ProgramLine programLine : programLines)
            analyzeLine(programLine);

    }

    private void analyzeLine(ProgramLine programLine) throws LexicalError {
        StringBuilder lexemeStr = new StringBuilder();
        programLine.getLine().append("\n");
        beginLogic(programLine, lexemeStr, -1, 0);
    }

    private void beginLogic(ProgramLine line, StringBuilder lexemeStr, int pointer, int stage)
            throws LexicalError {
        if(++pointer < line.getLine().length()){
            pointedChar = line.getLine().charAt(pointer);
            if(Pattern.compile(LETTER_PATTERN).matcher(pointedChar.toString()).matches())
                letterLogic(line, lexemeStr, pointer, stage);
            else if(Pattern.compile(NUMBER_PATTERN).matcher(pointedChar.toString()).matches())
                numberLogic(line, lexemeStr, pointer, stage);
            else if(Pattern.compile(TWO_SIGN_SYMBOL_PATTERN).matcher(pointedChar.toString()).matches())
                twoSignLogic(line, lexemeStr, pointer, stage);
            else if(Pattern.compile(ONE_SIGN_PATTERN).matcher(pointedChar.toString()).matches())
                oneSignLogic(line, lexemeStr, pointer, stage);
            else if(!Pattern.compile(DEVIDER_PATTERN).matcher(pointedChar.toString()).matches())
                throw new LexicalError("ERROR: Found unknown character:\t" + pointedChar + " at line number:\t" + line.getId());
            else
                beginLogic(line, lexemeStr, pointer, stage);
        }else return;
    }

    private void oneSignLogic(ProgramLine line, StringBuilder lexemeStr, int pointer, int stage)
            throws LexicalError {
        lexemeStr.append(pointedChar);
        if(++pointer < line.getLine().length()) {
            pointedChar = line.getLine().charAt(pointer);
            exit(line, lexemeStr, pointer, stage);
        }
        else return;
    }

    private void twoSignLogic(ProgramLine line, StringBuilder lexemeStr, int pointer, int stage)
            throws LexicalError {
        lexemeStr.append(pointedChar);
        if(++pointer < line.getLine().length()){
            pointedChar = line.getLine().charAt(pointer);
            if(pointedChar.charValue() == EQUAL)
                stage5Logic(line, lexemeStr, pointer, stage);
            else exit(line, lexemeStr, pointer, stage);
        }else return;
    }

    private void stage5Logic(ProgramLine line, StringBuilder lexemeStr, int pointer, int stage)
            throws LexicalError {
        lexemeStr.append(pointedChar);
        if(++pointer < line.getLine().length()) {
            pointedChar = line.getLine().charAt(pointer);
            exit(line, lexemeStr, pointer, stage);
        }
        else return;
    }

    private void stage4Logic(ProgramLine line, StringBuilder lexemeStr, int pointer, int stage)
            throws LexicalError {
        lexemeStr.append(pointedChar);
        if(++pointer < line.getLine().length()){
            pointedChar = line.getLine().charAt(pointer);
            if(Pattern.compile(NUMBER_PATTERN).matcher(pointedChar.toString()).matches())
                stage4Logic(line, lexemeStr, pointer, stage);
            else exit(line, lexemeStr, pointer, stage);
        }else return;
    }

    private void numberLogic(ProgramLine line, StringBuilder lexemeStr, int pointer, int stage)
            throws LexicalError {
        lexemeStr.append(pointedChar);
        if(++pointer < line.getLine().length()){
            pointedChar = line.getLine().charAt(pointer);
            if(Pattern.compile(NUMBER_PATTERN).matcher(pointedChar.toString()).matches())
                numberLogic(line, lexemeStr, pointer, stage);
            else if(pointedChar.charValue() == POINT)
                stage4Logic(line, lexemeStr, pointer, stage);
            else exit(line, lexemeStr, pointer, stage);
        }else return;
    }

    private void letterLogic(ProgramLine line, StringBuilder lexemeStr, int pointer, int stage)
            throws LexicalError {
        lexemeStr.append(pointedChar);
        if(++pointer < line.getLine().length()){
            pointedChar = line.getLine().charAt(pointer);
            if(Pattern.compile(LETTER_PATTERN).matcher(pointedChar.toString()).matches() ||
                    Pattern.compile(NUMBER_PATTERN).matcher(pointedChar.toString()).matches())
                letterLogic(line, lexemeStr, pointer, stage);
            else exit(line, lexemeStr, pointer, stage);
        }else return;
    }

    private void exit(ProgramLine line, StringBuilder lexemeStr, int pointer, int stage)
            throws LexicalError {
        pointer--;
        generateOutput(lexemeStr,line);
        beginLogic(line, lexemeStr, pointer, stage);
    }

    private void generateOutput(StringBuilder lexemeStr, ProgramLine line)
            throws LexicalError {
        Lexeme lexeme;
        if((lexeme = Lexeme.getLexeme(lexemeStr.toString().toLowerCase())) != null){
            units.add(new OutputUnit(line.getId(),lexeme,lexeme.getId(),-1));
            if(lexemeStr.toString().toLowerCase().equals(Lexeme.REAL.getName()) ||
                    lexemeStr.toString().toLowerCase().equals(Lexeme.INTEGER.getName()))
                setType(lexemeStr);
            if(lexemeStr.toString().equals(Lexeme.BEGIN.getName()))
                allowCharacterDeclaration = false;
        }else{
            if(lexemeStr.toString().matches(INTEGER_PATTERN)) {
                lexeme = Lexeme.getLexeme("const");
                Constant constant = new Constant(lexemeStr.toString(), Type.INTEGER);
                constants.add(constant);
                units.add(new OutputUnit(line.getId(), lexeme, lexeme.getId(), constant.getId()));
            }else if(lexemeStr.toString().matches(DOUBLE_PATTERN)){
                lexeme = Lexeme.getLexeme("const");
                Constant constant = new Constant(lexemeStr.toString(), Type.REAL);
                constants.add(constant);
                units.add(new OutputUnit(line.getId(), lexeme, lexeme.getId(), constant.getId()));
            }else {
                if(allowCharacterDeclaration) {
                    lexeme = Lexeme.getLexeme("id");
                    Identifier identifier = new Identifier(lexemeStr.toString(), null);
                    identifiers.add(identifier);
                    units.add(new OutputUnit(line.getId(), lexeme, lexeme.getId(), identifier.getId()));
                }else{
                    if (isCharDeclared(lexemeStr) == null &&
                            !Pattern.compile(NUMBER_PATTERN).matcher(lexemeStr.toString()).matches())
                        throw new LexicalError("ERROR: Undefined identifier found ( " +
                                lexemeStr.toString() + " ). Please declare it in var block");
                    lexeme = Lexeme.getLexeme("id");
                    units.add(new OutputUnit(line.getId(), lexeme, lexeme.getId(), isCharDeclared(lexemeStr).getId()));
                }
            }
        }
        lexemeStr.setLength(0);
    }

    private Identifier isCharDeclared(StringBuilder lexemeStr) {
        for(Identifier id : identifiers) {
            if (id.getName().equals(lexemeStr.toString()))
                return id;
        }
        return null;
    }

    private void createProgramLines() throws IOException {
        String line;
        while ((line = bufferedReader.readLine()) != null){
            programLines.add(new ProgramLine(line));
        }
    }

    public ArrayList<Constant> getConstants() {
        return constants;
    }

    public ArrayList<Identifier> getIdentifiers() {
        return identifiers;
    }

    public ArrayList<OutputUnit> getUnits() {
        return units;
    }

    private void setType(StringBuilder str) {
        int pos1 = getFirstPos();
        int pos2 = getNullTypeLength() + pos1;
        int counter = 0;
        for(Identifier id : identifiers ){
            counter++;
            if(counter >= pos1 && counter <= pos2){
                if(str.toString().equals(Type.INTEGER.toString().toLowerCase()))
                    id.setType(Type.INTEGER);
                else
                    id.setType(Type.REAL);
            }
        }
    }

    private int getFirstPos(){
        int pos = 0;
        for(Identifier id : identifiers){
            pos++;
            if(id.getType() == null) break;
        }
        return pos;
    }

    private int getNullTypeLength(){
        int length = 0;
        for(Identifier id : identifiers){
            if(id.getType() == null)
                length++;
        }
        return length;
    }

    @Override
    public String toString(){
        String str = "MAIN TABLE \n\n";
        for(OutputUnit outputUnit : units){
            str += outputUnit.toString() + "\n";
        }
        str += "\nIDENTIFIER TABLE\n\n";
        for(Identifier identifier : identifiers){
            str += identifier.toString() + "\n";
        }
        str += "\nCONSTANT TABLE\n\n";
        for(Constant constant : constants){
            str += constant.toString() + "\n";
        }
        return str;
    }
}
