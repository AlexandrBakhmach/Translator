package com.analizator.translator.dijkstra;

import com.analizator.Analyzer;
import com.analizator.lexycal_analizator.entity.OutputUnit;
import com.analizator.lexycal_analizator.entity.ProgramUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexandr on 4/4/17.
 */
@Component("mainLogic")
public class MainLogic
        implements Logic {
    private static List<Operation> elements;
    static {
        elements = new ArrayList<>();
        elements.add(new Operation("if",0));
        elements.add(new Operation("while",0));
        elements.add(new Operation("write",0));
        elements.add(new Operation("read",0));
        elements.add(new Operation("else",1));
        elements.add(new Operation("{",2));
        elements.add(new Operation("}",3));
        elements.add(new Operation(";",4));
        elements.add(new Operation("(",5));
        elements.add(new Operation(")",6));
        elements.add(new Operation("[",5));
        elements.add(new Operation("]",6));
        elements.add(new Operation(":=",7));
        elements.add(new Operation("or",8));
        elements.add(new Operation("and",9));
        elements.add(new Operation("not",10));
        elements.add(new Operation(">",11));
        elements.add(new Operation(">=",11));
        elements.add(new Operation("<",11));
        elements.add(new Operation("<=",11));
        elements.add(new Operation("==",11));
        elements.add(new Operation("!=",11));
        elements.add(new Operation("+",12));
        elements.add(new Operation("-",12));
        elements.add(new Operation("*",13));
        elements.add(new Operation("/",13));
    }

    @Value("#{false}") private boolean operated;
    @Value("#{false}") private boolean isIf;
    @Value("#{false}") private boolean isWhile;
    @Value("#{1}") private int labelCounter;
    @Value("#{labels}") private List<String> labels;
    @Value("#{true}") private boolean firstIf;
    @Value("#{whileLabels}") private List<String> whileLabels;
    @Value("") protected String data;
    @Value("#{false}") protected boolean print;
    @Value("#{timePolis}") protected List<ProgramUnit> polis;
    @Value("#{timeStack}") protected List<Operation> stack;
    @Value("#{analyzer}")Analyzer analyzer;


    @Override
    public List<ProgramUnit> getPolis() {
        return this.polis;
    }
    @Override
    public void made() throws PolisException {
        analise();
    }
    @Override
    public String toString(){
        String str = "";
        for(ProgramUnit e : polis){
            str += ((Element)e).getSign() + " ";
        }
        return str;
    }
    private void analise() throws PolisException {
        boolean isBegin = false;
        for(OutputUnit unit : analyzer.getLexAnalyser().getUnits()){
            if(unit.getLexeme().getName().toLowerCase().equals("end"))
                isBegin = false;
            if(isBegin)
                operate(unit);
            if(unit.getLexeme().getName().toLowerCase().equals("begin"))
                isBegin = true;
        }
    }

    private void operate(OutputUnit unit) throws PolisException {
        if(unit.getLexeme().getName() == "id"){
            String value = analyzer.getLexAnalyser().getIdentifiers().get(unit.getSpecNum()).getName();
            if(value.equals(",")) return;
            this.polis.add(
                    new Variable(value,
                            analyzer.getLexAnalyser().getIdentifiers().get(unit.getSpecNum()).getType(),
                            analyzer.getLexAnalyser().getIdentifiers().get(unit.getSpecNum()))
            );
            ifWriteRead();
        } else if (unit.getLexeme().getName() == "const") {
            this.polis.add(new Number(analyzer.getLexAnalyser().getConstants().get(unit.getSpecNum()).getName(),
                    analyzer.getLexAnalyser().getConstants().get(unit.getSpecNum()).getType()));
        }else{
            for (Operation operation : elements)
                if(unit.getLexeme().getName().equals(operation.getSign()))
                    reLocate(operation);
        }
    }

    private void ifWriteRead() throws PolisException {
        String word = searchWord();
        if(word == null)
            return;
        if(word.equals("write"))
            polis.add(new Operation("wt",0));
        else if(word.equals("read"))
            polis.add(new Operation("rd",0));
    }

    private void reLocate(Operation value) throws PolisException {
        if(value.getSign().equals("(") || value.getSign().equals("["))
            foundOpenBracket(value);
        else if(value.getSign().equals(")") || value.getSign().equals("]"))
            foundCloseBracket();
        else if(value.getSign().equals("{"))
            foundFigureOpenBracket();
        else if(value.getSign().equals("}"))
            foundFigureCloseBracket();
        else if(value.getSign().equals("if"))
            foundIf(value);
        else if(value.getSign().equals("else"))
            foundElse();
        else if(value.getSign().equals(";"))
            foundEnd();
        else if(value.getSign().equals("while")){
            foundWhile(value);
        }else if(value.getSign().equals("read")){
            foundWrite(value);
        }else if(value.getSign().equals("write")){
            foundRead(value);
        }
        else
            operatorFound(value);
    }

    private void foundRead(Operation value) {
        stack.add(value);
    }
    private void foundWrite(Operation value) {
        stack.add(value);
    }
    private void foundEnd() throws PolisException{
        int length = stack.size() - 1;
        while (length >= 0 &&
                !stack.get(length).getSign().contains("if") &&
                !stack.get(length).getSign().contains("while") &&
                !stack.get(length).getSign().contains("else")){
            polis.add(stack.get(length));
            stack.remove(length);
            length--;
        }
    }
    private void foundElse() {
        isIf = false;
        stack.add(new Operation("else",0));
    }
    private void foundWhile(Operation value){
        stack.add(value);
        polis.add(new Label("m" + labelCounter + ":"));
        whileLabels.add("m" + labelCounter);
        whileLabels.add("m" + (labelCounter + 1));
        labelCounter+=2;
    }
    private void foundIf(Operation value) {
        stack.add(value);
    }
    private void foundFigureCloseBracket() throws PolisException{
        String word = searchWord();
        if(word.equals("else")){
            int length = stack.size() - 1;
            while (!stack.get(length).getSign().equals("if")){
                stack.remove(length);
                length--;
            }
            stack.remove(length);
            pullLabels();
        }else if(word.equals("while")){
            polis.add(new Label(whileLabels.get(whileLabels.size() - 2)));
            polis.add(new Phrase("BP"));
            polis.add(new Label(whileLabels.get(whileLabels.size() - 1) + ":"));
            whileLabels.remove(whileLabels.size() - 1);
            whileLabels.remove(whileLabels.size() - 1);
            int length = stack.size() - 1;
            while (!stack.get(length).getSign().equals("while")){
                stack.remove(length);
                length--;
            }
            stack.remove(length);
        }
    }
    private void pullLabels(){
        int length = labels.size();
        while (length > 1){
            polis.add(new Label(labels.get(length - 1)+":"));
            labels.remove(length - 1);
            labels.remove(length - 2);
            length = labels.size();
        }
    }
    private void foundFigureOpenBracket() throws PolisException {
        String word = searchWord();
        if(word.equals("if")){
            polis.add(new Label("m" + labelCounter));
            polis.add(new Phrase("YPL"));
            labels.add("m" + labelCounter);
            labelCounter++;
        }else if(word.equals("else")){
            polis.add(new Label("m" + labelCounter));
            polis.add(new Phrase("BP"));
            polis.add(new Label(labels.get(labels.size() - 1)+":"));
            labels.add("m" + labelCounter);
            labelCounter++;
        }else if(word.equals("while")){
            polis.add(new Label(whileLabels.get(whileLabels.size() - 1)));
            polis.add(new Phrase("YPL"));
        }else
            throw new PolisException();

    }
    private void operatorFound(Operation value) throws PolisException {
        int length  = stack.size() - 1;
        if(length == -1) // Stack is empty
            stack.add(value);
        else if(value.getPriority() == stack.get(length).getPriority() ||
                value.getPriority() < stack.get(length).getPriority()){
            while (length >= 0 && (value.getPriority() == stack.get(length).getPriority() ||
                    value.getPriority() < stack.get(length).getPriority())){
                polis.add(stack.get(length));
                stack.remove(length);
                length  = stack.size() - 1;
            }
            stack.add(value);
        }
        else
            stack.add(value);
    }
    private void foundCloseBracket() throws PolisException {
        String word = searchWord();
        if((!word.equals("read") && !word.equals("write")) || word == null) {
            int length = stack.size() - 1;
            while (!stack.get(length).getSign().equals("(") && !stack.get(length).getSign().equals("[")) {
                if (length < 0)
                    throw new PolisException();
                polis.add(stack.get(length));
                stack.remove(length);
                length--;
            }
            stack.remove(length);
        }else{
            stack.remove(stack.size() - 1);
        }
    }
    private void foundOpenBracket(Operation value) {
        String word = searchWord();
        if(!word.equals("read") && !word.equals("write"))
            stack.add(value);
    }
    private String searchWord(){
        int length = stack.size() - 1;
        if(length >= 0){
            String word = stack.get(length).getSign();
            while (!stack.get(length).getSign().equals("while") &&
                    !stack.get(length).getSign().equals("if") &&
                    !stack.get(length).getSign().equals("else") &&
                    !stack.get(length).getSign().equals("read") &&
                    !stack.get(length).getSign().equals("write")){
                if(--length < 0)
                    break;
                word = stack.get(length).getSign();
            }
            return word;
        }else{
            return null;
        }

    }

    public static List<Operation> getElements() {
        return elements;
    }
}


