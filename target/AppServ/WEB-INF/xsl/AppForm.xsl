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
 </style>	
    		<script>
			function ButtonClik(button){
				if( button == "AppInsert" ){
					parent.ApplicationForm.location= "Show?REQUEST=AppInsertForm&amp;Button=" + button;
					parent.ServiceForm.location= "Show?REQUEST=ServUpdateForm&amp;Button=servUpdate";
					parent.SolrAppForm.location.assign("Show?REQUEST=AppSolrForm");
					parent.SolrServForm.location.assign("Show?REQUEST=ServSolrForm");
				}
				else
				if( button == "AppDelete" ){
					parent.ApplicationForm.location= "Show?REQUEST=AppForm&amp;Button=" + button;
					parent.ServiceForm.location= "Show?REQUEST=ServForm";
					parent.SolrAppForm.location.assign("Show?REQUEST=AppSolrForm");
					parent.SolrServForm.location.assign("Show?REQUEST=ServSolrForm");
				}
				else {
					parent.ApplicationForm.location= "Show?REQUEST=AppForm&amp;Button=" + button;
					parent.ServiceForm.location= "Show?REQUEST=ServForm";
				}
			}
			
		</script>
	
 </head>
<body>
<form action="Show?REQUEST=AppUpdateForm" name="AppUpdateForm" method="post">
	  <fieldset>
  	  	<legend style="ont-weight:bold;" >Application</legend>

		<input type="hidden" id="App_code" name="App_code" >
 				<xsl:attribute name="value">
  				<xsl:value-of select="Application/App_code"/>
  				</xsl:attribute> 
				</input>

		<label for="Name">Name:   </label>
  		<input type="text" id="Name" name="Name" style="width: 200px;" >
  				<xsl:attribute name="value">
  				<xsl:value-of select="Application/Name"/>
  				</xsl:attribute> 
  				</input> <br/>
  		<label for="Description">Description:</label>
  		<textarea type="text" id="Description" name="Description" cols="60" rows="2"  >
  				<xsl:value-of select="Application/Description"/>
  				</textarea><br/>
 		<label for="App_cost">App cost:</label>
  		<input type="number" id="App_cost" name="App_cost" style="width: 100px;" >
 				<xsl:attribute name="value"
  				><xsl:value-of select="Application/App_cost"/>
  				</xsl:attribute> 
  				</input><br/>
		<label for="App_group">App group:   </label>
  		<input type="text" id="App_group" name="App_group" style="width: 120px;" >
 				<xsl:attribute name="value"
  				><xsl:value-of select="Application/App_group"/>
  				</xsl:attribute> 
  				</input> <br/>
		<label for="App_type">App type:   </label>
  		<input type="text" id="App_type" name="App_type" style="width: 100px;" >
 				<xsl:attribute name="value"
  				><xsl:value-of select="Application/App_type"/>
  				</xsl:attribute> 
				</input> <br/>
  		<h5>Last Modified</h5>
 	  	<input type="date" id="Last_modified" name="Last_modified">
 				<xsl:attribute name="value"
  				><xsl:value-of select="Application/Last_modified"/>
  				</xsl:attribute> 
 	  			</input>
		<input type="submit" value="Update" style="font-weight:bold;"></input>
 	 </fieldset>
</form>
<table>
<tr>
<td>
		<button  onclick="ButtonClik('AppPrev'); " style="font-weight:bold;"  >
			<xsl:attribute name="title">Previous Application</xsl:attribute>
			Previous
		</button>
</td>
<td>
		<button onclick="ButtonClik('AppNext'); "  style="font-weight:bold;" >
			<xsl:attribute name="title">Next Application</xsl:attribute>
			Next
		</button>
</td>
<td>
		<button onclick="ButtonClik('AppInsert'); "  style="font-weight:bold;" >
			<xsl:attribute name="title">New Application</xsl:attribute>
			Insert
		</button>
</td>
<td>
		<button onclick="ButtonClik('AppDelete'); "  style="font-weight:bold;" >
			<xsl:attribute name="title">Delete Application</xsl:attribute>
			Delete
		</button>
</td>
</tr>	
</table>					
</body>
</html>




</xsl:template>
</xsl:stylesheet>

