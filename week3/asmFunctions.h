// For working with 64 bit integers
#include <stdint.h>
#include<inttypes.h>

int64_t __attribute__((ms_abi)) min(int64_t, int64_t, int64_t);
int64_t __attribute__((ms_abi)) p(int64_t, int64_t, int64_t, int64_t);
int64_t __attribute__((ms_abi)) gcd(int64_t, int64_t);
int64_t __attribute__((ms_abi)) q(int64_t, int64_t, int64_t, int64_t, int64_t);
void __attribute__((ms_abi)) shadowSpace();