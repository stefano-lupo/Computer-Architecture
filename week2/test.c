#include <stdio.h>

#include "asmFunctions.h"

int myGcd(int a, int b) {
    printf("")
    if(b==0) {
        return a;
    }

    return gcd(b, (a % b));
}

int main() {
    int a = 30, b = 15, c = 7, d = 10;
    printf("\nMin: The minimum of %d, %d, %d is %d\n", a, b, c, min(a,b,c));
    printf("P: The minimum of %d, %d, %d, %d and %d is %d\n", a, b, c, d, g, p(a,b,c,d));
    printf("GCD: gcd(%d,%d) = %d\n", a, b, gcd(a, b));
    printf("Actual gcd(%d, %d) = %d\n\n", a, b, myGcd(a, b));
    return 0;
}