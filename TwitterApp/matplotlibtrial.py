'''
Created on 22 May 2014

@author: theopavlakou
'''

import time
import numpy as np
import matplotlib.pyplot as plt

fig=plt.figure()

i=0
x=list()
y=list()
plt.axis([0, 300, -1, 5])
plt.ion()
plt.show()
plt.xlabel("time/s")
plt.ylabel("amplitude/a")

while i <60:
    temp_y=np.random.random()
    x.append(i)
    y.append(temp_y)
    if i % 25 == 0:
        plt.arrow(i, temp_y, 1, 4, width=0.005, head_width=0.05, head_starts_at_zero=False)
        plt.annotate("this is 100", (i, temp_y+4))
        plt.scatter(i,temp_y, c="red")
    else:
        plt.scatter(i,temp_y, c="blue")
    i+=1
    plt.draw()
    time.sleep(0.05)
plt.show(block=True)