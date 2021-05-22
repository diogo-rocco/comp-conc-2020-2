public class Handler implements Runnable{

    private final int sensor_id;
    private Measurement[] buffer;
    private final int buffer_size;
    private int buffer_position;
    private Measurement[] measurements_cash;
    private int cash_position;
    private ThreadController controller;
    private int currentMeasurementId;

    public Handler(int sensor_id, Measurement[] buffer, ThreadController controller){
        this.sensor_id = sensor_id;
        this.buffer = buffer;
        this.buffer_size = buffer.length;
        this.buffer_position = 0;
        this.measurements_cash = new Measurement[15];
        this.cash_position = 0;
        this.controller = controller;
        this.currentMeasurementId = 1;
    }

    public Measurement readMeasurement(){

        for (int i=0; i<this.buffer_size; i++){
            if (this.buffer[this.buffer_position] == null) {
                this.buffer_position = (this.buffer_position + 1) % this.buffer_size;
                continue;
            }

            if(this.buffer[this.buffer_position].getSensor_id() == this.sensor_id){
                Measurement measurement = this.buffer[this.buffer_position];
                if (this.currentMeasurementId==measurement.getMeasurement_id()){
                    this.measurements_cash[this.cash_position] = measurement;
                    this.cash_position = (this.cash_position+1)%15;
                    this.currentMeasurementId++;
                    return measurement;
                }
            }
            this.buffer_position = (this.buffer_position+1)%this.buffer_size;
        }
        return null;
    }

    public int checkStatus(){
        int countTemperatureAboveMax = 0;
        for (int i = 0; i<15; i++){
            int current_position = (this.cash_position+i)%15;
            if (this.measurements_cash[current_position] == null)
                continue;
            if (this.measurements_cash[current_position].getValue()>35) countTemperatureAboveMax ++;

            if(i==4 && countTemperatureAboveMax==5){
                System.out.printf("ALARME DE INCENDIO NO SENSOR %d\n", this.sensor_id);
                return 2;
            }

            if (countTemperatureAboveMax==5){
                System.out.printf("Alerta amarelo no sensor %d\n", this.sensor_id);
                return 1;
            }
        }
        System.out.printf("Tudo normal no sensor %d\n", this.sensor_id);
        return 0;
    }

    public double showTemperature(){
        double temperatureSum = 0;
        int temperatureCount = 0;
        for(int i=0; i<this.buffer_size; i++) {
            if (this.buffer[i] == null) continue;
            if (this.buffer[i].getSensor_id() == this.sensor_id) {
                temperatureSum += this.buffer[i].getValue();
                temperatureCount += 1;
            }
        }
        double average_temperature;
        if(temperatureCount == 0) average_temperature = 0;
        else average_temperature = temperatureSum/temperatureCount;

        System.out.printf("A temperatura atual no sensor %d é %f\n", this.sensor_id, average_temperature);
        return average_temperature;
    }

    public void run() {
        while (true){
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {}

            controller.enterReading();
            readMeasurement();
            controller.exitReading();
            checkStatus();
            showTemperature();

            //TODO remove condition when finished testing
            if(this.currentMeasurementId == 10) {break;}
        }
    }
}
