"""
main.py
Main is basically a server who receives commands via Bluetooth or WiFi
and handles them, FOR NOW IT'S ONLY FOR PROTOTYPING
"""

import time
import aranha


aranha.Aranha().start()
# keep threads alive, die on CTRL-C
while True:
    time.sleep(1)