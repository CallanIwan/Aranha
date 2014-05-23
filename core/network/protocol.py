"""
vision/protocol.py
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