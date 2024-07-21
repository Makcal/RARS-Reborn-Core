# RARS Reborn - Core

## Description

This is the core of the RARS Reborn project. The goal is to remake the old
RISC-V simulator [RARS](https://github.com/TheThirdOne/rars/),
improve architectural flexibility and update the GUI.

The Core contains main functionality to build your own modifications
of the simulator and some presets.

## Features

- General base for construction and simulators
- Registers (32 bit)
- Memory (RARS compatible sections)
- RISC-V instructions
- RISC-V system calls
- RISC-V parser, assembler, linker and decoder

## Installation and building

1. Clone the git repository
2. Run
    1. Since the Core does not have any dependencies, you can run it via IDE.
       Try to run [Example.java](src/rarsreborn/core/Example.java)
    2. Or you can build it with Maven and run the jar file:
    ```bash
    mvn clean package
    ```
    ```bash
    java -jar out/RARS_Reborn_Core-1.0.jar
    ```

## Usage

[Presets.java](src/rarsreborn/core/Presets.java) contains the prebuilt 32-bit
RISC-V simulator.

See [Example.java](src/rarsreborn/core/Example.java) to learn how to set up
the simulator and retrieve data.

Classes and packages that does not contain `riscv` in their name can
be used for building a general assembly simulator.

## Project structure

Here is a brief overview of the project structure and packages.

**Note:** all packages further will be referenced relatively to the root
[`rarsreborn.core`](src/rarsreborn/core).

### [`core`](src/rarsreborn/core/core)

This package contains interfaces and classes for main parts of
a computer and processor such as registers, RAM, assembly instructions, syscalls.

- All RISC-V instructions are stored in
  [`core.instruction.riscv.instructions`](src/rarsreborn/core/core/instruction/riscv/instructions)
- All RISC-V ecalls are stored in
  [`core.environment.riscv.ecalls`](src/rarsreborn/core/core/environment/riscv/ecalls)

### [`compilation`](src/rarsreborn/core/compilation)

This package contains classes for parsing, assembling, linking and decoding instructions.
The common scheme of executing is:

1. Preprocessing (modifying the source code as plain text)
2. Parsing into tokens
3. Compiling each file into an object file with encoded instructions, data, and other sections
4. Linking all object files together
5. Decoding and executing instructions one by one

### [`simulator`](src/rarsreborn/core/simulator)

This package contains classes for simulating the execution of instructions.
The main class is [`Simulator32`](src/rarsreborn/core/simulator/SimulatorRiscV.java)
that represents a 32-bit RISC-V simulator.

A typical program ([Example](src/rarsreborn/core/Example.java)) should obtain a simulator instance,
subscribe to events (e.g. syscalls), and run the simulation via `Simulator32.startWorker()`, preferably in
a separate thread.
