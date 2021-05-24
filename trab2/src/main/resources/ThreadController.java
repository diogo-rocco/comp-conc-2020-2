package resources;

/**
 * Classe responsavel pelo controle do fluxo de execução das thread e de atender aos requisitos do problema dos
 * leitores e escritores com prioridade de escrita
 */
public class ThreadController {

    /**
     * n_reading -> numero de threads lendo atualemte
     * n_writers_waiting -> numero escritoras esperando para executar
     * writing -> booleano que diz se tem alguma thread escritora escrevendo atualmente
     */
    private int n_reading;
    private int n_writers_waiting;
    private boolean writing;

    /**
     * construtor de ThreadController
     */
    public ThreadController(){
        this.n_reading = 0;
        n_writers_waiting = 0;
        this.writing = false;
    }

    /**
     * Entrada da leitura. Confere se tem alguma thread esperando para escrever ou se tem alguma thread escrevendo, caso
     * sim, entra em espera e só entra na leitura quando nenhuma das condições anteriores for atendida
     */
    public synchronized void enterReading(){
        try{
            while(this.n_writers_waiting>0 || this.writing) {wait();}
        } catch(InterruptedException e) {}
        this.n_reading += 1;
    }

    /**
     * Saída da leitura. Caso seja a ultima thread leitora lendo, ela notifica todas as threads e libera as escritoras
     * para a execução
     */
    public synchronized void exitReading(){
        this.n_reading -= 1;
        if(this.n_reading == 0){notifyAll();}
    }

    /**
     * Entrada da escrita. Sinaliza que existe uma escritora aguardando para escrever, depois erifica se existe alguma
     * thread lendo ou se há alguma thread escrevendo. Caso haja, entra em espera. Caso contrário, sinaliza que há
     * uma escritora escrevendo e retira o sinal de que há uma escritora na fila para escrever
     */
    public synchronized void enterWriting(){
        n_writers_waiting += 1;
        try{
            while(this.n_reading>0 || this.writing){
                wait();
            }
        } catch(InterruptedException e) {}
        writing = true;
        n_writers_waiting -= 1;
    }

    /**
     * Saída da escrita. Informa que não há mais uma escritora escrevendo e sinaliza para todas as threads.
     */
    public synchronized void exitWriting(){
        writing = false;
        notifyAll();
    }
}
