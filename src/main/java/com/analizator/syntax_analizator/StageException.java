package com.analizator.syntax_analizator;

import com.analizator.lexycal_analizator.entity.OutputUnit;

/**
 * Created by alexandr on 5/20/17.
 */
public class StageException extends Exception {
    public StageException(String msg){
        super(msg);
    }
    public StageException(Stage stage, OutputUnit unit){
        super(prepareMessage(stage, unit));
    }

    private static String prepareMessage(Stage stage, OutputUnit unit) {
        String msg = "SYNTAX ERROR:\tExpected: ";
        for(Jump jump : stage.getJumps())
            msg += "'" + jump.getJumpName() + "', ";
        msg += " found: '" + unit.getLexeme().getName() +
                "' on the line: " + unit.getLineNumber();
        return msg;
    }
}
