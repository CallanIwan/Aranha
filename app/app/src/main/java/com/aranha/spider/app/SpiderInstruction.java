package com.aranha.spider.app;

/**
 * Created by Rutger on 14-05-14.
 */
public enum SpiderInstruction {
    move {
        public String toString() { return "move";  }
    },
    moveLeft {
        public String toString() { return "moveRight";  }
    },
    moveRight {
        public String toString() { return "moveLeft";  }
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
    requestCameraImage {
        public String toString() { return "" + ((char) 1);  }
    },
    requestScriptList {
        public String toString() { return "" + ((char) 3);  }
    }
}
