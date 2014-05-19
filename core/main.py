"""
main.py
Main is basically a server who receives commands via Bluetooth or WiFi
and handles them, FOR NOW IT'S ONLY FOR PROTOTYPING
"""

import time
from network import bluetooth_server as bt, wifi_server as ws
#from control import ps3_handler as ps3


# start bluetoothserver for app
#btserve = bt.BluetoothServer().start()

# start wifi server for app
#wifiserve = ws.WifiServer().start()

# start PS3Handler thread
#ps3handler = ps3.PS3Handler().start()

# keep threads alive, die on CTRL-C
while True:
    time.sleep(1)
