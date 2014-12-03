import random

class BagOfWords:
	""" A class that represents the bag of words. It contains a set of words and methods to add to that set, based on some rules.
	This particular BagOfWords is probabilistic and will take a word with a certain probability, based on its member variable.
	The idea is that words that appear often will get accepted into the bag of words, whereas those that appear very sparsely will
	not be in the resulting bag of words. """

	def __init__(self, probability):
		self.setOfWords = set()
		# Probability of adding a word to the set
		self.probability = probability

	def addToSetWithProbability(self, word):
		""" Add to the set with a probability given by the member variable. """
		# If p is greater than the random number drawn from a uniform distribution
		if random.uniform(0, 1) < self.probability:
			self.setOfWords.add(word)

	def addFromSetWithProbability(self, wordSet):
		""" Add words from a wordSet, each with probability given by the member variable, probability. """
		for word in wordSet:
			self.addToSetWithProbability(word)

	def getListOfWords(self):
		""" Gets a list of words from the set of words, so that an order can be established. """
		returnList = []
		for word in self.setOfWords:
			returnList.append(word)
		return returnList

