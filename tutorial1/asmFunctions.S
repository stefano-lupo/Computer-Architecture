#include "asmConstants.h"

.intel_syntax noprefix
.section .text

// Export the following
.globl min, p, gcd

// Define types of the following
.type min, @function
.type p, @function
.type gcd, @function

// Define globals
.DATA
g: .quad 4


/*
  Min - Returns minimum of three parameters passed in
  int min(int a, int b, int c) - (a = ebp+8, b = epb+12, c = ebp+16, v = epb-4)
*/
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



/*
  p - returns the minimum of the 4 passed parameters and global variable g
  int p(int i [ebp+8], int j [ebp+12], int k [ebp+16], int l [ebp+20])
*/
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


/*
  gcd - Returns the greatest common divisor of two parameters
  int gcd(int a [ebp+8], int b[ebp+12])
 
*/
  gcd: 
  // Save context
  push ebp                  // Save old frame pointer
  mov ebp, esp              // Set our new frame pointer
  // NA                     // Allocate space for local variables
  // NA                     // Save non volatile registers (ebx)

  // Check if b is zero
  mov eax, [ebp+12]         // eax = b
  test eax, eax             // eax && eax (zero iff eax was 0)
  je zero


  /*
    div divides the 64 bits across EDX|EAX by the operand
    div stores the result of the division in EAX and the remainder in EDX
  */

  // Set up operands, compute a % b
  xor edx, edx              // Clear upper 32 bits
  mov eax, [ebp+8]          // eax = a (numerator)
  mov ecx, [ebp+12]         // ecx = b (denominator)
  div ecx                   // eax = (edx|eax / ecx, edx = (edx|eax) % ecx

  // push parameters
  push edx                  // push a % b
  mov eax, [ebp+12]         // eax = b
  push eax                  // push b

  // recurse
  call gcd

  // pop two pushed parameters
  sub esp, 8
  jmp return
 
zero:
  mov eax, [ebp+8]          // return a

return: 
  // Restore context
  mov esp, ebp
  pop ebp
  ret 0