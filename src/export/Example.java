package export;

import core.memory.IMemory;
import core.memory.Memory32;
import core.register.Register32File;
import simulator.Simulator32;

import java.util.Arrays;

public class Example {
    public static void main(String[] args) {
        try {
            Simulator32 simulator = Presets.classical;
            Register32File registers = simulator.getRegisterFile();
            IMemory memory = simulator.getMemory();

            simulator.compile("""
                       .data
                            h: .string "123"
                       .text
                           jal t2, test
                           addi t0, t0, 1
                       test:
                           addi t1, t0, 2
                    """);
            simulator.run();

            System.out.printf("t0: %d\n", registers.getRegisterByName("t0").getValue());
            System.out.printf("t1: %d\n", registers.getRegisterByName("t1").getValue());
            System.out.printf("t2: %d\n", registers.getRegisterByName("t2").getValue());
            System.out.printf(Arrays.toString(memory.readBytes(Memory32.DATA_SECTION_START, 4)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

