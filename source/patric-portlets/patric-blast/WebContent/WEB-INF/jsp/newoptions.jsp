  
<h2>BLAST Search main parameters</h2>

<dl>
	<dt><b><a name="descriptions" id="descriptions">DESCRIPTIONS</a></b></dt>
	<dd>Restricts the number of short descriptions of matching
		sequences reported to the number specified; default
		limit is 100 descriptions.  See also EXPECT.</dd>
		
	<dt><b><a name = "alignments" id ="alignments">ALIGNMENTS</a></b></dt>
	<dd>Restricts database sequences to the number specified for
		which high-scoring segment pairs (HSPs) are reported;
		the default limit is 100.  If more database sequences
		than this happen to satisfy the statistical
		significance threshold for reporting (see EXPECT below), 
		only the matches ascribed the greatest statistical 
		significance are reported.</dd>
		
	<dt><b><a name="expect" id="expect">EXPECT</a></b></dt>
	<dd>The statistical significance threshold for reporting
		matches against database sequences; the default value
		is 10, such that 10 matches are expected to be found
		merely by chance, according to the stochastic model
		of Karlin and Altschul (1990).  If the statistical
		significance ascribed to a match is greater than the
		EXPECT threshold, the match will not be reported.
		Lower EXPECT thresholds are more stringent, leading
		to fewer chance matches being reported.  Fractional
		values are acceptable.</dd>

	<dt><b><a name="inclusion" id="inclusion">INCLUSION THRESHOLD</a></b></dt>
	<dd>The statistical significance threshold for including a sequence
		in the model used by PSI-BLAST on the next iteration.</dd>
		
	<dt><b><a name="organism" id="organism">ORGANISM NAME</a></b></dt>
	<dd>Enter the organism name in the form "Genus species" (e.g., "Homo sapiens").
		A number of popular organism names are listed on a pull-down menu.</dd>

	<dt><b><a name="taxonomic" id="taxonomic">TAXONOMIC CLASSIFICATION</a></b></dt>
	<dd>Enter any taxonomic group from the NCBI taxonomy (e.g. "Mammalia").

		<PRE>
		Some popular groups are:
		
		Archaea
		Bacteria
		Eukaryota
		Embryophyta (higher plants)
		Fungi
		Metazoa	(multicellular animals)
		Vertebrata
		Mammalia
		Rodentia
		Primates
		</PRE>
	
		<A HREF="/Taxonomy/tax.html">Explore the taxonomy database at NCBI</A>
	</dd>
	
	<dt><b><a name="filter" id="filter">FILTER (Low-complexity)</a></b></dt>
	<dd>Mask off segments of the query sequence that have
		low compositional complexity, as determined by the
		SEG program of Wootton &amp; Federhen (Computers and
		Chemistry, 1993) or, for BLASTN, by the DUST
		program of Tatusov and Lipman (in preparation).  
		Filtering can eliminate statistically significant but 
		biologically uninteresting reports from the blast 
		output (e.g., hits against common acidic-, basic- or 
		proline-rich regions), leaving the more biologically 
		interesting regions of the query sequence available 
		for specific matching against database sequences. <BR>
		
		Filtering is only applied to the query sequence (or
		its translation products), not to database sequences.
		Default filtering is DUST for BLASTN, SEG for other
		programs. <BR>	
		
		It is not unusual for nothing at all to be masked
		by SEG, when applied to sequences in SWISS-PROT, 
		so filtering should not be expected to
		always yield an effect.  Furthermore, in some cases,
		sequences are masked in their entirety, indicating that
		the statistical significance of any matches reported
		against the unfiltered query sequence should be suspect.</dd>

	<dt><b><a name="filter_hr" id="filter_hr">FILTER (Human repeats)</a></b></dt>
	<dd>This option masks Human repeats (LINE's and SINE's) and is especially
		useful for human sequences that may contain these repeats.  This
		option is still experimental and under development, so it 
		may change in the near future.<BR></dd>
 
 	<dt><b><a name="filter_mask" id="filter_mask">FILTER (Mask for lookup table only)</a></b></dt>
	<dd>This option masks only for purposes of constructing the lookup
		table used by BLAST.  The BLAST extensions are performed without
		masking.  This option is still experimental and may change in the
		near future.</dd>

	<dt><b><a name="ncbi-gi" id="ncbi-gi">NCBI-gi</a></b></dt>
	<dd>Causes NCBI gi identifiers to be shown in the output,
		in addition to the accession and/or locus name.</dd>	

	<dt><b><a name="gencodes" id="gencodes">Query Genetic Code</a></b></dt>
	<dd>Genetic code to be used in blastx translation of the query.</dd>

	<dt><b><a name="graphical-overview" id="graphical-overview">Graphical Overview</a></b></dt>
	<dd>An overview of the database sequences aligned to the query 
		sequence is shown.  The score of each alignment is indicated
		by one of five different colors, which divides the range
		of scores into five groups.  Multiple alignments on the
		same database sequence are connected by a striped line.  
		<FONT color=red>Mousing over</FONT> a hit sequence 
		causes the definition and score to be shown in the
		window at the top, <FONT color=red>clicking</FONT> on a hit sequence takes the user to 
		the associated alignments.</dd>
</dl>