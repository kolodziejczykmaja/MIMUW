; ninv.asm
; oblicza y = floor(2^n / x) przy założeniu x jest n-bitowe (>1), n wielokrotność 64
; implementacja w asemblerze NASM (System V AMD64)


;r10 = liczba slow
;r8 = s

section .text
global ninv

ninv:
    ; PROLOG - zachowaj callee-saved rejestry
    push rbp
    mov rbp, rsp
    push rbx
    push r8
    push r12
    push r13
    push r14
    push r15

    ; argumenty
    mov r12, rdi        ; r12 = y (wynik)
    mov r13, rsi        ; r13 = x (dzielnik, const)
    mov r14, rdx        ; r14 = n (bits)

    ; oblicz m = n/64 (liczba słów)
    mov rax, r14
    shr rax, 6
    mov r10, rax        ; r10 = m

		xor rax, rax
		xor rcx, rcx
	.clear_y:
		cmp rcx, r10
		jge .clear_y_done
		mov [r12 + rcx*8], rax
		inc rcx
		jmp .clear_y
	.clear_y_done:
	
	; ---------- znajdź najwyższe niezerowe słowo x (msw) ----------
    ; będziemy przeszukiwać x[r10-1 .. 0]
    mov rcx, r10
    dec rcx
    mov rbx, rcx        ; tymczasowy indeks
.find_msw:
    mov rax, [r13 + rbx*8]
    test rax, rax
    jnz .found_msw
    dec rbx
    cmp rbx, 0
    jge .find_msw
    ; jeśli doszliśmy poniżej 0, i wszystkie słowa zerowe - ale x>1 więc nie powinno zajść
.found_msw:
    ; rbx = indeks najwyższego niezerowego słowa (>=0)
    mov rax, [r13 + rbx*8]
    ; policz s = liczba wiodących zer w tym słowie (0..63)
    lzcnt r8, rax       ; r8 = s

    ; --------- alokuj miejsce na x_norm (m * 8 bajtów) ----------
    mov rax, r10
    shl rax, 3
    sub rsp, rax
    mov r15, rsp        ; r15 -> x_norm buffer

    ; ---------- normalizacja x (shift left by s) ----------
    test r8, r8
    jz .copy_x_no_shift

    ; przygotuj przesunięcia: cl = s, dl = 64 - s (byte)
    mov cl, r8b
    mov rdx, 64
    sub rdx, r8
    mov dl, dl          ; (dl teraz = 64 - s) -- ok (dl low byte)

    xor rcx, rcx
    xor r9, r9          ; r9 = carry (holds bits shifted out for next word)
.normalize_x_loop:
    mov rax, [r13 + rcx*8]   ; orig word
    mov rdx, rax
	shl rax, cl              ; orig << s
	mov cl, dl
    shr rdx, cl              ; carry_out = orig >> (64 - s)
	mov cl, r8b
    or  rax, r9              ; add previous carry_in
    mov [r15 + rcx*8], rax
    mov r9, rdx              ; carry = carry_out
    inc rcx
    cmp rcx, r10
    jl .normalize_x_loop
    jmp .normalize_x_done

.copy_x_no_shift:
    xor rcx, rcx
.copy_x_loop:
    cmp rcx, r10
    jge .normalize_x_done
    mov rax, [r13 + rcx*8]
    mov [r15 + rcx*8], rax
    inc rcx
    jmp .copy_x_loop

.normalize_x_done:

    ; ---------- przygotuj U = 2^n w buforze U_norm ----------
    ; alokuj U_norm (m * 8 bajtów)
    mov rax, r10
	add rax, 1
    shl rax, 3
    sub rsp, rax
    mov r11, rsp        ; r11 -> U_norm

    ; ustaw sostatnie slowo z w pozycji n 
    ; r10 = m
	mov rcx, r10         ; indeks = m
	mov rax, 1
	shl rax, cl
	mov [r11 + rcx*8], rax

    ; zero lower words
    xor rax, rax
    xor rbx, rbx
.loop_zero_U:
    cmp rbx, rcx
    jge .done_zero_U
    mov [r11 + rbx*8], rax
    inc rbx
    jmp .loop_zero_U
.done_zero_U:

    ; --- przesunięcie U o s (normalizacja U) ---
    test r8, r8
    jz .normalize_U_done

.normalize_U_loop:
    mov rax, [r11 + rcx*8]
    shl rax, cl
    mov [r11 + rcx*8], rax

.normalize_U_done:



    ; ------------- główna pętla dzielenia (Knuth-like) -------------
    ; rcx = index we start from = m-1 downto 0
    mov rcx, r10

.loop_div:
    ; przygotuj RDX:RAX = U_high:U_low (dla div)
    mov rdx, [r11 + rcx*8]        ; HIGH (most significant of pair)
    cmp rcx, 0
    je .no_low
    mov rax, [r11 + (rcx-1)*8]    ; LOW
    jmp .have_pair
.no_low:
    xor rax, rax
.have_pair:

    ; pobierz najwyższe słowo dzielnika v_{m-1} do r9
    mov r9, [r15 + (r10-1)*8]

    ; wykonaj dzielenie 128/64 -> q in RAX, rem in RDX
    div r9
    mov rbx, rax                  ; rbX = q (przybliżenie ilorazu)



    xor rdi, rdi        ; i = 0
    xor r9, r9          ; borrow = 0

.loop_sub:
    mov rax, [r15 + rdi*8]   ; D[i]
    mul rbx                  ; RDX:RAX = D[i] * q̂

    ; adres w U: U[i]
    lea r8, [r11 + rdi*8]   ; r8 = &U[i]

    ; odejmij low part + borrow
	push r10
    mov r10, [r8]           ; tmp = U[i]
    sub r10, rax
    sbb r10, r9
    mov [r8], r10
	pop r10

    ; nowy borrow = high part + CF z poprzedniego sub
    mov r9, rdx
    adc r9, 0

    inc rdi
    cmp rdi, r10
    jl .loop_sub


	; jeśli powstał borrow -> q̂ było za duże
	jc .q_too_big
	jmp .q_ok

.q_too_big:
    xor rdi, rdi       ; i = 0
    xor r9, r9         ; carry = 0

.q_fix_loop:

	push r10
    lea r8, [r11 + rdi*8]   ; adres U[i]

    mov r10, [r8]           ; U[i]
    mov rax, [r15 + rdi*8]  ; D[i]
    add rax, r9              ; dodaj poprzedni carry
    adc r10, rax             ; U[i] += D[i] + carry
    mov [r8], r10
	pop r10

    ; ustaw nowy carry
    xor r9, r9
    adc r9, 0

    inc rdi
    cmp rdi, r10             ; r10 = m
    jl .q_fix_loop

    ; dodaj carry do U[m]
    lea r8, [r11 + r10*8]
    mov r10, [r8]
    add r10, r9
    mov [r8], r10

    ; zmniejsz q o 1
    dec rbx
.q_ok:
    ; teraz rbx zawiera poprawne q
    mov [r12 + rcx*8], rbx
	; zapisz q do y[rcx]
	dec rcx
	cmp rcx, 0
	jge .loop_div



.done:
	mov rax, r10
	add rax, 1
    shl rax, 3
	add rsp, rax	; zwolnij U_norm
    mov rax, r10
    shl rax, 3
    add rsp, rax    ; zwolnij x_norm

    pop r15
    pop r14
    pop r13
    pop r12
    pop r8
    pop rbx
    pop rbp
    ret
