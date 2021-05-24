import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import resources.ThreadController;
import testResources.TestReader;
import testResources.TestWriter;

public class ThreadControllerTest {

    int buffer_size;
    int n_threads;
    int[] buffer;
    int[] execution_order;
    Thread[] threads;
    ThreadController controller;

    @BeforeEach
    public void setUp(){
        buffer_size = 10000;
        n_threads = 10;
        buffer = new int[buffer_size];
        execution_order = new int[buffer_size];
        threads = new Thread[n_threads];
        controller = new ThreadController();
        TestWriter.resetCounter();
    }

    @Test
    public void oneWriterAtATimeTest(){
        for (int i=0; i<n_threads; i++)
            threads[i] = new Thread(new TestWriter(i+1, buffer, execution_order, controller, "one-writer-at-a-time"));

        for (int i=0; i<n_threads; i++)
            threads[i].start();

        for (int i=0; i<n_threads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {e.printStackTrace();}
        }

        for (int i=0; i<buffer_size; i++){
            Assertions.assertEquals(i, execution_order[i]);
        }
    }

    @Test void simultaneousReadingTest(){
        int[][] outputs = new int[n_threads][buffer_size];

        for (int i=0; i<buffer_size; i++)
            buffer[i] = i;

        for (int i=0; i<n_threads; i++)
            threads[i] = new Thread(new TestReader(i, buffer, outputs[i], controller, "simultaneous-reading"));

        for (int i=0; i<n_threads; i++)
            threads[i].start();

        for (int i=0; i<n_threads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {e.printStackTrace();}
        }

        for (int i=0; i<n_threads; i++)
            for (int j=0; j<buffer_size; j++)
                Assertions.assertEquals(j, outputs[i][j]);
    }

    @Test
    public void noReadingWhileWritingTest(){
        int[] output1 = new int[1];
        int[] output2 = new int[1];

        Thread escritor = new Thread(new TestWriter(1, buffer, null, controller, "no-reading-while-writing"));
        Thread leitor1 = new Thread(new TestReader(1, buffer, output1, controller, "no-reading-while-writing-fail"));
        Thread leitor2 = new Thread(new TestReader(1, buffer, output2, controller, "no-reading-while-writing-pass"));

        escritor.start();
        leitor1.start();
        leitor2.start();

        try {
            escritor.join();
            leitor1.join();
            leitor2.join();
        } catch (InterruptedException e) {e.printStackTrace();}

        Assertions.assertNotEquals(1000000, output1[0]);
        Assertions.assertEquals(1000000, output2[0]);

    }

    @Test
    public void writerMustWaitOnlyRunningReadersTest(){
        Thread[] first_readers = new Thread[5];
        Thread writer = new Thread(new TestWriter(1, buffer, null, controller, "writer-must-wait-running-readers"));
        Thread[] second_readers = new Thread[5];

        for (int i=0; i<5; i++){
            first_readers[i] = new Thread(new TestReader(i, buffer, null, controller, "writer-must-wait-running-readers-first"));
            second_readers[i] = new Thread(new TestReader(10+i, buffer, null, controller, "writer-must-wait-running-readers-second"));
        }

        for (int i=0; i<5; i++){
            first_readers[i].start();
        }
        writer.start();
        for (int i=0; i<5; i++){
            second_readers[i].start();
        }

        try {
            writer.join();
            for (int i=0; i<5; i++){
                first_readers[i].join();
                second_readers[i].join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i=0; i<5; i++)
            Assertions.assertEquals(i, buffer[100+i]);

    }

}
