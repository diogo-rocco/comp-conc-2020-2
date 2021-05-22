public class Measurement {
    private final int measurement_id;
    private final int sensor_id;
    private final double measurement_value;

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
