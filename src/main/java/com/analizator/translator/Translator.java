package com.analizator.translator;

import com.analizator.translator.dijkstra.MainLogic;
import com.analizator.translator.dijkstra.PolisException;
import com.analizator.translator.executor.Executor;
import com.analizator.translator.executor.ExecutorException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Created by alexandr on 5/10/17.
 */
@Service("translator")
public class Translator {

    @Value("#{mainLogic}")private MainLogic mainLogic;
    @Value("#{executor}")private Executor executor;

    public void translate() throws PolisException,
            ExecutorException, IOException {
        mainLogic.made();
        System.out.print("\nPOLIS\n" + mainLogic);
        executor.execute();
    }

    public MainLogic getMainLogic() {
        return mainLogic;
    }
}
