#include<stdio.h>
#include<pthread.h>

#define NUMBER_OF_THREADS 2
#define ARRAY_SIZE 10000

int control_array[ARRAY_SIZE]; //array de controle (o array que vamos usar para saber se o resultado esta correto)
int work_array[ARRAY_SIZE]; //array de trabalho (o array que as threads vao alterar)

//funcao que a thread vai executar
void * fill_array (void * arg){
    int local_id = *(int *)arg;
    
    if (local_id==1)
        for(int i=0; i<ARRAY_SIZE/2; i++)
            work_array[i]++;
    
    else
    for(int j=ARRAY_SIZE/2; j<ARRAY_SIZE; j++)
            work_array[j]++;

    pthread_exit(NULL);
}

//funcao principal do programa
int main(void){
    pthread_t thread_id[NUMBER_OF_THREADS]; //identificador da nova thread
    int local_id[NUMBER_OF_THREADS]; //identificador local da thread
    int different_arrays = 0; //armazena se os vetores de controle e trabalho sao diferentes

    //inicializando o vetor de trabalho e o vetor de controle
    for(int i=0; i<ARRAY_SIZE; i++){
        work_array[i] = i;
        control_array[i] = i+1;
    }

    //cirando as threads
    for(int i=0; i<NUMBER_OF_THREADS; i++){
        local_id[i] = i+1;
        if (pthread_create(&thread_id[i], NULL, fill_array, (void *)&local_id[i]))
            printf("An Error has ocurred at pthread_create");
    }

    //esperando as threads terminarem
    for (int i = 0; i < NUMBER_OF_THREADS; i++)
        if (pthread_join(thread_id[i], NULL))
            printf("An Error has ocurred at pthread_join");

    //validando o trabalho feito nas threads
    for (int i = 0; i < ARRAY_SIZE; i++)
        if (control_array[i] != work_array[i])
            different_arrays++;

    if (different_arrays)
        printf("Erro, os vetores de trabalho e controle sao diferentes\n");

    else
        printf("Sucesso, os vetores de trabalho e controle sao iguais\n");

    return 0;
}