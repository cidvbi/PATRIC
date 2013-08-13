  
<h2 align="center">Low complexity filtering</h2>

<p>The server filters your query sequence for low compositional
complexity regions by default.  Low complexity regions commonly give 
spuriously high scores that reflect compositional bias rather than 
significant position-by- position alignment.  Filtering can elminate 
these potentially confounding matches (e.g., hits against proline-rich 
regions or poly-A tails) from the blast reports, leaving regions whose 
blast statistics reflect the specificity of their pairwise alignment.
Queries searched with the blastn program are filtered with DUST.  Other
programs use SEG.  
</p>

<p>Low complexity sequence found by a filter program is substituted using the 
letter "N" in nucleotide sequence (e.g., "NNNNNNNNNNNNN") and the letter "X" 
in protein sequences (e.g., "XXXXXXXXX").  Users may turn off filtering by 
using the "Filter" option on the "Advanced options for the BLAST server" page. 
</p>


<h3>Reference for the DUST program:</h3>
 
<dd><b>Tatusov, R. L.</b> and <b>D. J. Lipman</b>, in preparation.</dd>

<dd><b>Hancock, J. M.</b> and <b>J. S. Armstrong</b> (1994). SIMPLE34: an 
improved and enhanced implementation for VAX and Sun computers of the 
SIMPLE algorithm for analysis of clustered repetitive motifs in nucleotide 
sequences.  Comput Appl Biosci 10:67-70. </dd>

<h3>Reference for the SEG program:</h3>
<dd><b>Wootton, J. C.</b> and <b>S. Federhen</b> (1993).  Statistics of
local complexity  in  amino acid sequences and sequence databases.
Computers in Chemistry 17:149-163.</dd>

<dd><b>Wootton, J. C.</b> and <b>S. Federhen</b> (1996).  Analysis of 
compositionally biased regions in sequence databases.  Methods in Enzymology
266: 554-571.</dd>

<h3>Reference for the role of filtering in search strategies:</h3>
<dd><b>Altschul, S. F.</b>, <b>M. S. Boguski</b>, <b>W. Gish</b>, 
<b>J. C. Wootton</b> (1994). Issues in searching molecular sequence 
databases. Nat Genet 6: 119-129.</dd>
