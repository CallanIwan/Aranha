"""
vision/protocol.py
This module defines the protocol which are used by the bluetooth and WiFi servers.
A client has this exact protocol. A client sends its commands conform this protocol,
so the network_handler knows which functions to call.
"""

# header commands
H_MOVEMENT = 0x01
H_VISION = 0x02
H_END = 0xFF

'''
movement commands (ps3 style)
0x10 - 0x19
'''
# left stick
MOV_N = 0x10
MOV_NE = 0x11
MOV_E = 0x12
MOV_SE = 0x13
MOV_S = 0x14
MOV_SW = 0x15
MOV_W = 0x16
MOV_NW = 0x17

#right stick