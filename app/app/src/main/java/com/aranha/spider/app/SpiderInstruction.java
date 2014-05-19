package com.aranha.spider.app;

/**
 * Created by Rutger on 14-05-14.
 */
public enum SpiderInstruction {
    move {
        public String toString() { return "move";  }
    },
    moveForward {
        public String toString() { return "moveForward";  }
    },
    moveBackwards {
        public String toString() { return "moveBackwards";  }
    },
    up {
        public String toString() { return "up";  }
    },
}
