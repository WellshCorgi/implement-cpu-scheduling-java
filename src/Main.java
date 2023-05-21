import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


class Process {
    int arrivalTime;
    int cpuRequiredTime;
    int cpuBurstTime;
    int ioBurstTime;
    int startProcessTime = -1;
    int burstedCpuTime = 0;
    int burstedIOTime = 0;

    public Process(int arrivalTime, int cpuRequiredTime, int cpuBurstTime, int ioBurstTime, int startProcessTime, int burstedCpuTime, int burstedIOTime) {
        this.arrivalTime = arrivalTime;
        this.cpuRequiredTime = cpuRequiredTime;
        this.cpuBurstTime = cpuBurstTime;
        this.ioBurstTime = ioBurstTime;
        this.burstedCpuTime = cpuRequiredTime;
    }
    public int getStartProcessTime() {
        return startProcessTime;
    }
    public void setStartProcessTime(int startProcessTime) {
        this.startProcessTime = startProcessTime;
    }
    public void setburstedCpuTime(int burstedCpuTime) {
        this.burstedCpuTime = burstedCpuTime;
    }
    public void setburstedIOTime(int burstedIOTime) {
        this.burstedIOTime = burstedIOTime;
    }
    public int getBurstedCpuTime(){
        return burstedCpuTime;
    }
    public int getBurstedIOTime(){
        return burstedIOTime;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getCpuBurstTime() {
        return cpuBurstTime;
    }

    public int getIoBurstTime() {
        return ioBurstTime;
    }
    public int getCpuRequiredTime() {
        return cpuRequiredTime;
    }
    public void setCpuRequiredTime(int cpuRequiredTime) {
        this.cpuRequiredTime = cpuRequiredTime;
    }
    @Override
    public String toString() {
        return  arrivalTime +
                " "+ cpuRequiredTime +
                " " + ioBurstTime +
                " " + cpuBurstTime;
    }
}


class SchedulingSimulation {
    private int processCnt = 0;
    private List<Process> setProcessInfo = new ArrayList<>();
    private static SchedulingSimulation schedulingSimulation;
    private SchedulingSimulation(){ //Singleton patten
        // 생성자가 외부에서 호출 못하게 private 지정
    }
    public static SchedulingSimulation getInstance(){
        if(schedulingSimulation == null) {
            schedulingSimulation = new SchedulingSimulation();
        }
        return schedulingSimulation;
    }
    private void launchSimulation( List<Integer> sp){
        List<List<Integer>> groupedProcesses = groupProcessInfo(sp,4);// 프로세스 정보 4개씩 그룹화
        List<Process> processList = new ArrayList<>();
        for(List<Integer> process : groupedProcesses){
            Process p = new Process(process.get(0), process.get(1),process.get(2), process.get(3),0,0,0);
            processList.add(p);
        }
        processList.sort(Comparator.comparingInt(p -> p.arrivalTime));// 도착한 순서대로 정렬
        setProcessInfo = processList;
    }
    public List<Process> getSetProcessInfo() {
        return setProcessInfo;
    }

    public void setProcessDataToLaunch(String data){ //data.txt 의 입력 값 input에 맞게 setting
        try{
            BufferedReader br = new BufferedReader(new FileReader(data));
            String inputProcessInfo = br.readLine();
            List<Integer> setProcessInfoFromFile = new ArrayList<>();
            while (inputProcessInfo != null) {
                StringTokenizer st = new StringTokenizer(inputProcessInfo, "() ");
                int i = 0;
                while (st.hasMoreTokens()) {
                    String token = st.nextToken();
                    if (i == 0) {
                        processCnt = Integer.parseInt(token);
                    } else {setProcessInfoFromFile.add(Integer.parseInt(token));}
                    i++;}
                inputProcessInfo = br.readLine();
            }
            br.close();
            launchSimulation(setProcessInfoFromFile);
        } catch(IOException e) {
            System.out.println("파일 입출력 오류발생!!! 확인바랍니다.");
        }

    }
    private List<List<Integer>> groupProcessInfo(List<Integer> processInfo, int groupSize) {
        List<List<Integer>> groupedProcesses = new ArrayList<>();
        int size = processInfo.size();
        for (int i = 0; i < size; i += groupSize) {
            List<Integer> group = processInfo.subList(i, Math.min(i + groupSize, processInfo.size()));
            groupedProcesses.add(new ArrayList<>(group));}
        return groupedProcesses;
    }
}
interface Scheduler{
    void run();
}
class FCFS implements Scheduler{
    private List<Process> processList;

    public FCFS(List<Process> processList){
        this.processList = processList;
    }
    public void run() {
        System.out.println("--------FCFS--------");
        Random random = new Random();
        int currentTime = 0;
        int finishingTime=0;
        int totalTurnaroundTime = 0;
        int totalWaitingTime = 0;
        int totalCpuBurstTime = 0;
        int totalIoBurstTime = 0;
        int totalProcessesCompleted = 0;
        for (Process p : processList) {
            while (currentTime < p.arrivalTime) {
                currentTime += 1;
            }
            int cpuTime = 0;
            int ioTime = 0;
            while (p.cpuRequiredTime > 0) {
                int randomCPUBurst = random.nextInt(Math.max(1, p.cpuBurstTime - 1)) + 1;
                while (randomCPUBurst > 0) {
                    p.cpuRequiredTime -= 1;
                    cpuTime += 1;
                    currentTime += 1;
                    if (p.cpuRequiredTime == 0) {
                        break;
                    }
                    randomCPUBurst -= 1;
                    int randomIOBurst = random.nextInt(Math.max(1, p.ioBurstTime - 1)) + 1;
                    ioTime += randomIOBurst;
                    currentTime += randomIOBurst;
                }
            }
            finishingTime = currentTime;
            int turnaroundTime = finishingTime - p.arrivalTime;
            totalTurnaroundTime += turnaroundTime;
            int waitTime = turnaroundTime - cpuTime -ioTime;
            totalWaitingTime += (turnaroundTime - cpuTime - ioTime);
            totalCpuBurstTime += cpuTime;
            totalIoBurstTime += ioTime;
            totalProcessesCompleted++;

            System.out.println("Finishing Time : " + finishingTime);
            System.out.println("Turnaround Time : " + turnaroundTime);
            System.out.println("CPU time : " + cpuTime);
            System.out.println("I/O time : " + ioTime);
            System.out.println("Waiting time : " + waitTime);
            System.out.println("-----------------");
        }
        System.out.println("============= [ Summary data of Executed Scheduler ] =============");
        System.out.println("Scheduler Finishing Time : "+finishingTime);
        System.out.println("Average turnaround time : "+ String.format("%.2f", ((double)totalTurnaroundTime/processList.size())));
        System.out.println("Average Waiting Time: " +  String.format("%.2f", ((double) totalWaitingTime / processList.size())));
        System.out.println("CPU Utilization: " + String.format("%.2f",((double) totalCpuBurstTime / finishingTime) * 100)+ "%");
        System.out.println("I/O Utilization: " + String.format("%.2f",((double) totalIoBurstTime / finishingTime) * 100)+ "%");
        System.out.println("Throughput (Processes completed per hundred time units): " + String.format("%.2f",((double)totalProcessesCompleted/(finishingTime)*100)));

    }
}
class SJF implements Scheduler {
    private List<Process> processList;

    public SJF(List<Process> processList) {
        this.processList = processList;
    }

    public void run() {
        System.out.println("--------SJF--------");
        Random random = new Random();
        int currentTime = 0;
        int finishingTime=0;
        int totalTurnaroundTime = 0;
        int totalWaitingTime = 0;
        int totalCpuBurstTime = 0;
        int totalIoBurstTime = 0;
        int totalProcessesCompleted = 0;
        List<Process> remainingProcesses = new ArrayList<>(processList);
        System.out.println(remainingProcesses.size());


        for (int i=0; i<processList.size();i++) {
            Process selectedProcess = null;
            int selectedIndex = -1;
            if(remainingProcesses.get(0).arrivalTime<currentTime){
                Process targetProcess = null;
                for(Process process:remainingProcesses){
                    if(process.getArrivalTime()<=currentTime){
                        targetProcess=process;
                        break;
                    }
                }
                if(targetProcess !=null){
                    int leastB = Integer.MAX_VALUE;
                    int targetIndex =-1;
                    for (int k = 0; k<remainingProcesses.size(); k++){
                        Process process = remainingProcesses.get(k);
                        if(process.getArrivalTime()<=currentTime && process.getCpuBurstTime()<leastB){
                            leastB=process.getCpuBurstTime();
                            targetIndex = k;
                            selectedIndex=k;
                        }
                        selectedProcess = remainingProcesses.get(targetIndex);
                    }
                }
            } else if(remainingProcesses.get(0).arrivalTime>=currentTime){ ////
                remainingProcesses.sort(Comparator.comparingInt(p -> p.arrivalTime));
                selectedProcess = remainingProcesses.get(0);
            }else
                break;
            Process currentProcess = selectedProcess;
            if(currentTime < currentProcess.getArrivalTime()){
                currentTime=currentProcess.arrivalTime;
            }
            int cpuTime = 0;
            int ioTime = 0;
            while (currentProcess.cpuRequiredTime > 0) {
                int randomCPUBurst = random.nextInt(Math.max(1, currentProcess.cpuBurstTime - 1)) + 1;
                while (randomCPUBurst > 0) {
                    currentProcess.cpuRequiredTime -= 1;
                    cpuTime += 1;
                    currentTime += 1;
                    if (currentProcess.cpuRequiredTime == 0) {
                        break;
                    }
                    randomCPUBurst -= 1;
                    int randomIOBurst = random.nextInt(Math.max(1, currentProcess.ioBurstTime - 1)) + 1;
                    ioTime += randomIOBurst;
                    currentTime += randomIOBurst;
                }
            }
            finishingTime = currentTime;
            int turnaroundTime = finishingTime - currentProcess.arrivalTime;
            totalTurnaroundTime += turnaroundTime;
            int waitTime = turnaroundTime - cpuTime - ioTime;
            totalWaitingTime += waitTime;
            totalCpuBurstTime += cpuTime;
            totalIoBurstTime += ioTime;
            totalProcessesCompleted++;
            System.out.println("Finishing Time: " + finishingTime);
            System.out.println("Turnaround Time: " + turnaroundTime);
            System.out.println("CPU Time: " + cpuTime);
            System.out.println("I/O Time: " + ioTime);
            System.out.println("Waiting Time: " + waitTime);
            System.out.println("-----------------");
            remainingProcesses.remove(selectedProcess); // 스케줄링이 끝난 프로세스 제거
        }
        System.out.println("============= [ Summary data of Executed Scheduler ] =============");
        System.out.println("Scheduler Finishing Time : "+ finishingTime);
        System.out.println("Average turnaround time : "+ String.format("%.2f", ((double)totalTurnaroundTime/processList.size())));
        System.out.println("Average Waiting Time: " +  String.format("%.2f", ((double) totalWaitingTime / processList.size())));
        System.out.println("CPU Utilization: " + String.format("%.2f",((double) totalCpuBurstTime / finishingTime) * 100 )+ "%");
        System.out.println("I/O Utilization: " + String.format("%.2f",((double) totalIoBurstTime / finishingTime) * 100)+ "%");
        System.out.println("Throughput (Processes completed per hundred time units): " + String.format("%.2f",((double)totalProcessesCompleted/(finishingTime)*100)));

    }
}
class RoundRobin implements Scheduler {
    private List<Process> processList;
    private int quantum;

    public RoundRobin(List<Process> processList, int quantum) {
        this.processList = processList;
        this.quantum = quantum;
    }

    public void run() {
        System.out.println("--------Round Robin--------");
        List<Process>listProcess = new ArrayList<>(processList);
        Queue<Process>readyQueue = new LinkedList<>();
        Random random = new Random();

        int currentTime=0;
        int totalCpuBurstTime=0;
        int totalTurnaroundTime =0;
        int totalWaitingTime = 0;
        int totalProcessesCompleted = 0;
        Process firstProcess = listProcess.get(0);
        if(firstProcess.getArrivalTime()>0){
            currentTime+=firstProcess.getArrivalTime();
        }
        readyQueue.offer(firstProcess);
        listProcess.remove(firstProcess);
        while(!readyQueue.isEmpty() || !listProcess.isEmpty()){
            int execQuantum = quantum;

            while (!listProcess.isEmpty() && listProcess.get(0).getArrivalTime() <= currentTime) {
                Process process = listProcess.get(0);
                readyQueue.offer(process);
                listProcess.remove(0);
            }
            if(readyQueue.isEmpty()&&!listProcess.isEmpty()){
                currentTime = listProcess.get(0).getArrivalTime();
                readyQueue.offer(listProcess.get(0));
                listProcess.remove(0);
            }

            int sumIOBurstTime=0;
            Process currentProcess = readyQueue.poll();
            if(currentProcess.getStartProcessTime()==-1){
                currentProcess.setStartProcessTime(currentTime);
                totalWaitingTime+=currentProcess.getStartProcessTime();
            }
            int randomCPUBurst = random.nextInt(Math.max(1, currentProcess.cpuBurstTime - 1)) + 1;
            int execCPUBurst = Math.min(quantum, randomCPUBurst);
            currentTime+=execCPUBurst;
            totalCpuBurstTime+=execCPUBurst;
            execQuantum -= execCPUBurst;
            int remainCPUBurst = currentProcess.cpuRequiredTime - execCPUBurst;
            if(execQuantum>0){
                while(execCPUBurst>0 && execQuantum>0 && remainCPUBurst>0){
                    int randomIOBurst = random.nextInt(Math.max(1, currentProcess.ioBurstTime - 1)) + 1;
                    int exeIOBurst = Math.min(execQuantum, randomIOBurst);
                    currentTime +=exeIOBurst;
                    execQuantum -=exeIOBurst;
                    sumIOBurstTime+= exeIOBurst;
                    currentProcess.setburstedIOTime(sumIOBurstTime);

                    int random2CPUBurst = random.nextInt(Math.max(1, currentProcess.cpuBurstTime - 1)) + 1;
                    int exec2CPUBurst = Math.min(execQuantum, random2CPUBurst);
                    currentTime+=exec2CPUBurst;
                    totalCpuBurstTime+=exec2CPUBurst;


                    execQuantum -= exec2CPUBurst;
                    remainCPUBurst -= exec2CPUBurst;


                    if(remainCPUBurst<0){
                        currentTime += Math.abs(remainCPUBurst);
                        break;
                    }
                    if (remainCPUBurst == 0) {
                        break;
                    }
                }
            }

            if(execQuantum==0 && remainCPUBurst>0){
                currentProcess.setCpuRequiredTime(remainCPUBurst); // cpuRequiredTime 수정
                readyQueue.offer(currentProcess);

            }
            if(remainCPUBurst == 0){
                totalProcessesCompleted++;
                totalTurnaroundTime+=(currentTime-currentProcess.getArrivalTime());
                System.out.println("Finishing Time: " + currentTime);
                System.out.println("Turnaround Time: " + (currentTime-currentProcess.getArrivalTime()));
                System.out.println("CPU Time: " + currentProcess.getBurstedCpuTime());
                System.out.println("I/O Time: " + currentProcess.getBurstedIOTime());
                System.out.println("Waiting Time: " + currentProcess.getStartProcessTime());
                System.out.println("-----------------");

            }


        }
        double cpuUtilization =((double) totalCpuBurstTime / currentTime);
        double ioUtilization = 1-cpuUtilization;
        System.out.println("============= [ Summary data of Executed Scheduler ] =============");
        System.out.println("Scheduler Finishing Time : "+ currentTime);
        System.out.println("Average turnaround time : "+ String.format("%.2f", ((double)totalTurnaroundTime/processList.size())));
        System.out.println("Average Waiting Time: " +  String.format("%.2f", ((double) totalWaitingTime / processList.size())));
        System.out.println("CPU Utilization: " + String.format("%.2f",cpuUtilization*100)+ "%");
        System.out.println("I/O Utilization: " + String.format("%.2f",ioUtilization*100)+ "%");
        System.out.println("Throughput (Processes completed per hundred time units): " + String.format("%.2f",((double)totalProcessesCompleted/(currentTime)*100)));

    }
}



public class Main {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("홍익대학교 소프트웨어융합학과 B989001 강보찬\n");
            System.out.println("23-1학기 운영체제 프로그래밍 과제입니다. \n");
            System.out.println("java Main <data 파일명> <스케줄링 이름> <Quantum number>");
            System.out.println("예시) java data.txt FCFS 0 \n java data.txt SJF 0\n java data.txt RoundRobin 10");
            System.out.println("다시 예시에 맞게 실행해주세요 :)");
            return;
        }

        String inputFile = args[0];
        String schedulingAlgorithm = args[1];
        int timeQuantum = Integer.parseInt(args[2]);

        SchedulingSimulation schedulingSimulation = SchedulingSimulation.getInstance();
        schedulingSimulation.setProcessDataToLaunch(inputFile);

        Scheduler scheduler;
        switch (schedulingAlgorithm) {
            case "FCFS":
                scheduler = new FCFS(schedulingSimulation.getSetProcessInfo());
                break;
            case "SJF":
                scheduler = new SJF(schedulingSimulation.getSetProcessInfo());
                break;
            case "RoundRobin":
                scheduler = new RoundRobin(schedulingSimulation.getSetProcessInfo(), timeQuantum);
                break;
            default:
                System.out.println("Invalid scheduling algorithm.");
                return;
        }
        scheduler.run();
    }
}