.data
    h: .space 20
    t: .string "Enter:\n"
    w: .word 42
    fl1: .double 2.5
    fl2: .double 2
.text
    la t0, fl1
    fld ft0, 0(t0)
    fld ft1, 8(t0)
    fmul.d fa0, ft0, ft1
    li a7, 3
    ecall

    la t4, w
    lw t4, 0(t4)
    jal x0, l
    li t3, -2
l:
    la a0, t
    li a7, 4
    ecall
    li t0, 1
    li t1, 1
    beq t0, t1, skip
    li t5, -1

skip:
    la a0, t
    li a7, 4
    ecall

    li a7, 5
    ecall
    srai a0, a0, 1
    jal ra, printInt

    li a7, 10
    ecall
    # not running, exited
    li t5, 11

printInt:
    mv s0, a0
    li a7, 1
    ecall
    li a0, ' '
    li a7, 11
    ecall

    mv a0, s0
    li a7, 34
    ecall
    li a0, ' '
    li a7, 11
    ecall

    mv a0, s0
    li a7, 37
    ecall
    li a0, ' '
    li a7, 11
    ecall

    mv a0, s0
    li a7, 35
    ecall
    li a0, ' '
    li a7, 11
    ecall

    mv a0, s0
    li a7, 36
    ecall
    li a0, '\n'
    li a7, 11
    ecall
    jalr zero, ra, 0
