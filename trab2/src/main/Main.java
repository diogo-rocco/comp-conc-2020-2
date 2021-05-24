import resources.Handler;
import resources.Measurement;
import resources.Sensor;
import resources.ThreadController;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        int n_sensors;
        final Scanner scanner = new Scanner(System.in);
        final int buffer_size = 60;

        while (true){
            System.out.println("Insira a quantidade de sensores");
            n_sensors = scanner.nextInt();

            if(n_sensors <= 0) System.out.println("A quantidade de sensores deve ser maior que 0");
            else break;
        }

        Measurement[] buffer = new Measurement[buffer_size];
        ThreadController controller = new ThreadController();
        Thread[] sensors = new Thread[n_sensors];
        Thread[] handlers = new Thread[n_sensors];

        for (int i=0; i<n_sensors; i++){
            sensors[i] = new Thread(new Sensor(i+1, buffer, controller));
            handlers[i] = new Thread(new Handler(i+1, buffer, controller));
        }

        for (int i=0; i<n_sensors; i++){
            sensors[i].start();
            handlers[i].start();
        }

        for (int i=0; i<n_sensors; i++){
            try {
                sensors[i].join();
            } catch (InterruptedException e) {e.printStackTrace();}
            try {
                handlers[i].join();
            } catch (InterruptedException e) {e.printStackTrace();}
        }

        System.out.println("Fim da Execução");
    }
}
