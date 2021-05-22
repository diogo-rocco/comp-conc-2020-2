import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Random;

public class SensorTest {

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

    @Test
    public void storeMeasurementTest(){
        Measurement[] buffer = new Measurement[5];
        Sensor sensor = new Sensor(0, buffer, new ThreadController());

        for (int i=0; i<5; i++)
            sensor.storeMeasurement(sensor.getMeasurement());

        for (int i=0; i<5; i++)
            Assertions.assertTrue(buffer[i].getValue() >= 25 && buffer[i].getValue() <= 40);
    }

    @Test
    public void runTest(){
        int buffer_size = 20; //buffer size = (number of thread)*(max number of steps in measurements)
        Measurement[] buffer = new Measurement[20];
        ThreadController controller = new ThreadController();

        Sensor sensor_1 = new Sensor(1, buffer, controller);
        Sensor sensor_2 = new Sensor(2, buffer, controller);

        sensor_1.run();
        sensor_2.run();

        for (int i=0; i<buffer_size; i++)
            Assertions.assertTrue(buffer[i].getValue() >= 25 && buffer[i].getValue() <= 40);

    }

}
