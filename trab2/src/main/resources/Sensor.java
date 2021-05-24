package resources;
import java.util.Random;

/**
 * A Classe Sensor é a responsavel por controlar as threads correspondentes aos sensores da aplicação.
 */
public class Sensor implements Runnable {
    /**
     * id-> id do sensor
     * n_measurements -> quantidade de medições que já foram feitas
     * rand -> objeto que vai gerar valores aleatorios para as medições
     * buffer -> buffer onde os sensores vão armazear as medições
     * controller -> instância de ThreadController que vai gerenciar o fluxo de execução
     * buffer_position -> varaivel estática (compartilhada por todas instâncias de Sensor que vai armazenar a posição atual que deve ser escrita no buffer
     */
    private final int id;
    private int n_measurements;
    private final Random rand;
    private Measurement[] buffer;
    private final ThreadController controller;
    private static int buffer_position = 0;

    /**
     * Construtor de Sensor
     * @param id -> id do sensor
     * @param buffer -> buffer onde os sensores vão armazear as medições (deve ser o mesmo para todos os sensores)
     * @param controller -> instância de ThreadController que vai gerenciar o fluxo de execução (deve ser a mesma para todos os sensores)
     */
    public Sensor(int id, Measurement[] buffer, ThreadController controller){
        this.id = id;
        this.n_measurements = 0;
        rand = new Random();
        this.buffer = buffer;
        this.controller = controller;
    }

    /**
     * Gera um valor de medição aleatória entre 25 e 40 para os sensores guardarem no buffer,
     * os ids da medição são sequenciais e obtidos com base na variavel n_measurements e o id do sensor é sempre o mesmo
     * @return -> o valor de medição que foi gerado
     */
    public Measurement getMeasurement(){
        this.n_measurements += 1;
        return new Measurement(this.n_measurements, this.id, 25+(15*rand.nextDouble()));
    }

    /**
     * Armazena o valor de medição que foi gerado
     * @param measurement -> valor de medição que será armazenada
     */
    public void storeMeasurement(Measurement measurement){
        this.buffer[Sensor.buffer_position] = measurement;
        Sensor.buffer_position = (Sensor.buffer_position+1)%this.buffer.length;
    }

    /**
     * Funcão que será executada quando dermos start() na instancia do sensor.
     * 1) Ela começa gerando uma medição aleatória
     * 2) Depois chama o método enterWriting para entrar na fila de escrita
     * 3) Armazena o valor de medição que foi gerado
     * 4) Chama exitWriting para sair da fila de escrita
     * 5) Faz um sleep de 1 segundo
     */
    public void run() {
        while (true){
            Measurement measurement = getMeasurement();
            this.controller.enterWriting();
            storeMeasurement(measurement);
            this.controller.exitWriting();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {}
        }
        
    }
    
}
