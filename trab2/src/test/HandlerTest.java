import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import resources.Handler;
import resources.Measurement;
import resources.ThreadController;

/**
 * teste dos métodos da classe Handler
 */
public class HandlerTest {

    /**
     * teste do método readMeasurement
     */
    @Test
    public void readMeasurementTest(){
        Measurement[] buffer = new Measurement[5];
        Handler handler = new Handler(1, buffer, new ThreadController());

        Assertions.assertNull(handler.readMeasurement());

        buffer[0] = new Measurement(1,2,27);
        buffer[1] = new Measurement(1,1,29);

        Assertions.assertEquals(buffer[1].getValue(), handler.readMeasurement().getValue());

        buffer[2] = new Measurement(2,2,25);
        buffer[3] = new Measurement(2,1,27);

        Assertions.assertEquals(buffer[3].getValue(), handler.readMeasurement().getValue());
    }

    /**
     *teste do método checkStatus
     */
    @Test
    public void checkStatusTest(){
        Measurement[] buffer = new Measurement[15];
        Handler handler = new Handler(1, buffer, new ThreadController());
        int measurement_count = 1;

        Assertions.assertEquals(0, handler.checkStatus());

        for (int i=0; i<15; i++) {
            buffer[i] = new Measurement(measurement_count, 1, 27);
            measurement_count++;
            handler.readMeasurement();
        }

        Assertions.assertEquals(0, handler.checkStatus());

        for (int i=0; i<15; i++) {
            if (i<4 || i==10) buffer[i] = new Measurement(measurement_count, 1, 40);
            else buffer[i] = new Measurement(measurement_count, 1, 27);
            measurement_count++;
            handler.readMeasurement();
        }

        Assertions.assertEquals(1, handler.checkStatus());

        for (int i=0; i<5; i++) {
            buffer[i] = new Measurement(measurement_count, 1, 40);
            measurement_count++;
            handler.readMeasurement();
        }

        Assertions.assertEquals(2, handler.checkStatus());

        for (int i=0; i<12; i++) {
            if (i>=7 && i<=11) buffer[i] = new Measurement(measurement_count, 1, 40);
            else buffer[i] = new Measurement(measurement_count, 1, 27);
            measurement_count++;
            handler.readMeasurement();
        }
        Assertions.assertEquals(2, handler.checkStatus());
    }

    /**
     * teste do método showAverageTemperature
     */
    @Test
    public void showAverageTemperatureTest(){
        Measurement[] buffer = new Measurement[15];
        Handler handler = new Handler(1, buffer, new ThreadController());
        double avg_value = 0;

        Assertions.assertEquals(0, handler.showAverageTemperature());

        for (int i=0; i<15; i++) {
            double measurement_value = 27+(i+1);
            buffer[i] = new Measurement(i+1, 1, measurement_value);
            avg_value += measurement_value;
            handler.readMeasurement();
        }

        avg_value = avg_value/15;
        Assertions.assertEquals(avg_value, handler.showAverageTemperature());
    }

}
