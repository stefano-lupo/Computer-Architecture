# Tutorial 4: MIPS
Name: Stefano Lupo   
Student Number: 14334933   
Date: 17/11/17   
Course: Computer Engineering   
Module: CS3421 Computer Architecture II   

# Q1
### O1 to MUX6
- This datapath would be used when there is a memory access hazard (an instruction uses a register whos value is currently being loaded in from memory)
```assembly
  LD    r3, r1, x        ; Load into r3, from the address contained in r1, offset by x
  ADD   r1, r3, r2      ; r1 = r3 + r2
```
![Q1a](screenshots/Q1_O1-mux6.png)
   
### O0 to MUX7 and O1 to MUX 6
- The **O1 -> MUX6** datapath is used due to memory access hazards as before.
- The **O0 -> MUX7** datapath is used to due a data hazard
```assembly
  LD    r3, r1, x        ; Load from memory
  ADD   r1, r2, r2      ; r1 = r2 + r2
  ADD   r2, r3, r1      ; r2 = r3 + r1 - this requires both of the source registers from the previous two instructions
```
![Q1b](screenshots/Q1_O1-mux6_O0-mux7.png)

### O0 to MUX8
- This datapath is used when the result of an ALU operation is required to be written to memory on the next instruction.
- The result of the ALU operation will have made it to O0, and is then required to be in SMR (memory data in) on the next tick.
- Thus the ALU forwards it's result from O0 to SMR through MUX8.
```assembly
  ADD   r1, r2, r2      ; r1 = r2 + r2
  ST    r1, r3, x       ; store r1 @ address r3 + x
```
![Q1c](screenshots/Q1_O0-mux8.png)

### EX to MUX7
- This datapath is used when an *effective memory address* calculation is performed by the ALU.
- The desired offset from the specified memory address is contained in the machine code for the instruction.
- Thus this offset is added to the specified memory address (contained in the given register).
```assembly
  ADD   r1, r2, r2      ; r1 = r2 + r2
  ST    r1, r3, 2       ; store r1 @ address r3 + 2
```
![Q1d](screenshots/Q1_EX-mux7.png)

### Data Cache to MUX 9
- This datapath is used during the memory access stage of loading data from memory.
```assembly
  LD    r0, r0, r0         ; Load data from memory
```
![Q1d](screenshots/Q1_DC-mux9.png)

### O0 to Zero Detector
- This datapath is used when a branch if equal (or not equal) to zero instruction is executed after some sort of compare operation.
- The result of the compare operation determines whether or not the branch should be taken.
- This is done by detecting if the result was zero and using that to control mux3 (which controls whether or not the branch should be taken).
```assembly
  AND     R0, R0, R0        ; Some operation to act as a comparison (equivalent to setting condition code flags)
  BEQZ    _, R0, x          ; Branch if the result of the previous operation was 0
```
![Q1e](screenshots/Q1_O0-ZD.png)

### Register File to MUX1

```assembly

```
![Q1f](screenshots/Q1_RF-mux1.png)

### Branch Target Buffer to MUX1
- This datapath will be used when a PC that contains a branch instruction is reached AND that PC has been seen before.
  - This means the BTB knows where this branch should go and thus it can supply the next PC to MUX 1.
- A small infinitely looping program demonstrates this.
  - Initially the `jump -12` instruction (PC = 0x0c) will not be in the BTB and must be calcualted etc (producing stalls)
  - Every subsequent time we reach 0x0C, the BTB will simply supply the next PC producing no stalls.
- The screenshot below shows the loop after a few iterations and thus the next PC is contained in the BTB.

```assembly
  ADD     r1, r1, r1        ; Dummy loop body
  ADD     r2, r2, r2        ; Dummy loop body
  ADD     r3, r3, r3        ; Dummy loop body
  J       0xF4              ; Jump back to start of the loop

```
![Q1g](screenshots/Q1_BTB-mux1.png)

    
# Q2
The **correct** program would be the following:
```
r1 = 1
r2 = 2
r1 = r1 + r2 = 1 + 2 = 3
r2 = r1 + r2 = 3 + 2 = 5
r1 = r1 + r2 = 3 + 5 = 8
r2 = r1 + r2 = 8 + 5 = 13
r1 = r1 + r2 = 8 + 13 = 21
```
Therefore the resulting value of r1 should be 21.

## ALU Forwarding Enabled
Assuming pipeline is initally empty, the instructions will execute as follows:   

| Instruction   |  1  |  2  |  3  |  4  |  5  |  6  |  7  |  8  |  9  |
| ------------- |-----|-----|-----|-----|-----|-----|-----|-----|-----|
| I1            |  F  |  D  |  E  |  M  |  W  |     |     |     |     |
| I2            |     |  F  |  D  |  E  |  M  |  W  |     |     |     |   
| I3            |     |     |  F  |  D  |  E  |  M  |  W  |     |     | 
| I4            |     |     |     |  F  |  D  |  E  |  M  |  W  |     |  
| I5            |     |     |     |     |  F  |  D  |  E  |  M  |  W  |

Thus it will require a total of 9 clock cycles and will produce the correct answer of 21 due to ALU forwarding.

## ALU Forwarding Disabled, CPU data Dependency Interlocks Enabled
Since ALU forwarding is now disable, we need a mechanism to handle data hazards (since our program is full of them - one on every instruction in fact). CPU Data Dependency Interlocks can fulfill this role. These work by stalling the pipeline until the data dependency is resolved whenever a data dependency is discovered. That is, the execution phase of the following instruction is stalled by two cycles until the write back phase of the current instruction is completed.

The stalls occur for two reasons:
1. The next process cannot progress to the next phase of the pipeline until the previous process is progressing to the phase after that.
  - For example, P2 cannot fetch until P1 is decoding, otherwise P2's data would overwrite P1's data.
2. No process can execute until its predecessor has finished writing back (exited the pipeline).
  - This is due to the data hazard problem

Thus the program will compute the correct result of 21, but will require more clock cycles due to the stalls.    

The pipeline with stalls is shown below.     

![Q2 No Forwarding](screenshots/q2_no_forwarding_with_CDD.png)   

Thus the total number of clock cycles required is **17**.    
*Note this does not include the clock cycles for the HALT instruction and instead shows the more interesting number of clock cycles to compute the answer.* 

Running the simulation gave the same total of 17 clock ticks to execute the 5 instructions and produced a result of 0x15 which is 21 as expected.
![Q2 no forwarding simulation](screenshots/q2b-simulation.png)


## ALU Forwarding Disabled and Data Dependency Interlocks Disabled
Now that we have no mechanism of detecting data hazards, the computed answer will be incorrect due to non updated values of R1 and R2 being used between subsequent instructions. However there will be no stalls in this pipeline and thus we expect the same number of clock cycles as the case when ALU forwarding was enabled (causing no stalls).

As expected, the simulation shows that it will take a total of 9 clock cycles to execute the 5 instructions and the resulting answer will 6 instead of the correct 21 due to the data hazards.

![Q2 no forwarding or interlock](screenshots/q2c-simulation.png)
