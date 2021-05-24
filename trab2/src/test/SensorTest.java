import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import resources.Measurement;
import resources.Sensor;
import resources.ThreadController;

import java.util.Random;

/**
 * testes da classe Sensor
 */
public class SensorTest {

    /**
     * testa o método getMeasurement
     */
    @Test
    public void getMeasurementTest(){
        int sensor_id = new Random().nextInt();
        Sensor sensor = new Sensor(sensor_id, new Measurement[5], new ThreadController());

        for(int i=0; i<100; i++) {
            Measurement actual = sensor.getMeasurement();
            Assertions.assertTrue(actual.getValue() >= 25 && actual.getValue() <= 40);
            Assertions.assertEquals(sensor_id, actual.getSensor_id());
            Assertions.assertEquals(i+1, actual.getMeasurement_id());
        }
    }

    /**
     * testa o método storeMeasurement
     */
    @Test
    public void storeMeasurementTest(){
        Measurement[] buffer = new Measurement[5];
        Sensor sensor = new Sensor(0, buffer, new ThreadController());

        for (int i=0; i<5; i++)
            sensor.storeMeasurement(sensor.getMeasurement());

        for (int i=0; i<5; i++)
            Assertions.assertTrue(buffer[i].getValue() >= 25 && buffer[i].getValue() <= 40);
    }

}
