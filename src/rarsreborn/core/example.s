.data
    h: .space 20
    t: .string "Enter:\n"
.text
    jal x0, l
l:
    li t5, 5

    la a0, t
    li a1, 0
    ecall

    la a0, h
    li a1, 4
    li a7, 1
    ecall
    li a7, 0
    ecall