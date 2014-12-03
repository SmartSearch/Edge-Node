import json
import time
from datetime import datetime
from Tweet import Tweet

class TweetRetriever:
	""" A class that retrieves Tweets from the file name specified. It retrieves tweets in a rolling window fashion. The window size is specified and so is the incrementing batch size."""

	def __init__(self, nameFileOfTweets, windowSize, batchSize):
		self.nameFileOfTweets = nameFileOfTweets
		self.windowSize = windowSize
		self.batchSize = batchSize
		self.firstIter = True
		self.currentTweets = []
		self.startTime = 0
		self.iterTime = 0
		self.endTime = 0
		self.eof = 0

	def initialise(self):
		""" Must be called before anything else. """
		self.jsonFile = open(self.nameFileOfTweets, 'r')
		self.decoder = json.JSONDecoder()

		# Get the first Tweet timestamp
		firstTweet = self.jsonFile.readline()
		firstTweetDecoded = self.decoder.decode(firstTweet)
		timestamp = firstTweetDecoded["created_at"]
		modifiedTimestamp = datetime.strptime(timestamp, '%a %b %d %H:%M:%S +0000 %Y')
		self.startTime = time.mktime(modifiedTimestamp.timetuple())
		self.jsonFile.seek(0)

	def getNextWindow(self):
		""" Retrieves the next window of Tweets and gives the previous list of Tweets also.
		Output:	(self.currentTweets, oldBatch) """
		if self.eof == 1:
			return -1

		# Initialise time information for the iteration
		self.endTime = self.startTime + self.windowSize
		self.iterTime = self.startTime + self.batchSize
		
		# If empty then we have not yet got any Tweets
		index = 0
		oldBatch = []
		numberConsecutivePasses = 0
		currentPyTimestamp = 0
		
		while numberConsecutivePasses < 100 and currentPyTimestamp <= self.endTime:
			try:

				# Get all lines in the file
				line = self.jsonFile.readline()
				
				# The lines will contain a non-empty string until the eof
				# so we can break the loop when this happens
				if line == "":
					print("------ Reached the end of file ------")
					self.eof = 1
					break
				
				# Translate the JSON string into python JSON representation
				line = unicode(line, errors = 'replace')
				jsonObject = self.decoder.decode(line)
				tweet = Tweet(jsonObject["text"], jsonObject["created_at"])

				# Transforming Tweet timestamps into PYTHON time information
				currentTimestamp = datetime.strptime(tweet.date, '%a %b %d %H:%M:%S +0000 %Y')
				currentPyTimestamp = time.mktime(currentTimestamp.timetuple())
				tweet.PyTime = currentPyTimestamp

                                # If we have more than the window size then pop the oldest off
				if not self.firstIter:
					while self.currentTweets[0].PyTime < self.startTime:
						oldBatch.append(self.currentTweets.pop(0))
							
                                # Add the Tweet to the list
				self.currentTweets.append(tweet)
				
				numberConsecutivePasses = 0
				
			# Some of the lines have encoding errors so ignore them
			except UnicodeDecodeError:
				numberConsecutivePasses += 1
				print 'Error'
				pass
			except:
				numberConsecutivePasses += 1
				print 'Error'
				pass
                
		self.startTime = self.startTime + self.batchSize
		self.firstIter = False

		return (self.currentTweets, oldBatch)
