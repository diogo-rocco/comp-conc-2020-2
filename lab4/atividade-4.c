#include<stdio.h>
#include<stdlib.h>
#include<pthread.h>

/*
state 0 -> nada foi executado (executar thread 4)
state 1 -> a thread 4 foi executada (executar 2 ou 3)
state 2 -> a thread 4 foi executada e ou a 2 ou a 3 tambem (executar a que ainda falta)
state 3 -> todas menos a 1 foram executadas (executar a 1)
*/
int state = 0;
pthread_mutex_t mutex;
pthread_cond_t condition;

void *thread_1(void* arg){
    pthread_mutex_lock(&mutex);
    while(state!=3) pthread_cond_wait(&condition, &mutex);
    printf("Volte sempre!\n");
    state++;
    pthread_mutex_unlock(&mutex);
    pthread_exit(NULL);
}

void *thread_2(void* arg){
    pthread_mutex_lock(&mutex);
    while(state!=1 && state!=2) pthread_cond_wait(&condition, &mutex);
    printf("Fique a vontade.\n");
    state++;
    pthread_cond_broadcast(&condition); //broadcast porque se for signal eu corro o risco de dar unlock na 1 e entrar em deadlock
    pthread_mutex_unlock(&mutex);
    pthread_exit(NULL);
}

void *thread_3(void* arg){
    pthread_mutex_lock(&mutex);
    while(state!=1 && state!=2) pthread_cond_wait(&condition, &mutex);
    printf("Sente-se por favor.\n");
    state++;
    pthread_cond_broadcast(&condition); //broadcast porque se for signal eu corro o risco de dar unlock na 1 e entrar em deadlock
    pthread_mutex_unlock(&mutex);
    pthread_exit(NULL);
}

void *thread_4(void* arg){
    pthread_mutex_lock(&mutex);
    printf("Seja bem-vindo!\n");
    state++;
    pthread_cond_broadcast(&condition); //broadcast porque se for signal eu corro o risco de dar unlock na 1 e entrar em deadlock
    pthread_mutex_unlock(&mutex);
    pthread_exit(NULL);
}

int main(){
    pthread_t threads[4];

    pthread_mutex_init(&mutex, NULL);
    pthread_cond_init (&condition, NULL);

    pthread_create(&threads[0], NULL, thread_1, NULL);
    pthread_create(&threads[1], NULL, thread_2, NULL);
    pthread_create(&threads[2], NULL, thread_3, NULL);
    pthread_create(&threads[3], NULL, thread_4, NULL);

    for (int i = 0; i < 4; i++)
        pthread_join(threads[i], NULL);

    pthread_mutex_destroy(&mutex);
    pthread_cond_destroy(&condition);
}