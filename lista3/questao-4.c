#include<stdio.h>
#include<pthread.h>
#include<semaphore.h>
#include<stdlib.h>
#include <unistd.h>

sem_t slotFull, slotEmpty;
sem_t mutexProduce, mutexConsume;
sem_t *freeProduce;
sem_t *freeConsume;

int *buffer;
int in = 0;
int *consumer_out;
int *consumers_left_to_consume; //vetor que vai armazenar quantos consumidores ainda precisam consumir daquela posicao do buffer
int P;
int C;
int N;
int value = 0;

void *insert(void *arg){
    int id = *(int *) arg;
    while(1){
        
        sem_wait(&mutexProduce);
        sem_wait(&freeProduce[in]);

        printf("\033[0;32m");
        printf("A thread %d vai inserir o valor %d, na posição %d do buffer\n", id, value, in);
        buffer[in] = value;
        in = (in + 1)%N;

        sem_wait(&mutexConsume);
        if(in==0) consumers_left_to_consume[N-1] = C;
        else consumers_left_to_consume[in-1] = C;
        sem_post(&mutexConsume);
        
        value ++;
        sleep(1);
        
        if(in==0) sem_post(&freeConsume[N-1]);
        else sem_post(&freeConsume[in-1]);
        sem_post(&mutexProduce);
    }
}

void *consume(void *arg){
    int consumer_id = *(int *) arg;
    while(1){
        int out = consumer_out[consumer_id];

        sem_wait(&freeConsume[out]);
        
        int value = buffer[out];
        
        sem_wait(&mutexConsume);
        printf("\033[0;31m");
        printf("A thread %d está tratando o valor %d, obtido na posição %d do buffer\n", consumer_id, value, out);
        consumers_left_to_consume[out]--;
        consumer_out[consumer_id] = (consumer_out[consumer_id]+1)%N;
        sleep(1);

        if(consumers_left_to_consume[out]>0) sem_post(&freeConsume[out]);
        else sem_post(&freeProduce[out]);
        sem_post(&mutexConsume);
    }
    
}

int main(int argc, char *argv[]){

    pthread_t *producers_id;
    pthread_t *consumers_id;

    if(argc<4){
        fprintf(stderr, "Digite: %s <numero de threads podutoras> <numero de threads consumidoras> <tamanho do buffer>\n", argv[0]);
        return 1;
    }

    P = atoi(argv[1]);
    C = atoi(argv[2]);
    N = atoi(argv[3]);

    //Alocacao das variaveis
    sem_init(&mutexConsume, 0, 1);
    sem_init(&mutexProduce, 0, 1);
    sem_init(&slotFull, 0, 0);
    sem_init(&slotEmpty, 0, N);

    
    buffer = malloc(sizeof(int)*N);
    if(buffer==NULL){
        fprintf(stderr, "ERROR--malloc buffer");
        return 2;
    }

    producers_id = malloc(sizeof(pthread_t)*P);
    if(producers_id==NULL){
        fprintf(stderr, "ERROR--malloc producers_id");
        return 2;
    }

    consumers_id = malloc(sizeof(pthread_t)*C);
    if(consumers_id==NULL){
        fprintf(stderr, "ERROR--malloc consumers_id");
        return 2;
    }

    freeConsume = malloc(sizeof(sem_t)*N);
    if(freeConsume==NULL){
        fprintf(stderr, "ERROR--malloc freeConsume");
        return 2;
    }

    freeProduce = malloc(sizeof(sem_t)*N);
    if(freeProduce==NULL){
        fprintf(stderr, "ERROR--malloc freeProduce");
        return 2;
    }

    for(int i=0; i<N; i++){
        sem_init(&freeProduce[i], 0, 1);
        sem_init(&freeConsume[i], 0, 0);
    }

    consumer_out = malloc(sizeof(int)*C);
    if(consumer_out==NULL){
        fprintf(stderr, "ERROR--malloc consumer_out");
        return 2;
    }

    for(int i=0; i<C; i++)
        consumer_out[i] = 0;

    consumers_left_to_consume = malloc(sizeof(int)*C);
    if(consumer_out==NULL){
        fprintf(stderr, "ERROR--malloc consumers_left_to_consume");
        return 2;
    }

    for(int i=0; i<C; i++)
        consumers_left_to_consume[i] = 0;

    
    //inicializacao das threads
    int local_producer_ids[P];
    for (int i = 0; i < P; i++){
        local_producer_ids[i] = i;
        if( pthread_create(&producers_id[i], NULL, insert, (void *)&local_producer_ids[i]) ){
            fprintf(stderr, "ERROR--pthread_create\n");
            return 3;
        }
    }

    int local_consumer_ids[C];
    for (int i = 0; i < C; i++){
        local_consumer_ids[i] = i;
        if( pthread_create(&consumers_id[i], NULL, consume, (void *)&local_consumer_ids[i])){
            fprintf(stderr, "ERROR--pthread_create\n");
            return 3;
        }
    }

    //esperando as thread terminarem
        for (int i = 0; i < P; i++){
        if( pthread_join(producers_id[i], NULL) ){
            fprintf(stderr, "ERROR--pthread_join\n");
            return 3;
        }
    }

    for (int i = 0; i < C; i++){
        if( pthread_join(consumers_id[i], NULL)){
            fprintf(stderr, "ERROR--pthread_join\n");
            return 3;
        }
    }
    return 0;
}