<!--Code by Telesto Telesto Technologies-->
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
	Query Term:&nbsp;
	<input type="text" name="fname" id="QueryTerm">
	<button onclick="search()">Search</button>
	<hr />
	<script>

		
		
		
		function search() {

			//queryTerm = document.getElementById('queryPar').value;
			var results = "";

			dname = '/SocialNetworkManager/Search/General/Filter/<%out.print(  request.getParameter("FilterName") );%>?term=' + document.getElementById("QueryTerm").value;
		
			var client;
			if (typeof window.ActiveXObject != 'undefined') {
				client = new ActiveXObject("Microsoft.XMLHTTP");

			} else {
				client = new XMLHttpRequest();

			}

			client.open("get", dname, false);
			client.send();

			if (client.status == 200) {
				xmlDoc = client.responseText;
				//alert(client.responseText);
				//document.getElementById("demo").value = xmlDoc;
				document.getElementById("response").innerHTML =''+xmlDoc;
				document.getElementById("demo").innerHTML ='<h4>URL of GET Request:</h4>'+dname+'<hr/><h4>Response (Accept: text/rdf):</h4>';
				
				//console.log(client.responseXML.xml);
			}else
			alert("false");
		}
	</script>

	<p id="demo"></p>
<textarea id="response" rows=31% cols=70%></textarea>

</body>
</html>