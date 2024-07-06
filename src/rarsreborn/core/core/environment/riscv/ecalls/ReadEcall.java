package rarsreborn.core.core.environment.riscv.ecalls;

import rarsreborn.core.core.environment.riscv.RiscVSystemCall;
import rarsreborn.core.exceptions.memory.MemoryAccessException;

import java.util.Scanner;

public class ReadEcall extends RiscVSystemCall {
    @Override
    public void call() throws MemoryAccessException {
        Scanner scanner = new Scanner(System.in);
        String s = scanner.nextLine();
        long address = getRegisterValue(10); // a0
        for (int i = 0; i < s.length(); i++) {
            memory.setByte(address + i, (byte) s.charAt(i));
        }
    }
}
