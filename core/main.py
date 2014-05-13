"""
main.py
Main is basically a server who receives commands via Bluetooth or WiFi
and handles them, FOR NOW IT'S ONLY FOR PROTOTYPING
"""

import sys
from vision import vision_handler
from network import bluetooth_server as bt
from control import ps3_handler as pshandler


#vhandler = vision_handler.Handler(0)
#vhandler.find_balloon("red")

btserve = bt.BluetoothServer().start()
#ps3handler = pshandler.PS3Handler().start()
x = raw_input()
sys.exit(0)