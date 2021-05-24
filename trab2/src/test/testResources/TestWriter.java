package testResources;
import resources.Sensor;
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

    public synchronized void write(){
        if (TestWriter.counter<this.buffer.length) {
            this.buffer[TestWriter.counter] = this.id;
            this.execution_order[TestWriter.counter] = TestWriter.counter;
            TestWriter.counter++;
        }
    }

    public void oneWriterAtATimeTestMethod(){
        while (TestWriter.counter<this.buffer.length) {
            controller.enterWriting();
            write();
            controller.exitWriting();
        }
    }

    public void noReadingWhileWritingTestMethod(){
        controller.enterWriting();
        for (int i=0; i<=1000000; i++){
            buffer[0] = i;
        }
        controller.exitWriting();
    }

    public void writerMustWaitOnlyRunningReadersTestMethod(){
        try {
            sleep(1000);
        } catch (InterruptedException e) {e.printStackTrace();}

        controller.enterWriting();
        for (int i=0; i<5; i++)
            buffer[100+i] = buffer[i];
        controller.exitWriting();
    }

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
