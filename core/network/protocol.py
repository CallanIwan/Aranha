"""
vision/protocol.py
This module defines the protocol which are used by the bluetooth and WiFi servers.
A client has this exact protocol. A client sends its commands conform this protocol,
so the network_handler knows which functions to call.
"""

# header commands
H_CONNECT = 0x01
H_CONTROL = 0x02
H_VISION = 0x04