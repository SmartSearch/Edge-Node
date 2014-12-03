'''
Created on 25 August 2014
@author: Angathan FRANCIS
'''
import math

class EventClassifier(object):
    '''
    This class performs online binary classification of an observed stream of Tweets
    '''

    def __init__(self):
        self.detection = 0

    def main(self, word):
        # Finally done on MATLAB

    def distanceToReference(self, currentSignal, referenceSignal):
        """ Provides the minimum distance between the desired signal and a reference one of the same length """
        self.currentSignalNumber = len(currentSignal)
        self.refSignalNumber = len(referenceSignal)
        self.distMin = float('inf')
        self.index = 0

        for self.index in range(self.refSignalNumber - self.currentSignalNumber):
            self.distMin = min(self.distMin, self.distance(currentSignal, referenceSignal[self.index : self.index + self.currentSignalNumber)

        return self.distMin       
                        
    def distance(self, currentSignal, sameSizeSignal):
        """ Provides the Euclidian distance between two signals of the same length """
        self.dist = 0
        self.index = 0

        for self.index in range(len(currentSignal)):
            self.dist = self.dist + math.pow(currentSignal[self.index] - sameSizeSignal[self.index], 2)

        return self.index

    def probability(self, distances, gamma):
        """ Provides the probability that the desired signal belongs to a specific label """
        self.proba = 0
        self.index = 0

        for self.index in range(len(distances)):
            self.proba = self.proba + math.exp(- (gamma * distances[self.index]))

        return self.proba
