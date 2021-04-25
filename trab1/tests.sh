#!/bin/bash


gcc norma-seq.c -o norma-seq -Wall -lpthread -lm
gcc norma-conc-1.c -o norma-conc-1 -Wall -lpthread -lm
gcc norma-conc-2.c -o norma-conc-2 -Wall -lpthread -lm

#-----------------------------------------------------------------#

MATRIX_SIZE=1000

NUMBER_OF_THREADS=2
for i in 1 2 3 4 5
do
    ./norma-seq $MATRIX_SIZE $i
    ./norma-conc-1 $MATRIX_SIZE $NUMBER_OF_THREADS $i
    ./norma-conc-2 $MATRIX_SIZE $NUMBER_OF_THREADS $i
done

NUMBER_OF_THREADS=4
for i in 1 2 3 4 5
do
    ./norma-seq $MATRIX_SIZE $i
    ./norma-conc-1 $MATRIX_SIZE $NUMBER_OF_THREADS $i
    ./norma-conc-2 $MATRIX_SIZE $NUMBER_OF_THREADS $i
done

#-----------------------------------------------------------------#

MATRIX_SIZE=10000

NUMBER_OF_THREADS=2
for i in 1 2 3 4 5
do
    ./norma-seq $MATRIX_SIZE $i
    ./norma-conc-1 $MATRIX_SIZE $NUMBER_OF_THREADS $i
    ./norma-conc-2 $MATRIX_SIZE $NUMBER_OF_THREADS $i
done

NUMBER_OF_THREADS=4
for i in 1 2 3 4 5
do
    ./norma-seq $MATRIX_SIZE $i
    ./norma-conc-1 $MATRIX_SIZE $NUMBER_OF_THREADS $i
    ./norma-conc-2 $MATRIX_SIZE $NUMBER_OF_THREADS $i
done

#------------------------------------------------------------------#

MATRIX_SIZE=20000

NUMBER_OF_THREADS=2
for i in 1 2 3 4 5
do
    ./norma-seq $MATRIX_SIZE $i
    ./norma-conc-1 $MATRIX_SIZE $NUMBER_OF_THREADS $i
    ./norma-conc-2 $MATRIX_SIZE $NUMBER_OF_THREADS $i
done

NUMBER_OF_THREADS=4
for i in 1 2 3 4 5
do
    ./norma-seq $MATRIX_SIZE $i
    ./norma-conc-1 $MATRIX_SIZE $NUMBER_OF_THREADS $i
    ./norma-conc-2 $MATRIX_SIZE $NUMBER_OF_THREADS $i
done