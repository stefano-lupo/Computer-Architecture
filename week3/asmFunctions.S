.intel_syntax noprefix
.section .text

// Export the following
.globl min, p, gcd, q

// Define types of the following
.type min, @function
.type p, @function
.type gcd, @function
.type q, @function

// Define globals
.DATA
g: .quad 4  
fmt:  .string "%ld\n"        

/****************************************************
  Note: USING UNIX CALLING CONVENTION
  Param 1 - 6 stored in rdi, rsi, rdx, rcx, r8, r9
  ebp, rbx, r12, r13, r14, r15 are NON volatile
  return through rax (accumulator)
*****************************************************/

/****************************************************
  Note: USING WINDOWS CALLING CONVENTION
  Caller must allocate 32 bytes of shadow space (before pushing any extra params) 
  Param 1 - 4 stored in rcx, rdx, r8, r9
  ebp, rbx, r12, r13, r14, r15 are NON volatile
  return through rax (accumulator)
*****************************************************/

/*
  Min - Returns minimum of three parameters passed in
  int min(int a, int b, int c) - (a = rcx, b = rdx, c = r8), v = rax
*/
min:
  // Save context
  push rbp                  // Save caller's frame pointer
  mov rbp, rsp              // Set the frame pointer to the current value of the stack pointer (which just pushed)

  mov rax, rcx              // rax = v = a
  
  cmp rdx, rax              // cmp(b,v)
  jge b_greatereq_v
  mov rax, rdx              // v = b

b_greatereq_v:

  cmp r8, rax               // cmp(c,v)
  jge c_greatereq_v
  mov rax, r8               // v = c

c_greatereq_v:                
  // Restore context
  mov rsp, rbp
  pop rbp
  ret




/*
  p - returns the minimum of the 4 passed parameters and global variable g
  int p(int i (rcx), int j (rdx), int k (r8), int l (r9))
*/

p: 
  // Save context
  push rbp                  // Save old frame pointer
  mov rbp, rsp              // Set our new frame pointer

  push r8                   // save k
  push r9                   // save l
  mov r8, g

                            // i = rcx, j = rdx, rdx = r8
  sub rsp, 32               // Allocate 32 bytes (although it is unused for our function) of shadow space (4x64 bit words)
  call min                  // rax = min(g, i, j)
  add rsp, 32               // Deallocate the 32 bytes of shaodwspace

  mov rcx, rax              // rcx = rax = min(i,j,g)
  pop rdx                   // rdx = k
  pop r8                    // r8 = l

  sub rsp, 32               // Allocate 32 bytes of shadow space (4x64 bit words)
  call min                  // min(min(i,j,g), k, l)
  add rsp, 32               // Deallocate the 32 bytes of shaodwspace
                
p_restore:
  // Restore context
  mov rsp, rbp
  pop rbp
  ret


/*
  gcd - Returns the greatest common divisor of two parameters
  int gcd(int a (rcx), int b (rdx))
 */

  gcd: 
  // Save context
  push rbp                  // Save old frame pointer
  mov rbp, rsp              // Set our new frame pointer

  // Check if b is zero
  test rdx, rdx             // rdx && rdx (zero iff rdx was 0)
  je zero


  /*
    idiv divides the 128 bits across RDX|RAX by the operand
    idiv stores the result of the division in RAX and the remainder in RDX
 */

  mov r10, rdx              // save b in r10

  mov rax, rcx              // prepare rax for sign extension
  cqo                       // sign extends rax into 128 bits accross rdx:rax

  idiv r10                  // rax = (rdx|rax) / r10, rdx = (rdx|rax) % r10
  mov rcx, r10              // rcx = b, rdx = a % b

  // recurse gcd(b, a%b) - No need for shadow space as function doesnt make use of it
  call gcd
  jmp return


zero:
  mov rax, rcx              // return a

return: 
  // Restore context
  mov rsp, rbp
  pop rbp
  ret


/*
  Print
*/
q:
  // Save context
  push rbp                  // Save old frame pointer
  mov rbp, rsp              // Set our new frame pointer


  mov rdi, fmt
  mov rsi, 10
  mov rax, 0
  call printf

  mov rax, fmt

  // Restore context
  mov rsp, rbp
  pop rbp
  ret



