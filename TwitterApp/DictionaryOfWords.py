class DictionaryOfWords:
	""" A class that represents a dictionary of words as keys and their occurrences as the value. """
	def __init__(self):
		self.dict = {}

	def addToDictionary(self, word):
		""" Add to dictionary the occurrence of the word.
			Input:
				word:	A word to be put into to the dictionary.
		"""
		if word in self.dict:
			self.dict[word] += 1
		else:
			self.dict[word] = 1

	def addFromSet(self, wordSet):
		""" Add words from a wordSet.
			Input:
				wordSet: 	A set/list of words to be put into the dictionary.
		"""
		for word in wordSet:
			self.addToDictionary(word)

	def getSortedListOfTuples(self):
		""" Output:
		 		a sorted list of tuples of the form (countForWord, word).
		"""
		listOfTuples = []
		for key in self.dict:
			listOfTuples.append((self.dict[key], key))
		return sorted(listOfTuples, reverse=True)

	def getMostPopularWords(self, limit):
		""" Gets the most popular words ranked by the number of times they have appeared.
			Input:
				limit:	The max number of words to return.
			Output:
				A list of words in sorted order from most popular to least.
		"""
		listOfTuples = self.getSortedListOfTuples()
		listToReturn = []
		x = 1
		for (value, key) in listOfTuples:  # @UnusedVariable
			if x <= limit:
				listToReturn.append(key)
				x += 1
			else:
				break
		return listToReturn

	def getMostPopularWordsAndOccurrences(self, limit):
		""" Gets the most popular words ranked by the number of times they have
			appeared and the number of times they have appeared as a tuple.
			Input:
				limit:	The max number of words to return.
			Output:
				A list of (word, occurrence) tupples in sorted order from most
				popular to least.
		"""
		listOfTuples = self.getSortedListOfTuples()
		listToReturn = []
		x = 0
		for (value, key) in listOfTuples:
			if x < limit:
				listToReturn.append((key, value))
				x += 1
			else:
				break
		return listToReturn

	def getMostPopularWordsAndRank(self, limit):
		""" Gets the most popular words ranked by the number of times they have
			appeared in a dictionary with the rank as the values.
			Input:
				limit:	The max number of words to return.
			Output:
				A dictionary with words as the keys and rank as the values.
		"""
		listOfTuples = self.getSortedListOfTuples()
		dictToReturn = {}
		x = 0
		for (countForWord, word) in listOfTuples:  # @UnusedVariable
			if x < limit:
				dictToReturn[word] = x
				x += 1
			else:
				break
		return dictToReturn

