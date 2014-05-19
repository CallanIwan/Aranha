"""
main.py
Main is basically a server who receives commands via Bluetooth or WiFi
and handles them, FOR NOW IT'S ONLY FOR PROTOTYPING
"""

import time
from network import bluetooth_server as bt, wifi_server as ws
from control import control_handler as ch


# start bluetoothserver for app
btserve = bt.BluetoothServer().start()

# start wifi server for app
#wifiserve = ws.WifiServer().start()

# start ControlHandler thread
controlhandler = ch.ControlHandler()
controlhandler.start()

# keep threads alive, die on CTRL-C
while True:
    time.sleep(1)