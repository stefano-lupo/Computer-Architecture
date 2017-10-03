# Tutorial 1: Function Calling in x86
Name: Stefano Lupo   
Student Number: 14334933   
Date: 3/10/17   
Course: Computer Engineering   
Module: CS3421 Computer Architecture II   
   


## int min(int a, int b, int c)
This function returns the minimum of a, b, and c.
```Assembly
min:
  // Save context
  push ebp                  // Save old frame pointer
  mov ebp, esp              // Set our new frame pointer
  sub esp, 4                // Allocate space for local variables
  // NA                     // Save non volatile registers (ebx)
  
  mov eax, [ebp + 8]        // eax = a
  mov [ebp-4], eax          // v = a
  mov ecx, [ebp+12]         // ecx = b
  cmp ecx, eax              // cmp(b,v)
  jge b_greatereq_v
  mov ecx, [ebp+12]         // ecx = b (mechanical)
  mov [ebp-4], ecx          // v = b

b_greatereq_v:
  mov eax, [ebp+16]         // eax = c
  mov ecx, [ebp-4]          // ecx = v
  cmp eax, ecx
  jge c_greatereq_v
  mov eax, [ebp+16]         // eax = c
  mov [ebp-4], eax          // v = c

c_greatereq_v:
  mov eax, [ebp-4]          // return v

  // Restore context
  mov esp, ebp
  pop ebp
  ret 0
```


## int p(int i, int j, int k, int l)
This function returns the minimum of the 4 passed parameters `i`, `j`, `k`, `l` and global variable `g` by making use of the previously defined `min` function.

```Assembly
p: 
  // Save context
  push ebp                  // Save old frame pointer
  mov ebp, esp              // Set our new frame pointer
  sub esp, 4                // Allocate space for local variables
  // NA                     // Save non volatile registers (ebx)

  // v = min(g, i, j)
  push [ebp+12]             // push j
  push [ebp+8]              // push i         
  push g                    // push g
  call min
  mov [ebp-4], eax          // save result of function call in v
  add esp, 12               // pop the 3 pushed parameters off the stack

  // return min(v, k, l)
  push [ebp+20]             // push l
  push [ebp+16]             // push k
  push [ebp-4]              // push v
  call min                  // eax = min(v, k, l) 

  // Restore context
  mov esp, ebp
  pop ebp
  ret 0
```
