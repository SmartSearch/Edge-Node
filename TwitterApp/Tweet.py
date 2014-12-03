from TweetParser import TweetParser

class Tweet:
	""" A class that represents a single tweet event. It has a string for the tweet content,
        a time and date that the event happened and a location as a point in the Cartesian coordinate system.
        """

	def __init__(self, contentIn, dateIn="", coordinateXIn = -1, coordinateYIn = -1):
		""" Initialises the Tweet with the content of the Tweet and the time
		and date of the Tweet along with the coordinates of where the event happened."""
		self.content = contentIn
		self.date = dateIn
		self.PyTime = int()
		self.coordinateX = coordinateXIn
		self.coordinateY = coordinateYIn
		self.parser = TweetParser()

	def listOfWords(self):
		""" Output:
				A list of words from the Tweet parsed by the parser.
		"""
		return self.parser.listOfCleanWords(self.content)

	def dictionaryOfWords(self):
		""" Output:
				A dictionary of words from the Tweet parsed by the parser.
		"""
		return self.parser.dictionaryOfCleanWords(self.content)
