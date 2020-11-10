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
				var url = "Show?REQUEST=AppUpdateForm&amp;Button=" + button;
				parent.ApplicationForm.location=url;
			}
			
			function OnClick(){
				document.getElementById("AppUpdateForm").submit();
			}

 		</script>
	
	</head>
<body>
<form action="Show?REQUEST=AppInsertForm" id="AppUpdateForm" name="AppUpdateForm" method="post">
	  <fieldset>
  	  	<legend>Application</legend>

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
  		<label for="Description" style="align-items: center;" >Description:</label>
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
 	  			
 		<button type="submit" form="AppInsertForm" value="Submit" onClick="OnClick()" style="font-weight:bold;" >Insert</button>

	 </fieldset>
</form>
<table>
<tr>
<td>
		<button onclick="ButtonClik('AppUpdate'); "  style="font-weight:bold;" >
			<xsl:attribute name="title">Update Application</xsl:attribute>
			Update
		</button>
</td>
</tr>	
</table>					
</body>
</html>




</xsl:template>
</xsl:stylesheet>

