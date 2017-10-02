#!/usr/bin/env bash

gcc -o test asmFunctions.S test.c -m32;
./test