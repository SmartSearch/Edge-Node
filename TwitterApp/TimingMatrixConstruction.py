'''
Created on 21 May 2014

@author: theopavlakou
'''
###########################################################################################################################
## A parser for the twitter data file with all the JSON data. Reads in the JSON data and prints out a matrix of the form:
# This prints out to the file in the following format:
# 1, 5, 7, 8
# 2,
# 3, 6
# which means that Tweet 1 contains words {5, 7, 8}, Tweet 2 contains no words in
# the bag of words and Tweet 3 contains word {6}.
############################################################################################################################

from DictionaryOfWords import DictionaryOfWords
from TweetRetriever import TweetRetriever
from MatrixBuilder import MatrixBuilder
import time
import matplotlib.pyplot as plt
import pickle

####################################################
##  The file containing the Tweets as JSONs
####################################################
jsonFileName = '/Users/Angathan/Desktop/Project/Data/twitter_data'

smallestWindow = 3000
largestWindow = 30000
increment = 2000
times = []
windows = range(smallestWindow, largestWindow, increment)
for sizeWindow in windows:
    print("For size of window = " + str(sizeWindow))
    ####################################################
    ##  Initialize
    ####################################################
    sizeOfWindow = sizeWindow
    tweetRetriever = TweetRetriever(jsonFileName, sizeOfWindow, 400)
    tweetRetriever.initialise()

    ####################################################
    ##  Load the Tweets from the file
    ####################################################

    ta = time.time()
    print("--- Loading Tweets ---")
    tweetSet = tweetRetriever.getNextWindow()

    ########################################################
    ##  Make a list of the 3000 most common words in the
    ##  Tweets which will be the columns of the matrix.
    ########################################################
    print("--- Loading most common words in the Tweets ---")
    dictOfWords = DictionaryOfWords()
    for tweet in tweetSet:
        dictOfWords.addFromSet(tweet.listOfWords())
    listOfWords = dictOfWords.getMostPopularWordsAndOccurrences(3000)
    print("--- Finished loading most common words in the Tweets ---")

    ########################################################
    ##  Create Sparse Matrix
    ########################################################
    matrixBuilder = MatrixBuilder(sizeOfWindow, len(listOfWords))

    ############################################################################################
    # For each Tweet, find the index of the words that correspond to the words in the Tweet.
    ############################################################################################
    numIt = 5

    tStartPop = time.time()
    for i in range(numIt):
        print("--- Populating matrix ---")
        tweetNumber = 0
        startDate = tweetSet[0].date
        endDate = tweetSet[len(tweetSet)-1].date
        for tweet in tweetSet:
            # Get the list of words in the tweet
            tweetWordList = tweet.listOfWords()
            # The first number is the index of the tweet (the row number)
            # Check for each word in the list of unique words, if it is in the Tweet, then print the index of the word
            for wordNumber in range(len(listOfWords)):
                if listOfWords[wordNumber][0] in tweetWordList:
                    matrixBuilder.addElement(tweetNumber, wordNumber, 1)
            # Next row
            tweetNumber = tweetNumber + 1
        print("--- Finished populating matrix ---")
    tEndPop = time.time()
    tAverage = (tEndPop - tStartPop)/numIt
    times.append(tAverage)

outputPickle = open("matrixTimings.pkl", 'wb')
pickle.dump([windows, times], outputPickle)
outputPickle.close()

pkl_file = open('matrixTimings.pkl', 'rb')
data1 = pickle.load(pkl_file)

plt.title("Time to create matrix vs number of Tweets")
plt.xlabel("Number of Tweets")
plt.ylabel("Average Time to Construct Matrix/s")
plt.ion()

i = 0
for w in windows:
    plt.scatter(data1[0][i], data1[1][i], c="blue")
    i += 1
plt.show(block=True)
print("--- End ---")
