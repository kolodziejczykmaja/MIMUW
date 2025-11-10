CC = gcc
CFLAGS = -Wall -Wextra -Wno-implicit-fallthrough -std=gnu17 -fPIC -O2
LDFLAGS = -shared -Wl,--wrap=malloc -Wl,--wrap=calloc -Wl,--wrap=realloc \
          -Wl,--wrap=reallocarray -Wl,--wrap=free -Wl,--wrap=strdup -Wl,--wrap=strndup

SRC = ma.c memory_tests.c
OBJ = $(SRC:.c=.o)

all: libma.so

libma.so: $(OBJ)
	$(CC) $(LDFLAGS) -o $@ $^

%.o: %.c
	$(CC) $(CFLAGS) -c -o $@ $<

test: libma.so
	$(CC) -o test_exec ma_example.c -L. -lma -Wl,-rpath=.

clean:
	rm -f *.o libma.so test_exec
	
.PHONY: all clean test
