'''
Created on 26 May 2014
@author: theopavlakou
'''

import numpy as np

class EventProbabilityCalculator():
    '''
    Converts the value of lambda to a probability of an event.
    '''

    def __init__(self, weightingVector):
        self.w = weightingVector

    def probabilityOfEventWithLambda(self, l):
        """
        Input:
            l: The lambda value of the particular principal component
        Output:
            The probability that it is an event based on the logistic
            function defined by self.w, the weighting vector.
        """
        return 1.0/(1.0 + np.exp(-self.w[0] - self.w[1]*l))
