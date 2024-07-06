package rarsreborn.core;

import rarsreborn.core.core.environment.StringInputDevice;
import rarsreborn.core.core.memory.IMemory;
import rarsreborn.core.core.memory.Memory32;
import rarsreborn.core.core.register.Register32File;
import rarsreborn.core.events.ConsolePrintEvent;
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
            simulator.subscribeEvent(
                ConsolePrintEvent.class,
                consolePrintEvent -> System.out.print(consolePrintEvent.text())
            );

            Register32File registers = simulator.getRegisterFile();
            IMemory memory = simulator.getMemory();

            String content = new Scanner(new File("src/rarsreborn/core/example.s")).useDelimiter("\\Z").next();
            simulator.compile(content);
            simulator.run();

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
}

