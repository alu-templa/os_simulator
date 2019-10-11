import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

public class Gui extends JFrame implements ActionListener {
    private CPU cpu1;
    private CPU cpu2;
    private MMU mmu;
    private ArrayList<Process> processes;

    private int currentCpuTick;
    private boolean finished;

    private JPanel panel;

    private JScrollPane scrollPaneText;
    private JTextArea textArea;

    private JScrollPane scrollPaneRunning;
    private JTextArea textAreaRunning;

    private JScrollPane scrollPaneProcess;
    private JTextArea textAreaProcess;

    private JTextField textFieldRunCycles;
    private JTextField textFieldAddProcesses;

    private JButton b1;
    private JButton b2;
    private JButton b3;

    public Gui(CPU cpu1, CPU cpu2, MMU mmu,
               ArrayList<Process> processes) {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        setAlwaysOnTop(true);
        int width = dimension.width / 5;
        int height = dimension.height / 5;
        int rows = 25;
        int columns = 60;

        this.setLocation(width, height);
        this.panel = new JPanel();

        this.textArea = new JTextArea(rows, columns);
        textAreaSettings(textArea);
        this.scrollPaneText = new JScrollPane(textArea);

        this.textAreaRunning = new JTextArea(rows, columns);
        textAreaSettings(textAreaRunning);
        this.scrollPaneRunning = new JScrollPane(textAreaRunning);

        this.textAreaProcess = new JTextArea(rows, columns);
        textAreaSettings(textAreaProcess);
        this.scrollPaneProcess = new JScrollPane(textAreaProcess);


        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(969, 969);

        b1 = new JButton("Cycles to run");
        b1.addActionListener(this);
        b1.setActionCommand("b1");

        b2 = new JButton("Add process");
        b2.addActionListener(this);
        b2.setActionCommand("b2");

        b3 = new JButton("Run all cycles");
        b3.addActionListener(this);
        b3.setActionCommand("b3");

        textFieldRunCycles = new JTextField("");
        textFieldRunCycles.setColumns(3);
        textFieldAddProcesses = new JTextField("");
        textFieldAddProcesses.setColumns(3);

        panel.add(textFieldAddProcesses);
        panel.add(b2);
        panel.add(textFieldRunCycles);
        panel.add(b1);
        panel.add(b3);

        this.getContentPane().add(BorderLayout.SOUTH, panel);
        this.getContentPane().add(BorderLayout.CENTER, scrollPaneText);
        this.getContentPane().add(BorderLayout.EAST, scrollPaneRunning);
        this.getContentPane().add(BorderLayout.NORTH, scrollPaneProcess);

        this.cpu1 = cpu1;
        this.cpu2 = cpu2;
        this.mmu = mmu;
        this.processes = processes;
        this.finished = false;
        this.currentCpuTick = 0;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String buttonPress = e.getActionCommand();
        boolean cpuFinished;

        if (buttonPress.equals("b1")) {
            String cyclesToRun = textFieldRunCycles.getText();
            int cycles;
            if (!cyclesToRun.isEmpty()) {
                cycles = Integer.parseInt(cyclesToRun);
            } else {
                cycles = 1;
            }
            textArea.append("Cycles to run: " + cycles + "\n");
            for (int i = 0; i < cycles; i++) {
                runCycle(cpu1, cpu2);
                currentCpuTick = currentCpuTick + 1;
                cpuFinished = finishedCores(cpu1, cpu2);
                if (cpuFinished) {
                    finished = true;
                }
            }
        } else if (buttonPress.equals("b2")) {
            String processesToSpawn = textFieldAddProcesses.getText();
            int toSpawn;
            if (!processesToSpawn.isEmpty()) {
                toSpawn = Integer.parseInt(processesToSpawn);
            } else {
                toSpawn = 1;
            }
            for (int i = 0; i < toSpawn; i++) {
                spawnProcess(cpu1, processes);
                spawnProcess(cpu2, processes);
                currentCpuTick = currentCpuTick + 1;
                textArea.append(toSpawn + " New Processes Added \n");
                finished = false;
            }
        } else if (buttonPress.equals("b3")) {
            while (!finished) {
                runCycle(cpu1, cpu2);
                cpuFinished = finishedCores(cpu1, cpu2);
                currentCpuTick = currentCpuTick + 1;
                if (cpuFinished) {
                    textArea.append("All cycles complete\n");
                    finished = true;
                }
            }
        }
        printProcesses();
        textArea.append("Current Tick: " + currentCpuTick + "\n");
        printRunningProcesses();
    }

    public void viewGui() {
        this.setVisible(true);
    }

    public void printRunningProcesses(){
        textAreaRunning.setText("");
        for(Process p : processes){
            if(p.getCurrentState() == CurrentState.RUNNING){
                textAreaRunning.append(p + " " + "\n");
            }
        }
    }

    private void printProcesses(){
        textAreaProcess.setText("");
        for(Process p : processes){
            textAreaProcess.append(p + "\n");

        }
    }

    private void spawnProcess(CPU cpu, ArrayList<Process> processes) {
        Random rand = new Random();
        int processId = Main.globalPid++;
        Main.globalPid = Main.globalPid++;


        String stringifiedProcessId = Integer.toString(processId);
        String processName = "process";
        processName = processName + stringifiedProcessId;

        Process process = new Process(processId);
        process.setName(processName);

        int instructionNumber = rand.nextInt(25) + 1;
        int criticalSection = rand.nextInt(instructionNumber);

        for (int i = 0; i < instructionNumber; i++) {
            int instructionType = rand.nextInt(3);
            int instructionTicks = rand.nextInt(25) + 1;
            if(instructionType == 0) {
                if (i == criticalSection) {
                    process.addInstruction(new Instruction(InstructionEnum.COMPUTE, instructionTicks, true));
                } else {
                    process.addInstruction(new Instruction(InstructionEnum.COMPUTE, instructionTicks, false));
                }
            } else if(instructionType == 1) {
                if (i == criticalSection) {
                    process.addInstruction(new Instruction(InstructionEnum.IO, instructionTicks, true));
                } else {
                    process.addInstruction(new Instruction(InstructionEnum.IO, instructionTicks, false));
                }
            }else if(instructionType == 2) {
                if (i == criticalSection) {
                    process.addInstruction(new Instruction(InstructionEnum.IO, instructionTicks, true));
                } else {
                    process.addInstruction(new Instruction(InstructionEnum.IO, instructionTicks, false));
                }
            }
        }
        if(!processes.contains(process)) {
            processes.add(process);
            cpu.addProcess(process);
        }

    }


    private void cpuRun(CPU cpu) {
        if (cpu.cpuStillRunning()) {
            cpu.run();
        }
    }

    private void runCycle(CPU cpu1, CPU cpu2){
        cpuRun(cpu1);
        cpuRun(cpu2);
    }

    private boolean finishedCores(CPU cpu1, CPU cpu2){
        boolean cpu1Finished = !cpu1.cpuStillRunning();
        boolean cpu2Finished = !cpu2.cpuStillRunning();
        return cpu1Finished && cpu2Finished;
    }

    private void textAreaSettings(JTextArea textArea) {
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
    }
}
