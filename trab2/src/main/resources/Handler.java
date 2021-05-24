package resources;

public class Handler implements Runnable{

    /**
     * sensor_id -> id do sensor atrelado à esse handler
     * buffer -> buffer onde os sensores vão armazear as medições
     * buffer_position -> a posição no buffer em que o handler deve ler agora
     * measurements_cash -> array de medições que fizam armazenadas para fazer avaliações de estado
     * cash_position -> a posição atual no measurements_cash
     * controller -> instância de ThreadController que vai gerenciar o fluxo de execução
     * currentMeasurementId -> valor da medição que deveria ser realizada da proxima vez que o handler buscasse um valor no buffer
     */
    private final int sensor_id;
    private Measurement[] buffer;
    private int buffer_position;
    private Measurement[] measurements_cash;
    private int cash_position;
    private ThreadController controller;
    private int currentMeasurementId;

    /**
     * Construtor de Handler
     * @param sensor_id -> id do sensor atrelado à esse handler
     * @param buffer -> buffer onde os sensores vão armazear as medições (deve ser o mesmo para todos os handlers)
     * @param controller -> nstância de ThreadController que vai gerenciar o fluxo de execução (deve ser a mesma para todos os handlers)
     */
    public Handler(int sensor_id, Measurement[] buffer, ThreadController controller){
        this.sensor_id = sensor_id;
        this.buffer = buffer;
        this.buffer_position = 0;
        this.measurements_cash = new Measurement[15];
        this.cash_position = 0;
        this.controller = controller;
        this.currentMeasurementId = 1;
    }

    /**
     * responsavel por percorrer o buffer e ler uma medição do buffer
     * @return a medição que foi lida
     */
    public Measurement readMeasurement(){
        for (int i=0; i<this.buffer.length; i++){

            //convere se a posição do buffer que está sendo vista é nula
            if (this.buffer[this.buffer_position] == null) {
                this.buffer_position = (this.buffer_position + 1) % this.buffer.length;
                continue;
            }

            //confere se a medição que está sendo vista agora pertence ao sensor desse handler
            if(this.buffer[this.buffer_position].getSensor_id() == this.sensor_id){
                Measurement measurement = this.buffer[this.buffer_position];

                //confere se a medição que está sendo vista agora é mais recente que a ultima medição lida
                if (this.currentMeasurementId<=measurement.getMeasurement_id()){
                    this.buffer_position = (this.buffer_position+1)%this.buffer.length;
                    this.measurements_cash[this.cash_position] = measurement;
                    this.cash_position = (this.cash_position+1)%15;
                    this.currentMeasurementId++;
                    return measurement;
                }
            }
            this.buffer_position = (this.buffer_position+1)%this.buffer.length;
        }
        return null;
    }

    /**
     * verifica os valores measurements_cash e emite sinais de alerta conforme os requisitos do problema
     * @return 0 (tudo normal), 1 (alerta amarelo) ou 3 (aciona o alarme de incendio)
     */
    public int checkStatus(){
        int countTemperatureAboveMax = 0;
        int countTemperatures = 0;

        //percorre a cache
        for (int i = 1; i<=15; i++){
            countTemperatures++; //variavel que armazena quantas temperaturas já foram lidas
            int current_position = (this.cash_position+(15-i))%15; //posição atual na cache (temos que olhar a cache de trás para frente)
            if (this.measurements_cash[current_position] == null)
                continue;

            //confere se a temeperatura está acima do máximo permitido
            if (this.measurements_cash[current_position].getValue()>35) countTemperatureAboveMax ++;

            //aciona o alarme caso as cinco medições mais recentes na cache (countTemperatures=5) sejam maior que o limite máximo
            if(countTemperatures==5 && countTemperatureAboveMax==5){
                System.out.printf("ALARME DE INCENDIO NO SENSOR %d\n", this.sensor_id);
                return 2;
            }

            //aciona o alerta amarelo caso pelo menos 5 temperaturas na cache estejam acima do limite máximo
            if (countTemperatureAboveMax==5){
                System.out.printf("Alerta amarelo no sensor %d\n", this.sensor_id);
                return 1;
            }
        }
        //System.out.printf("Tudo normal no sensor %d\n", this.sensor_id);
        return 0;
    }

    /**
     * mostra a temperatura das ultimas 15 medições
     * @return a temperatura média
     */
    public double showAverageTemperature(){
        double temperatureSum = 0;
        int temperatureCount = 0;
        for(int i=0; i<this.buffer.length; i++) {
            if (this.buffer[i] == null) continue; //confere se o valor lido no momento é nulo

            //coma os valores não nulos
            if (this.buffer[i].getSensor_id() == this.sensor_id) {
                temperatureSum += this.buffer[i].getValue();
                temperatureCount += 1;
            }
        }
        double average_temperature;
        if(temperatureCount == 0) average_temperature = 0;//tratamento do caso onde não há nenhuma medição na cache
        else average_temperature = temperatureSum/temperatureCount;

        System.out.printf("A temperatura média atual no sensor %d é %f\n", this.sensor_id, average_temperature);
        return average_temperature;
    }

    /**
     * Função que será executada quando dermos start() na instancia do handler
     * 1) Chama o método enterWriting para entrar na fila para leitura
     * 2) Chama o método readMeasurement para começar a leitura
     * 3) Chama o método exitReading para sair da fila de leitura
     * 4) Mostra a temperatura média
     * 5) Verifica a necessidade de mostrar algum alarme
     */
    public void run() {
        while (true){
            controller.enterReading();
            readMeasurement();
            controller.exitReading();
            showAverageTemperature();
            checkStatus();

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {}
        }
    }
}
