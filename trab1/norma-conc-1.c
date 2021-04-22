#include<stdio.h>
#include<pthread.h>
#include<stdlib.h>
#include<math.h>
#include "utils.h"
#include "timer.h"

int matrix_dimension;
double *input_matrix;
double *normalized_matrix;
int n_threads;

void *normalize_arrays(void *arg){
    int id = *(int *) arg;
    int block_size = matrix_dimension/n_threads;
    int start_position = id*block_size;
    int end_position;
    
    if (id == n_threads-1) end_position = matrix_dimension;
    else end_position = start_position + block_size;

    for (int j = start_position; j < end_position; j++){
        double norm = 0;
        for (int i = 0; i < matrix_dimension; i++){
            double element = input_matrix[i*matrix_dimension + j];
            norm += element*element; 
        }

        norm = sqrt(norm);
        
        for (int i = 0; i < matrix_dimension; i++)
            normalized_matrix[i*matrix_dimension + j] = input_matrix[i*matrix_dimension + j]/norm;
    }
    pthread_exit(NULL);
}


int main(int argc, char* argv[]){
    double start_time, end_time;
    pthread_t *thread_id;
    int *local_ids;

    if(argc<3) {printf("Digite %s <tamanho da matriz> <numero de threads>\n", argv[0]); return 1;}
    matrix_dimension = atoi(argv[1]);
    n_threads = atoi(argv[2]);
    
    //alocando memoria para as matrizes
    GET_TIME(start_time);
    input_matrix = malloc(sizeof(double)*matrix_dimension*matrix_dimension);
    if (input_matrix == NULL){printf("ERROR--malloc input\n"); return 2;}
    normalized_matrix = malloc(sizeof(double)*matrix_dimension*matrix_dimension);
    if (normalized_matrix == NULL){printf("ERROR--malloc normalized\n"); return 2;}
    GET_TIME(end_time);
    initialize_matrix(input_matrix, matrix_dimension);
    printf("allocating time = %lf\n", end_time-start_time);
    
    //normalizacao concorrente
    GET_TIME(start_time);
    thread_id = malloc(sizeof(pthread_t)*n_threads);
    if(thread_id == NULL){printf("ERROR--malloc thread_id\n"); return 2;}
    local_ids = malloc(sizeof(int)*n_threads);
    if(local_ids == NULL){printf("ERROR--malloc local_ids\n"); return 2;}
    
    for (int i = 0; i<n_threads; i++){
        local_ids[i] = i;
        if(pthread_create(&thread_id[i], NULL, normalize_arrays, &local_ids[i])){
            printf("ERROR--pthread_create[%d]", i);
            return 3;
        }
    }
    for (int i = 0; i < n_threads; i++)
    {
        if(pthread_join(thread_id[i], NULL)){
            printf("ERROR--pthread_join[%d]", i);
            return 3;
        }
    }
    
    GET_TIME(end_time);

    if(!check_correctness(normalized_matrix, matrix_dimension)){
        printf("Wrong!!\n");
        puts("");
        print_matrix(normalized_matrix, matrix_dimension);
        return 1;
    }
    printf("processing time = %lf\n", end_time-start_time);
    log_execution_time("norma-conc-1-log", end_time-start_time);
    
    free(normalized_matrix);
    free(input_matrix);
    return 0;
}