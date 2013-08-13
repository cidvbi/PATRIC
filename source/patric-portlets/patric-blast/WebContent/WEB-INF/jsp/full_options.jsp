
	<h2 align="center">Full list of the BLAST Advanced options</h2>
    <UL>
      <LI> <a href = "#blastn">blastn</a> </LI>
      <LI> <a href = "#blastp">blastp</a> </LI>
      <LI> <a href = "#blastx">blastx</a> </LI>
      <LI> <a href = "#tblastn">tblastn</a> </LI>
    </UL>

    <h3><a name = blastn><FONT color="green">
        BLASTN Program Advanced Options</FONT></a></h3>
<PRE>
  -G  Cost to open a gap [Integer]
    default = 5
  -E  Cost to extend a gap [Integer]
    default = 2
  -q  Penalty for a mismatch in the blast portion of run [Integer]
    default = -3
  -r  Reward for a match in the blast portion of run [Integer]
    default = 1
  -e  Expectation value (E) [Real]
    default = 10.0
  -W  Word size, default is 11 for blastn, 3 for other programs.
  -v  Number of one-line descriptions (V) [Integer]
    default = 100
  -b  Number of alignments to show (B) [Integer]
    default = 100
</PRE>

    <h3><a name = blastp><FONT color="green">
         BLASTP Program Advanced Options</FONT></a></h3>
         
    <h3><a name = blastx><FONT color="green">
        BLASTX Program Advanced Options</FONT></a></h3>
        
    <h3><a name = tblastn><FONT color="green">
        TBLASTN Program Advanced Options</FONT></a></h3>
<PRE>
  -G  Cost to open a gap [Integer]
    default = 11
  -E  Cost to extend a gap [Integer]
    default = 1
  -e  Expectation value (E) [Real]
    default = 10.0
  -W  Word size, default is 11 for blastn, 3 for other programs.
  -v  Number of one-line descriptions (V) [Integer]
    default = 100
  -b  Number of alignments to show (B) [Integer]
    default = 100


  Limited values for gap existence and extension are supported for these three programs.  
  Some supported and suggested values are:

  Existence	Extension

     10             1
     10             2
     11             1
      8             2
      9             2
     
</PRE>
