package com.aranha.spider.app;

/**
 * Created by Rutger on 14-05-14.
 */
public enum SpiderInstruction {

    /**
     * Send Extra data:
     *          "0"     = forward
     *          "90"    = right
     *          "180"   = backwards
     *          "270"   = left
     */
    move {
        public String toString() { return "" + ((char) 4);  }
    },
    /**
     * Send Extra data:
     *              "l" = strafe left
     *              "r" = strafe left
     */
    strafe {
        public String toString() { return "" + ((char) 5); }
    },

    /**
     *
     */
    requestCameraImage {
        public String toString() { return "" + ((char) 1);  }
    },

    requestSpiderInfo {
        public String toString() { return "" + ((char) 2);  }
    },

    requestScriptList {
        public String toString() { return "" + ((char) 3);  }
    },

    /**
     * Set the spider to the standard position.
     */
    relax {
        public String toString() { return "" + ((char) 6);  }
    },
    /**
     * Send as extra data: "u" for up. "d" for down.
     */
    spiderUpDown {
        public String toString() { return "" + ((char) 7);  }
    },

    /**
     * Extra data:
     *      "<String>" = Name of one from the requested script list.
     */
    startScript {
        public String toString() { return "" + ((char) 8); }
    },
    stopScript {
        public String toString() { return "" + ((char) 8) + ";stop"; }
    }


}
