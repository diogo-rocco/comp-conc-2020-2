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

    /**
     * testa a condição de que somente um escritor pode escrever por vez na região compartilhada de memória. Para isso
     * ela inicia 10 escritores e todos eles vão escrever num buffer de tamanho 10000. O valor que eles vão armazenar
     * no buffer deveria ser igual o valor da posição que eles estão acessando, pois a classe TestWriter possui um
     * contador estático. Caso cada elemento do buffer seja exatamente igual a posição do mesmo, isso significa que não
     * houve mais de um escritor escrevendo na mesma região do buffer
     */
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

    /**
     * testa a condição de que leitores podem ler simultaneamente. Para isso inicializamos o buffer com 10000 onde em
     * cada posição está armazenado o valor daquela posição, além disso criamos 10 threads leitoras que vão percorrer
     * o buffer e guardar seus valores num vetor de outputs. Caso ao final da execução todas as posições de todos os
     * vetores de outputs estejam iguais às do buffer, isso quer dizer que todas as threads leitoras executaram juntas.
     */
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

    /**
     * Testa a condição de que uma thread leitora não pode executar enquanto uma thread escritora estiver sendo
     * executada. Para isso, criamos tres threads: escritor, leitor1 e leitor2. escritor vai pegar a primeira posição
     * do buffer e somar 1 à ela 1000000. leitor1 e leitor2 vão ler esse valor e armazena-lo ele em seus respectivos
     * outputs. A diferença é que o leitor2 realiza a rotina de entrar e sair da leitura, enquanto o leitor1 não.
     * Sendo assim se ao final da execução o leitor2 tiver o valor correto do output e o leitor1 não, isso quer dizer
     * que o leitor2 esperou o escritor terminar de escrever para acessar a posição de memória, enquanto o leitor1 não
     */
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

    /**
     * Testa a condição de que uma thread escritora espera somente as leitoras que já estão executando terminarem para
     * que ela possa ser executdda, ou seja, testa a prioridade da leitura. Para isso, iniciamos 5 threads leitoras que
     * vão começar sua execução antes da escritora, 5 que vão entrar após a escritora e a próproa thread escritora.
     * As leitoras que entram antes entram na fila de leitura e dão um sleep() para garantir que a escritoro também
     * entrou. A escritora entra depois dessas 5 primeiras leitoras e dá um sleep para garantir que as ultimas leitoras
     * também entraram. As ultimas leitoras entram e executam direto. Estando então numa situação onde temos uma fila
     * com 5 leitoras, 1 escritora e 5 leitoras. As 5 primeiras e as 5 ultimas leitoras escrevem seus ids nas 5
     * primeiras posições do buffer a escritora lê as 5 primeiras posições do buffer e escreve elas nas posições 100 à
     * 104. Se ao final da execução o valor nessas posições for o dos ids das 5 primeiras leitoras a condição é atendida
     */
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
