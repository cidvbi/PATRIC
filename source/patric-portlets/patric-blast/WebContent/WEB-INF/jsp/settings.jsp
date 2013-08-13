<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects />
<%
String windowID = resourceRequest.getWindowID();
//String contextPath = response.encodeURL(resourceRequest.getContextPath());
%>
<!--
/* $Id: blast.html,v 1.4 2003/05/22 16:20:45 dondosha Exp $
* ===========================================================================
*
*                            PUBLIC DOMAIN NOTICE
*               National Center for Biotechnology Information
*
*  This software/database is a "United States Government Work" under the
*  terms of the United States Copyright Act.  It was written as part of
*  the author's official duties as a United States Government employee and
*  thus cannot be copyrighted.  This software/database is freely available
*  to the public for use. The National Library of Medicine and the U.S.
*  Government have not placed any restriction on its use or reproduction.
*
*  Although all reasonable efforts have been taken to ensure the accuracy
*  and reliability of the software and data, the NLM and the U.S.
*  Government do not and cannot warrant the performance or results that
*  may be obtained by using this software or data. The NLM and the U.S.
*  Government disclaim all warranties, express or implied, including
*  warranties of performance, merchantability or fitness for any particular
*  purpose.
*
*  Please cite the author in any work or product based on this material.
*
* ===========================================================================
*
* File Name:  $RCSfile: blast.html,v $
*
* Author:  Sergei Shavirin
*
* Initial Version Creation Date: 03/14/2000
*
* $Revision: 1.4 $
*
* File Description:
*         Template for standalone BLAST Web page
*
* $Log: blast.html,v $
* Revision 1.4  2003/05/22 16:20:45  dondosha
* Removed references to blast_form.map: describe the map inside HTML
*
* Revision 1.3  2003/05/09 17:54:38  dondosha
* Added select menu for database genetic codes
*
* Revision 1.2  2003/05/05 18:14:02  dondosha
* Uncommented discontiguous megablast options; added subsequence options
*
* Revision 1.1  2002/08/06 19:03:51  dondosha
* WWW BLAST server, initial CVS revision
*

-->
<!--
<map name=img_map>
<area shape=rect coords=2,1,48,21 href="http://www.ncbi.nlm.nih.gov">
<area shape=rect coords=385,1,435,21 href="index.html">
<area shape=rect coords=436,1,486,21 href="http://www.ncbi.nlm.nih.gov/Entrez/">
<area shape=rect coords=487,1,508,21 href="http://patric.gepasi.org/software/blast/docs/blast_help.html">
</map>
<img USEMAP=#img_map WIDTH=509 HEIGHT=22 SRC="images/blast_form.gif" ISMAP> 
-->
<div id="blast-landing">
	<hr>
	<i>This server runs NCBI WWW BLAST 2.2.22</i>
	<hr>
	<br \>
	NOTE: This service searches only the features within PATRIC's assigned organisms.
	<br \><br \>
	<FORM ACTION="<%=request.getContextPath()%>/cgi/wrapBlast.cgi"
	    METHOD = 'POST'
	    ENCTYPE= "multipart/form-data"
	    TARGET="<%=windowID%>_catch"
	    ID="<%=windowID%>_MainBlastForm">
	<B>Choose program to use and database to search:</B>
	<P><a href="javascript:showBlastHelp('<%=windowID%>', 'blast_program')">
		Program</a>
		<select name = "PROGRAM">
		    <option VALUE="blastn" > blastn </option>
		    <option VALUE="blastp" > blastp </option>
		    <option VALUE="blastx" > blastx </option>
		    <option VALUE="tblastn" > tblastn </option>
		    <option VALUE="tblastx" > tblastx </option>
		</select>
		<a href="javascript:showBlastHelp('<%=windowID%>', 'blast_databases')">
		Database</a>
		<select name = "DATALIB">
		<option VALUE = "patric_cds"> PATRIC CDS</option>
		<option VALUE = "patric_rna"> PATRIC RNA</option>
		<option VALUE = "patric_protein"> PATRIC protein</option>		
		<option VALUE = "brc_cds"> Legacy BRC CDS</option>
		<option VALUE = "brc_rna"> Legacy BRC RNA</option>
		<option VALUE = "brc_protein"> Legacy BRC protein</option>
		<option VALUE = "refseq_cds"> RefSeq CDS</option>
		<option VALUE = "refseq_rna"> RefSeq RNA</option>
		<option VALUE = "refseq_protein"> RefSeq protein</option>
		<option VALUE = "genome_seq"> Genomic sequences</option>
		<option VALUE = "plasmid_seq"> Plasmid sequences</option>
		<option VALUE = "patric_protein_plasmid"> PATRIC protein (plasmid)</option>
		<option VALUE = "refseq_protein_plasmid"> RefSeq protein (plasmid)</option>
		<option VALUE = "patric_protein_transcriptomics"> PATRIC protein (transcriptomics)</option>
		</select>
	</P>
	<!--
	Enter here your input data as 
	<select name = "INPUT_TYPE"> 
	    <option VALUE="Sequence in FASTA format" > Sequence in FASTA format 
	    <option VALUE="Accession or GI" > Accession or GI 
	</select>
	-->
	
	<P>Enter sequence below in <a href="javascript:showBlastHelp('<%=windowID%>', 'fasta')">
		FASTA</a>  format 
		<BR>
		<textarea name="SEQUENCE" rows="6" cols="60"></textarea>
		<BR>
	</P>
	Or load it from disk 
	<INPUT TYPE="file" NAME="SEQFILE" id="<%=windowID%>_fastaFile">
	<P>
		Set subsequence: From
		&nbsp;&nbsp;<input TYPE="text" NAME="QUERY_FROM" VALUE="" SIZE="10">
		&nbsp;&nbsp;&nbsp;&nbsp; To
		<input TYPE="text" NAME="QUERY_TO" VALUE="" SIZE="10">
	</P>
	<br/>
	<input type="button" class="button" value="Clear Sequence" onclick="ClearBlastFasta('<%=windowID%>')" />
	<input type="button" class="button rightarrow" value="Search" onClick="RunBlastCGI('<%=windowID%>');return false;" />

	<HR>
	
	The query sequence is 
	<a href="javascript:showBlastHelp('<%=windowID%>', 'filtered')">
	filtered</a> 
	for low complexity regions by default.
	<BR>
	<a href="javascript:scrollBlastHelp('<%=windowID%>', 'newoptions','filter')">
	Filter</a>
	 <INPUT TYPE="checkbox" VALUE="L" NAME="FILTER" CHECKED> Low complexity
	 <INPUT TYPE="checkbox" VALUE="m" NAME="FILTER"> Mask for lookup table only
	<P>
	<a href="javascript:scrollBlastHelp('<%=windowID%>', 'newoptions','expect')">
	Expect</a>
	<select name = "EXPECT">
	    <option VALUE="0.0001" > 0.0001 </option>
	    <option VALUE="0.01" > 0.01 </option>
	    <option VALUE="1" > 1 </option>
	    <option VALUE="10" selected> 10 </option> 
	    <option VALUE="100" > 100 </option>
	    <option VALUE="1000" > 1000 </option>
	</select>
	&nbsp;&nbsp;
	
	<a href="javascript:showBlastHelp('<%=windowID%>', 'matrix_info')">
	Matrix</a>
	<select name = "MAT_PARAM">
	    <option value = "PAM30	 9	 1"> PAM30 </option>
	    <option value = "PAM70	 10	 1"> PAM70 </option> 
	    <option value = "BLOSUM80	 10	 1"> BLOSUM80 </option>
	    <option selected value = "BLOSUM62	 11	 1"> BLOSUM62 </option>
	    <option value = "BLOSUM45	 14	 2"> BLOSUM45 </option>
	</select>
	<INPUT TYPE="checkbox" NAME="UNGAPPED_ALIGNMENT" VALUE="is_set"> Perform ungapped alignment
	</P>
	 
	<P>
	<a href="javascript:scrollBlastHelp('<%=windowID%>', 'newoptions','gencodes')">
	Query Genetic Codes (blastx only) 
	</a>
	<select name = "GENETIC_CODE">
	 <option VALUE="Standard (1)" > Standard (1) </option>
	 <option VALUE="Vertebrate Mitochondrial (2)" > Vertebrate Mitochondrial (2)</option> 
	 <option VALUE="Yeast Mitochondrial (3)" > Yeast Mitochondrial (3) </option>
	 <option VALUE="Mold Mitochondrial; ... (4)" > Mold Mitochondrial; ... (4) </option>
	 <option VALUE="Invertebrate Mitochondrial (5)" > Invertebrate Mitochondrial (5) </option>
	 <option VALUE="Ciliate Nuclear; ... (6)" > Ciliate Nuclear; ... (6) </option>
	 <option VALUE="Echinoderm Mitochondrial (9)" > Echinoderm Mitochondrial (9) </option>
	 <option VALUE="Euplotid Nuclear (10)" > Euplotid Nuclear (10) </option>
	 <option VALUE="Bacterial (11)" > Bacterial (11) </option>
	 <option VALUE="Alternative Yeast Nuclear (12)" > Alternative Yeast Nuclear (12)</option> 
	 <option VALUE="Ascidian Mitochondrial (13)" > Ascidian Mitochondrial (13) </option>
	 <option VALUE="Flatworm Mitochondrial (14)" > Flatworm Mitochondrial (14) </option>
	 <option VALUE="Blepharisma Macronuclear (15)" > Blepharisma Macronuclear (15) </option>
	</select>
	</P>
	<P>
	<a href="javascript:scrollBlastHelp('<%=windowID%>', 'newoptions','gencodes')">
	Database Genetic Codes (tblast[nx] only)
	</a>
	<select name = "DB_GENETIC_CODE">
	 <option VALUE="Standard (1)" > Standard (1)</option>
	 <option VALUE="Vertebrate Mitochondrial (2)" > Vertebrate Mitochondrial (2)</option>
	 <option VALUE="Yeast Mitochondrial (3)" > Yeast Mitochondrial (3)</option>
	 <option VALUE="Mold Mitochondrial; ... (4)" > Mold Mitochondrial; ... (4)</option>
	 <option VALUE="Invertebrate Mitochondrial (5)" > Invertebrate Mitochondrial (5)</option>
	 <option VALUE="Ciliate Nuclear; ... (6)" > Ciliate Nuclear; ... (6)</option>
	 <option VALUE="Echinoderm Mitochondrial (9)" > Echinoderm Mitochondrial (9)</option>
	 <option VALUE="Euplotid Nuclear (10)" > Euplotid Nuclear (10)</option>
	 <option VALUE="Bacterial (11)" > Bacterial (11)</option>
	 <option VALUE="Alternative Yeast Nuclear (12)" > Alternative Yeast Nuclear (12)</option>
	 <option VALUE="Ascidian Mitochondrial (13)" > Ascidian Mitochondrial (13)</option>
	 <option VALUE="Flatworm Mitochondrial (14)" > Flatworm Mitochondrial (14)</option>
	 <option VALUE="Blepharisma Macronuclear (15)" > Blepharisma Macronuclear (15)</option>
	</select>
	</P>
	<P>
	<a href="javascript:showBlastHelp('<%=windowID%>', 'oof_notation')">
	Frame shift penalty</a> for blastx 
	<select NAME = "OOF_ALIGN"> 
	 <option VALUE="6" > 6</option>
	 <option VALUE="7" > 7</option>
	 <option VALUE="8" > 8</option>
	 <option VALUE="9" > 9</option>
	 <option VALUE="10" > 10</option>
	 <option VALUE="11" > 11</option>
	 <option VALUE="12" > 12</option>
	 <option VALUE="13" > 13</option>
	 <option VALUE="14" > 14</option>
	 <option VALUE="15" > 15</option>
	 <option VALUE="16" > 16</option>
	 <option VALUE="17" > 17</option>
	 <option VALUE="18" > 18</option>
	 <option VALUE="19" > 19</option>
	 <option VALUE="20" > 20</option>
	 <option VALUE="25" > 25</option>
	 <option VALUE="30" > 30</option>
	 <option VALUE="50" > 50</option>
	 <option VALUE="1000" > 1000</option>
	 <option selected VALUE = "0"> No OOF</option>
	</select>
	</P>
	<P>
	<a href="javascript:showBlastHelp('<%=windowID%>', 'full_options')">
	Other advanced options:</a> 
	&nbsp;&nbsp;&nbsp;&nbsp; 
	<INPUT TYPE="text" NAME="OTHER_ADVANCED" VALUE="" MAXLENGTH="50">
	<HR>
	<!--
	<INPUT TYPE="checkbox" NAME="NCBI_GI" >&nbsp;&nbsp;
	<a href="<?php echo $URLROOT ?>/software/blast/docs/newoptions.html#ncbi-gi"> NCBI-gi</a>
	&nbsp;&nbsp;&nbsp;&nbsp;
	-->
	<INPUT TYPE="checkbox" NAME="OVERVIEW"  CHECKED> 

	<a href="javascript:scrollBlastHelp('<%=windowID%>', 'newoptions','graphical-overview')">Graphical Overview</a>
	&nbsp;&nbsp;
	<a href="javascript:scrollBlastHelp('<%=windowID%>', 'options','alignmentviews')">Alignment view</a>
	<select name = "ALIGNMENT_VIEW">
	    <option value=0> Pairwise</option>
	    <option value=1> master-slave with identities</option>
	    <option value=2> master-slave without identities</option>
	    <option value=3> flat master-slave with identities</option>
	    <option value=4> flat master-slave without identities</option>
	    <option value=7> BLAST XML</option>
	    <option value=9> Hit Table</option>
	</select>
	<BR>
	<a href="javascript:scrollBlastHelp('<%=windowID%>', 'newoptions','descriptions')">Descriptions</a>
	<select name = "DESCRIPTIONS">
	    <option VALUE="0" > 0 </option>
	    <option VALUE="10" > 10 </option>
	    <option VALUE="50" > 50 </option>
	    <option VALUE="100" selected> 100 </option>
	    <option VALUE="250" > 250 </option>
	    <option VALUE="500" > 500 </option>
	</select>
	&nbsp;&nbsp;
	<a href="javascript:scrollBlastHelp('<%=windowID%>', 'newoptions','alignments')">Alignments</a>
	<select name = "ALIGNMENTS">
	    <option VALUE="0" > 0 </option>
	    <option VALUE="10" > 10 </option>
	    <option VALUE="50" selected> 50 </option>
	    <option VALUE="100" > 100 </option>
	    <option VALUE="250" > 250 </option>
	    <option VALUE="500" > 500 </option>
	</select>
	<a href="javascript:showBlastHelp('<%=windowID%>', 'color_schema');return false;">Color schema</a>
	<select name = "COLOR_SCHEMA">
	    <option selected value = 0> No color schema </option>
	    <option value = 1> Color schema 1 </option>
	    <option value = 2> Color schema 2 </option>
	    <option value = 3> Color schema 3 </option>
	    <option value = 4> Color schema 4 </option>
	    <option value = 5> Color schema 5 </option>
	    <option value = 6> Color schema 6 </option>
	</select>
	<br/>
	<input type="button" class="button" value="Clear Sequence" onclick="ClearBlastFasta('<%=windowID%>')" />
	<input type="button" class="button rightarrow" value="Search" onClick="RunBlastCGI('<%=windowID%>');return false;" />
 	
	</form>
</div>		 

