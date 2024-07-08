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
   1. Since the Core does not have any dependencies, you can run it via IDE
   2. Or you can build it with Maven and run the jar file: `mvn clean package`
    and `java -jar out/RARS_Reborn_Core-1.0.jar`

## Usage
See [Example.java](src/rarsreborn/core/Example.java) to learn how to setup 
the simulator and retrieve data.
