#include<stdio.h>
#include<pthread.h>
#include<stdlib.h>
#include<math.h>

void print_matrix(double *matrix, int matrix_dimension){
    for (int i = 0; i < matrix_dimension; i++){
        for (int j = 0; j < matrix_dimension; j++){
            printf("%lf ", matrix[i*matrix_dimension + j]);
        }
        printf("\n");
    }
    
}

void initialize_matrix(double *input, int matrix_dimension, int init_value){

    for (int i = 0; i < matrix_dimension; i++)
    {
        for (int j = 0; j < matrix_dimension; j++)
        {
            input[i*matrix_dimension + j] = 1;
        }
        
    }
}

int check_correctness(double *matrix, int matrix_dimension, int init_value){
    double reference_value = init_value/sqrt(matrix_dimension);
    
    for (int i = 0; i < matrix_dimension; i++){
        for (int j = 0; j < matrix_dimension; j++)
            if (matrix[i*matrix_dimension + j] > reference_value+0.00001 || matrix[i*matrix_dimension + j] < reference_value-0.00001)
                return 0;
    }
    return 1;
    
}

void log_execution_time(char* file_name, double execution_time, int matrix_size, int n_threads){
    FILE *file;
    file = fopen(file_name, "a+");
    if(file){
        if(n_threads>0)
            fprintf(file, "matriz size = %d | number of threads | execution time %lf s\n", matrix_dimension, n_threads, execution_time);
        else fprintf(file, "matriz size = %d | execution time %lf s\n", matrix_dimension, execution_time);
        }
}