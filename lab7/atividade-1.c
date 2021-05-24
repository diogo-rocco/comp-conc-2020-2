#include<stdio.h>
#include<stdlib.h>
#include<pthread.h>
#include <semaphore.h>

int state=0;
sem_t cond_1, cond_23;

void *thread_1(void* arg){
    sem_wait(&cond_1);
    printf("Volte sempre!\n");
    state++;
    pthread_exit(NULL);
}

void *thread_2(void* arg){
    sem_wait(&cond_23);
    printf("Fique a vontade.\n");
    state++;
    if(state==2) sem_post(&cond_1);
    sem_post(&cond_23);
    pthread_exit(NULL);
}

void *thread_3(void* arg){
    sem_wait(&cond_23);
    printf("Sente-se por favor.\n");
    state++;
    if(state==2) sem_post(&cond_1);
    sem_post(&cond_23);
    pthread_exit(NULL);
}

void *thread_4(void* arg){
    printf("Seja bem-vindo!\n");
    sem_post(&cond_23);
    pthread_exit(NULL);
}

int main(){
    pthread_t threads[4];

    sem_init(&cond_1, 0, 0);
    sem_init(&cond_23, 0, 0);

    pthread_create(&threads[0], NULL, thread_1, NULL);
    pthread_create(&threads[1], NULL, thread_2, NULL);
    pthread_create(&threads[2], NULL, thread_3, NULL);
    pthread_create(&threads[3], NULL, thread_4, NULL);

    for (int i = 0; i < 4; i++)
        pthread_join(threads[i], NULL);
}