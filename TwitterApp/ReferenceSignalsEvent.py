'''
Created on 22 August 2014
@author: Angathan FRANCIS
'''

import pickle
import numpy as np
import pandas as pd
import seaborn as sns
import matplotlib.pyplot as plt
from ReferenceTimeSeries import ReferenceTimeSeries

newTimeSeries = ReferenceTimeSeries()
columns = []

## Event 1
###################################################
offset = ['Sat', 'Sep', 22, 18, 30, 30, 2012]
setTweets = newTimeSeries.desiredData('twitter_data', 420, offset)

words = ['morocco', 'donations', 'homeless', 'world cup']
columns.append('Event 1')

RefTimeSeries = newTimeSeries.getTimeSeries(words, setTweets)

# EventsFrame = pd.DataFrame(columns = columns, index = range(len(RefTimeSeries)))
#
# for index in range(len(RefTimeSeries)):
#    EventsFrame.ix[index] = RefTimeSeries[index]
#
# EventsFrame.to_pickle('RefTimeSeries/Events/DataFrame')
# Event = pd.read_pickle('RefTimeSeries/Events/DataFrame')

#Another method would be: storing each event in a different file
with open('RefTimeSeries/Events/11', 'wb') as SaveFile:
    pickle.dump(RefTimeSeries, SaveFile)

"""with open('RefTimeSeries/Events/1', 'r') as ReadFile:
    Event = pickle.load(ReadFile)

newTimeSeries.getPlot(Event)"""

## Event 2
###################################################
offset = ['Sun', 'Sep', 23, 19, 30, 30, 2012]
setTweets = newTimeSeries.desiredData('twitter_data', 420, offset)

words = ['retired', 'terry', 'retirement', 'retires']
columns.append('Event 2')

RefTimeSeries = newTimeSeries.getTimeSeries(words, setTweets)

EventsFrame = pd.DataFrame(columns = columns, index = range(len(RefTimeSeries)))

for index in range(len(RefTimeSeries)):
    EventsFrame.ix[index] = RefTimeSeries[index]

EventsFrame.to_pickle('RefTimeSeries/Events/DataFrame')
Event = pd.read_pickle('RefTimeSeries/Events/DataFrame')

#Another method would be: storing each event in a different file
with open('RefTimeSeries/Events/21', 'wb') as SaveFile:
    pickle.dump(RefTimeSeries, SaveFile)

"""with open('RefTimeSeries/Events/2', 'r') as ReadFile:
    Event = pickle.load(ReadFile)

newTimeSeries.getPlot(Event)"""


## Event 3
###################################################
offset = ['Wed', 'Sep', 26, 14, 00, 30, 2012]
setTweets = newTimeSeries.desiredData('twitter_data', 420, offset)

words = ['police', 'murders', 'officers', 'colleagues']
columns.append('Event 3')

RefTimeSeries = newTimeSeries.getTimeSeries(words, setTweets)

# EventsFrame = pd.DataFrame(columns = columns, index = range(len(RefTimeSeries)))
#
# for index in range(len(RefTimeSeries)):
#    EventsFrame.ix[index] = RefTimeSeries[index]
#
# EventsFrame.to_pickle('RefTimeSeries/Events/DataFrame')
# Event = pd.read_pickle('RefTimeSeries/Events/DataFrame')

#Another method would be: storing each event in a different file
with open('RefTimeSeries/Events/31', 'wb') as SaveFile:
    pickle.dump(RefTimeSeries, SaveFile)

"""with open('RefTimeSeries/Events/3', 'r') as ReadFile:
    Event = pickle.load(ReadFile)

newTimeSeries.getPlot(Event)"""


## Event 4
###################################################
offset = ['Wed', 'Sep', 26, 17, 45, 30, 2012]
setTweets = newTimeSeries.desiredData('twitter_data', 420, offset)

words = ['arsenal', 'coventry', 'gunners', 'giroud']
columns.append('Event 4')

RefTimeSeries = newTimeSeries.getTimeSeries(words, setTweets)

# EventsFrame = pd.DataFrame(columns = columns, index = range(len(RefTimeSeries)))
#
# for index in range(len(RefTimeSeries)):
#    EventsFrame.ix[index] = RefTimeSeries[index]
#
# EventsFrame.to_pickle('RefTimeSeries/Events/DataFrame')
# Event = pd.read_pickle('RefTimeSeries/Events/DataFrame')

#Another method would be: storing each event in a different file
with open('RefTimeSeries/Events/41', 'wb') as SaveFile:
    pickle.dump(RefTimeSeries, SaveFile)

"""with open('RefTimeSeries/Events/4', 'r') as ReadFile:
    Event = pickle.load(ReadFile)

newTimeSeries.getPlot(Event)"""


## Event 5
###################################################
offset = ['Sat', 'Sep', 29, 13, 45, 30, 2012]
setTweets = newTimeSeries.desiredData('twitter_data', 420, offset)

words = ['arsenal', 'chelsea', 'gunners', 'blues']

RefTimeSeries = newTimeSeries.getTimeSeries(words, setTweets)

# EventsFrame = pd.DataFrame(columns = columns, index = range(len(RefTimeSeries)))
#
# for index in range(len(RefTimeSeries)):
#    EventsFrame.ix[index] = RefTimeSeries[index]
#
# EventsFrame.to_pickle('RefTimeSeries/Events/DataFrame')
# Event = pd.read_pickle('RefTimeSeries/Events/DataFrame')

#Another method would be: storing each event in a different file
with open('RefTimeSeries/Events/51', 'wb') as SaveFile:
    pickle.dump(RefTimeSeries, SaveFile)

"""with open('RefTimeSeries/Events/5', 'r') as ReadFile:
    Event = pickle.load(ReadFile)

newTimeSeries.getPlot(Event)"""

## Event 6
###################################################
offset = ['Sat', 'Sep', 29, 17, 45, 30, 2012]
setTweets = newTimeSeries.desiredData('twitter_data', 420, offset)

words = ['spurs', 'united', 'trafford', 'thfc']

RefTimeSeries = newTimeSeries.getTimeSeries(words, setTweets)

# EventsFrame = pd.DataFrame(columns = columns, index = range(len(RefTimeSeries)))
#
# for index in range(len(RefTimeSeries)):
#    EventsFrame.ix[index] = RefTimeSeries[index]
#
# EventsFrame.to_pickle('RefTimeSeries/Events/DataFrame')
# Event = pd.read_pickle('RefTimeSeries/Events/DataFrame')

#Another method would be: storing each event in a different file
with open('RefTimeSeries/Events/61', 'wb') as SaveFile:
    pickle.dump(RefTimeSeries, SaveFile)

"""with open('RefTimeSeries/Events/6', 'r') as ReadFile:
    Event = pickle.load(ReadFile)

newTimeSeries.getPlot(Event)"""

## Event 7
###################################################
offset = ['Sun', 'Sep', 30, 18, 30, 30, 2012]
setTweets = newTimeSeries.desiredData('twitter_data', 420, offset)

words = ['xfactor', 'rylan', 'nicole', 'shows']

RefTimeSeries = newTimeSeries.getTimeSeries(words, setTweets)

# EventsFrame = pd.DataFrame(columns = columns, index = range(len(RefTimeSeries)))
#
# for index in range(len(RefTimeSeries)):
#    EventsFrame.ix[index] = RefTimeSeries[index]
#
# EventsFrame.to_pickle('RefTimeSeries/Events/DataFrame')
# Event = pd.read_pickle('RefTimeSeries/Events/DataFrame')

#Another method would be: storing each event in a different file
with open('RefTimeSeries/Events/71', 'wb') as SaveFile:
    pickle.dump(RefTimeSeries, SaveFile)

"""with open('RefTimeSeries/Events/7', 'r') as ReadFile:
    Event = pickle.load(ReadFile)

newTimeSeries.getPlot(Event)"""

## Event 8
###################################################
offset = ['Sun', 'Sep', 30, 20, 45, 30, 2012]
setTweets = newTimeSeries.desiredData('twitter_data', 420, offset)

words = ['rydercup', 'rydercup2012', 'golf', 'europe']

RefTimeSeries = newTimeSeries.getTimeSeries(words, setTweets)

# EventsFrame = pd.DataFrame(columns = columns, index = range(len(RefTimeSeries)))
#
# for index in range(len(RefTimeSeries)):
#    EventsFrame.ix[index] = RefTimeSeries[index]
#
# EventsFrame.to_pickle('RefTimeSeries/Events/DataFrame')
# Event = pd.read_pickle('RefTimeSeries/Events/DataFrame')

#Another method would be: storing each event in a different file
with open('RefTimeSeries/Events/81', 'wb') as SaveFile:
    pickle.dump(RefTimeSeries, SaveFile)

"""with open('RefTimeSeries/Events/8', 'r') as ReadFile:
    Event = pickle.load(ReadFile)

newTimeSeries.getPlot(Event)"""

## Event 9
###################################################
offset = ['Mon', 'Oct', 1, 15, 45, 30, 2012]
setTweets = newTimeSeries.desiredData('twitter_data', 420, offset)

words = ['happy', 'maxmonday', 'max\'s', 'birthday']

RefTimeSeries = newTimeSeries.getTimeSeries(words, setTweets)

# EventsFrame = pd.DataFrame(columns = columns, index = range(len(RefTimeSeries)))
#
# for index in range(len(RefTimeSeries)):
#    EventsFrame.ix[index] = RefTimeSeries[index]
#
# EventsFrame.to_pickle('RefTimeSeries/Events/DataFrame')
# Event = pd.read_pickle('RefTimeSeries/Events/DataFrame')

#Another method would be: storing each event in a different file
with open('RefTimeSeries/Events/91', 'wb') as SaveFile:
    pickle.dump(RefTimeSeries, SaveFile)

"""with open('RefTimeSeries/Events/9', 'r') as ReadFile:
    Event = pickle.load(ReadFile)

newTimeSeries.getPlot(Event)"""

## Event 10
###################################################
offset = ['Mon', 'Oct', 1, 15, 45, 30, 2012]
setTweets = newTimeSeries.desiredData('twitter_data', 420, offset)

words = ['black', 'history', 'month', 'october']

RefTimeSeries = newTimeSeries.getTimeSeries(words, setTweets)

# EventsFrame = pd.DataFrame(columns = columns, index = range(len(RefTimeSeries)))
#
# for index in range(len(RefTimeSeries)):
#    EventsFrame.ix[index] = RefTimeSeries[index]
#
# EventsFrame.to_pickle('RefTimeSeries/Events/DataFrame')
# Event = pd.read_pickle('RefTimeSeries/Events/DataFrame')

#Another method would be: storing each event in a different file
with open('RefTimeSeries/Events/101', 'wb') as SaveFile:
    pickle.dump(RefTimeSeries, SaveFile)

"""with open('RefTimeSeries/Events/10', 'r') as ReadFile:
    Event = pickle.load(ReadFile)

newTimeSeries.getPlot(Event)"""
