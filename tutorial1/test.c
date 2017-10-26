#include <stdio.h>
#include <time.h>
#include <stdlib.h>

#include "asmFunctions.h"

int actualGCD(int a, int b) {
    if(b==0) {
        return a;
    }

    return actualGCD(b, (a % b));
}

int main() {
    // Set seed for random numbers
    srand(time(NULL));

    int a = 99, b = 30, c = 7, d = 10;
    printf("\nMin: The minimum of %d, %d, %d is %d\n", a, b, c, min(a,b,c));
    printf("P: The minimum of %d, %d, %d, %d and %d is %d\n", a, b, c, d, g, p(a,b,c,d));
    printf("\nGCD: gcd(%d,%d) = %d\n", a, b, gcd(a, b));
    printf("actualGCDcd(%d, %d) = %d\n\n", a, b, actualGCD(a, b));

    // Test two algorithms 100 times
    for(int i=0;i<100;i++) {
        int x = rand() % 100, y = rand() % 100;
        int actual = actualGCD(x,y);
        int assembly = gcd(x,y);
        if(actual != assembly) {
            printf("Error in algorithm\n");
            printf("gcd(%d, %d) = %d but algorithm returned %d\n\n", x, y, actual, assembly);
            exit(0);
        } 
    }   

    printf("100 tests were successfull\n\n");   
    return 0;
}