#!/usr/bin/env bash

# Note need gcc-multilib to compile to 32bit
gcc -o test.o test.c asmFunctions.S;
./test.o