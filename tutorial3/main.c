#include <stdio.h>
#include <time.h>
#include <sys/time.h>

int numWindows;
int windowsUsed;
int numOverflows;
int numUnderflows;
int numCalls;

int currentNumOverflows;
int maxNumOverflows;

void overFlow() {
  if(windowsUsed == numWindows) {
    numOverflows ++;
    currentNumOverflows++;
    if(currentNumOverflows > maxNumOverflows) {
      maxNumOverflows = currentNumOverflows;
    }
  } else {
    windowsUsed ++;
  }
}

void underFlow() {
    if(windowsUsed == 2) {
      numUnderflows ++;
      currentNumOverflows --;
    } else {
      windowsUsed --;
    }
}


int ackermann(int x, int y) {
  numCalls ++;
  overFlow();

  if(x==0) {
    underFlow();
    return y+1;
  } else if(y==0) { 
    int val = ackermann(x-1, 1);
    underFlow();
    return val;
  } else {
    int val = ackermann(x-1, ackermann(x, y-1));
    underFlow();
    return val;
  }
}

void resetVars(int _numWindows) {
  numWindows = _numWindows;
  windowsUsed = 0;
  numOverflows = 0;
  numUnderflows = 0;
  numCalls = 0;
  maxNumOverflows = 0;
  currentNumOverflows = 0;
  
}

int ackermannBare(int x, int y) {
  if(x==0) {
    return y+1;
  } else if(y==0) { 
    return ackermann(x-1, 1);
  } else {
    return ackermann(x-1, ackermann(x, y-1));
  }
}

int main() {
  resetVars(6);
  int ans = ackermann(3,6);
  printf("6 Windows\n");
  printf("Ack(3,6) = %d, num calls %d.\n", ans, numCalls);
  printf("Number of overflows: %d, maxRegWindowDepth (#registers): %d\n", numOverflows, maxNumOverflows*16);
  printf("Number of underflows: %d\n", numUnderflows);
  printf("Max number of overflows: %d\n\n", maxNumOverflows);

  resetVars(8);
  ans = ackermann(3,6);
  printf("8 Windows\n");
  printf("Ack(3,6) = %d, num calls %d.\n", ans, numCalls);
  printf("Number of overflows: %d, maxRegWindowDepth (#registers): %d\n", numOverflows, maxNumOverflows*16);
  printf("Number of underflows: %d\n", numUnderflows);
  printf("Max number of overflows: %d\n\n", maxNumOverflows);

  resetVars(16);
  ans = ackermann(3,6);
  printf("16 Windows\n");
  printf("Ack(3,6) = %d, num calls %d.\n", ans, numCalls);
  printf("Number of overflows: %d, maxRegWindowDepth (#registers): %d\n", numOverflows, maxNumOverflows*16);
  printf("Number of underflows: %d\n", numUnderflows);
  printf("Max number of overflows: %d\n\n", maxNumOverflows);

  double meanTime = 0;
  for(int i=0;i<10; i++) {
    clock_t begin = clock();
    int answer = ackermannBare(3,6);
    clock_t end = clock();
    double timeSpent = (double)(end-begin)/CLOCKS_PER_SEC;
    meanTime += timeSpent;
    printf("CPU Time: %f sec\n\n", timeSpent);
  }
  meanTime /= 10;
  printf("Mean CPU time to compute ackermann(3,6) was %f\n\n", meanTime);
  

  return 0;
}