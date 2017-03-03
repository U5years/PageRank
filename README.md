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
        Dead ends: a page that contains no internal links to other pages, and the PR matrix will lose weight every step then                      finally become more and more close to zero;
        Spider Traps: a page only contains link to itself. and the PR matrix will be dominated by one page and become 1 for                      that page but others' 0;
        
To avoid these above => PRN = (1 - a) * PR(N - 1) * Transition Matrix + a * e;
