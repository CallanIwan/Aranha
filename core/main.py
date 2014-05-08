"""
main.py
Main is basically a server who receives commands via Bluetooth or WiFi
and handles them, FOR NOW IT'S ONLY FOR PROTOTYPING
"""


from vision import vision_handler

vhandler = vision_handler.Handler(0)
vhandler.find_balloon("red")

'''
from network import bluetooth_server as bt
btserve = bt.BluetoothServer()
'''