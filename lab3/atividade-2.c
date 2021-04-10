#include<stdio.h>
#include<stdlib.h>
#include<pthread.h>
#include<math.h>
#include "timer.h"

int number_of_steps;
int number_of_threads;

void *concurrent_pi(void *arg){
    int id = *(int *) arg;
    double *local_sum_pi;
    local_sum_pi = (double*) malloc(sizeof(double));
    if(local_sum_pi==NULL){
        fprintf(stderr, "ERROR--malloc local_sum_pi");
        exit(1);
    }
    
    long int block_size = number_of_steps/number_of_threads;
    long int start_position = id * block_size;
    long int end_position;

    if(id == number_of_threads-1) end_position = number_of_threads;
    else end_position = start_position + block_size;
    
    for(int i = start_position; i<end_position; i++){
        if(i%2 == 0) *local_sum_pi += 1.0/(2.0*i+1.0);
        else *local_sum_pi -= 1.0/(2.0*i+1.0);
    }
    *local_sum_pi = 4*(*local_sum_pi);

    pthread_exit((void *) local_sum_pi); 
}

double sequential_pi(int number_of_steps){
    double pi = 0;
    for(int i; i<number_of_steps; i++){
        if(i%2 == 0) pi += 1.0/(2.0*i+1.0);
        else pi -= 1.0/(2.0*i+1.0);
    }
    return 4*pi;
}

int main(int argc, char *argv[]){
    if(argc<3){
        fprintf(stderr, "Digite: %s <numero_de_iterações> <numero de threads>\n", argv[0]);
        return 1;
    }

    pthread_t *thread_ids;
    number_of_steps = atoi(argv[1]);
    number_of_threads = atoi(argv[2]);
    double concurrent_sum_of_pi = 0;
    
    double *thread_return_value;
    float start_time, end_time;

    //solucao sequencial
    GET_TIME(start_time)
    double sequential_sum_of_pi = sequential_pi(number_of_steps);
    GET_TIME(end_time);
    printf("sequential time: %.6f\n", end_time-start_time);

    GET_TIME(start_time);
    //criacao das threads
    thread_ids = malloc(sizeof(pthread_t)*number_of_threads);
    if(thread_ids == NULL){
        fprintf(stderr, "ERROR--malloc thread_ids");
        return 2;
    }
    int *local_ids = malloc(sizeof(int)*number_of_threads);
    if(thread_ids == NULL){
        fprintf(stderr, "ERROR--malloc local_ids");
        return 2;
    }
    for (int i = 0; i < number_of_threads; i++){
        local_ids[i] = i;
        if( pthread_create(&thread_ids[i], NULL, concurrent_pi, (void *)&local_ids[i]) ){
            fprintf(stderr, "ERROR--pthread_create\n");
            return 3;
        }
    }

    //esperando as threads terminarem
    for (int i = 0; i < number_of_threads; i++){
        if ( pthread_join(thread_ids[i], (void **) &thread_return_value) ){
            fprintf(stderr, "ERROR--pthread_join\n");
            return 3;
        }
        concurrent_sum_of_pi += *thread_return_value;
        free(thread_return_value);
    }
    GET_TIME(end_time);
    printf("concurrent time: %.6f\n", end_time-start_time);
    
    puts("");
    printf("sequential pi = %.15lf\n", sequential_sum_of_pi);
    printf("concurrent pi = %.15lf\n", concurrent_sum_of_pi);
    printf("reference  pi = %.15lf\n", M_PI);

    puts("");
    printf("sequential - reference = %.15lf\n", sequential_sum_of_pi - M_PI);
    printf("concurrent - reference = %.15lf\n", concurrent_sum_of_pi - M_PI);

    //liberacao de memoria
    free(thread_ids);
    free(local_ids);

    return 0;
}