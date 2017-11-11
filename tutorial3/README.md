# Tutorial 3: RISC-1
Name: Stefano Lupo   
Student Number: 14334933   
Date: 10/11/17   
Course: Computer Engineering   
Module: CS3421 Computer Architecture II   
   
# Q1 RISC-1 Code Translation
Note: assuming jumps are the same as x86
Convention:
- r1 : return value
- r2 - r9 : globals
- r10 - r15 : params to pass to func 
- r16 - r24 : local vars
- r25 : return address
- r26 - r31 : passed params

Note : sub Ra, Rb, R0 --> R0 = Ra - Rb

## min(int a, int b, int c)
The function in C.
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

## Unoptimized RISC-1 Equivalent
```Assembly
add r0, #4, r9              ; r9 = g = 4

; Min is called with the following params - a:r26, b:r27, c:r28.
min: 
    add r26, r0, r1         ; v = r1 = a
    sub r27, r1, r0 {C}     ; cmp b, v  throw away result and set CCF
    jge min0                ;
    xor r0, r0, r0          ; no op
    add r27, r0, r1         ; r1 = r27 (v = b)
min0:
    sub r28, r1, r0 {C}     ; c < v?
    jge min1
    xor r0, r0, r0
    add r28, r0, r1         ; v = c
min1:
    ret r25, 0              ; return to address contained in r25
    xor r0, r0, r0
```
    
No optimizations possible.


## int p(int i, int j, int k, int l)
The slightly adjusted function in C.
```c
int p(int i, int j, int, k, int l) {
    int x = min(g, i, j);
    return min(x, k, l);
}
```


Unoptimized RISC-1
i = r26, j=r27, k=r28, l=r29
```Assembly
p:
    ; Setup params for intermediate min call
    add r9, r0, r10                 ; param1 = g
    add r26, r0, r11                ; param2 = i
    add r27, r0, r12                ; param3 = j
    callr r25, min                  ; call min and save return address in r25
    xor r0, r0, r0                  ; no op

    ; Result of min is now in r1, we want this to be first param to next call
    add r1, r0, r10                 ; param1 = min(g, i, j)
    add r28, r0, r11                ; param2 = k
    add r29, r0, r12                ; param3 = l
    callr r25, min                  ; 
    xor r0, r0, r0                  ; delay slot

    ; Result now is already contained in r1
    ret r25, 0
    xor r0, r0, r0                  ; no op
    
```

### Optimised
We note that we can put one parameter in a register AFTER calling the function that requires that parameter.
```Assembly
p:
    ; Setup params for intermediate min
    add r9, r0, r10                 ; param1 = g
    add r26, r0, r11                ; param2 = i
    callr r25, min                  ; call p and save return address in r25
    add r12, r0, r12                ; param3 = j

    ; Result of min is now in r1, we want this to be first param to next call
    add r1, r0, r10                 ; param1 = min(g, i, j)
    add r28, r0, r11                ; param2 = k
    callr r25, min                  ; 
    add r29, r0, r12                ; param3 = l

    ; Result now is already contained in r1
    ret r25, 0
    xor r0, r0, r0                  ; no op
    
```


## int gcd(int a, int b)
Note assuming a `mod` function exists in RISC-1 such that: `mod Ra, Rb, Rd`  is  `Rd <- Ra % Rb`
The function in C is the following.
```c
int gcd(int a, int b) {
    if (b == 0) {
        return a;
    } else {
        return gcd(b, a % b);
    }
}
```

The function in unoptimized RISC-1   
a = r26, b = r27
```Assembly
gcd:
    sub r27, r0, r0 {C}         ; cmp r27, #0
    jne repeat
    xor r0, r0, r0              ; no op
    add r0, r26, r1             ; r1 = a
    ret r25, 0                  ; return a
    xor r0, r0, r0              ; no op
repeat:
    mod r26, r27, r11           ; param2 = a % b
    add r27, r0, r10            ; param1 = b
    callr r25, gcd              ; gcd(b, a%b)
    xor r0, r0, r0              ; no op
    ret r25, 0                  ; return gcd(b, a%b)
    xor r0, r0, r0              ; no op

```

The function in optimized RISC-1
a = r26, b = r27
```Assembly
gcd:
    sub r27, r0, r0 {C}         ; cmp r27, #0
    jne recurse                 
    xor r0, r0, r0              ; no op
    add r0, r26, r1             ; r1 = a
    ret r25, 0                  ; return a
    xor r0, r0, r0              ; no op
recurse:
    mod r26, r27, r11           ; param2 = a % b
    callr r25, gcd              ; gcd(_, a%b)
    add r27, r0, r10            ; _ = b
    ret r25, 0                  ; return gcd(b, a%b)
    xor r0, r0, r0              ; no op
```