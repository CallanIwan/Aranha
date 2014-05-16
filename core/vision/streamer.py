from threading import Thread
import SimpleCV
import time


class Streamer(Thread):

    def __init__(self):
        super(Streamer, self).__init__()
        self.daemon = True
        self.cam = SimpleCV.Camera(0)
        self.js = SimpleCV.JpegStreamer(9090)

    def run(self):
        while True:
            frame = self.cam.getImage()
            frame.save(self.js.framebuffer)
            # save cpu power, max 5 fps though
            time.sleep(0.2)