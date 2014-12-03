'''
Created on 22 August 2014
@author: Angathan FRANCIS
'''

import pickle
import numpy as np
import seaborn as sns
import matplotlib.pyplot as plt
from ReferenceTimeSeries import ReferenceTimeSeries

newTimeSeries = ReferenceTimeSeries()

## Non Event 1
###################################################
offset = ['Sat', 'Sep', 22, 15, 30, 30, 2012]
setTweets = newTimeSeries.desiredData('twitter_data', 180, offset)

words = ['iphone', 'phone', 'smartphone', 'samsung']

RefTimeSeries = newTimeSeries.getTimeSeries(words, setTweets)

with open('RefTimeSeries/NonEvents/1', 'wb') as SaveFile:
    pickle.dump(RefTimeSeries, SaveFile)

with open('RefTimeSeries/NonEvents/1', 'r') as ReadFile:
    Event = pickle.load(ReadFile)

newTimeSeries.getPlot(Event)

## Non Event 2
###################################################
offset = ['Sun', 'Sep', 23, 15, 30, 30, 2012]
setTweets = newTimeSeries.desiredData('twitter_data', 180, offset)

words = ['sunday', 'weekend', 'family', 'barbecue']

RefTimeSeries = newTimeSeries.getTimeSeries(words, setTweets)

with open('RefTimeSeries/NonEvents/2', 'wb') as SaveFile:
    pickle.dump(RefTimeSeries, SaveFile)

with open('RefTimeSeries/NonEvents/2', 'r') as ReadFile:
    Event = pickle.load(ReadFile)

newTimeSeries.getPlot(Event)

## Non Event 3
###################################################
offset = ['Wed', 'Sep', 26, 14, 00, 30, 2012]
setTweets = newTimeSeries.desiredData('twitter_data', 180, offset)

words = ['wednesday', 'middle', 'week', 'tired']

RefTimeSeries = newTimeSeries.getTimeSeries(words, setTweets)

with open('RefTimeSeries/NonEvents/3', 'wb') as SaveFile:
    pickle.dump(RefTimeSeries, SaveFile)

with open('RefTimeSeries/NonEvents/3', 'r') as ReadFile:
    Event = pickle.load(ReadFile)

newTimeSeries.getPlot(Event)

## Non Event 4
###################################################
offset = ['Wed', 'Sep', 26, 17, 45, 30, 2012]
setTweets = newTimeSeries.desiredData('twitter_data', 180, offset)

words = ['music', 'clip', 'youtube', 'new']

RefTimeSeries = newTimeSeries.getTimeSeries(words, setTweets)

with open('RefTimeSeries/NonEvents/4', 'wb') as SaveFile:
    pickle.dump(RefTimeSeries, SaveFile)

with open('RefTimeSeries/NonEvents/4', 'r') as ReadFile:
    Event = pickle.load(ReadFile)

newTimeSeries.getPlot(Event)

## Non Event 5
###################################################
offset = ['Sat', 'Sep', 29, 17, 45, 30, 2012]
setTweets = newTimeSeries.desiredData('twitter_data', 180, offset)

words = ['night', 'fun', 'dance', 'club']

RefTimeSeries = newTimeSeries.getTimeSeries(words, setTweets)

with open('RefTimeSeries/NonEvents/5', 'wb') as SaveFile:
    pickle.dump(RefTimeSeries, SaveFile)

with open('RefTimeSeries/NonEvents/5', 'r') as ReadFile:
    Event = pickle.load(ReadFile)

newTimeSeries.getPlot(Event)

## Non Event 6
###################################################
offset = ['Sun', 'Sep', 29, 15, 45, 30, 2012]
setTweets = newTimeSeries.desiredData('twitter_data', 180, offset)

words = ['outside', 'london', 'sunny', 'barbecue']

RefTimeSeries = newTimeSeries.getTimeSeries(words, setTweets)

with open('RefTimeSeries/NonEvents/6', 'wb') as SaveFile:
    pickle.dump(RefTimeSeries, SaveFile)

with open('RefTimeSeries/NonEvents/6', 'r') as ReadFile:
    Event = pickle.load(ReadFile)

newTimeSeries.getPlot(Event)

## Non Event 7
###################################################
offset = ['Sun', 'Sep', 30, 17, 30, 30, 2012]
setTweets = newTimeSeries.desiredData('twitter_data', 180, offset)

words = ['directioner', 'harry', 'direction', 'zayn']

RefTimeSeries = newTimeSeries.getTimeSeries(words, setTweets)

with open('RefTimeSeries/NonEvents/7', 'wb') as SaveFile:
    pickle.dump(RefTimeSeries, SaveFile)

with open('RefTimeSeries/NonEvents/7', 'r') as ReadFile:
    Event = pickle.load(ReadFile)

newTimeSeries.getPlot(Event)

## Non Event 8
###################################################
offset = ['Sun', 'Sep', 30, 20, 45, 30, 2012]
setTweets = newTimeSeries.desiredData('twitter_data', 180, offset)

words = ['bored', 'tomorrow', 'end', 'weekend']

RefTimeSeries = newTimeSeries.getTimeSeries(words, setTweets)

with open('RefTimeSeries/NonEvents/8', 'wb') as SaveFile:
    pickle.dump(RefTimeSeries, SaveFile)

with open('RefTimeSeries/NonEvents/8', 'r') as ReadFile:
    Event = pickle.load(ReadFile)

newTimeSeries.getPlot(Event)

## Non Event 9
###################################################
offset = ['Mon', 'Oct', 1, 7, 45, 30, 2012]
setTweets = newTimeSeries.desiredData('twitter_data', 180, offset)

words = ['monday', 'back', 'school', 'work']

RefTimeSeries = newTimeSeries.getTimeSeries(words, setTweets)

with open('RefTimeSeries/NonEvents/9', 'wb') as SaveFile:
    pickle.dump(RefTimeSeries, SaveFile)

with open('RefTimeSeries/NonEvents/9', 'r') as ReadFile:
    Event = pickle.load(ReadFile)

newTimeSeries.getPlot(Event)

## Non Event 10
###################################################
offset = ['Tue', 'Oct', 2, 14, 45, 30, 2012]
setTweets = newTimeSeries.desiredData('twitter_data', 180, offset)

words = ['tuesday', 'london', 'city', 'event']

RefTimeSeries = newTimeSeries.getTimeSeries(words, setTweets)

with open('RefTimeSeries/NonEvents/10', 'wb') as SaveFile:
    pickle.dump(RefTimeSeries, SaveFile)

with open('RefTimeSeries/NonEvents/10', 'r') as ReadFile:
    Event = pickle.load(ReadFile)

newTimeSeries.getPlot(Event)
newTimeSeries.getPlot(Event)

