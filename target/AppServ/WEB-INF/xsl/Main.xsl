<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	
<xsl:template match="/data">
<html>
<head>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8"></META>
<title>Applications and Services</title>
<script type="text/javascript">
function window_onload()
{
	parent.ApplicationForm.location.assign("Show?REQUEST=AppForm");
	parent.ServiceForm.location.assign("Show?REQUEST=ServForm");
	parent.SolrAppForm.location.assign("Show?REQUEST=AppSolrForm");
	parent.SolrServForm.location.assign("Show?REQUEST=ServSolrForm");
	return;	
}
</script>
</head>
		<frameset onload="window_onload()" cols="25%,*,25%">
		  <frame id="SolrAppForm" src="" name="SolrAppForm" />
			<frameset rows="50%,50%">
			  <frame id="ApplicationForm" src="" name="ApplicationForm"/>
			  <frame id="ServiceForm" src="" name="ServiceForm"/>
			</frameset>
		  <frame id="SolrServForm" src="" name="SolrServForm"/>
		</frameset>
</html>

</xsl:template>

</xsl:stylesheet>
