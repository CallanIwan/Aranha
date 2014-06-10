"""
main.py
Start Aranha, and keep all daemons alive.
Aranha starts BT server, WiFi server and handles controls.
"""

import time
import aranha


# start aranha process
aranha.Aranha().start()

# keep threads alive, die on CTRL-C
while True:
    time.sleep(1)