
<h2 align="center">Out-Of-Frame BLAST notation</h2>
<p>
When protein aligned to the nucleotide there are 6 possibilities of
match at any point. In OOF alignment - upper sequence is DNAP - 3-frame 
translated DNA. Lower sequence is protein. At any position next protein 
base may be aligned to 6 possible bases in DNAP:
</p>

<P>(TBO - traditional blast output)</P>
<P>0:  &nbsp; &nbsp; &nbsp; &nbsp; 3 nucleotides missing - gap (TBO notation "-")</P>
<PRE>

OOF alignment with DNAP:

      DTRGGDTPQKSVFSRAQNTLWGERGDTQKRGGAQRGDIFSLWGG-GVLCV
      |  |  |  |  |  |  |  |  |  |  |  |  |  |  |   |  |
      D  G  T  K  F  A  T  G  G  Q  G  Q  D  S  G K V  V

TBO:

      DGTKFATGGQGQDSG-VV
      DGTKFATGGQGQDSG VV
      DGTKFATGGQGQDSGKVV

</PRE>
1: &nbsp; &nbsp; &nbsp; &nbsp; 2 nucleotides missing - "frameshift -2" (TBO notation "\\")
<PRE>

OOF alignment with DNAP:

      DTRGGDTPQKSVFSRAQNTLWGERGDTQKRGGAQRGDIFSLWGGGGVLCV
      |  |  |  |  |  |  |  |  |  |  |  |  |  |  |/  |  |
      D  G  T  K  F  A  T  G  G  Q  G  Q  D  S  GK  V  V

TBO:

      DGTKFATGGQGQDSG\\GVV
      DGTKFATGGQGQDSG   VV
      DGTKFATGGQGQDSG  KVV

</PRE>
2:  &nbsp; &nbsp; &nbsp; &nbsp; 1 nucletide missing - "frameshift -1" (TBO notation "\")
<PRE>

OOF alignment with DNAP:

      DTRGGDTPQKSVFSRAQNTLWGERGDTQKRGGAQRGDIFSLWGGERGV
      |  |  |  |  |  |  |  |  |  |  |  |  |  | /  |  |  
      D  G  T  K  F  A  T  G  G  Q  G  Q  D  S G  K  V  
TBO:

      DGTKFATGGQGQDS\GEV
      DGTKFATGGQGQDS G V
      DGTKFATGGQGQDS GKV  

</PRE>
3:  &nbsp; &nbsp; &nbsp; &nbsp; Complete match
<PRE>

OOF alignment with DNAP:

      DTRGGDTPQKSVFSRAQNTLWGERGDTQKRGGAQRGDIFSLWGGEKRGV
      |  |  |  |  |  |  |  |  |  |  |  |  |  |  |  |  | 
      D  G  T  K  F  A  T  G  G  Q  G  Q  D  S  G  K  V 

TBO:

      DGTKFATGGQGQDSGKV
      DGTKFATGGQGQDSGKV 
      DGTKFATGGQGQDSGKV 

</PRE>
4:  &nbsp; &nbsp; &nbsp; &nbsp; 1 nucleotide insertion - "frameshift +1" (TBO notation "/")
<PRE>

OOF alignment with DNAP:

      DTRGGDTPQKSVFSRAQNTLWGERGDTQKRGGAQRGDIFSLWGGVEKRGV
      |  |  |  |  |  |  |  |  |  |  |  |  |  |  |   \
      D  G  T  K  F  A  T  G  G  Q  G  Q  D  S  G   K  V

TBO:

      DGTKFATGGQGQDSG/KV
      DGTKFATGGQGQDSG KV
      DGTKFATGGQGQDSG KV

</PRE>
5:  &nbsp; &nbsp; &nbsp; &nbsp; 2 nucleotides insertion - "frameshift +2" (TBP notation "//")
<PRE>

OOF alignment with DNAP:

      DTRGGDTPQKSVFSRAQNTLWGERGDTQKRGGAQRGDIFSLFLWGGEKRGV
      |  |  |  |  |  |  |  |  |  |  |  |  |  |    \  |  |
      D  G  T  K  F  A  T  G  G  Q  G  Q  D  S    G  K  V

TBO:

      DGTKFATGGQGQDS//GKV
      DGTKFATGGQGQDS  GKV
      DGTKFATGGQGQDS  GKV

</PRE>


    <address><a href="mailto:shavirin@ncbi.nlm.nih.gov">Sergei Shavirin</a></address>
