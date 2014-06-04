import SimpleCV


class Test(object):

    def run(self, img):
        return 1



def balloonScore(b):
        base = SimpleCV.Image("a.png")
        balloons = base.findBlobs()

        print b.match(base)

