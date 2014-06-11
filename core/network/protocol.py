"""
network/protocol.py
This module defines the protocol which are used by the bluetooth and WiFi servers.
A client has this exact protocol. A client sends its commands conform this protocol,
so the network_handler knows which functions to call.
"""
import base64
import json

# end of message byte
H_END = ("" + chr(0)) * 990

# header bytes, intended for the app to know what to receive
H_IMAGE = "" + chr(1)
H_SENSOR = "" + chr(2)
H_VISION = "" + chr(3)
H_VISION_MODUS = "" + chr(5)

# receiving header bytes
H_MOV_RECV = "" + chr(4)

# vision scripts
H_VISION_COLORCARD = "" + chr(10)
H_VISION_COLORCARD_TX = "" + chr(11)
H_VISION_BALLOON = "" + chr(12)
H_VISION_OIL = "" + chr(13)

# variables
vision_modus = H_VISION_COLORCARD
colorcard = []


# handle the data that is received and send data back if necessary
def handle(handler, data):
    global colorcard
    global vision_modus

    if len(data) == 1:                                                          # a header is 1 byte
        #print "me gotz header:", ord(data)
        if ord(data) is ord(H_IMAGE):                                           # check if header is image
            with open("/tmp/stream/pic.jpg", "rb") as image_file:               # try to open camera image
                image = image_file.read()                                       # save image to ram
            #handler.encode_and_send(H_IMAGE, image)                            # encode and send image
            #print "Sending image"
        elif ord(data) is ord(H_SENSOR):                                        # check if header is sensor
            with open("/home/pi/sensors.json", "rb") as json_file:              # try to open camera image
                json_str = json_file.read()                                     # save image to ram
            handler.encode_and_send(H_SENSOR, json_str)
            print "Sending sensor data"
        elif ord(data) is ord(H_VISION):
            handler.encode_and_send(H_VISION, "Ballon vinden;Olie analyseren")
            print "Sending vision scripts"
        elif ord(data) is ord(H_VISION_COLORCARD_TX):
            handler.encode_and_send(H_VISION_COLORCARD_TX, colorcard)
            print "Sending colorcard"
        elif ord(data) is ord(H_VISION_MODUS):
            handler.encode_and_send(H_VISION_MODUS, vision_modus)
    elif len(data) > 1:
        header = ord(data[0])
        if header is ord(H_MOV_RECV):
            print "MOVE: " + data.split(";")[1]
        elif header is ord(H_VISION_COLORCARD):
            colorcard = json.loads(data)[0]
            vision_modus = H_VISION_COLORCARD
            print "COLOR ORDER: " + colorcard
        elif header is ord(H_VISION_BALLOON):
            vision_modus = H_VISION_BALLOON
            print "VISION BALLOON"
        elif header is ord(H_VISION_OIL):
            vision_modus = H_VISION_OIL
            print "VISION OIL"


def decode_json(json_str):
    return json.dumps(json_str)
