import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class HandlerTest {

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

        for (int i=0; i<15; i++) {
            if (i<5) buffer[i] = new Measurement(measurement_count, 1, 40);
            else buffer[i] = new Measurement(measurement_count, 1, 27);
            measurement_count++;
            handler.readMeasurement();
        }

        Assertions.assertEquals(2, handler.checkStatus());
    }

    @Test
    public void showTemperatureTest(){
        Measurement[] buffer = new Measurement[15];
        Handler handler = new Handler(1, buffer, new ThreadController());
        double avg_value = 0;

        Assertions.assertEquals(0, handler.showTemperature());

        for (int i=0; i<15; i++) {
            double measurement_value = 27+(i+1);
            buffer[i] = new Measurement(i+1, 1, measurement_value);
            avg_value += measurement_value;
            handler.readMeasurement();
        }

        avg_value = avg_value/15;
        Assertions.assertEquals(avg_value, handler.showTemperature());
    }

    @Test
    public void runTest(){
        Measurement[] buffer = new Measurement[30];
        int n_measurements1 = 1;
        int n_measurements2 = 1;
        ThreadController controller = new ThreadController();
        double handler1_sum = 0;
        double handler2_sum = 0;

        Handler handler1 = new Handler(1, buffer, controller);
        Handler handler2 = new Handler(2, buffer, controller);

        for (int i=0; i<30; i++){
            if (i%2==0){
                buffer[i] = new Measurement(n_measurements2, 2, 30+i);
                handler2_sum += 30+i;
                n_measurements2++;
            }
            else {
                buffer[i] = new Measurement(n_measurements1, 1, 40+i);
                handler1_sum += 40+i;
                n_measurements1++;
            }
        }

        Assertions.assertEquals(handler1_sum/15, handler1.showTemperature());
        Assertions.assertEquals(handler2_sum/15, handler2.showTemperature());
    }

}
