package testResources;
import resources.ThreadController;

import static java.lang.Thread.sleep;

public class TestWriter implements Runnable{

    private int id;
    private int[] buffer;
    private int[] execution_order;
    ThreadController controller;
    private String testType;
    private static int counter = 0;

    public TestWriter(int id, int[] buffer, int[] execution_order, ThreadController controller, String testType){
        this.id = id;
        this.buffer = buffer;
        this.execution_order = execution_order;
        this.controller = controller;
        this.testType = testType;
    }

    public static void resetCounter(){
        TestWriter.counter=0;
    }

    /**
     * escreve o valor do contador na posição correspondente do buffer
     */
    public synchronized void write(){
        if (TestWriter.counter<this.buffer.length) {
            this.buffer[TestWriter.counter] = this.id;
            this.execution_order[TestWriter.counter] = TestWriter.counter;
            TestWriter.counter++;
        }
    }

    /**
     * itera no buffer chamando a função de escrita
     */
    public void oneWriterAtATimeTestMethod(){
        while (TestWriter.counter<this.buffer.length) {
            controller.enterWriting();
            write();
            controller.exitWriting();
        }
    }

    /**
     * soma 1000000 à posição 0 do buffer
     */
    public void noReadingWhileWritingTestMethod(){
        controller.enterWriting();
        for (int i=0; i<=1000000; i++){
            buffer[0] = i;
        }
        controller.exitWriting();
    }

    /**
     * aguarda para entrar na fila de execução e leva as 5 primeiras posições do buffer para as posições de 100 à 104
     */
    public void writerMustWaitOnlyRunningReadersTestMethod(){
        try {
            sleep(1000);
        } catch (InterruptedException e) {e.printStackTrace();}

        controller.enterWriting();
        for (int i=0; i<5; i++)
            buffer[100+i] = buffer[i];
        controller.exitWriting();
    }

    /**
     * chama as funções de teste baseado no código fornecido na instanciação da classe
     */
    public void run() {
        switch (testType){
            case "one-writer-at-a-time":
                oneWriterAtATimeTestMethod();
                break;
            case "no-reading-while-writing":
                noReadingWhileWritingTestMethod();
                break;
            case "writer-must-wait-running-readers":
                writerMustWaitOnlyRunningReadersTestMethod();
                break;

        }
    }
}
