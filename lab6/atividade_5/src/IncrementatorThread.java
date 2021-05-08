package atividade_5.src;

public class IncrementatorThread implements Runnable{
    private Incrementator incrementator;
    private int id;
    private int n_threads;
    private int array_size;

    public IncrementatorThread(int id, int n_threads, Incrementator incrementator){
        this.incrementator = incrementator;
        this.id = id;
        this.n_threads = n_threads;
        this.array_size = incrementator.getSize();
    }

    public void run() {
        for(int i=id; i<array_size; i += n_threads)
            incrementator.increment(i);
    }

}
