#include<stdio.h>
#include<pthread.h>
#include<stdlib.h>
#include<math.h>
#include "utils.h"
#include "timer.h"

int matrix_dimension;
double *input_matrix;
double *normalized_matrix;

void normalize_arrays(double* input_matrix, double* work_matrix){
    for (int j = 0; j < matrix_dimension; j++){
        double norm = 0;
        for (int i = 0; i < matrix_dimension; i++){
            double element = input_matrix[i*matrix_dimension + j];
            norm += element*element; 
        }

        norm = sqrt(norm);
        
        for (int i = 0; i < matrix_dimension; i++)
            work_matrix[i*matrix_dimension + j] = input_matrix[i*matrix_dimension + j]/norm;
    }
    
}


int main(int argc, char* argv[]){
    double start_time, end_time;
    int init_value;

    if(argc<3) {printf("Digite %s <tamanho da matriz> <valor de inicializacao>\n", argv[0]); return 1;}
    matrix_dimension = atoi(argv[1]);
    init_value = atoi(argv[2]);
    
    GET_TIME(start_time);
    input_matrix = malloc(sizeof(double)*matrix_dimension*matrix_dimension);
    if (input_matrix == NULL){printf("ERROR--malloc input\n"); return 2;}
    normalized_matrix = malloc(sizeof(double)*matrix_dimension*matrix_dimension);
    if (normalized_matrix == NULL){printf("ERROR--malloc normalized\n"); return 2;}
    initialize_matrix(input_matrix, matrix_dimension, init_value);
    GET_TIME(end_time);
    printf("allocating time = %lf\n", end_time-start_time);
    
    
    //printf("Input Matrix:\n");
    
    //print_matrix(input_matrix, matrix_dimension);
    //puts("\nNormalized Arrays Matrix:");


    GET_TIME(start_time);
    normalize_arrays(input_matrix, normalized_matrix);
    //print_matrix(normalized_matrix, matrix_dimension);
    GET_TIME(end_time);

    if(!check_correctness(normalized_matrix, matrix_dimension, init_value)){
        printf("Wrong!!\n");
        return 1;
    }
    printf("processing time = %lf\n", end_time-start_time);
    log_execution_time("norma-seq-log", end_time-start_time, matrix_dimension, 0);

    free(normalized_matrix);
    free(input_matrix);
    return 0;
}