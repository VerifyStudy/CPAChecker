	.text
	.file	"const_1-1.c"
	.globl	reach_error             # -- Begin function reach_error
	.p2align	4, 0x90
	.type	reach_error,@function
reach_error:                            # @reach_error
	.cfi_startproc
# %bb.0:
	pushq	%rbp
	.cfi_def_cfa_offset 16
	.cfi_offset %rbp, -16
	movq	%rsp, %rbp
	.cfi_def_cfa_register %rbp
	movabsq	$.L.str, %rdi
	movabsq	$.L.str.1, %rsi
	movl	$3, %edx
	movabsq	$.L.str.2, %rcx
	callq	__assert_fail
.Lfunc_end0:
	.size	reach_error, .Lfunc_end0-reach_error
	.cfi_endproc
                                        # -- End function
	.globl	__VERIFIER_assert       # -- Begin function __VERIFIER_assert
	.p2align	4, 0x90
	.type	__VERIFIER_assert,@function
__VERIFIER_assert:                      # @__VERIFIER_assert
	.cfi_startproc
# %bb.0:
	pushq	%rbp
	.cfi_def_cfa_offset 16
	.cfi_offset %rbp, -16
	movq	%rsp, %rbp
	.cfi_def_cfa_register %rbp
	subq	$16, %rsp
	movl	%edi, -4(%rbp)
	cmpl	$0, -4(%rbp)
	jne	.LBB1_3
# %bb.1:
	jmp	.LBB1_2
.LBB1_2:
	callq	reach_error
	callq	abort
.LBB1_3:
	addq	$16, %rsp
	popq	%rbp
	retq
.Lfunc_end1:
	.size	__VERIFIER_assert, .Lfunc_end1-__VERIFIER_assert
	.cfi_endproc
                                        # -- End function
	.globl	main                    # -- Begin function main
	.p2align	4, 0x90
	.type	main,@function
main:                                   # @main
	.cfi_startproc
# %bb.0:
	pushq	%rbp
	.cfi_def_cfa_offset 16
	.cfi_offset %rbp, -16
	movq	%rsp, %rbp
	.cfi_def_cfa_register %rbp
	subq	$16, %rsp
	movl	$0, -4(%rbp)
	movl	$1, -8(%rbp)
	movl	$0, -12(%rbp)
.LBB2_1:                                # =>This Inner Loop Header: Depth=1
	cmpl	$1024, -12(%rbp)        # imm = 0x400
	jae	.LBB2_3
# %bb.2:                                #   in Loop: Header=BB2_1 Depth=1
	movl	$0, -8(%rbp)
	movl	-12(%rbp), %eax
	addl	$1, %eax
	movl	%eax, -12(%rbp)
	jmp	.LBB2_1
.LBB2_3:
	cmpl	$0, -8(%rbp)
	sete	%al
	andb	$1, %al
	movzbl	%al, %edi
	callq	__VERIFIER_assert
	movl	-4(%rbp), %eax
	addq	$16, %rsp
	popq	%rbp
	retq
.Lfunc_end2:
	.size	main, .Lfunc_end2-main
	.cfi_endproc
                                        # -- End function
	.type	.L.str,@object          # @.str
	.section	.rodata.str1.1,"aMS",@progbits,1
.L.str:
	.asciz	"0"
	.size	.L.str, 2

	.type	.L.str.1,@object        # @.str.1
.L.str.1:
	.asciz	"const_1-1.c"
	.size	.L.str.1, 12

	.type	.L.str.2,@object        # @.str.2
.L.str.2:
	.asciz	"reach_error"
	.size	.L.str.2, 12


	.ident	"clang version 6.0.0-1ubuntu2 (tags/RELEASE_600/final)"
	.section	".note.GNU-stack","",@progbits
