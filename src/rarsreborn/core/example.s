.data
    h: .space 20
    t: .string "Enter:\n"
    w: .word 42
.text
    la t4, w
    lw t4, t4, 0
    jal x0, l
    li t3, -2
l:
    la a0, t
    li a1, 0
    ecall
    li t0, 1
    li t1, 1
    beq t0, t1, skip
    li t5, -1

skip:
    la a0, t
    li a1, 0
    ecall

    la a0, h
    li a1, 4
    li a7, 1
    ecall
    li a7, 0
    ecall