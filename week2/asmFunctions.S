#include "asmConstants.h"

.intel_syntax noprefix
.section .text
.globl min
.type min, @function

// int min(int a, int b, int c) - (a = ebp+8, b = epb+12, c = ebp+16, v = epb-4)
min:
  // Persist context
  push ebp
  mov ebp, esp              // Set our new frame pointer

  sub esp, 4                // Allocate space for local variables
                            // Save non volatile registers (ebx.. etc)
  
  mov eax, [ebp + 8]        // eax = a
  mov [ebp-4], eax          // v = a
  mov ebx, [ebp+12]         // ebx = b
  cmp ebx, eax              // cmp(b,v)
  jge b_greatereq_v
  mov ebx, [ebp+12]         // ebx = b (mechanical)
  mov [ebp-4], ebx          // v = b

b_greatereq_v:
  mov eax, [ebp+16]         // eax = c
  mov ebx, [ebp-4]         // ebx = v
  cmp eax, ebx
  jge c_greatereq_v
  mov eax, [ebp+16]         // eax = c
  mov [ebp-4], eax          // v = c

c_greatereq_v:
  mov eax, [ebp-4]          // return v

  // Restore context
  mov esp, ebp
  pop ebp
  ret 0