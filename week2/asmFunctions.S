#include "asmConstants.h"

.intel_syntax noprefix
.section .text
.globl asm_foo
.type asm_foo, @function


asm_foo:
  mov eax, ASM_CONST_1
  ret 0