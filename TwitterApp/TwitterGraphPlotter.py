'''
Created on 23 May 2014
@author: theopavlakou
'''

import matplotlib.pyplot as plt
import pickle
from EventProbabilityCalculator import EventProbabilityCalculator

# TODO: Graph plotter should NOT know anything about when a point is an actual event or not.
# This info should be passed to it.
class TwitterGraphPlotter(object):
    """
    A class to plot graphs given data from the TwitterParserStreaming module.
    """

    def __init__(self, dataIn, xlabel="Data Point Number", ylabel="Eigenvalue", title="How eigenvalue changes with time"):
        """ Data given in format:
            [(['word', 'word', 'word'], eigenValue, startDate, endDate), (...), ...]
        """
        self.data = dataIn
        self.colours = {"no_event": "blue", "event":"red"}
        self.currentColour = self.colours["no_event"]
        plt.title(title)
        plt.xlabel(xlabel)
        plt.ylabel(ylabel)
        plt.ion()

    def plotGraph(self, plotDates=True):
        """
        Plots the graph of all the data points it possesses.
        Plots points that signify events in a different colour.
        """
        #TODO: Really bad coding here
        pkl_file = open("w.pkl", 'rb')
        w = pickle.load(pkl_file)
        pkl_file.close()
        eventProbabilityCalculator = EventProbabilityCalculator(w)
        i = 0

        numDataPoints = len(self.data)
        currentColour = "blue"
        for dataPoint in self.data:
            eigVal = dataPoint[1]
            rIntensity = eventProbabilityCalculator.probabilityOfEventWithLambda(eigVal)
            currentColour = (rIntensity, 0, 1-rIntensity)
            
            if plotDates:
                plt.annotate(dataPoint[2], (i, eigVal+20))
            if i == 0:
                plt.annotate(dataPoint[2], (i, eigVal+20))
            elif i == numDataPoints - 1:
                plt.annotate(dataPoint[3], (i, eigVal+20))
                
            plt.scatter(i,eigVal, c=currentColour)
            i = i + 1
            plt.draw()

    def keepGraphLocked(self):
        """
        Keeps the graph open
        """
        plt.show(block=True)

    def saveGraph(self, nameOfFile):
        plt.savefig(nameOfFile, format='eps', dpi=1000)
