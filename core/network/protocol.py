"""
network/protocol.py
This module defines the protocol which are used by the bluetooth and WiFi servers.
A client has this exact protocol. A client sends its commands conform this protocol,
so the network_handler knows which functions to call.
"""
# end of message byte
H_END = ("" + chr(0)) * 990

# header bytes, intended for the app to know what to receive
H_IMAGE = "" + chr(1)
H_SENSOR = "" + chr(2)
H_VISION = "" + chr(3)

# receiving header bytes
H_MOV_RECV = "" + chr(4)


# handle the data that is received and send data back if necessary
def handle(handler, data):
    if len(data) == 1:                                                          # a header is 1 byte
        print "me gotz header:", ord(data)
        if ord(data) is ord(H_IMAGE):                                           # check if header is image
            with open("/tmp/stream/pic.jpg", "rb") as image_file:               # try to open camera image
                image = image_file.read()                                       # save image to ram
            handler.encode_and_send(H_IMAGE, image)                             # encode and send image
            print "Sending image"
        elif ord(data) is ord(H_SENSOR):                                        # check if header is sensor
            handler.encode_and_send(H_SENSOR, "JSON with sensor shiz")
            print "Sending sensor data"
        elif ord(data) is ord(H_VISION):
            handler.encode_and_send(H_VISION, "Ballon vinden;Olie analyseren")
            print "Sending vision scripts"
    elif len(data) > 1:
        if ord(data[0]) is ord(H_MOV_RECV):
            print "MOVE: " + data.split(";")[1]
