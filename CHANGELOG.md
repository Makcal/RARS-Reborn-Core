# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.3.1] - 2024-07-22

### Added

- Add end-line commentaries

### Changed

- Improve syntax error exceptions
- Update version to 1.3.1

## [1.3] - 2024-07-22

### Added

- Add event on before instruction execution; notify instruction executed after pc is updated
- Add a method to get the current instruction's number
- Add `nop` instruction
- Add `not` instruction
- Add `neg` instruction
- Add new instructions to the preset
- Add `seqz` instruction
- Add `snez` instruction
- Add `sltz` and `sgtz` instructions
- Add `sgt` instruction
- Add `beqz` and `bnez` instructions
- Add `blez` and `bgez` instructions
- Add `sgtu` instruction
- Add `bltz` and `bgtz` instructions
- Add `bgt` and `ble` instructions
- Add `bgtu` and `bleu` instructions
- Add `j`, `jr`, and `ret` instructions
- Add shorthands for `jal` and `jalr`
- Add `call` instruction
- Add `tail` instruction
- Add float registers
- Add ecalls for printing floats and doubles
- Add `flw` and `fld` instructions
- Add ecalls for inputting floats and doubles
- Add `fsw` and `fsd` instructions
- Add basic arithmetic instructions
- Add `fmin`, `fmax`, `fsqrt` instructions
- Add event on simulator start
- Add float conversion instructions
- Add float sign injection instructions
- Add `fmv` instructions
- Add `fmv.x.w` and `fmv.w.x` instructions
- Add parsing special float values
- Add comparison instructions for floats
- Add `fclass` instruction

### Changed

- Start from label `main` if exists
- Make instructions' `exec` public
- Rename a variable
- Create util functions for instructions
- Allow space as an argument separator
- Rename Simulator32.java to SimulatorRiscV.java
- Pass parsers a read-only register collection; search by string only
- Update version to 1.2
- Split rv32fd in two dirs
- Rollback changes in float registers
- Update version to 1.3
- Update changelog

### Fixed

- Negative numbers in |&^ immediate instructions
- `mulhsu`, `mulhu` names
- `jalr` used register after modification
- Split arguments with a separator inside single quotes
- Step back during waiting for input

## [1.1] - 2024-07-19

### Added

- Add scripts for installation as a Maven lib
- Add project structure to README.md
- Add back step event, rename pause and stop events
- Add instructions `toString` method
- Add simulator `getProgramInstructions()` method
- Add `ebreak` instruction
- Add breakpoint event, move execution environment up to the simulator base, remove execution break exception
- Add check for decoding buffer size
- Add load instructions to the preset
- Add `lui` instruction
- Add memory management unit and ecalls
- Add `sb` instruction
- Add `sh` instruction
- Add `slti` instruction
- Add `sltiu` instruction
- Add `slt` instruction
- Add `sltu` instruction
- Add `rem` instruction
- Added 'mulh' instruction
- Add casts to store instructions
- Add tests for new instructions
- Add unsigned instructions from RV32M
- Add test for unsigned instructions from RV32M

### Changed

- Use own VM for CI/CD
- Deploy package to GitLab registry
- Move artifacts up to the root
- Change group and artifact ids
- Set artifact name
- Move riscvprogram to program.riscvprogram
- Back stepper observers
- Rename `FUNCT3` to `FUNCT_3`
- Return back handlers for immediate right shifts
- Let linkable instructions calculate symbol offset themselves (more flexible)
- Allow li generate "myriad sequences" for treating large constants
- Use conventional syntax for load/store instructions
- Increase memory limits
- Update version to 1.1

### Fixed

- Detect program counter changes using observers (zero jump)
- Decreasing counter of instructions to run
- Error in sltiu and sltu instructions
- Negative imm in slti[u] instructions
- `mulh` instruction
- `sltiu` instruction

## [1.0] - 2024-07-14

### Added

- Add README.md
- Add Maven Wrapper
- Add `lw` instruction
- Add `beq` instruction
- Add `bge` instruction
- Add `blt` instruction
- Add `bne` instruction
- Add `bgeu` instruction
- Add `bltu` instruction
- Add `jalr` instruction
- Add observers to the Memory32
- Add observers to the Register32
- Add module-info.java
- Add back stepping
- Add `lb` instruction
- Add `lh` instruction
- Add `lbu` instruction
- Add `lhu` instruction
- Add `sw` instruction
- Add CHANGELOG.md via git-cliff
- Add tests for load instructions
- Add tests for instructions interacting with pc
- Add tests for branching instructions
- Add IllegalRegisterException as an ExecutionException to remove a lot of try-catch blocks
- Add exit ecall
- Add ecall for reading integers
- Add ecall for printing integers
- Add ecalls for printing and reading one character
- Add parsing chars and numeral systems in immediate instructions
- Add ecalls for printing numbers as 0b, 0, 0x, and as unsigned
- Add some events on execution flow, small worker improvements

### Changed

- Disable script for assigning reviewers
- Include new instructions in the preset
- Ignore comments in code
- A simulator can be run in a separate thread
- Change event subscription
- Change event subscription
- Rename classes
- Put printing events to a package
- Update CHANGELOG.md
- Update CHANGELOG.md
- Update CHANGELOG.md

### Fixed

- Fix typo in README.md
- Accept negative numbers in immediate instructions
- Linking and negative offsets (see issue #38)
- Parse numbers in .data
- Fix jalr instructions
- Funct3, style, jal is linkable
- Improve resetting a simulator's state on a new run
- Set instructionsToRun when starting worker
- Revert not triggering observers
- Load instructions
- Load instructions
- Fix creation of branching instructions
- Jump instructions
- Fix tests for `auipc` and `jal`
- `mv` parsing and encoding
- Encoding immediate right shifts

### Removed

- Remove ILinkable from `jalr`

## [MVP-v2] - 2024-07-07

### Added

- Addressing instruction in multiple object files
- Add `ecall` instruction
- Add read ecall
- Add events and input
- Add tests for `add`
- Add tests for `sub`
- Add testing and building pipeline
- Add instructions sll srl
- Add xori instruction 
- Add andi, ori, slli, srli instructions
- Add new instruction to the preset
- Add `sra` and `srai` instructions
- Add tests for basic instruction

### Changed

- Improve exception info
- Move example into a file
- Install Maven
- Set pull policy for Docker
- Revert pull policy changes 
- Setup image tools manually

### Fixed

- Negative jump and J-instruction decoding
- Reset registers (pc and sp) on run
- Fix printing in the example
- Fix grammar error in naming
- Fix manifest version in pom.xml
- Fix error in xori instruction
- Fix srl and srli instructions
- Truncate immediate in shifts; disable `srai`

### Removed

- Remove ILinkableInstruction interface from some instructions

## [MVP-v1] - 2024-06-30

### Added

- Add memory module
- Add RISC-V instruction hierarchy
- Add `addi` and an example
- Add data section parsing
- Add object file and linker interfaces
- Add linker
- Add `jal` instruction
- Add `add` instruction
- Add `sub` instruction
- Add `mul` instruction
- Add `div` instruction
- Add `xor` instruction
- Add `and` instruction
- Add a preset of a Simulator32 and an example
- Add helper methods for parsers
- Add export
- Add `li`, `la`, `mv`; add request for linking two subsequent instructions
- Add reset method for a simulator

### Changed

- Modify .gitignore
- Load program into the memory and decode it during the run
- Create package for RISC-V instructions
- Return the number of consumed bytes in a decoder
- Rename exception
- Make RISC-V instruction fields protected and `imm` not final
- Create symbol and relocation tables, parsing labels
- Changing libs names
- Attach registers and memory to instruction handlers at registration moment
- Simplify instruction handler
- Register new instructions
- Rename linker parts
- Make zero register read-only
- Move to rarsreborn.core root package
- Small improvements
- Replace tabs with spaces when compile

### Fixed

- Jal tried to use erased label name for error
- Ignore linkable instruction that has no LinkRequest
- Relative addressing
- $pc change and relative linking for pseudo instruction

### Removed

- Remove format parameter from decoder register instruction methods

## [MVP-v0] - 2024-06-23

### Added

- Add LICENCE
- Add .gitignore
- Add issue template for user stories
- Add template for a user story issue with acceptance criteria
- Add script for random selection of code reviewer

### Changed

- Improve .gitlab-ci.yml reviewer script
- Architecture and skeleton
- Rename dir riscv -> assembler
- Rename dir assembler -> core

### Fixed

- Fix selection script
- Fix .gitlab-ci.yml
- Fix .gitlab-ci.yml

[1.3.1]: https://gitlab.pg.innopolis.university/swapik/rars-reborn-core/compare/v1.3...v1.3.1
[1.3]: https://gitlab.pg.innopolis.university/swapik/rars-reborn-core/compare/v1.1...v1.3
[1.1]: https://gitlab.pg.innopolis.university/swapik/rars-reborn-core/compare/v1.0...v1.1
[1.0]: https://gitlab.pg.innopolis.university/swapik/rars-reborn-core/compare/MVP-v2...v1.0
[MVP-v2]: https://gitlab.pg.innopolis.university/swapik/rars-reborn-core/compare/MVP-v1...MVP-v2
[MVP-v1]: https://gitlab.pg.innopolis.university/swapik/rars-reborn-core/compare/MVP-v0...MVP-v1

<!-- generated by git-cliff -->
