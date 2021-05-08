package atividade5;

import java.util.Random;

public class Incrementator {
    int array_size;
    int[] array;
    int[] control_array;
    private final Random random = new Random();

    public Incrementator(int array_size){
        this.array_size = array_size;
        this.array = new int[array_size];
        this.control_array = new int[array_size];
        
        for(int i=0; i<this.array_size; i++) {
            this.array[i] = random.nextInt(this.array_size);
            this.control_array[i] = array[i];
        }
    }

    public void increment(int position){
        this.array[position] += 1;
    }

    public int getSize(){
        return this.array_size;
    }

    public boolean validate(){
        for(int i=0; i<this.array_size; i++)
            if(this.array[i] != this.control_array[i]+1) return false;
        
        return true;
    }

    public void print_array(){
        for(int i=0; i<array_size; i++)
            System.out.printf("%d ", this.array[i]);
        System.out.printf("\n");
    }
}
