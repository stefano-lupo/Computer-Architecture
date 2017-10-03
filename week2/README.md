# Function Calling in x86
## int min(int int int)
This function returns the minimum of the three passed parameters.
```
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