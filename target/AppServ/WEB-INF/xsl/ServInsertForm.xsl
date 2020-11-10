<?xml version="1.0" encoding="ISO-8859-15"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output encoding="ISO-8859-15" indent="yes" method="html" omit-xml-declaration="yes"/>


<xsl:template match="data">

<html>
	<head>
		<title>Service Form</title>
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
		var url = "Show?REQUEST=ServUpdateForm&amp;Button=" + button;
		parent.ServiceForm.location=url;
	}
			
</script>
	
</head>
<body>
<form action="Show?REQUEST=ServInsertForm" name="ServUpdateForm" method="post">
	  <fieldset>
  	  	<legend style="ont-weight:bold;" >Service</legend>

		<input type="hidden" id="App_code" name="App_code" >
 				<xsl:attribute name="value">
  				<xsl:value-of select="Service/App_code"/>
  				</xsl:attribute> 
				</input>
		<input type="hidden" id="App_code" name="Service_code" >
 				<xsl:attribute name="value">
  				<xsl:value-of select="Service/Service_code"/>
  				</xsl:attribute> 
				</input>
		<label for="Name">Name    :</label>
  		<input type="text" id="Name" name="Name" style="width: 200px;" >
  				<xsl:attribute name="value">
  				<xsl:value-of select="Service/Name"/>
  				</xsl:attribute> 
  				</input> <br/>
  		<label for="Description">Description:</label>
  		<textarea type="text" id="Description" name="Description" cols="60" rows="2"  >
  				<xsl:value-of select="Service/Description"/>
  				</textarea><br/>
 		<label for="Type">Type   :</label>
  		<input type="text" id="Type" name="Type" style="width: 80px;" >
 				<xsl:attribute name="value"
  				><xsl:value-of select="Service/Type"/>
  				</xsl:attribute> 
  				</input><br/>
		<label for="Sub_type">Sub type:   </label>
  		<input type="text" id="Sub_type" name="Sub_type" style="width: 80px;" >
 				<xsl:attribute name="value"
  				><xsl:value-of select="Service/Sub_type"/>
  				</xsl:attribute> 
  				</input> <br/>
  		<h5>Last Modified</h5>
 	  	<input type="date" id="Last_modified" name="Last_modified">
 				<xsl:attribute name="value"
  				><xsl:value-of select="Service/Last_modified"/>
  				</xsl:attribute> 
 	  			</input>
 		<input type="submit" value="Insert" style="font-weight:bold;"></input>
 
 </fieldset>
</form>
<table>
<tr>
<td>
		<button onclick="ButtonClik('servUpdate'); "  style="font-weight:bold;" >
			<xsl:attribute name="title">Update Services</xsl:attribute>
			Update
		</button>
</td>
</tr>	
</table>					
</body>
</html>




</xsl:template>
</xsl:stylesheet>

