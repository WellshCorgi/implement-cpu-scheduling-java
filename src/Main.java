import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.util.*;


class Process { //PCB 프로세스를 객체로 생성하여 정보 저장
    int arrivalTime;
    int cpuRequiredTime;
    int cpuBurstTime;
    int ioBurstTime;
    int startProcessTime = -1;
    int burstedCpuTime = 0;
    int burstedIOTime = 0;
    int pid = 0;

    public Process(int arrivalTime, int cpuRequiredTime, int cpuBurstTime, int ioBurstTime, int startProcessTime, int burstedCpuTime, int burstedIOTime, int pid) {
        this.arrivalTime = arrivalTime;
        this.cpuRequiredTime = cpuRequiredTime;
        this.cpuBurstTime = cpuBurstTime;
        this.ioBurstTime = ioBurstTime;
        this.burstedCpuTime = cpuRequiredTime;
        this.pid =pid;
    }
    public int getStartProcessTime() {
        return startProcessTime;
    }
    public void setStartProcessTime(int startProcessTime) {
        this.startProcessTime = startProcessTime;
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
        // 생성자가 외부에서 호출 못하게 private 지정(싱글톤 패턴)
    }
    public static SchedulingSimulation getInstance(){
        if(schedulingSimulation == null) {
            schedulingSimulation = new SchedulingSimulation();
        }
        return schedulingSimulation;
    }

    public void setProcessDataToLaunch(String data){ //data.txt 의 입력 값 input에 맞게 setting
        try{
            BufferedReader br = new BufferedReader(new FileReader(data));
            String inputProcessInfo = br.readLine();
            List<Integer> setProcessInfoFromFile = new ArrayList<>();
            while (inputProcessInfo != null) {
                StringTokenizer st = new StringTokenizer(inputProcessInfo, "() "); //입력값 치환
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
            System.out.println("파일 입출력 에서 오류발생!!! data.txt를 확인바랍니다.");
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
    private void launchSimulation( List<Integer> sp){
        List<List<Integer>> groupedProcesses = groupProcessInfo(sp,4);// 프로세스 정보 4개씩 그룹화
        List<Process> processList = new ArrayList<>();
        int num=1;
        for(List<Integer> process : groupedProcesses){
            Process p = new Process(process.get(0), process.get(1),process.get(2), process.get(3),0,0,0,num);
            processList.add(p);
            num++;
        }
        processList.sort(Comparator.comparingInt(p -> p.arrivalTime));// 도착한 순서대로 정렬
        setProcessInfo = processList;
    }
    public List<Process> getSetProcessInfo() {
        return setProcessInfo;
    }
}
interface Scheduler{
    void run();
}
class FCFS implements Scheduler{
    private List<Process> processList;

    public FCFS(List<Process> processList) {
        this.processList = processList;
    }

    public void run() {
        System.out.println("--------FCFS--------");
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
            while (!listProcess.isEmpty() && listProcess.get(0).getArrivalTime() <= currentTime) {
                Process process = listProcess.get(0); // 현재시간 이전에 도착한 프로세스들 래디 큐에 삽입
                readyQueue.offer(process);
                listProcess.remove(0);
            }
            if(readyQueue.isEmpty()&&!listProcess.isEmpty()){ //레디큐는 비어있으나 도착시간이 뒤에있어 프로세스 리스트에 남아있는 상황
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
            int randomCPUBurst = random.nextInt(currentProcess.cpuBurstTime + 1);
            int execCPUBurst = Math.min(currentProcess.cpuRequiredTime, randomCPUBurst);
            currentTime+=execCPUBurst;
            totalCpuBurstTime+=execCPUBurst;
            int remainCPUBurst = currentProcess.cpuRequiredTime - execCPUBurst;
            if(remainCPUBurst>0){
                int randomIOBurst = random.nextInt(currentProcess.ioBurstTime +1);
                currentTime +=randomIOBurst;
                sumIOBurstTime= currentProcess.burstedIOTime+randomIOBurst;
                currentProcess.setburstedIOTime(sumIOBurstTime);
                currentProcess.setCpuRequiredTime(remainCPUBurst); // cpuRequiredTime 수정
                readyQueue.offer(currentProcess);

            } else if (remainCPUBurst == 0){
                totalProcessesCompleted++;
                totalTurnaroundTime+=(currentTime-currentProcess.getArrivalTime());
                System.out.println("Process ID: "+currentProcess.pid);
                System.out.println("Finishing Time: " + currentTime);
                System.out.println("Turnaround Time: " + (currentTime-currentProcess.getArrivalTime()));
                System.out.println("CPU Time: " + currentProcess.getBurstedCpuTime());
                System.out.println("I/O Time: " + currentProcess.burstedIOTime);
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
class SJF implements Scheduler {
    private List<Process> processList;

    public SJF(List<Process> processList) {
        this.processList = processList;
    }

    public void run() {
        System.out.println("--------SJF--------");
        List<Process>listProcess = new ArrayList<>(processList);
        PriorityQueue<Process>readyQueue = new PriorityQueue<>(Comparator.comparingInt(Process::getCpuRequiredTime));
        Queue<Process>waitQueue= new LinkedList<>();
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
            while (!listProcess.isEmpty() && listProcess.get(0).getArrivalTime() <= currentTime) {
                Process process = listProcess.get(0); // 현재시간 이전에 도착한 프로세스들 래디 큐에 삽입
                readyQueue.offer(process);
                listProcess.remove(0);
            }
            if(readyQueue.isEmpty()&&!listProcess.isEmpty()){ //레디큐는 비어있으나 도착시간이 뒤에있어 프로세스 리스트에 남아있는 상황
                currentTime = listProcess.get(0).getArrivalTime();
                readyQueue.offer(listProcess.get(0));
                listProcess.remove(0);
            }
            int sumIOBurstTime=0;
            Process currentProcess = readyQueue.poll();
            if(!waitQueue.isEmpty()){
                readyQueue.offer(waitQueue.poll());
            }
            if(currentProcess.getStartProcessTime()==-1){
                currentProcess.setStartProcessTime(currentTime);
                totalWaitingTime+=currentProcess.getStartProcessTime();
            }
            int randomCPUBurst = random.nextInt(currentProcess.cpuBurstTime + 1);
            int execCPUBurst = Math.min(currentProcess.cpuRequiredTime, randomCPUBurst);
            currentTime+=execCPUBurst;
            totalCpuBurstTime+=execCPUBurst;
            int remainCPUBurst = currentProcess.cpuRequiredTime - execCPUBurst;
            if(remainCPUBurst>0){
                int randomIOBurst = random.nextInt(currentProcess.ioBurstTime +1);
                currentTime +=randomIOBurst;
                sumIOBurstTime= currentProcess.burstedIOTime+randomIOBurst;
                currentProcess.setburstedIOTime(sumIOBurstTime);
                currentProcess.setCpuRequiredTime(remainCPUBurst); // cpuRequiredTime 수정
                Process nextProcess = readyQueue.peek();
                if(nextProcess == null){
                    readyQueue.add(currentProcess);
                }else {
                    waitQueue.offer(currentProcess);
                }




            } else if (remainCPUBurst == 0){

                totalProcessesCompleted++;
                totalTurnaroundTime+=(currentTime-currentProcess.getArrivalTime());
                System.out.println("Process ID: "+currentProcess.pid);
                System.out.println("Finishing Time: " + currentTime);
                System.out.println("Turnaround Time: " + (currentTime-currentProcess.getArrivalTime()));
                System.out.println("CPU Time: " + currentProcess.getBurstedCpuTime());
                System.out.println("I/O Time: " + currentProcess.burstedIOTime);
                System.out.println("Waiting Time: " + currentProcess.getStartProcessTime());
                System.out.println("-----------------");
                readyQueue.remove(currentProcess);

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
                Process process = listProcess.get(0); // 현재시간 이전에 도착한 프로세스들 래디 큐에 삽입
                readyQueue.offer(process);
                listProcess.remove(0);
            }
            if(readyQueue.isEmpty()&&!listProcess.isEmpty()){ //레디큐는 비어있으나 도착시간이 뒤에있어 프로세스 리스트에 남아있는 상황
                currentTime = listProcess.get(0).getArrivalTime();
                readyQueue.offer(listProcess.get(0));
                listProcess.remove(0);
            }

            Process currentProcess = readyQueue.poll();
            if(currentProcess.getStartProcessTime()==-1){
                currentProcess.setStartProcessTime(currentTime);
                totalWaitingTime+=currentProcess.getStartProcessTime();
            }
            int randomCPUBurst = random.nextInt(currentProcess.cpuBurstTime + 1);
            int execCPUBurst = Math.min(randomCPUBurst,currentProcess.cpuRequiredTime);
            if(execCPUBurst>=quantum){
                execCPUBurst = quantum;
            }
            currentTime+=execCPUBurst;
            totalCpuBurstTime+=execCPUBurst;
            execQuantum -= execCPUBurst;
            int remainCPUBurst = currentProcess.cpuRequiredTime - execCPUBurst;
            if(remainCPUBurst>0 && execQuantum>0){
                int randomIOBurst = random.nextInt(currentProcess.ioBurstTime + 1);
                int exeIOBurst = Math.min(execQuantum, randomIOBurst);
                currentTime +=exeIOBurst;
                int sumIOBurstTime = currentProcess.getBurstedIOTime()+exeIOBurst;
                currentProcess.setburstedIOTime(sumIOBurstTime);
                currentProcess.setCpuRequiredTime(remainCPUBurst);
                readyQueue.offer(currentProcess);
            }

            if(execQuantum==0 && remainCPUBurst>0){
                currentProcess.setCpuRequiredTime(remainCPUBurst); // cpuRequiredTime 수정
                readyQueue.offer(currentProcess);

            }
            if(remainCPUBurst == 0){
                totalProcessesCompleted++;
                totalTurnaroundTime+=(currentTime-currentProcess.getArrivalTime());
                System.out.println("Process ID: "+currentProcess.pid);
                System.out.println("Finishing Time: " + currentTime);
                System.out.println("Turnaround Time: " + (currentTime-currentProcess.getArrivalTime()));
                System.out.println("CPU Time: " + currentProcess.getBurstedCpuTime());
                System.out.println("I/O Time: " + currentProcess.burstedIOTime);
                System.out.println("Waiting Time: " + currentProcess.getStartProcessTime());
                System.out.println("-----------------");
                readyQueue.remove(currentProcess);
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