/*
 * Maja Kołodziejczyk
 * 469377
 *
 * Moore Machine Simulator
 * The task involves implementing a dynamically loaded library in C that
 * simulates Moore machines. A Moore machine is a type of deterministic finite
 * automaton used in synchronous digital circuits.
 *
 * A Moore machine is represented as an ordered sextuple ⟨X, Y, Q, t, y, q⟩,
 * where:
 * - X is the set of input signal values (binary inputs)
 * - Y is the set of output signal values (binary outputs)
 * - Q is the set of internal states
 * - t: X × Q → Q is the transition function
 * - y: Q → Y is the output function
 * - q ∈ Q is the initial state
 *
 * We consider only binary automata with:
 * - n single-bit input signals (X = {0,1}^n)
 * - m single-bit output signals (Y = {0,1}^m)
 * - s-bit state (Q = {0,1}^s)
 *
 * In each step of the machine's operation:
 * - The transition function t computes the new state based on input values and
 * current state
 * - The output function y computes output values based on the current state
 */

#include "ma.h"
#include <errno.h>
#include <stdint.h>
#include <stdlib.h>
#include <string.h>

typedef struct input_source {
	moore_t *source_automat;
	size_t output_index;
} input_source_t;

struct moore {
	size_t n;
	size_t m;
	size_t s;
	transition_function_t t;
	output_function_t y;
	uint64_t *state;
	uint64_t *next_state;
	uint64_t *output;
	input_source_t *input_sources;
	uint64_t *input_values;
};

static size_t uint64_count(size_t bits) {
	return (bits + 63) / 64;
}

static uint64_t *alloc_bit_array(size_t bits) {
	return calloc(uint64_count(bits), sizeof(uint64_t));
}

/* Clears unused bits in the last word of a bit array */
static void clear_unused_bits(uint64_t *array, size_t used_bits) {
	if (used_bits % 64 != 0) {
		size_t last_word = (used_bits - 1) / 64;
		size_t unused_bits = 64 - (used_bits % 64);
		array[last_word] &= ~(UINT64_MAX << unused_bits);
	}
}

/* Creates a new Moore automaton with full configuration */
moore_t *ma_create_full(size_t n, size_t m, size_t s, transition_function_t t,
						output_function_t y, uint64_t const *q) {
	if (m == 0 || s == 0 || t == NULL || y == NULL || q == NULL) {
		errno = EINVAL;
		return NULL;
	}
	moore_t *a = malloc(sizeof(moore_t));
	if (!a) {
		errno = ENOMEM;
		return NULL;
	}
	a->n = n;
	a->m = m;
	a->s = s;
	a->t = t;
	a->y = y;
	a->state = alloc_bit_array(s);
	a->next_state = alloc_bit_array(s);
	a->output = alloc_bit_array(m);
	if (!a->state || !a->next_state || !a->output) {
		free(a->state);
		free(a->next_state);
		free(a->output);
		free(a);
		errno = ENOMEM;
		return NULL;
	}
	memcpy(a->state, q, uint64_count(s) * sizeof(uint64_t));
	clear_unused_bits(a->state, s);
	if (n > 0) {
		a->input_sources = calloc(n, sizeof(input_source_t));
		a->input_values = alloc_bit_array(n);
		if (!a->input_sources || !a->input_values) {
			free(a->input_sources);
			free(a->input_values);
			free(a->state);
			free(a->next_state);
			free(a->output);
			free(a);
			errno = ENOMEM;
			return NULL;
		}
	} else {
		a->input_sources = NULL;
		a->input_values = NULL;
	}
	a->y(a->output, a->state, m, s);
	clear_unused_bits(a->output, m);
	return a;
}

/* Default output function for simple automaton (identity function) */
static void output_function(uint64_t *output, uint64_t const *state, size_t s,
							size_t m) {
	(void)m;
	memcpy(output, state, uint64_count(s) * sizeof(uint64_t));
}

/* Creates a simple Moore automaton with identity output function */
moore_t *ma_create_simple(size_t n, size_t s, transition_function_t t) {
	if (s == 0 || t == NULL) {
		errno = EINVAL;
		return NULL;
	}
	uint64_t *q = alloc_bit_array(s);
	if (!q) {
		errno = ENOMEM;
		return NULL;
	}
	output_function_t y = output_function;
	moore_t *a = ma_create_full(n, s, s, t, y, q);
	free(q);
	return a;
}

/* Deletes an automaton and frees all resources */
void ma_delete(moore_t *a) {
	if (!a)
		return;
	if (a->n > 0) {
		ma_disconnect(a, 0, a->n);
		free(a->input_sources);
		free(a->input_values);
	}
	free(a->state);
	free(a->next_state);
	free(a->output);
	free(a);
}

/* Connects 'num' inputs of a_in to outputs of a_out */
int ma_connect(moore_t *a_in, size_t in, moore_t *a_out, size_t out,
			   size_t num) {
	if (!a_in || !a_out || num == 0 || in + num > a_in->n ||
		out + num > a_out->m) {
		errno = EINVAL;
		return -1;
	}
	if (ma_disconnect(a_in, in, num) != 0) {
		return -1;
	}
	for (size_t i = 0; i < num; i++) {
		a_in->input_sources[in + i].source_automat = a_out;
		a_in->input_sources[in + i].output_index = out + i;
	}
	return 0;
}

/* Disconnects 'num' inputs starting at index 'in' */
int ma_disconnect(moore_t *a_in, size_t in, size_t num) {
	if (!a_in || num == 0 || in + num > a_in->n) {
		errno = EINVAL;
		return -1;
	}
	for (size_t i = 0; i < num; i++) {
		a_in->input_sources[in + i].source_automat = NULL;
	}
	return 0;
}

/* Sets input values for unconnected inputs */
int ma_set_input(moore_t *a, uint64_t const *input) {
	if (!a || !input || a->n == 0) {
		errno = EINVAL;
		return -1;
	}
	for (size_t i = 0; i < a->n; i++) {
		if (a->input_sources[i].source_automat == NULL) {
			size_t word = i / 64;
			size_t bit = i % 64;
			uint64_t mask = UINT64_C(1) << bit;
			if (input[word] & mask) {
				a->input_values[word] |= mask;
			} else {
				a->input_values[word] &= ~mask;
			}
		}
	}
	clear_unused_bits(a->input_values, a->n);
	return 0;
}

/* Sets the automaton's current state */
int ma_set_state(moore_t *a, uint64_t const *state) {
	if (!a || !state) {
		errno = EINVAL;
		return -1;
	}
	memcpy(a->state, state, uint64_count(a->s) * sizeof(uint64_t));
	clear_unused_bits(a->state, a->s);
	a->y(a->output, a->state, a->m, a->s);
	clear_unused_bits(a->output, a->m);
	return 0;
}

/* Gets the automaton's current output */
uint64_t const *ma_get_output(moore_t const *a) {
	if (!a) {
		errno = EINVAL;
		return NULL;
	}
	return a->output;
}

/* Executes one simulation step for all automats */
int ma_step(moore_t *at[], size_t num) {
	if (!at || num == 0) {
		errno = EINVAL;
		return -1;
	}
	for (size_t i = 0; i < num; i++) {
		if (!at[i]) {
			errno = EINVAL;
			return -1;
		}
	}
	uint64_t **inputs = malloc(num * sizeof(uint64_t *));
	if (!inputs) {
		errno = ENOMEM;
		return -1;
	}
	// Prepare inputs and compute next states
	for (size_t i = 0; i < num; i++) {
		moore_t *a = at[i];
		if (a->n > 0) {
			inputs[i] = alloc_bit_array(a->n);
			if (!inputs[i]) {
				for (size_t j = 0; j < i; j++) {
					free(inputs[j]);
				}
				free(inputs);
				errno = ENOMEM;
				return -1;
			}
			for (size_t j = 0; j < a->n; j++) {
				if (a->input_sources[j].source_automat) {
					moore_t *src = a->input_sources[j].source_automat;
					size_t out_idx = a->input_sources[j].output_index;
					size_t word = out_idx / 64;
					size_t bit = out_idx % 64;
					uint64_t mask = UINT64_C(1) << bit;
					size_t dst_word = j / 64;
					size_t dst_bit = j % 64;
					if (src->output[word] & mask) {
						inputs[i][dst_word] |= (UINT64_C(1) << dst_bit);
					}
				} else {
					size_t word = j / 64;
					size_t bit = j % 64;
					uint64_t mask = UINT64_C(1) << bit;

					if (a->input_values[word] & mask) {
						inputs[i][word] |= (UINT64_C(1) << bit);
					}
				}
			}
			a->t(a->next_state, inputs[i], a->state, a->n, a->s);
			clear_unused_bits(a->next_state, a->s);
			free(inputs[i]);
		} else {
			a->t(a->next_state, NULL, a->state, 0, a->s);
			clear_unused_bits(a->next_state, a->s);
		}
	}
	free(inputs);
	// Update states and outputs
	for (size_t i = 0; i < num; i++) {
		moore_t *a = at[i];
		uint64_t *temp = a->state;
		a->state = a->next_state;
		a->next_state = temp;
		a->y(a->output, a->state, a->m, a->s);
		clear_unused_bits(a->output, a->m);
	}
	return 0;
}