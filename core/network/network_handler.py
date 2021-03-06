"""
vision/network_handler.py
Basically a wrapper to real network functions.
Handles the commands which have to with Receiving Client commands (control, vision etc)
"""
import bluetooth_server as bt
import wifi_server as ws


class NetworkHandler():

    def __init__(self, apphandler):
    def __init__(self, apphandler, driver):
        self.apphandler = apphandler
        self.bts = bt.BluetoothServer(apphandler)
        self.ws = ws.WifiServer()
        self.bts = bt.BluetoothServer(apphandler, driver)
        self.ws = ws.WifiServer(driver)
        self.bts.start()
        self.ws.start()

    def poll_app(self):
        pass