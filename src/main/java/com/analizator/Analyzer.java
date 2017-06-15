package com.analizator;

import com.analizator.lexycal_analizator.entity.LexicalError;
import com.analizator.syntax_analizator.StageException;
import com.analizator.translator.Translator;
import com.analizator.translator.dijkstra.PolisException;
import com.analizator.translator.executor.ExecutorException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Created by alexandr on 5/10/17.
 */
@Service("analyzer")
public class Analyzer {
    private static String pathToFile;
    @Value("#{lexAnalyzer}") private com.analizator.lexycal_analizator.Analyzer lexAnalyser;
    @Value("#{synAnalyzer}") private com.analizator.syntax_analizator.Analyzer synAnalyzer;
    @Value("#{translator}") private  Translator translator;

    public void analise(){
        try {
            lexAnalyser.analyze();
            synAnalyzer.analyze();
            translator.translate();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LexicalError e){
            e.printStackTrace();
        } catch (StageException e) {
            e.printStackTrace();
        } catch (PolisException e) {
            e.printStackTrace();
        } catch (ExecutorException e) {
            e.printStackTrace();
        }
    }

    public com.analizator.lexycal_analizator.Analyzer getLexAnalyser() {
        return lexAnalyser;
    }

    public com.analizator.syntax_analizator.Analyzer getSynAnalyzer() {
        return synAnalyzer;
    }

    public Translator getTranslator() {
        return translator;
    }

    public static String getPathToFile() {
        return pathToFile;
    }

    public static void setPathToFile(String pathToFile) {
        Analyzer.pathToFile = pathToFile;
    }

}
