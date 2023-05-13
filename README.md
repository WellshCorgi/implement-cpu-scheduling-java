# implement-cpu-scheduling-java
## 🖥️ 프로젝트 소개 (Project Introduction)
- CPU Scheduling of Operating System 
<br>

## 🕰️ 개발 기간 (Development period)
* 23.05.06일 - 23.05.13일

### ⚙️ 개발 환경 (Development environment)
- Language : Java(jdk 1.8(Zulu Open JDK 11.04), Java 11)
- Tool : [JetBrain IntelliJ IDEA CE](https://www.jetbrains.com/ko-kr/idea/download/#section=mac)

## 📌 CPU Scheduling
#### FCFS
- 작업이 도착한 순서대로 처리되는 방식으로 동작.
- 먼저 도착한 작업이 먼저 실행되고, 작업이 완료될 때까지 다른 작업은 대기. [호위효과 - Convoy effect]
#### SJF
- 도착한 순서와 상관없이 실행 시간이 가장 짧은 작업이 먼저 실행되는 특징.
- 평균 대기 시간을 최소화할 수 있는 알고리즘

#### RoundRobin
- 각 작업에 동일한 시간 할당량을 부여 (Quantum)
- 할당된 시간 동안 작업을 실행한 후 다음 작업으로 전환하는 방식으로 동작


## 👽 How to execute it?
1. Open the terminal
2. git clone https://github.com/WellshCorgi/implement-cpu-scheduling-java.git
3. cd implement-cpu-scheduling-java/src
4. javac Main.java
5. [~Let's run the Java file~]
   - java Main data.txt FCFS 0
   - java Main data.txt SJF 0
   - java Main data.txt RoundRobin 1
   - java Main data.txt RoundRobin 10
   - java Main data.txt RoundRobin 100

### About Each Process Control Block

- n (A,C,B,IO)
- n is count of processes
- The time when the process arrived is A
- Let C be the total CPU time required until the process ends.
- Turnaround Time(Finishing Time - @param A)
- CPU burst time is the unique distributed random integer between 0 and any number B.
- IO burst time is the unique distributed random integer between 0 and any number IO.

### Test Cases
1. >> 1 (0 5 1 1)
2. >> 3 (0 5 1 1)(0 5 1 1)(3 5 1 1)
3. >> 5 (0 200 3 3)(0 500 9 3)(0 500 20 3)(100 100 1 0)(100 500 100 3)
### Output about Test Cases
- Case 1 [어떤 출력이든 고정된 값]
```
============= [ Summary of Scheduler ] =============
Scheduler Finishing Time : 9
Average turnaround time : 9.00
Average waiting time : 0.00
CPU Utilization : 55.56 %
I/O Utilization : 44.44 %
Throughput in processes completed per hundred time units : 11.11 %
```
- Case 2 [어떤 출력이든 고정된 값]

```
============= [ Summary of Scheduler ] =============
Scheduler Finishing Time : 15
Average turnaround time : 12.67
Average waiting time : 3.67
CPU Utilization : 100.00 %
I/O Utilization : 80.00 %
Throughput in processes completed per hundred time units : 20.00 %
```
- Other Case
```
Cpu Burst , Io Burst가 Random값으로 오차범위의 값 이내의 결과 출력
```
### ★What needs to be improved in this project
- 자바 디자인 패턴 (SRP,DIP)를 적용하기 및 OCP 규칙 위반 수정하기. [Applying Java design patterns and implementing SRP, correcting OCP rule violations]