from numpy import dot
from numpy import array
from scipy import *
from numpy.linalg import *
from scipy.sparse import *

class TPowerAlgorithm:
	""" A class to perform the TPower algorithm. """
	def truncateOperator(self, v, k):
		""" Performs the truncate operation for a vector in the TPower
		algorithm.
		Inputs:
			v:      The vector to truncate
			k:      The number of elements of the vector to keep
		Outputs:
			u:      The v vector zeroed apart from the k highest elements.
		"""
		u = zeros(v.shape[0])
		sortedIndices = argsort(abs(v), axis=0)
		sortedIndices = fliplr(sortedIndices.T).T
		vRestricted = v[sortedIndices[0:k],0]
		normV = 0
		if isinstance(vRestricted, csc_matrix):
			normV = norm(vRestricted.todense())
		else:
			normV = norm(vRestricted)
		if normV == 0:
			return csc_matrix(zeros(v.shape[0]))
		a = array(double(vRestricted))/normV
		u[sortedIndices[0:k]] = a[0:k, 0]
		u = csc_matrix(u)
		return u

	def getSparsePC(self, A, k):
		""" Performs the TPower algorithm to retrieve the first sparse PC.
		Inputs:
			A:      The (word x word) co-occurence matrix.
			k:      The desired sparsity (number of non-zero elements)
		Outputs:
			x:      The first k-sparse PC.
			f:      The eigenvalue associated with that PC.
		"""
		# Tolerance related to the difference in eigenvalues.
		# These could be made inputs.
		tolerance = 0.0000001
		maxIterations = 50
		x0 = ones(A.shape[0])

		# Turns x into a sparse vector
		x = csc_matrix(x0)

		# Power step
		s = dot(A, transpose(x.todense()))
		g = 2*s
		f = dot(x.todense(), s)

		# Truncate step
		x = self.truncateOperator(g, k)
		fOld = f
		i = 1

		# Main Loop
		while i <= maxIterations:
			s = dot(A, transpose(x.todense()))
			g = 2*s
			x = self.truncateOperator(g, k)
			f = dot(x.todense(), s)

			# If the old and new eigenvalues are almost equal, stop
			if (abs(f - fOld) < tolerance):
				break
			fOld = f
			i += 1
		return x.T, f[0, 0]



