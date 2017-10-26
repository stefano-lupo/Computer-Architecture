# Tutorial 3: RISC-1
Name: Stefano Lupo   
Student Number: 14334933   
Date: 26/10/17   
Course: Computer Engineering   
Module: CS3421 Computer Architecture II   
   
# Q1 RISC-1 Code Translation
Note: assuming jumps are the same as x86
Store g in r9
OUR CONVENTION: use r1 for return value
OUR CONVENTION: use r25 for return address
Remeber destination address is last param in RISC


## min(int a, int b, int c)
calling min, a:r26, b:r27, c:r28
stick v in r1 as its accum return val
```c
int g = 4;
int min(int a, int b, int c) {
    int v = a;
    if (b < v)
        v = b;
    if (c < v)
        v = c;
    return v;
}
```

```Assembly
// g = r9 = 4
add r0, #4, r9

// min(int a, int b, int c)
min: 
    add (r26, r0, r1)       ; v = r1 = a
    sub r27, r1, r0 {C}     ; b < v? throw away result and set CCF
    jge min0                ;
    xor r0, r0, r0          ; nop
    add r27, r0, r1         ; r1 = r27 (v = b)
min0:
    sub r28, r1, r0 {C}     ; c < v?
    jge min1
    xor r0, r0, r0
    add r28, r0, r1         ; v = b
min1:
    ret r25, 0              ; return to address contained in r25
    xor r0, r0, r0
```

~~Noop one: bring min0: cmp in place of first noop~~ Actually cant do this  
Noop two: Might be able to bring return instruction up



## int p(int i, int j, int k, int l)

```c
// parameters i:r26, j:r27, k:r28, l:r29
// Pass parameters to next function in r10..r15
int p(int i, int j, int, k, int l) {
    // params below need to be in: r10, r11, r12 before calling
    return min(min(g, i, j), k, l);
}
```
Or in a more assemby like structure

```c
int p(int i, int j, int, k, int l) {
    int x = min(g, i, j);
    return min(x, k, l);
}
```

```Assembly
p:
    ; Setup params for intermediate min
    add r9, r0, r10                 ; param1 = g
    add r26, r0, r11                ; param2 = i
    add r12, r0, r12                ; param3 = j
    callr r25, min                  ; call p and save return address in r25
    xor r0, r0, r0                  ; delay slot

    ; Result of min is now in r1, we want this to be first param to next call
    add r1, r0, r10                 ; param1 = min(g, i, j)
    add r28, r0, r11                ; param2 = k
    add r29, r0, r12                ; param3 = l
    callr r25, min                  ; 
    xor r0, r0, r0                  ; delay slot

    ; Result now is already contained in r1
    ret r25, 0
    xor r0, r0, r0                  ; noop
    
```

Can get rid of noop one: set parameters after calling function
can get rid of noop two: same

## int gcd(int a, int b)
Note assuming a `mod` function exists in RISC-1
```c
int gcd(int a, int b) {
    if (b == 0) {
        return a;
    } else {
        return gcd(b, a % b);
    }
}