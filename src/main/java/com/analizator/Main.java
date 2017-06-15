package com.analizator;

import org.springframework.context.support.GenericXmlApplicationContext;

/**
 * Created by alexandr on 5/9/17.
 */
public class Main {
    private static final String PATH = "/home/alexandr/Documents/KpiTEFstudy/Web/Analizer_Production/src/main/resources/program.txt";
    public static void main(String[] args){
        GenericXmlApplicationContext cgm = new GenericXmlApplicationContext();
        cgm.load("classpath:spring-config.xml");
        cgm.refresh();
        Analyzer analyzer = (Analyzer)cgm.getBean("analyzer");
        analyzer.setPathToFile(PATH);
        analyzer.analise();
    }
}
