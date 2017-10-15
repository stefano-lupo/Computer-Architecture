#include <stdio.h>
#include <time.h>
#include <stdlib.h>

#include "asmFunctions.h"

int64_t actualGCD(int64_t a, int64_t b) {
    if(b==0) {
        return a;
    }

    return actualGCD(b, (a % b));
}

int main() {
    // Set seed for random numbers
    srand(time(NULL));

    int64_t a = 100, b = 50, c = -3, d = -15;
    printf("\nMin: The minimum of %" PRId64 ", %" PRId64 ", %" PRId64 " is %" PRId64 "\n", a, b, c, min(a,b,c));
    printf("P: The minimum of %" PRId64 ", %" PRId64 ", %" PRId64 ", %" PRId64 " and g is %" PRId64" \n", a, b, c, d, p(a,b,c,d));

    printf("\nGCD: gcd(%" PRId64 ",%" PRId64 ") = %" PRId64 "\n", a, b, gcd(a, b));
    printf("actualGCDcd(%" PRId64 ", %" PRId64 ") = %" PRId64 "\n\n", a, b, actualGCD(a, b));

    // Test two algorithms 100 times
    for(int i=0;i<10;i++) {
        int x = rand() % 100, y = rand() % 100;
        int actual = actualGCD(x,y);
        int assembly = gcd(x,y);

        if(actual != assembly) {
            printf("Error in algorithm\n");
            printf("gcd(%d, %d) = %d but algorithm returned %d\n\n", x, y, actual, assembly);
            exit(0);
        } else if(actual != 1) {
            // printf("GCD: gcd(%" PRId64 ",%" PRId64 ") = %" PRId64 "\n", x, y, assembly);
            // printf("actualGCDcd(%" PRId64 ", %" PRId64 ") = %" PRId64 "\n\n", x, y, actual);
        }
    }   

    printf("100 tests were successfull\n\n");   
    printf("%c\n",q());
    return 0;
}