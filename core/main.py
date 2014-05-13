"""
main.py
Main is basically a server who receives commands via Bluetooth or WiFi
and handles them, FOR NOW IT'S ONLY FOR PROTOTYPING
"""

from network import bluetooth_server as bt

btserve = bt.BluetoothServer().start()
#ps3handler = pshandler.PS3Handler().start()
x = raw_input()
sys.exit(0)