#include <stdio.h>
#include <pthread.h>
#include <stdlib.h>
#include <math.h>

int state=0;
int iteration = 0;
int n_threads;
int array_size;
int *work_array;
int *concurrent_array;
int *sequential_array;

pthread_mutex_t x_mutex;
pthread_cond_t x_cond;


void initialize(int *array){
    for(int i=0; i<array_size; i++){
        work_array[i] = rand()%100;
        concurrent_array[i] = work_array[i];
        sequential_array[i] = work_array[i];
    }
}


void barrier(){
    pthread_mutex_lock(&x_mutex);
    if(state != n_threads-1){
        state++;
        pthread_cond_wait(&x_cond, &x_mutex);
    }
    else{
        state = 0;
        pthread_cond_broadcast(&x_cond);
    }
    pthread_mutex_unlock(&x_mutex);
}


void *concurrent_sum(void* arg){
    int id = *(int *) arg;
    int aux;
    int i = pow(2, iteration);
    if(id >= i) aux = concurrent_array[id-i] + concurrent_array[id];
    else aux = concurrent_array[id];
    barrier();
    concurrent_array[id] = aux;
    pthread_exit(NULL);
}



void sequential_sum(int* work_array){
    for(int i=1; i<array_size; i++)
        sequential_array[i] = work_array[i] + work_array[i-1];
}


int main(int argc, char* argv[]){
    if(argc<2){printf("Digite <nome do programa> <tamanho do array>\n"); return 1;}
    array_size = atoi(argv[1]);
    n_threads = array_size;
    int *local_id;
    pthread_t *thread_id;
    
    work_array = (int *) malloc(sizeof(int)*array_size);
    if(work_array==NULL){printf("ERROR--malloc work array\n"); return 2;}
    
    sequential_array = (int *) malloc(sizeof(int)*array_size);
    if(sequential_array==NULL){printf("ERROR--malloc sequential array\n"); return 2;}
    
    concurrent_array = (int *) malloc(sizeof(int)*array_size);
    if(concurrent_array==NULL){printf("ERROR--malloc concurrent array\n"); return 2;}
    
    initialize(work_array);
    
    thread_id = malloc(sizeof(pthread_t)*array_size);
    if(thread_id==NULL){printf("ERROR--malloc thread_id\n"); return 1;}
    
    local_id = malloc(sizeof(int)*array_size);
    if(local_id==NULL){printf("ERROR--malloc local_id\n"); return 1;}
    
    while(iteration<=log2(array_size)){
        for(int i=0; i<array_size; i++){
            local_id[i] = i;
            if(pthread_create(&thread_id[i], NULL, concurrent_sum, &local_id[i])){printf("ERROR--pthread_create\n"); return 2;}
        }
        
        for(int i=0; i<array_size; i++){
            pthread_join(thread_id[i], NULL);
        }
        iteration++;
    }
    
    free(thread_id);
    free(local_id);
    
    sequential_sum(sequential_array);
    
    for(int i=0; i<array_size; i++)
        if(sequential_array[i] != concurrent_array[i]){printf("Wrong Answer on position %d\n", i); return 0;}

    printf("Right Answer!!\n");
    return 0;
}
