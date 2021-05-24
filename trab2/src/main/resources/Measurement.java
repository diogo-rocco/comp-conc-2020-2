package resources;

/**
 * Clase feita para funcionar como uma estrutura de dados para armazenar todas as informações de uma medição
 */
public class Measurement {
    private final int measurement_id;
    private final int sensor_id;
    private final double measurement_value;

    /**
     * construtor de um measurement
     * @param measurement_id -> id da medição (gerado sequencialmente pelos sensores)
     * @param sensor_id -> id do sensor que fez a medição
     * @param value -> valor da medição
     */
    public Measurement(int measurement_id, int sensor_id, double value){
        this.measurement_id = measurement_id;
        this.sensor_id = sensor_id;
        this.measurement_value = value;
    }

    public int getSensor_id() {
        return this.sensor_id;
    }

    public int getMeasurement_id(){
        return this.measurement_id;
    }

    public double getValue(){
        return this.measurement_value;
    }
}
