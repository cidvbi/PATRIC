
<h2 align="center">FASTA format description</h2>

<p>
	A sequence in FASTA format begins with a single-line description, 
	followed by lines of sequence data.  The description line is 
	distinguished from the sequence data by a greater-than ("&gt;") symbol 
	in the first column.  It is recommended that all lines of text be 
	shorter than 80 characters in length. An example sequence in FASTA 
	<BR>format is:
</p>
	<PRE>
	&gt;gi|532319|pir|TVFV2E|TVFV2E envelope protein
	ELRLRYCAPAGFALLKCNDADYDGFKTNCSNVSVVHCTNLMNTTVTTGLLLNGSYSENRT
	QIWQKHRTSNDSALILLNKHYNLTVTCKRPGNKTVLPVTIMAGLVFHSQKYNLRLRQAWC
	HFPSNWKGAWKEVKEEIVNLPKERYRGTNDPKRIFFQRQWGDPETANLWFNCHGEFFYCK
	MDWFLNYLNNLTVDADHNECKNTSGTKSGNKRAPGPCVQRTYVACHIRSVIIWLETISKK
	TYAPPREGHLECTSTVTGMTVELNYIPKNRTNVTLSPQIESIWAAELDRYKLVEITPIGF
	APTEVRRYTGGHERQKRVPFVXXXXXXXXXXXXXXXXXXXXXXVQSQHLLAGILQQQKNL
	LAAVEAQQQMLKLTIWGVK
	</PRE>


<p>
	Sequences are expected to be represented in the standard 
	IUB/IUPAC amino acid and nucleic acid codes, with these 
	exceptions:  lower-case letters are accepted and are mapped 
	into upper-case; a single hyphen or dash can be used to represent 
	a gap of indeterminate length; and in amino acid sequences, U and *
	are acceptable letters (see below).  Before submitting a request, 
	any numerical digits in the query sequence should either be 
	removed or replaced by appropriate letter codes (e.g., N for 
	unknown nucleic acid residue or X for unknown amino acid residue).
	<BR>
	The nucleic acid codes supported are:
</p>
	<PRE>
	        A --&gt; adenosine           M --&gt; A C (amino)
	        C --&gt; cytidine            S --&gt; G C (strong)
	        G --&gt; guanine             W --&gt; A T (weak)
	        T --&gt; thymidine           B --&gt; G T C
	        U --&gt; uridine             D --&gt; G A T
	        R --&gt; G A (purine)        H --&gt; A C T
	        Y --&gt; T C (pyrimidine)    V --&gt; G C A
	        K --&gt; G T (keto)          N --&gt; A G C T (any)
	                                  -  gap of indeterminate length
	</PRE>


<p>
	For those programs that use amino acid query sequences (BLASTP 
	and TBLASTN), the accepted amino acid codes are:
</p>
	<PRE>
	
	    A  alanine                         P  proline
	    B  aspartate or asparagine         Q  glutamine
	    C  cystine                         R  arginine
	    D  aspartate                       S  serine
	    E  glutamate                       T  threonine
	    F  phenylalanine                   U  selenocysteine
	    G  glycine                         V  valine
	    H  histidine                       W  tryptophan
	    I  isoleucine                      Y  tyrosine
	    K  lysine                          Z  glutamate or glutamine
	    L  leucine                         X  any
	    M  methionine                      *  translation stop
	    N  asparagine                      -  gap of indeterminate length
	</PRE>
