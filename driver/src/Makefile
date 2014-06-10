CC			= g++-4.7
CFLAGS		= -c -Wall -lm -lc -lgcc -std=c++11
INCLUDES	= -l bcm2835 -lpthread
TARGET		= main
#List all files that end with .cpp as .o here
SOURCES		= $(wildcard *.cpp)
OBJECTS		= $(SOURCES:.cpp=.o)

all: $(TARGET)

clean: $(TARGET)
	rm *.o

remake: all clean

main: $(OBJECTS)
	$(CC) $(OBJECTS) -o $(TARGET) $(INCLUDES)
	$(CC) $(OBJECTS) -shared -o $(TARGET).so $(INCLUDES)

%.o: %.cpp
	$(CC) $(CFLAGS) $< -o $@
