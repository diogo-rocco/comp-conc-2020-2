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

    public void simultaneousReadingTestMethod(){
        for (int i=0; i<this.buffer.length; i++){
            output[i] = buffer[i];
        }
    }

    public void noReadingWhileWritingTestFailMethod(){
        output[0] = buffer[0];
    }

    public void noReadingWhileWritingTestPassMethod(){
        controller.enterReading();
        output[0] = buffer[0];
        controller.exitReading();
    }

    public void writerMustWaitOnlyRunningReadersFirstTestMethod(){
        controller.enterReading();
        try {
            sleep(5000);
        } catch (InterruptedException e) {e.printStackTrace();}
        buffer[this.id]=this.id;
        controller.exitReading();
    }

    public void writerMustWaitOnlyRunningReadersSecondTestMethod(){
        try {
            sleep(3000);
        } catch (InterruptedException e) {e.printStackTrace();}

        controller.enterReading();
        buffer[this.id-10]=this.id;
        controller.exitReading();
    }

    public void run() {
        if(this.testType.equals("simultaneous-reading"))
            simultaneousReadingTestMethod();

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
