<?xml version="1.0" encoding="ISO-8859-15"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output encoding="ISO-8859-15" indent="yes" method="html" omit-xml-declaration="yes"/>


<xsl:template match="data">

<html>
    <head>
		<title>Application Form</title>
   <style rel="stylesheet" type="text/css" media="screen">
legend {
    font-size:  1.4em;
    font-weight:  bold;
    position:  relative;
    top:  -.4em;
}
form label{
  font-weight:bold;
}
td {
  text-align: center;
}
 </style>	
 <script>
function ObjectClick(App_code){
	parent.ApplicationForm.location.assign("Show?REQUEST=AppForm&amp;Selected_App=" + App_code);
	parent.ServiceForm.location.assign("Show?REQUEST=ServForm");
}
</script>
 
 </head>
<body>
<form action="Show?REQUEST=AppSolrForm" name="AppSolrForm" method="post">
	  <fieldset>
  	  	<legend style="ont-weight:bold;" >Application search</legend>

		<label for="Pattern">Pattern:   </label>
  		<input type="text" id="Pattern" name="Pattern" style="width: 140px;" />
		<input type="submit" value="Go" style="font-weight:bold;"></input>
 	 </fieldset>
</form>
<table>
<xsl:for-each select="Solr/Application">
	<tr>
	<td style="width: 200px;text-align: center;">
		<a>
		<xsl:attribute name="href">javascript:ObjectClick('<xsl:value-of select="App_code"/>')</xsl:attribute>
				<xsl:value-of select="Name"/>
		</a>
	</td>
	</tr>
</xsl:for-each>
</table>
</body>
</html>

</xsl:template>
</xsl:stylesheet>

