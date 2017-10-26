#!/usr/bin/env bash

# Note need gcc-multilib to compile to 32bit
gcc -o test.o asmFunctions.S test.c -m32;
./test.o