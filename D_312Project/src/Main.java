import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Main {

    public static int globalPid = 0;

    public static void main(String[] args) {

        Scanner userInput = new Scanner(System.in);
        System.out.println("Process #: ");
        int processNumber = userInput.nextInt();
        userInput.close();

        ArrayList<Process> processArrayList = new ArrayList<>();

        String filePath = new File("processList.txt").getAbsolutePath();

        try {
            PrintWriter writer = new PrintWriter(filePath, "UTF-8");

            for (int i = 0; i < processNumber; i++) {

                globalPid = globalPid + 1;

                writer.printf("\r\nprocess%d %d \r\n", i, i);

                Random rand = new Random();
                int instructionNumber = rand.nextInt(25) + 25;
                int criticalSection = rand.nextInt(instructionNumber);
                int currentInstructionNumber = 0;
                boolean hasInstruction = true;

                while (hasInstruction) {
                    if(currentInstructionNumber >= instructionNumber){
                        hasInstruction = false;
                    }
                    if (currentInstructionNumber < instructionNumber) {
                        int instructionTypeVariable = rand.nextInt(4);
                        int instructionTickVariable = rand.nextInt(25) + 1;

                        if(instructionTypeVariable == 0) {
                            if (currentInstructionNumber == criticalSection) {
                                writer.printf("<CRITICAL_SECTION>COMPUTE: %d \r\n", instructionTickVariable);
                            } else {
                                writer.printf("COMPUTE: %d \r\n", instructionTickVariable);
                            }
                        }else if(instructionTypeVariable == 1) {
                            if (currentInstructionNumber == criticalSection) {
                                writer.printf("<CRITICAL_SECTION>IO: %d \r\n", instructionTickVariable);
                            } else {
                                writer.printf("IO: %d \r\n", instructionTickVariable);
                            }
                        }else if(instructionTypeVariable == 2) {
                            if (currentInstructionNumber == criticalSection) {
                                writer.printf("<CRITICAL_SECTION>YIELD: %d \r\n", instructionTickVariable);
                            } else {
                                writer.printf("YIELD: %d \r\n", instructionTickVariable);
                            }
                        }else if(instructionTypeVariable == 3) {
                            writer.printf("FORK \r\n");
                        }
                    } else {
                        int instructionTypeVariable = rand.nextInt(4);
                        int instructionTickVariable = rand.nextInt(25) + 25;

                        if(instructionTypeVariable == 0) {
                            if (currentInstructionNumber == criticalSection) {
                                writer.printf("<CRITICAL_SECTION>COMPUTE: %d \r\n", instructionTickVariable);
                            } else {
                                writer.printf("COMPUTE: %d", instructionTickVariable);
                            }
                        }else if(instructionTypeVariable == 1) {
                            if (currentInstructionNumber == criticalSection) {
                                writer.printf("<CRITICAL_SECTION>IO: %d \r\n", instructionTickVariable);
                            } else {
                                writer.printf("IO: %d", instructionTickVariable);
                            }
                        }else if(instructionTypeVariable == 2) {
                            if (currentInstructionNumber == criticalSection) {
                                writer.printf("<CRITICAL_SECTION>YIELD: %d \r\n", instructionTickVariable);
                            } else {
                                writer.printf("YIELD: %d", instructionTickVariable);
                            }
                        }else if(instructionTypeVariable == 3){
                                writer.printf("FORK \r\n");
                        }
                    }
                    currentInstructionNumber++;
                }
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String firstCpu = "firstCpu";
        String secondCpu = "secondCpu";

        MMU mmu = new MMU();
        CPU cpu1 = new CPU(mmu, 32, firstCpu);
        CPU cpu2 = new CPU(mmu, 32, secondCpu);
        Process process = new Process(0);
        Instruction instruction;
        InstructionEnum instructionType;

        String processName = null;
        int ID = 0;
        String instructionName;
        int instructionTicks;

        try {
            FileReader fileReader = new FileReader(filePath);
            Scanner scanner = new Scanner(fileReader);

            int halfInstructions = processNumber / 2;

            while (scanner.hasNextLine()) {
                if(scanner.hasNext()) {
                    instructionName = scanner.next();

//                    switch (instructionName) {
//                        case "COMPUTE:":
                    if(instructionName.equals("COMPUTE:")) {
                        instructionType = InstructionEnum.COMPUTE;
                        instructionTicks = scanner.nextInt();
                        instruction = new Instruction(instructionType, instructionTicks, false);
                        process.addInstruction(instruction);
//                            break;
//                        case "IO:":
                    }else if(instructionName.equals("IO:")) {
                        instructionType = InstructionEnum.IO;
                        instructionTicks = scanner.nextInt();
                        instruction = new Instruction(instructionType, instructionTicks, false);
                        process.addInstruction(instruction);
//                            break;
//                        case "YIELD:":
                    }else if(instructionName.equals("YIELD:")) {
                        instructionType = InstructionEnum.YIELD;
                        instructionTicks = scanner.nextInt();
                        instruction = new Instruction(instructionType, instructionTicks, false);
                        process.addInstruction(instruction);
//                            break;
//                        case "<CRITICAL_SECTION>COMPUTE:":
                    }else if(instructionName.equals("<CRITICAL_SECTION>COMPUTE:")) {
                        instructionType = InstructionEnum.COMPUTE;
                        instructionTicks = scanner.nextInt();
                        instruction = new Instruction(instructionType, instructionTicks, true);
                        process.addInstruction(instruction);
//                            break;
//                        case "<CRITICAL_SECTION>IO:":
                    }else if(instructionName.equals("<CRITICAL_SECTION>IO:")) {
                        instructionType = InstructionEnum.IO;
                        instructionTicks = scanner.nextInt();
                        instruction = new Instruction(instructionType, instructionTicks, true);
                        process.addInstruction(instruction);
//                            break;
//                        case "<CRITICAL_SECTION>YIELD:":
                    }else if(instructionName.equals("<CRITICAL_SECTION>YIELD:")) {
                        instructionType = InstructionEnum.YIELD;
                        instructionTicks = scanner.nextInt();
                        instruction = new Instruction(instructionType, instructionTicks, true);
                        process.addInstruction(instruction);
//                            break;
//                        case "FORK":
                    }else if(instructionName.equals("FORK")) {
                        process.fork();
//                            break;
//                        case "reqestResources":
                    }else if(instructionName.equals("reqestResources")) {
                        process.addInstruction(new Instruction(InstructionEnum.RESOURCE));
                    }else{
                            if (process.instructions.isEmpty()) {
                                processName = instructionName;
                                ID = scanner.nextInt();
                                process.setName(processName);
                                process.setID(ID);
                            } else {
                                processArrayList.add(process);
                                processName = instructionName;
                                ID = scanner.nextInt();
                                process = new Process(0);
                                process.setName(processName);
                                process.setID(ID);
                            }
                    }
                }
            }

            processArrayList.add(process);
            for(Process p : processArrayList){
                if(halfInstructions > 0){
//                    processExists(p, cpu1);
                    if(p != null){
                        cpu1.addProcess(p);
                    }
                    halfInstructions = halfInstructions - 1;
                } else {
//                    processExists(p, cpu2);
                    if(p != null){
                        cpu1.addProcess(p);
                    }
                }
            }
            scanner.close();
            fileReader.close();
        } catch (Exception e) {
            System.out.println("Failed to reead File");
            e.printStackTrace();
        }

        Gui gui = new Gui(cpu1, cpu1, mmu, processArrayList);
        gui.viewGui();

    }

//    private static void processExists(Process process, CPU cpu) {
//        if (process != null) {
//            cpu.addProcess(process);
//        }
//    }
}

