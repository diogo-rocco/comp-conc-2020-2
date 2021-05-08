package atividade_5.src;

import java.util.Scanner;

public class Main {
    //static final int array_size = 20;
    //static final int n_threads = 10;
    static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws Exception {
        int array_size;
        int n_threads;

        while(true){
            System.out.println("Insira o tamanho do array");
            array_size = scanner.nextInt();

            System.out.println("Insira a quantidade de threads");
            n_threads = scanner.nextInt();

            if(n_threads > array_size) System.out.println("A quantidade de threads deve ser menor ou igual ao tamanho do array");
            else break;
        }
        
        Incrementator incrementator = new Incrementator(array_size);
        incrementator.print_array();
        Thread[] threads = new Thread[n_threads];

        for(int i=0; i<n_threads; i++)
            threads[i] = new Thread(new IncrementatorThread(i, n_threads, incrementator));

        for(int i=0; i<n_threads; i++){
            threads[i].start();
        }

        for(int i=0; i<n_threads; i++){
            try{
            threads[i].join();
            } catch(InterruptedException e) {return;}
        }

        incrementator.print_array();
        if(incrementator.validate()) System.out.println("Tudo Certo");
        else System.out.println("Errado");
    }
}
