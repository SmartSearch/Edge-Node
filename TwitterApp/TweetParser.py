import re
import pickle

class TweetParser:
	""" A class that parses Tweet content making sure that only relevant and informative words are taken into consideration """

	def __init__(self):
		self.irrelevantStrings = []
		self.wordInit()

	def wordInit(self):
		with open('wordlist.txt', 'rb') as wordfile:
			self.words = pickle.Unpickler(wordfile)
			self.irrelevantStrings = self.words.load()

	def listOfCleanWords(self, string):
		""" Takes in a string of the content of a tweet and returns a list that has only relevant informative words in it.
			Input:
				string:		The string to be parsed.
			Output:
				A list of filtered words.
		"""
		words = string.rsplit()
		cleanWords = []
		for word in words:
			# Strip all the punctuation from the edges of the words and should all be lowercase
			wordStripped = word.strip("~`;?!*,.\"'-+=:#)(^%$_").lower()

			if len(wordStripped) > 3 and wordStripped not in self.irrelevantStrings and self.isValidWord(wordStripped):
				cleanWords.append(wordStripped)
		return cleanWords

	def isValidWord(self, word):
		"""
		Tests the input word to see whether it passes a few tests to check if it is valid.
		Input:
			word:	The word to be checked.
		Output:
			True if the word is valid.
			False if the word is invalid.
		"""
		# TODO
		# Improvements to add:
		#   Get rid of speech marks

		# Any website or name should be ignored. Matches the beginning of the word.
		matchBeginning = re.match("@|(http)|&|\d", word)
		# If any letter is repeated three times or more
		patternRepeatedChars = re.compile(r'(.)\1{2}')
		matchRepeatedChars = patternRepeatedChars.search(word)
		# If any punctuation is in the middle of the word
		# Need to escape the - i.e. will think it is like [a-z]
		patternPunctuation = re.compile(r'[a-z]*[?!_@.\-/(,)]+[a-z]*')
		matchPunctuation = patternPunctuation.search(word)
		isValid = (matchBeginning == None and matchRepeatedChars == None and matchPunctuation == None)
		return isValid

	def dictionaryOfCleanWords(self, string):
		""" Takes in a string of the content of a tweet and returns a dictionary that has only relevant informative words in it mapped to 1
			Input:
				string:		The string to be parsed.
			Output:
				A dictionary of filtered words mapped to 1.
		"""
		words = string.rsplit()
		cleanWords = {}
		for word in words:
			# Strip all the punctuation from the edges of the words and should all be lowercase
			wordStripped = word.strip("~`;?!*,.\"'-+=:#)(^%$_").lower()

			if len(wordStripped) > 3 and wordStripped not in self.irrelevantStrings and self.isValidWord(wordStripped):
				cleanWords[wordStripped] = 1
		return cleanWords
