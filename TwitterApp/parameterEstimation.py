'''
Created on 26 May 2014

@author: theopavlakou
'''

import numpy as np
import scipy as sp
import matplotlib.pyplot as plt

from matplotlib import rc
import os
import pickle
from EventProbabilityCalculator import EventProbabilityCalculator

rc('font',**{'family':'sans-serif','sans-serif':['Helvetica']})
rc('text', usetex=True)
os.environ['PATH'] += ':/usr/texbin/'


def getLambdasAndProbabilities():
    res = []
    res.append([99, 0.7])
    res.append([226, 0.5])
    res.append([191, 0.9])
    res.append([141, 0.4])
    res.append([212, 0.7])
    res.append([30, 0.15])
    res.append([27, 0.1])
    res.append([335, 0.9])
    res.append([360, 0.91])
    res.append([152, 0.2])
    res.append([47, 0.3])
    res.append([43, 0.2])
    res.append([53, 0.6])
    res.append([174, 0.5])
    res.append([204, 0.8])
    res.append([349, 0.97])
    res.append([51, 0.25])
    res.append([174, 0.5])
    res.append([40, 0.05])
    res.append([188, 0.7])
    res.append([40, 0.2])
    res.append([343, 0.97])
    res.append([36, 0.12])
    res.append([131, 0.6])
    res.append([90, 0.7])
    res.append([93, 0.6])
    res.append([85, 0.9])
    res.append([47, 0.3])
    res.append([136, 0.8])
    res.append([188, 0.5])
    res.append([50, 0.2])
    res.append([92, 0.8])
    res.append([160, 0.95])
    res.append([54, 0.6])
    res.append([71, 0.75])
    res.append([50, 0.6])
    res.append([56, 0.5])
    res.append([150, 0.7])
    return res

if __name__ == '__main__':
    pickleFileName = "w.pkl"
    lambdasAndProbabilities = getLambdasAndProbabilities()
    l = []
    p = []
    y = []
    for [x1, x2] in lambdasAndProbabilities:
        l.append(x1)
        p.append(x2)
        y.append(np.log(x2/(1-x2)))
    plt.scatter(l, p)

    y = np.array(y)
    l = np.array([np.ones(len(l)), l]).T

    print(l)
    print(y.shape)
    w = np.linalg.lstsq(l, y)[0]
    print(w)
    outputPickle = open(pickleFileName, 'wb')
    pickle.dump(w, outputPickle)
    outputPickle.close()


#     lAxis = np.linspace(0.0,700.0, num=1000)
#     exponent = np.dot(np.array([np.ones(len(lAxis)), lAxis]).T, w )
#     probabilities = 1.0/(1.0 + np.exp(-exponent))
#     plt.title(r'How likely an event is based on the eigenvalue, $\lambda$, associated with the principal component')
#     plt.xlabel(r'$\lambda$')
#     plt.ylabel(r'Probability of Event')
#     plt.plot(lAxis, probabilities)
#     plt.show(block=True)

    epc = EventProbabilityCalculator(w)
    print(epc.probabilityOfEventWithLambda(300))






