#include<stdio.h>
#include<stdlib.h>
#include<pthread.h>
#include<math.h>
#include "timer.h"



double sequential_pi(int number_of_steps){
    double pi = 0;
    for(int i; i<number_of_steps; i++){
        if(i%2 == 0) pi += 1.0/(2.0*i+1.0);
        else pi -= 1.0/(2.0*i+1.0);
    }
    return 4*pi;
}

int main(int argc, char *argv[]){
    if(argc<2){
        fprintf(stderr, "Digite: %s <numero_de_iterações>\n", argv[0]);
        return 1;
    }

    int number_of_steps = atoi(argv[1]);
    double pi = sequential_pi(number_of_steps);
    
    printf("sequential pi = %.15lf\n", pi);
    printf("reference pi =  %.15lf\n", M_PI);

    return 0;
}