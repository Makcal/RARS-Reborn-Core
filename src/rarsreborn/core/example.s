.data
    h: .space 20
.text
    li t1, 1
    li t2, 2
    jal x0, l
    li t3, 3
    li t4, 4
l:
    li t5, 5
    la a0, h
    li a7, 1
    ecall
    li a7, 0
    ecall