package rarsreborn.core;

import rarsreborn.core.core.environment.StringInputDevice;
import rarsreborn.core.core.memory.IMemory;
import rarsreborn.core.core.memory.Memory32;
import rarsreborn.core.core.register.Register32File;
import rarsreborn.core.core.environment.ConsolePrintEvent;
import rarsreborn.core.exceptions.execution.ExecutionException;
import rarsreborn.core.simulator.Simulator32;

import java.io.File;
import java.util.Arrays;
import java.util.Scanner;

public class Example {
    public static void main(String[] args) {
        try {
            Simulator32 simulator = Presets.getClassicalRiscVSimulator(new StringInputDevice() {
                @Override
                public String requestString(int count) {
                    Scanner scanner = new Scanner(System.in);
                    String s = scanner.nextLine() + "\n";
                    return s.length() <= count ? s : s.substring(0, count);
                }
            });
            simulator.getExecutionEnvironment().addObserver(
                ConsolePrintEvent.class,
                consolePrintEvent -> System.out.print(consolePrintEvent.text())
            );

            Register32File registers = simulator.getRegisterFile();
            IMemory memory = simulator.getMemory();

            String content = new Scanner(new File("src/rarsreborn/core/example.s")).useDelimiter("\\Z").next();
            simulator.compile(content);
            new Thread(() -> {
                try {
                    simulator.startWorker();
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }).start();
            waitUntilRunning(simulator);

            simulator.runSteps(9);
            waitUntilPaused(simulator);
            for (int i = 0; i < 5; i++) {
                simulator.stepBack();
            }
            simulator.runSteps(5);
            waitUntilPaused(simulator);
            for (int i = 0; i < 5; i++) {
                simulator.stepBack();
            }
            simulator.runSteps(5);
            waitUntilPaused(simulator);
            simulator.run();
            waitUntilStopped(simulator);

            System.out.printf("t0: 0x%x\n", registers.getRegisterByName("t0").getValue());
            System.out.printf("t1: 0x%x\n", registers.getRegisterByName("t1").getValue());
            System.out.printf("t2: %d\n", registers.getRegisterByName("t2").getValue());
            System.out.printf("t3: %d\n", registers.getRegisterByName("t3").getValue());
            System.out.printf("t4: %d\n", registers.getRegisterByName("t4").getValue());
            System.out.printf("t5: %d\n", registers.getRegisterByName("t5").getValue());
            System.out.println(Arrays.toString(memory.readBytes(Memory32.DATA_SECTION_START, 4)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void waitUntilRunning(Simulator32 simulator) {
        while (!simulator.isRunning()) {
            Thread.onSpinWait();
        }
    }

    private static void waitUntilStopped(Simulator32 simulator) {
        while (simulator.isRunning()) {
            Thread.onSpinWait();
        }
    }

    private static void waitUntilPaused(Simulator32 simulator) {
        while (!simulator.isPaused()) {
            Thread.onSpinWait();
        }
    }
}
