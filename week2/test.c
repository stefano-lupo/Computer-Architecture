#include <stdio.h>

#include "asmFunctions.h"


int main() {
    int a = 5, b = 0, c = -7;
    printf("\nThe Minimum of %d, %d, %d is %d\n", a, b, c, min(a,b,c));

    return 0;
}