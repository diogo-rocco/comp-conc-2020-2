#include<stdio.h>
#include<pthread.h>
#include<stdlib.h>
#include "timer.h"

float *first_input_matrix; //matriz de entrada
float *second_input_matrix; //vetor
float *output_matrix; //vetor de saida

int number_of_threads; //numero de threads

pthread_t *thread_id;

typedef struct thread_arguments{
    int element_id;
    int dim;
} thread_arguments;

//funcao de execucao das threads
void *multiply_matrix(void *arg){
    thread_arguments *args = (thread_arguments *) arg;
    int id = args -> element_id;
    int dimension = args -> dim;
    
    for(int i=id; i<dimension; i += number_of_threads)
        for(int j=0; j<dimension; j++)
            for(int k=0; k<dimension; k++)
                output_matrix[i*dimension + j] += first_input_matrix[i*dimension + k] * second_input_matrix[k*dimension + j];
    
    free(arg);
    pthread_exit(NULL);
}

void print_matrix(float* output_matrix, int dim){
    for(int i=0; i<dim; i++){
        for(int j=0; j<dim; j++)
            printf("%.1f ", output_matrix[i*dim + j]);
        puts("");
    }
    puts("");
}


int main(int argc, char* argv[]){
    int dim; //dimensao da matriz e do vetor de entrada
    thread_arguments *arguments;
    double start, finish, elapsed;


    GET_TIME(start);
    //leitura e avaliacao dos parametros de entrada
    if (argc<3){
        printf("Digite: %s <dimensao da matriz> <numero de threads>\n", argv[0]);
        return 1;
    }
    dim = atoi(argv[1]); //converte string para int
    number_of_threads =  atoi(argv[2]);
    if (number_of_threads>dim) number_of_threads = dim;

    //alocacao de memoria para as estruturas de dados
    first_input_matrix = (float *) malloc(sizeof(float)*dim*dim);
    if (first_input_matrix == NULL) printf("ERROR--malloc input_matrix\n");

    second_input_matrix = (float *) malloc(sizeof(float)*dim*dim);
    if (second_input_matrix == NULL) printf("ERROR--malloc array\n");

    output_matrix = (float *) malloc(sizeof(float)*dim*dim);
    if (output_matrix == NULL) printf("ERROR--malloc output_array\n");
    
    //inicializacao das estruturas de dados
    for(int i=0; i<dim; i++){
        for(int j=0; j<dim; j++){
            first_input_matrix[i*dim + j] = i+1; //equivalente a input_matrix[i][j]
            second_input_matrix[i*dim + j] = j+1;
            output_matrix[i*dim + j] = 0;
        }
    }
    
    GET_TIME(finish);
    elapsed = finish-start;
    printf("A inicializacao levou %f segundos\n", elapsed);
    
    //multiplicacao da matriz pelo vetor
    thread_id = malloc(sizeof(pthread_t)*dim);
    if (thread_id==NULL) {puts("ERROR--malloc thread_id"); return 1;}
    
    GET_TIME(start);
    for(int i=0; i<number_of_threads; i++){
        arguments = malloc(sizeof(thread_arguments));

        arguments -> dim = dim;
        arguments -> element_id = i;
        
        pthread_create(&thread_id[i], NULL, multiply_matrix, (void *) arguments);
    }
    

    //espera as threads terminarem a execucao
    for(int i=0; i<dim; i++) pthread_join(thread_id[i], NULL);
    
    GET_TIME(finish);
    elapsed = finish-start;
    printf("A multiplicacao levou %f segundos\n", elapsed);
    
    GET_TIME(start);
    //liberacao da memoria
    free(thread_id);
    free(first_input_matrix);
    free(second_input_matrix);
    free(output_matrix);
    GET_TIME(finish);
    elapsed = finish-start;
    printf("A liberacao de memoria levou %f segundos\n", elapsed);

    return 0;
}