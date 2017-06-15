package com.analizator.syntax_analizator;

import com.analizator.lexycal_analizator.entity.OutputUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by alexandr on 5/10/17.
 */
@Service("synAnalyzer")
public class Analyzer {

    @Value("#{stageArray}")private ArrayList<Stage> stages;
    @Value("#{bufferedReader1}") private BufferedReader bufferedReader;
    @Value("#{stageStack}")private ArrayList<Integer> stack;
    @Value("#{analyzer}")private com.analizator.Analyzer analyzer;
    private static final String JUMP_PATTERN = "[\\|]";
    private static final String NOT_EQUAL = "#";
    private boolean isEndAnalyze = false;
    private boolean isNotEqual = false;
    private Stage cursor;
    private boolean found;

    public void analyze() throws IOException, StageException {
    //    System.out.print(analyzer.getLexAnalyser().toString());
        readStages();
        analyzeByStages();
    }

    private void analyzeByStages() throws StageException {
        cursor = stages.get(0);
        for(OutputUnit unit : analyzer.getLexAnalyser().getUnits())
            if (!isEndAnalyze)
            analyze(unit);
        if(!isEndAnalyze) throw new StageException("SYNTAX ERROR: Some brackets was not closed");
    }

    private void analyze(OutputUnit unit) throws StageException {
        found = false;
        isNotEqual = false;
        for(Jump jump : cursor.getJumps())
            if(jump.getJumpName().equals(unit.getLexeme().getName())){
                jump(jump, unit);
            }
        if(!found)
        for(Jump jump : cursor.getJumps())
            if(jump.getJumpName().equals(NOT_EQUAL)){
                isNotEqual = true;
                jump(jump, unit);
            }
        if(!found ) throw new StageException(cursor, unit);

    }

    private void jump(Jump jump, OutputUnit unit) throws StageException {
        madeJump(jump, unit);
        found = true;
    }

    private void madeJump(Jump stageJump, OutputUnit unit) throws StageException {
        boolean foundStage = false;
        Jump jump = new Jump(stageJump.getJumpName(), stageJump.getJumpStage(), stageJump.getJumpStack());
        if(jump.getJumpStage() == 0) {
            jump = getFromStack(jump);
        }
        for(Stage stage : stages){
            if(jump.getJumpStage() == stage.getCurrentStage()) {
                foundStage = true;
                if(jump.getJumpStack() != -1)
                    stack.add(jump.getJumpStack());
                cursor = stage;
                if(isNotEqual) {
                    analyze(unit);
                }
                break;
            }
        }
        if(!foundStage || jump.getJumpStage() == 0)
            isEndAnalyze = true;
        else if(!foundStage) throw new StageException("SYNTAX ERROR:\tNo suitable stage was found for jump with parameters:\t" +
                jump + " stage:\t" + cursor);
    }

    private void readStages() throws IOException {
        String line;
        while ((line = bufferedReader.readLine()) != null){
            createStageFromLine(line);
        }
  //      for(Stage s : stages)
    //        System.out.print(s + "\n");
    }

    private void createStageFromLine(String line) {
        String[] strings = line.split(JUMP_PATTERN);

        int stageNumber = Integer.parseInt(strings[0]);
        ArrayList<Jump> jumps = createJumps(new ArrayList<>(Arrays.asList(strings)));
        stages.add(new Stage(stageNumber,jumps));
    }

    private ArrayList<Jump> createJumps(ArrayList<String> strings) {
        strings.removeIf(item -> item == null || "".equals(item));
        ArrayList<Jump> jumps = new ArrayList<>();
        for(int i = 1; i < strings.size() - 1; i++){
            String jumpWord = strings.get(i).split("'")[1];
            int jumpNumber = Integer.parseInt(strings.get(i).split("[\\s]")[2]);
            int stackNumber = -1;
            if(strings.get(i).split("[\\s]").length > 3)
                stackNumber = Integer.parseInt(strings.get(i).split("[\\s]")[5]);
            jumps.add(new Jump(jumpWord,jumpNumber,stackNumber));
        }
        return jumps;
    }

    public Jump getFromStack(Jump jump) {
        if(stack.size() > 0) {
            jump.setJumpStage(stack.get(stack.size() - 1));
            stack.remove(stack.get(stack.size() - 1));
        }
        return jump;
    }
}
