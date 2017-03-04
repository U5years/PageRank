# PageRank
MapReduce &amp; Transition Matrix

Basic theories behind PageRank:
      1. More important websites are likely to recieve more links from other websites;
      2. Websites with higher PageRank will pass higher weight;
      
How to represent the directivity between pages => Transition Matrix. For instance: Website A links to B, C, D; B links to A,    D; C links to A; D links to B, C;
                To\From    A     B     C     D 
A -> B C D          A      0    1/2    1     0                                   0   1/2  1  0
B -> A D            B     1/3    0     0    1/2        =>  Transition Matrix =  1/3   0   0 1/2
C -> A              C     1/3    0     0    1/2                                 1/3   0   0 1/2
D -> B C            D     1/3   1/2    0     0                                  1/3  1/2  0  0

How to represent the importance of each website => initialize with a number as PR0, in this project use 1/numbers of websites.

  PR1 = PR0 * transition matrix  PR2 = PR1 * transition matrix ... PRN = PR(N-1) * transition matrix.
  This will finally converge, because the important page will get a obvious higher than these pages which are not so important
  
Some corner cases:
        Dead ends: a page that contains no internal links to other pages, and the PR matrix will lose weight every step then finally become more and more close to zero;
        Spider Traps: a page only contains link to itself. and the PR matrix will be dominated by one page and become 1 for that page but others' 0;
        
To avoid these above => PRN = (1 - a) * PR(N - 1) * Transition Matrix + a * e;


Based on the theory above, let's implement on MapReduce! 

	And the first question need to be sloved is what is the input? The Matrix? 

		No, one reason is the transition matrix is sparse matrix, resulted from the number of links in one page is much more tiny compared to the amount of pages, and this result in space waste; 
	    	    another reason is when new page comes or is need to remove, it is pretty difficult to insect or delete this informations onto the matrix.

		Then the actual imput format for transition matrix is one page followed by the pages it links to, e.g. 1  2,7,8,29.  The input for PR0 is the page with 1/6012, which means the amount of pages;


	With declaration of the input files clearly, the next question come into mind is how to calculate the matrix product?

		The amount of the page is so huge that it is too difficult to calculate the matrix product in memory, and is almost impossible for the hardware technology so far.

		We need two MapReducers to complete this processing, and the first MapReducer also needs two mappers, one for transition matrix cell, one for PR matrix cell and the reducer of it calculate the product of matrix cell and PR cell.

		The second MapReducer is to sum all cells together for each page.


The workflow is showed as below:

                  MR1.mapper1           +           MR1.mapper2         =>                    MR1.reducer            =>                MR2.mapper                 =>                  MR2.reducer
1 2,7,8,29       key: 1 | Value: 2=1/4
                 key: 1 | Value: 7=1/4
		 key: 1 | Value: 8=1/4
		 key: 1 | Value: 28=1/4
                       ...
1 1/6012                                      key: 1 | value: 1/6012             1 | 2=1/4, 7=1/4, 8=1/4, 28=1/4 1/6012         Key: 2 | Value: 1/4 * 1/6012
                                                      ...                                         ...                           Key: 7 | Value: 1/4 * 1/6012                calcul all values of the same key
																Key: 8 | Value: 1/4 * 1/6012
																Key: 28 | Value: 1/4 * 1/6012
																             ...