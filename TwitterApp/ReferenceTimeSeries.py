'''
Created on 11 August 2014
@author: Angathan FRANCIS
'''

import json
import time
import re
import pickle
import unicodedata
from pylab import *
from datetime import datetime
from TweetRetriever import TweetRetriever
from Tweet import Tweet

class ReferenceTimeSeries:
    """ Creates the reference timeseries related to both events and non-events.
    First, a source database containing JSON Tweets is called. Actually, only the
    timestamp and text are kept. These timestamps are transformed into timestamp 
    readable by PYTHON. Second, for a given window, the occurence of a specific
    (non)-event is computed and stored. Finally, the timeseries are easily created.
    """

    def __init__(self):
        self.decoder = json.JSONDecoder()
        self.Tweets = []
        self.timeSeries = {}
        self.count = int()

    def readable(self, filename):
        """ Stores all the tweets from the given database into a list with timestamp and
        text content readable by PYTHON """
        self.data = open('Database/' + filename, 'r')
        
        while True:
            try:
                self.line = self.data.readline()

                # Ending the iteration once there is no Tweets
                if self.line == "":
                    break

                self.line = unicode(self.line, errors = 'replace')
                self.jsonObject = self.decoder.decode(self.line)
                self.tweet = Tweet(self.jsonObject["text"], self.jsonObject["created_at"])

                # Transforming Tweet timestamps into PYTHON time information
                self.ts = datetime.strptime(self.tweet.date, '%a %b %d %H:%M:%S +0000 %Y')
                self.tweet.PyTime = time.mktime(self.ts.timetuple())
                self.tweet.content = unicodedata.normalize('NFKD', self.tweet.content.lower()).encode('ascii','ignore')
                
                self.Tweets.append(self.tweet)

            # Some of the lines have encoding errors so ignore them
            except UnicodeDecodeError:
                print "Error"
                pass

        self.data.close()

    def desiredData(self, filename, windowSize, offset):
        """ This function aims to create a smaller set of desired Tweets from a bigger set. Actually,
        only a small portion of a signal, surrounding the already known event, is necessary in order to
        set reference signals.
        Input: - filename: The name of the full set of data located in Database/
               - windowSize: The period of time in minutes for the desired set of tweets to be stored
               - offset: The start time of the desired set of tweets
               offset needs to be written such as [day_string[2], month_string[2], day_number, hour, minuts, seconds, year]
               Output: - Tweets: The set of desired tweets
        """
        # Transform minutes into seconds
        self.windowSize = windowSize * 60
        self.BigSet = TweetRetriever('Database/' + filename, self.windowSize, 0)
        self.BigSet.initialise()
        # Catch each specific time information from the offset list
        self.offsetTime = offset[0] + ' ' + offset[1] + ' ' + str(offset[2]) + ' ' + str(offset[3]) + ':' + str(offset[4]) + \
                     ':' + str(offset[5]) + ' ' + '+0000' + ' ' + str(offset[6])
        self.offsetTime = datetime.strptime(self.offsetTime, '%a %b %d %H:%M:%S +0000 %Y')
        self.offsetPyTime = time.mktime(self.offsetTime.timetuple())
        self.Tweets = self.BigSet.dataAfterOffset(self.offsetPyTime)

        return self.Tweets

    def getCount(self):
        """ Provides the number of tweets stored in any large database """
        self.currentOffset = 0
        database = open('Database/test', 'r')
        
        while True:
            database.seek(self.currentOffset)
            line = database.readline()
            search = 'profile_background_tile'
            
            if line == "":
                break

            if search in line:
                self.count += 1

            self.currentOffset += len(line)

        return self.count

    def getTimeSeries(self, words, *setTweets):
        """ Transform a set of Tweets into time series.
        Input: - words: The list of words to search, must be strings
               - *setTweets: Used to permit the function to be callable by another function
        Output: - timeSeries: The time discrete cumulative volume about a topic
        """
        for bunch in setTweets:
            self.Tweets = bunch
            
        self.startTime = self.Tweets[0].PyTime
 
        """ timeWindow defines the time window in seconds used to get the occurences """
        self.timeWindow = 60
        """ timeUnit defines the unit of time for the reference signal """
        self.timeUnit = 0

        self.timeSeries = {0:0}
        self.index = 0
        self.occu = 0
        self.dico = str()

        # Calculating the occurence of a specific word in each time window
        for self.timeUnit in range(len(self.Tweets)):
            while self.Tweets[self.index].PyTime <= self.startTime + self.timeWindow:
                self.dico = ''.join([self.dico, ' ', self.Tweets[self.index].content])
                self.index += 1

                self.occu = sum(1 for _ in re.finditer(r'\b%s\b' % re.escape(words[0]), self.dico)) + \
                    sum(1 for _ in re.finditer(r'\b%s\b' % re.escape(words[1]), self.dico)) + \
                    sum(1 for _ in re.finditer(r'\b%s\b' % re.escape(words[2]), self.dico)) + \
                    sum(1 for _ in re.finditer(r'\b%s\b' % re.escape(words[3]), self.dico))
                            
                self.timeSeries[self.timeUnit + 1] = self.timeSeries[self.timeUnit] + self.occu
                
                if self.index == len(self.Tweets):
                    return self.timeSeries

            self.dico = str()
            self.startTime = self.startTime + self.timeWindow + 1

    def getTweetRate(self, words, *setTweets):
        """ Get the Tweet rate of a set of Tweets.
        Input: - words: The list of words to search, must be strings
               - *setTweets: Used to permit the function to be callable by another function
        Output: - rate: The tweet rate related to the topic
        """
        for bunch in setTweets:
            self.Tweets = bunch
            
        self.startTime = self.Tweets[0].PyTime
 
        """ timeWindow defines the time window in seconds used to get the occurences """
        self.timeWindow = 60
        """ timeUnit defines the unit of time for the reference signal """
        self.timeUnit = 0

        self.rate = {0:0}
        self.index = 0
        self.occu = 0
        self.dico = str()

        # Calculating the occurence of a specific word in each time window
        for self.timeUnit in range(len(self.Tweets)):
            while self.Tweets[self.index].PyTime <= self.startTime + self.timeWindow:
                self.dico = ''.join([self.dico, ' ', self.Tweets[self.index].content])
                self.index += 1

                self.occu = sum(1 for _ in re.finditer(r'\b%s\b' % re.escape(words[0]), self.dico)) + \
                    sum(1 for _ in re.finditer(r'\b%s\b' % re.escape(words[1]), self.dico)) + \
                    sum(1 for _ in re.finditer(r'\b%s\b' % re.escape(words[2]), self.dico)) + \
                    sum(1 for _ in re.finditer(r'\b%s\b' % re.escape(words[3]), self.dico))
                            
                self.rate[self.timeUnit + 1] = self.occu
                
                if self.index == len(self.Tweets):
                    return self.rate

            self.dico = str()
            self.startTime = self.startTime + self.timeWindow + 1

    def saveToFile(self, filename, isEvent, *timeSeries):
        """" Saves the time series into a file
        Input: - filename: The name of the file containing the time series, located in RefTimeSeries folder
               - timeSeries: The desired time series
               - isEvent: True = Event, False = Non Event
        """
        for series in timeSeries:
            self.timeSeries = series

        if isEvent == True:
            self.filename = open('RefTimeSeries/Events/' + filename, 'wb')
        else:
            self.filename = open('RefTimeSeries/NonEvents/' + filename, 'wb')
            
        pickle.dump(self.timeSeries, self.filename)
        self.filename.close()
        
    def getPlot(self, *timeSeries):

        for series in timeSeries:
            self.timeSeries = series

        """ Plots the time series """
        x = self.timeSeries.keys()
        y = self.timeSeries.values()
        
        for data in self.timeSeries:
            plot(x, y, color = '#4682B4')

        grid(True)
        show()
