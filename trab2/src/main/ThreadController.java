public class ThreadController {
    
    private int n_reading;
    private int n_writers_waiting;
    private boolean writing;
    
    public ThreadController(){
        this.n_reading = 0;
        n_writers_waiting = 0;
        this.writing = false;
    }

    public synchronized void enterReading(){
        try{
            while(this.n_writers_waiting>0 || this.writing) {wait();}
        } catch(InterruptedException e) {}
        this.n_reading += 1;
    }

    public synchronized void exitReading(){
        this.n_reading -= 1;
        if(this.n_reading == 0){notifyAll();}
    }

    public synchronized void enterWriting(){
        try{
            while(this.n_reading>0 || this.writing){
                n_writers_waiting += 1;
                wait();
            }
        } catch(InterruptedException e) {}
        writing = true;
        n_writers_waiting -= 1;
    }

    public synchronized void exitWriting(){
        writing = false;
        notifyAll();
    }
}
