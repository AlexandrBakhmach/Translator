package com.analizator.translator.executor;

import com.analizator.Analyzer;
import com.analizator.lexycal_analizator.entity.ProgramUnit;
import com.analizator.lexycal_analizator.entity.Type;
import com.analizator.translator.dijkstra.*;
import com.analizator.translator.dijkstra.Number;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexandr on 5/21/17.
 */
@Service("executor")
public class Executor {
    @Value("#{analyzer}")private Analyzer analyzer;
    @Value("#{executionStack}")private ArrayList<Element> executionStack;
    @Value("#{bufferedReaderForExecutor}")private BufferedReader bufferedReader;
    private static final String DOUBLE_PATTERN = "^[0-9]*[.,]?[0-9]+$";
    private static final String INTEGER_PATTERN = "^-?\\d+$";
    private static int cursor;
    private static final String BL_SIGN = "BP";

    public void execute() throws ExecutorException, IOException {
        cursor = 0;
        start(analyzer.getTranslator().getMainLogic().getPolis());
    }

    private void start(List<ProgramUnit> polis) throws ExecutorException, IOException {
        if(cursor < polis.size()){
            if(polis.get(cursor).getClass().equals(Variable.class) ||
                    polis.get(cursor).getClass().equals(Number.class))
               executionStack.add((Element) polis.get(cursor));
            else if(polis.get(cursor).getClass().equals(Operation.class))
                executeFromStack(polis);
            else if(polis.get(cursor).getClass().equals(Phrase.class))
                remove(polis);
            cursor++;
            start(polis);
        }
    }

    private void remove(List<ProgramUnit> polis) throws ExecutorException {
        if(((Phrase)polis.get(cursor)).getSign().equals(BL_SIGN))
            bmRemove(polis);
        else
            yplRemove(polis);

    }

    private void yplRemove(List<ProgramUnit> polis) throws ExecutorException {
        Number number = (Number)executionStack.get(executionStack.size()-1);
        if (Boolean.valueOf(number.getValue()) == false){
            cursor--;
            cursor = getCursorToLabel(polis);
        }
    }

    private void bmRemove(List<ProgramUnit> polis) throws ExecutorException {
        cursor--;
        cursor = getCursorToLabel(polis);
    }

    private void executeFromStack(List<ProgramUnit> polis) throws ExecutorException, IOException {
        Operation operation = (Operation)polis.get(cursor);
        if(operation.getSign().equals(":="))
            executeForAssignment();
        else {
            checkForNull(operation);
            if (operation.getSign().equals("+"))
                executeForPlus();
            else if (operation.getSign().equals("-"))
                executeForMinus();
            else if (operation.getSign().equals("*"))
                executeForMultiplication();
            else if (operation.getSign().equals("/"))
                executeForDivision();
            else if(operation.getSign().equals("wt"))
                executeForWrite();
            else if(operation.getSign().equals("rd"))
                executeForRead();
            else if (operation.getSign().equals(">"))
                executeForMore();
            else if (operation.getSign().equals("<"))
                executeForLess();
            else if (operation.getSign().equals("!="))
                executeForNotEqual();
            else if (operation.getSign().equals("=="))
                executeForEqual();
            else if (operation.getSign().equals(">="))
                executeForMoreOrEqual();
            else if (operation.getSign().equals("<="))
                executeForLessOrEqual();
            else if (operation.getSign().equals("not"))
                executeForNot();
            else if (operation.getSign().equals("and"))
                executeForAnd();
            else if (operation.getSign().equals("or"))
                executeForOr();
        }
    }

    private void checkForNull(Operation operation) throws ExecutorException {
        ExecutionUnit executionUnit1 = (ExecutionUnit)executionStack.get(executionStack.size() - 1);
        if(executionStack.size() > 1 && !operation.getSign().equals("rd")) {
            ExecutionUnit executionUnit2 = (ExecutionUnit) executionStack.get(executionStack.size() - 2);
            if (executionUnit1.getValue() == null)
                throw new ExecutorException("EXECUTION EXCEPTION: Variable: " + ((Variable) executionUnit1).getSign() + " was not initialized.");
            else if (executionUnit2.getValue() == null)
                throw new ExecutorException("EXECUTION EXCEPTION: Variable: " + ((Variable) executionUnit2).getSign() + " was not initialized.");
        }else if(!operation.getSign().equals("rd")){
            if (executionUnit1.getValue() == null)
                throw new ExecutorException("EXECUTION EXCEPTION: Variable: " + ((Variable) executionUnit1).getSign() + " was not initialized.");
        }
    }

    private void executeForOr() throws ExecutorException {
        ExecutionUnit executionUnit1 = (ExecutionUnit)executionStack.get(executionStack.size() - 1);
        ExecutionUnit executionUnit2 = (ExecutionUnit)executionStack.get(executionStack.size() - 2);
        if (executionUnit1.getType().equals(Type.BOOLEAN) || executionUnit2.getType().equals(Type.BOOLEAN)) {
            boolean res = Boolean.valueOf(executionUnit2.getValue()) && Boolean.valueOf(executionUnit1.getValue());
            upDateExecutionStack(res);
        } else
            throw new ExecutorException("EXECUTOR EXCEPTION: Type error.");
    }

    private void executeForAnd() throws ExecutorException {
        ExecutionUnit executionUnit1 = (ExecutionUnit)executionStack.get(executionStack.size() - 1);
        ExecutionUnit executionUnit2 = (ExecutionUnit)executionStack.get(executionStack.size() - 2);
        if (executionUnit1.getType().equals(Type.BOOLEAN) && executionUnit2.getType().equals(Type.BOOLEAN)) {
            boolean res = Boolean.valueOf(executionUnit2.getValue()) && Boolean.valueOf(executionUnit1.getValue());
            upDateExecutionStack(res);
        } else
            throw new ExecutorException("EXECUTOR EXCEPTION: Type error.");
    }

    private void executeForNot() throws ExecutorException {
        ExecutionUnit executionUnit1 = (ExecutionUnit)executionStack.get(executionStack.size() - 1);
        if (executionUnit1.getType().equals(Type.BOOLEAN)) {
            boolean value = Boolean.valueOf(executionUnit1.getValue());
            executionUnit1.setValue(String.valueOf(!value));
        } else
            throw new ExecutorException("EXECUTOR EXCEPTION: Type error.");
    }

    private void executeForLessOrEqual() {
        ExecutionUnit executionUnit1 = (ExecutionUnit)executionStack.get(executionStack.size() - 1);
        ExecutionUnit executionUnit2 = (ExecutionUnit)executionStack.get(executionStack.size() - 2);
        if (Double.valueOf(executionUnit2.getValue()) <= Double.valueOf(executionUnit1.getValue())) {
            upDateExecutionStack(true);
        } else {
            upDateExecutionStack(false);
        }
    }

    private void executeForMoreOrEqual() {
        ExecutionUnit executionUnit1 = (ExecutionUnit)executionStack.get(executionStack.size() - 1);
        ExecutionUnit executionUnit2 = (ExecutionUnit)executionStack.get(executionStack.size() - 2);
        if (Double.valueOf(executionUnit2.getValue()) >= Double.valueOf(executionUnit1.getValue())) {
            upDateExecutionStack(true);
        } else {
            upDateExecutionStack(false);
        }
    }

    private void executeForEqual() {
        ExecutionUnit executionUnit1 = (ExecutionUnit)executionStack.get(executionStack.size() - 1);
        ExecutionUnit executionUnit2 = (ExecutionUnit)executionStack.get(executionStack.size() - 2);
        if (Double.valueOf(executionUnit2.getValue()) == Double.valueOf(executionUnit1.getValue())) {
            upDateExecutionStack(true);
        } else {
            upDateExecutionStack(false);
        }
    }

    private void executeForNotEqual() {
        ExecutionUnit executionUnit1 = (ExecutionUnit)executionStack.get(executionStack.size() - 1);
        ExecutionUnit executionUnit2 = (ExecutionUnit)executionStack.get(executionStack.size() - 2);
        if (Double.valueOf(executionUnit2.getValue()) != Double.valueOf(executionUnit1.getValue())) {
            upDateExecutionStack(true);
        } else {
            upDateExecutionStack(false);
        }
    }

    private void executeForLess() {
        ExecutionUnit executionUnit1 = (ExecutionUnit)executionStack.get(executionStack.size() - 1);
        ExecutionUnit executionUnit2 = (ExecutionUnit)executionStack.get(executionStack.size() - 2);
        if (Double.valueOf(executionUnit2.getValue()) < Double.valueOf(executionUnit1.getValue())) {
            upDateExecutionStack(true);
        } else {
            upDateExecutionStack(false);
        }
    }

    private void executeForMore() {
        ExecutionUnit executionUnit1 = (ExecutionUnit)executionStack.get(executionStack.size() - 1);
        ExecutionUnit executionUnit2 = (ExecutionUnit)executionStack.get(executionStack.size() - 2);
        if (Double.valueOf(executionUnit2.getValue()) > Double.valueOf(executionUnit1.getValue())) {
            upDateExecutionStack(true);
        } else {
            upDateExecutionStack(false);
        }
    }

    private void executeForRead() throws IOException, ExecutorException {
        ExecutionUnit executionUnit1 = (ExecutionUnit)executionStack.get(executionStack.size() - 1);
        read(executionUnit1);
    }

    private void executeForWrite() {
        ExecutionUnit executionUnit1 = (ExecutionUnit)executionStack.get(executionStack.size() - 1);
        write(executionUnit1);
    }

    private void executeForDivision() {
        ExecutionUnit executionUnit1 = (ExecutionUnit)executionStack.get(executionStack.size() - 1);
        ExecutionUnit executionUnit2 = (ExecutionUnit)executionStack.get(executionStack.size() - 2);
        double res = Double.valueOf(executionUnit2.getValue()) / Double.valueOf(executionUnit1.getValue());
        upDateExecutionStack(res);
    }

    private void executeForMultiplication() throws ExecutorException {
        ExecutionUnit executionUnit1 = (ExecutionUnit)executionStack.get(executionStack.size() - 1);
        ExecutionUnit executionUnit2 = (ExecutionUnit)executionStack.get(executionStack.size() - 2);
        Type type = getType(executionUnit1, executionUnit2);
        if (type.equals(Type.INTEGER)) {
            int res = Integer.valueOf(executionUnit2.getValue()) * Integer.valueOf(executionUnit1.getValue());
            upDateExecutionStack(res);
        } else {
            double res = Double.valueOf(executionUnit2.getValue()) * Double.valueOf(executionUnit1.getValue());
            upDateExecutionStack(res);
        }
    }

    private void executeForMinus() throws ExecutorException {
        ExecutionUnit executionUnit1 = (ExecutionUnit)executionStack.get(executionStack.size() - 1);
        ExecutionUnit executionUnit2 = (ExecutionUnit)executionStack.get(executionStack.size() - 2);
        Type type = getType(executionUnit1, executionUnit2);
        if (type.equals(Type.INTEGER)) {
            int res = Integer.valueOf(executionUnit2.getValue()) - Integer.valueOf(executionUnit1.getValue());
            upDateExecutionStack(res);
        } else {
            double res = Double.valueOf(executionUnit1.getValue()) - Double.valueOf(executionUnit2.getValue());
            upDateExecutionStack(res);
        }
    }

    private void executeForPlus() throws ExecutorException {
        ExecutionUnit executionUnit1 = (ExecutionUnit)executionStack.get(executionStack.size() - 1);
        ExecutionUnit executionUnit2 = (ExecutionUnit)executionStack.get(executionStack.size() - 2);
        Type type = getType(executionUnit1, executionUnit2);
        if (type.equals(Type.INTEGER)) {
            int res = Integer.valueOf(executionUnit2.getValue()) + Integer.valueOf(executionUnit1.getValue());
            upDateExecutionStack(res);
        } else {
            double res = Double.valueOf(executionUnit2.getValue()) + Double.valueOf(executionUnit1.getValue());
            upDateExecutionStack(res);
        }
    }

    private void executeForAssignment() throws ExecutorException {
        ExecutionUnit executionUnit1 = (ExecutionUnit)executionStack.get(executionStack.size() - 1);
        ExecutionUnit executionUnit2 = (ExecutionUnit)executionStack.get(executionStack.size() - 2);
        if(executionUnit1.getType().equals(executionUnit2.getType()))
            appropriate(executionUnit2, executionUnit1, Type.INTEGER);
        else if(executionUnit2.getType().equals(Type.REAL))
            appropriate(executionUnit2, executionUnit1, Type.REAL);
        else
            throw new ExecutorException("EXECUTOR EXCEPTION: Type error.");
    }

    private void read(ExecutionUnit executionUnit1) throws ExecutorException, IOException {
        Variable variable = (Variable)executionUnit1;
        System.out.print("READ:\tEnter value:\t" + variable.getSign() + " ");
        String redValue = bufferedReader.readLine();
        if(redValue.matches(INTEGER_PATTERN)){
            variable.setValue(String.valueOf(Integer.valueOf(redValue)));
        }else if(redValue.matches(DOUBLE_PATTERN) && variable.getType().equals(Type.REAL)){
            variable.setValue(String.valueOf(Double.valueOf(redValue)));
        }else throw new ExecutorException("EXECUTOR EXCEPTION: Type error.");
    }

    private void write(ExecutionUnit executionUnit1) {
        Variable variable = (Variable)executionUnit1;
        System.out.print("WRITE:\tVariable name:\t" + variable.getSign() + " " +executionUnit1.getValue()+"\n");
        int size = executionStack.size();
        executionStack.remove(size-1);
    }

    private void appropriate(ExecutionUnit executionUnit1, ExecutionUnit executionUnit2, Type type) {
        if(type.equals(Type.INTEGER))
            executionUnit1.setValue(String.valueOf(executionUnit2.getValue()));
        if(type.equals(Type.REAL))
            executionUnit1.setValue(String.valueOf(Double.valueOf(executionUnit2.getValue())));
    }

    private void upDateExecutionStack(int res) {
        int size = executionStack.size();
        executionStack.remove(size-1);
        executionStack.remove(size-2);
        executionStack.add(new Number(String.valueOf(res),Type.INTEGER));
    }
    private void upDateExecutionStack(double res) {
        int size = executionStack.size();
        executionStack.remove(size-1);
        executionStack.remove(size-2);
        executionStack.add(new Number(String.valueOf(res),Type.REAL));
    }

    private void upDateExecutionStack(Boolean res) {
        int size = executionStack.size();
        executionStack.remove(size-1);
        executionStack.remove(size-2);
        executionStack.add(new Number(String.valueOf(res),Type.BOOLEAN));
    }


    private Type getType(ExecutionUnit executionUnit1, ExecutionUnit executionUnit2) throws ExecutorException {
        if(executionUnit1.getType().equals(Type.INTEGER) && executionUnit2.getType().equals(Type.INTEGER))
            return Type.INTEGER;
        else if(!executionUnit1.getType().equals(Type.BOOLEAN) && !executionUnit2.getType().equals(Type.BOOLEAN))
            return Type.REAL;
        else
            throw new ExecutorException("EXECUTOR EXCEPTION: Type error.");
    }


    private int getCursorToLabel(List<ProgramUnit> polis) throws ExecutorException {
        Label label = (Label)polis.get(cursor);
        for(int i = 0; i < polis.size(); i++){
            if(((Element)polis.get(i)).getSign().equals(label.getSign()+":")){
                return i;
            }
        }
        throw new ExecutorException("EXECUTOR EXCEPTION: No suitable jump found for label:\t" + label);
    }


}
