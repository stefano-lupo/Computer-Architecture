// For working with 64 bit integers
#include <stdint.h>
#include<inttypes.h>

#include "asmConstants.h"


#define __cdecl __attribute__((__cdecl__))

int64_t min(int64_t, int64_t, int64_t);
int64_t p(int64_t, int64_t, int64_t, int64_t);
int64_t gcd(int64_t, int64_t);


int64_t __attribute__((cdecl)) myTest(int64_t, int64_t, int64_t, int64_t, int64_t, int64_t);

