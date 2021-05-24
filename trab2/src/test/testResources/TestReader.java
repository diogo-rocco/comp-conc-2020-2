package testResources;

import resources.ThreadController;

import static java.lang.Thread.sleep;

public class TestReader implements Runnable{

    private int id;
    private int[] buffer;
    ThreadController controller;
    private int[] output;
    private String testType;

    public TestReader(int id, int[] buffer, int[] output, ThreadController controller, String testType){
        this.id = id;
        this.buffer = buffer;
        this.controller = controller;
        this.output = output;
        this.testType = testType;
    }

    /**
     * copia o buffer para o output
     */
    public void simultaneousReadingTestMethod(){
        controller.enterReading();
        for (int i=0; i<this.buffer.length; i++){
            output[i] = buffer[i];
        }
        controller.exitReading();
    }

    /**
     * guarda a primeira posição do buffer na primeira posição do output (sem entrar e sair da fila de leitura)
     */
    public void noReadingWhileWritingTestFailMethod(){
        output[0] = buffer[0];
    }

    /**
     * guarda a primeira posição do buffer na primeira posição do output (entrando e saindo da fila de leitura)
     */
    public void noReadingWhileWritingTestPassMethod(){
        controller.enterReading();
        output[0] = buffer[0];
        controller.exitReading();
    }

    /**
     * entra na fila, espera 5 segundos, e guarda seu id em uma das 5 primeiras posições do buffer
     */
    public void writerMustWaitOnlyRunningReadersFirstTestMethod(){
        controller.enterReading();
        try {
            sleep(5000);
        } catch (InterruptedException e) {e.printStackTrace();}
        buffer[this.id]=this.id;
        controller.exitReading();
    }

    /**
     * guarda seu id em uma das 5 primeiras posições do buffer
     */
    public void writerMustWaitOnlyRunningReadersSecondTestMethod(){
        try {
            sleep(3000);
        } catch (InterruptedException e) {e.printStackTrace();}

        controller.enterReading();
        buffer[this.id-10]=this.id;
        controller.exitReading();
    }

    /**
     * chama as funções de teste baseado no código fornecido na instanciação da classe
     */
    public void run() {
        switch (testType){
            case "simultaneous-reading":
                simultaneousReadingTestMethod();
                break;
            case "no-reading-while-writing-fail":
                noReadingWhileWritingTestFailMethod();
                break;
            case "no-reading-while-writing-pass":
                noReadingWhileWritingTestPassMethod();
                break;
            case "writer-must-wait-running-readers-first":
                writerMustWaitOnlyRunningReadersFirstTestMethod();
                break;
            case "writer-must-wait-running-readers-second":
                writerMustWaitOnlyRunningReadersSecondTestMethod();
                break;
        }
    }
}
