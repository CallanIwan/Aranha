#ifndef HEADER_GUARD_GLOBALS
#define HEADER_GUARD_GLOBALS

#include "ForwardDeclarations.h"

#define GLOBAL_LEG_COUNT		6
#define GLOBAL_MOTOR_COUNT		20

#ifndef PI
#define PI 3.14159265358979323846
#endif

//en.wikipedia.org/wiki/ANSI_escape_code

#define TERM_NORMAL		"\x1B[0m"
#define TERM_BOLD		"\x1B[1m"
#define TERM_RED		"\x1B[31m"
#define TERM_GREEN		"\x1B[32m"
#define TERM_YELLOW		"\x1B[33m"
#define TERM_BLUE		"\x1B[34m"
#define TERM_MAGENTA	"\x1B[35m"
#define TERM_CYAN		"\x1B[36m"
#define TERM_WHITE		"\x1B[37m"
#define TERM_RESET		"\033[0m"

#endif