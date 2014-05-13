"""
main.py
Main is basically a server who receives commands via Bluetooth or WiFi
and handles them, FOR NOW IT'S ONLY FOR PROTOTYPING
"""

import sys
from network import bluetooth_server as bt
from vision import streamer

# start stream for commandcenter
stream = streamer.Streamer().start()

# start bluetoothserver for app
btserve = bt.BluetoothServer().start()

# start PS3Handler thread
#ps3handler = pshandler.PS3Handler().start()

# exit on keypress
x = raw_input()
sys.exit(0)