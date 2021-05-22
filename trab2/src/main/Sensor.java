import java.util.Random;


public class Sensor implements Runnable {
    private final int id;
    private int n_measurements;
    private final Random rand;
    private Measurement[] buffer;
    private final ThreadController controller;
    private static int buffer_position = 0;

    public Sensor(int id, Measurement[] buffer, ThreadController controller){
        this.id = id;
        this.n_measurements = 0;
        rand = new Random();
        this.buffer = buffer;
        this.controller = controller;
    }

    public Measurement getMeasurement(){
        this.n_measurements += 1;
        return new Measurement(this.n_measurements, this.id, 25+(15*rand.nextDouble()));
    }

    public void storeMeasurement(Measurement measurement){
        this.buffer[Sensor.buffer_position] = measurement;
        Sensor.buffer_position += 1;
    }
    
    public void run() {
        while (true){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {}

            Measurement measurement = getMeasurement();

            this.controller.enterWriting();
            storeMeasurement(measurement);
            this.controller.exitWriting();

            //TODO remove condition when finished testing
            if(this.n_measurements == 10) {break;}
        }
        
    }
    
}
