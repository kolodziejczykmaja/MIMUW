section .text
global nsqrt

; void nsqrt(uint64_t *Q, uint64_t *X, unsigned n)
; rdi = Q 
; rsi = X 
; edx = n 

nsqrt:
    push    rbp
    push    rbx
    push    r12
    push    r13
    push    r14
    push    r15

    mov     r13, rdi        ; wskaźnik do Q
    mov     r14, rsi        ; wskaźnik do X (bufor roboczy)
    mov     r15d, edx       ; liczba bitów n

    ; Liczba 64-bitowych słów (n / 64)
    mov     eax, r15d
    shr     eax, 6          ; podziel przez 64
    mov     r12d, eax       ; r12 = liczba słów (n / 64)

    ; Czyszczenie bufora Q (wszystkie słowa na 0)
    xor     eax, eax
    mov     rcx, r12
    mov     rdi, r13
.clear_Q:
    mov     qword [rdi], 0  ; Q[i] = 0
    add     rdi, 8          ; przejdź do następnego słowa
    loop    .clear_Q

    xor     ecx, ecx        ; j = 0
.main_loop:
    inc     ecx             ; j++
    cmp     ecx, r15d       ; sprawdź czy j > n
    ja      .done           ; zakończ jeśli tak

    ; Przesunięcia bitowe
    mov     eax, r15d
    sub     eax, ecx        ; n - j
    mov     ebx, eax        ; ebx = n - j
    inc     eax             ; eax = n - j + 1 (shift1)
    add     ebx, eax        ; ebx = 2n - 2j + 2 (shift2)

    ; Kopia Q do Tj 
    lea     rdi, [r14]      ; dst = X
    lea     rsi, [r13]      ; src = Q
    mov     edx, r12d
    call    copy_words      ; Tj = Q

    ; Przesunięcie Tj w lewo o shift1 bitów
    mov     edi, r14d
    mov     esi, eax        ; shift1
    mov     edx, r12d
    call    shift_left

    ; Dodanie 1 << shift2 do Tj (bufora w X)
    mov     edi, r14d
    mov     esi, ebx        ; shift2
    call    add_bit_to_buffer

    ; Porównanie Rj i Tj
    lea     rsi, [r14]      ; Rj = X
    lea     rdi, [r14]      ; Tj też w X
    mov     edx, r12d
    call    compare_buffers
    cmp     eax, 0
    jl      .bit_zero       ; jeśli Rj < Tj, to qj = 0

    ; Ustawienie bitu (n - j) w Q (qj = 1)
    mov     eax, r15d
    sub     eax, ecx
    mov     edi, r13d       ; Q
    call    set_bit_in_buffer

    ; Rj -= Tj 
    lea     rdi, [r14]      ; wynik do Rj (czyli X)
    lea     rsi, [r14]      ; Tj także w X
    mov     edx, r12d
    call    sub_buffers

.bit_zero:
    jmp     .main_loop

.done:
    pop     r15
    pop     r14
    pop     r13
    pop     r12
    pop     rbx
    pop     rbp
    ret

; ----------------------
; Kopia num słów z src do dst
copy_words:
    push    rcx
    mov     ecx, edx
.copy_loop:
    mov     rax, [rsi]      ; załaduj słowo ze źródła
    mov     [rdi], rax      ; zapisz do celu
    add     rsi, 8
    add     rdi, 8
    loop    .copy_loop
    pop     rcx
    ret

; Przesuwa bufor w lewo o shift bitów (buf ma num słów)
shift_left:
    push    rbx
    push    r10
    push    r11
    push    rcx

    mov     ecx, esi        ; shift
    mov     ebx, ecx
    shr     ecx, 6          ; ecx = przesunięcie słowne
    and     ebx, 63         ; ebx = przesunięcie bitowe w słowie

    ; Jeśli przesunięcie słowne > 0, przesuń całe słowa
    test    ecx, ecx
    jz      .skip_word_shift

    mov     r8d, edx
    dec     r8d
.word_shift_loop:
    mov     r9d, r8d
    sub     r9d, ecx
    cmp     r9d, 0
    jl      .zero_remaining
    mov     rax, [rdi + r9*8]
    mov     [rdi + r8*8], rax
    dec     r8d
    jns     .word_shift_loop
.zero_remaining:
    mov     r9d, ecx
.zero_loop:
    lea     r10, [rdi + r9*8]
    sub     r10, 8
    mov     qword [r10], 0
    dec     r9
    jnz     .zero_loop

.skip_word_shift:
    test    ebx, ebx
    jz      .done_shift     ; jeśli brak bitowego przesunięcia

    ; Przesunięcie bitowe w obrębie słów z uwzględnieniem przeniesienia
    mov     r8d, 0
    mov     r9d, edx
    dec     r9d
    xor     r10, r10        ; przeniesienie = 0

.bit_shift_loop:
    mov     rax, [rdi + r8*8]
    mov     r11, rax
    shl     rax, cl         ; przesunięcie bieżącego słowa
    or      rax, r10        ; dodaj przeniesienie z poprzedniego
    mov     [rdi + r8*8], rax

    mov     r10, r11        ; przygotowanie nowego przeniesienia
    mov     r11d, 64
    sub     r11d, ebx
    mov     cl, r11b
    shr     r10, cl         ; nowe przeniesienie
    mov     cl, bl          ; przywróć cl
    inc     r8d
    cmp     r8d, edx
    jb      .bit_shift_loop

.done_shift:
    pop     rcx
    pop     r11
    pop     r10
    pop     rbx
    ret

; Dodaje pojedynczy bit w pozycji bit_index do bufora
add_bit_to_buffer:
    push    rbx
    push    rcx
    push    r8
    push    r9

    mov     eax, esi
    cmp     eax, edx
    jae     .done           ; jeśli poza zakresem, wyjdź

    mov     ecx, eax
    shr     ecx, 6          ; indeks słowa
    and     eax, 63         ; przesunięcie w słowie
    mov     rbx, 1
    mov     cl, al
    shl     rbx, cl         ; rbx = 1 << offset

    lea     r8, [rdi + rcx*8]
    add     qword [r8], rbx
    jc      .carry_loop     ; jeśli dodanie spowodowało przeniesienie

    jmp     .done

.carry_loop:
    inc     ecx
    mov     r9d, edx
    shr     r9d, 6
    cmp     ecx, r9d
    jae     .done           ; wyjdź, jeśli poza zakresem

    lea     r8, [rdi + rcx*8]
    add     qword [r8], 1
    jc      .carry_loop     ; kontynuuj przenoszenie

.done:
    pop     r9
    pop     r8
    pop     rcx
    pop     rbx
    ret

; Porównuje dwa bufory A i B – każdy po num słów
; Zwraca eax: 1 jeśli A > B, 0 jeśli A == B, -1 jeśli A < B
compare_buffers:
    push    rcx
    push    r8
    push    r9

    mov     ecx, edx
    shr     ecx, 6
    dec     ecx             ; ostatni indeks
    js      .equal

.compare_loop:
    mov     r8, [rdi + rcx*8]
    mov     r9, [rsi + rcx*8]
    cmp     r8, r9
    ja      .greater
    jb      .less
    dec     ecx
    jns     .compare_loop

.equal:
    xor     eax, eax        ; A == B
    jmp     .done

.greater:
    mov     eax, 1
    jmp     .done

.less:
    mov     eax, -1

.done:
    pop     r9
    pop     r8
    pop     rcx
    ret

; Ustawia bit na pozycji bit_index w buforze
set_bit_in_buffer:
    push    rbx
    push    rcx
    push    r8

    cmp     esi, edx
    jae     .done

    mov     eax, esi
    mov     ecx, eax
    shr     ecx, 6
    and     eax, 63
    mov     rbx, 1
    mov     cl, al
    shl     rbx, cl
    lea     r8, [rdi + rcx*8]
    or      qword [r8], rbx

.done:
    pop     r8
    pop     rcx
    pop     rbx
    ret

; Odejmuje bufor B od bufora A (A -= B), każdy ma num słów
sub_buffers:
    push    rcx
    push    rbx
    push    r8
    push    r9

    xor     r8d, r8d         ; przeniesienie = 0
    mov     ecx, edx
    shr     ecx, 6           ; liczba słów

    xor     r9d, r9d         ; indeks słowa

.sub_loop:
    cmp     r9d, ecx
    jae     .done

    mov     rax, [rdi + r9*8]
    mov     rbx, [rsi + r9*8]
    sub     rax, rbx
    sbb     r8, 0
    mov     [rdi + r9*8], rax

    inc     r9d
    jmp     .sub_loop

.done:
    pop     r9
    pop     r8
    pop     rbx
    pop     rcx
    ret
