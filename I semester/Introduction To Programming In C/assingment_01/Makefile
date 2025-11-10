CC = gcc
CFLAGS = -std=c17 -pedantic -Wall -Wextra -Wduplicated-cond -Wjump-misses-init -Wformat-security -Wfloat-equal -Wshadow -Wconversion -Wlogical-not-parentheses -Wnull-dereference -Wvla -Werror -fstack-protector-strong -fsanitize=undefined -fno-sanitize-recover -g -fno-omit-frame-pointer -O1
LDFLAGS = 
TARGET = set_solver
SOURCE = src/set.c
TEST_DIR = test
VALGRIND_CMD = valgrind --leak-check=full -q --error-exitcode=1

.PHONY: all build clean test

all: build

build: $(TARGET)

$(TARGET): $(SOURCE)
	@echo "Compiling $(SOURCE)..."
	$(CC) $(CFLAGS) $(SOURCE) -o $(TARGET) $(LDFLAGS)

test: $(TARGET)
	@echo "Running automated tests (1 to 113) with Valgrind and Diff"
	@FAILED_TESTS=0; \
	for i in $$(seq 1 113); do \
		INPUT_FILE="$(TEST_DIR)/test$$i.in"; \
		OUTPUT_FILE="$(TEST_DIR)/test$$i.out"; \
		\
		printf "Test %s... " "$$i"; \
		\
		if ! ${VALGRIND_CMD} "./$(TARGET)" < "$$INPUT_FILE" | diff - "$$OUTPUT_FILE" > /dev/null; then \
			printf "FAIL\n"; \
			FAILED_TESTS=$$((FAILED_TESTS + 1)); \
		else \
			printf "OK\n"; \
		fi; \
	done; \
	\
	if [ $$FAILED_TESTS -eq 0 ]; then \
		echo "ALL 113 TESTS PASSED SUCCESSFULLY!"; \
	else \
		echo "$$FAILED_TESTS TESTS FAILED."; \
		exit 1; \
	fi
clean:
	rm -f $(TARGET)