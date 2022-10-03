import serial
import numpy
import matplotlib.pyplot as plt

def readData():
    s = serial.Serial('com3', 9600) # podesite odgovarajuci COM port
    k = 0
    niz = []

    while (k < 2):
        if s.inWaiting() > 0:
            m = s.readline()
            try:
                r = float(m)
               # print (r)
                niz.append(r)
                k = k + 1
            except:
                pass

    return niz
