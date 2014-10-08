<!--Code by Telesto Telesto Technologies-->
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<%
	Class[] driverClasses = eu.smartfp7.SocialNetworkManager.GeneralSearch
			.getClasses("eu.smartfp7.SocialNetworkDriver");
	Class selectedDriver = null;
	System.out.println(request.getParameter("DriverName"));
	for (int i = 0; i < driverClasses.length; i++) {
		System.out.println(driverClasses[i].getName());
		if (driverClasses[i].getName()
				.replace("eu.smartfp7.SocialNetworkDriver.", "")
				.contentEquals(request.getParameter("DriverName"))) {
			selectedDriver = driverClasses[i];
			System.out.println("true");
		}
	}

	java.lang.reflect.Method method = null;
	for (int i = 0; i < selectedDriver.getDeclaredMethods().length; i++) {
		if (selectedDriver.getDeclaredMethods()[i].getName()
				.contentEquals(request.getParameter("MethodName"))) {
			method = selectedDriver.getDeclaredMethods()[i];

		}
	}
%>
<body>

<h4>URL of PUT Request: </h4>SocialNetworkManager/Search/General/NonStandard/DriverMethod<br/>
			
	<p id="demo"></p>
	<h4>Request Body (Content-Type: text/xml) - XML Descritpion of Driver Specific Call:</h4>

	<textarea id="XMLDescription" rows=9% cols=70%></textarea>
	<button onclick="search()">Test Driver Specific Call</button>
	<hr />
	<h4>Response (Accept: text/xml):</h4>
	<textarea id="Response" rows=9% cols=70%></textarea>
	<script>

		
		
		function createXMLDescription() {

				var parameterNumber=<%out.print(method.getParameterTypes().length);%>;
				document.getElementById("demo").innerHTML =" <h4>Argument Values:</h4><%for (int i = 0; i < method.getParameterTypes().length; i++)
				out.print("ArgumentValue" + i + " ("
						+ method.getParameterTypes()[i].getName()
						+ "):&nbsp;<input type='text' id='ArgValue" + i
						+ "' onKeyUp='updateXMLDescription()'><br>");%><hr/>";
				XMLDescription='<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<driverSpecificCall>\n<ClassName>'
					+ <%out.print("'" + request.getParameter("DriverName") + "'");%>
					+ '</ClassName><MethodName><%out.print(method.getName());%></MethodName>'
					+'<%for (int i = 0; i < method.getParameterTypes().length; i++)
				out.print("<ArgTypes>"
						+ method.getParameterTypes()[i].getName()
						+ "</ArgTypes>");%>\n';
					
					
					argValues="";
					for(var i=0;i<parameterNumber;i++){
						argValues+='<ArgValues>'+document.getElementById("ArgValue"+i).value+'</ArgValues>\n';
					};
					
				
				document.getElementById("XMLDescription").innerHTML ='<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<driverSpecificCall>\n<ClassName>'
						+ <%out.print("'" + request.getParameter("DriverName") + "'");%>
						+ '</ClassName><MethodName><%out.print(method.getName());%></MethodName>'
						+'<%for (int i = 0; i < method.getParameterTypes().length; i++)
				out.print("<ArgTypes>"
						+ method.getParameterTypes()[i].getName()
						+ "</ArgTypes>");%>\n'
					+ argValues + '</driverSpecificCall>';
		}
		
		function updateXMLDescription() {

			var parameterNumber=<%out.print(method.getParameterTypes().length);%>;
			XMLDescription='<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<driverSpecificCall>\n<ClassName>'
				+ <%out.print("'" + request.getParameter("DriverName") + "'");%>
				+ '</ClassName><MethodName><%out.print(method.getName());%></MethodName>'
				+'<%for (int i = 0; i < method.getParameterTypes().length; i++)
				out.print("<ArgTypes>"
						+ method.getParameterTypes()[i].getName()
						+ "</ArgTypes>");%>\n';
				
				
				argValues="";
				for(var i=0;i<parameterNumber;i++){
					argValues+='<ArgValues>'+document.getElementById("ArgValue"+i).value+'</ArgValues>\n';
				};
				
			
			document.getElementById("XMLDescription").innerHTML ='<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<driverSpecificCall>\n<ClassName>'
					+ <%out.print("'" + request.getParameter("DriverName") + "'");%>
					+ '</ClassName><MethodName><%out.print(method.getName());%></MethodName>'
					+'<%for (int i = 0; i < method.getParameterTypes().length; i++)
				out.print("<ArgTypes>"
						+ method.getParameterTypes()[i].getName()
						+ "</ArgTypes>");%>'
		
					+'\n' + argValues + '</driverSpecificCall>';
		}

		function search() {

			//queryTerm = document.getElementById('queryPar').value;
			var results = "";

			
			var xmlHttp; //returns a XMLHttpRequest object  
	
			if (typeof window.ActiveXObject != 'undefined') {
				xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");

			} else {
				xmlHttp = new XMLHttpRequest();

			}

			url='Search/General/NonStandard/DriverMethod';

			var mimeType = "text/xml";
			xmlHttp.open('PUT', url, false); // true : asynchrone false: synchrone  
			xmlHttp.setRequestHeader('Content-Type', mimeType);

			xmlHttp.send(document.getElementById("XMLDescription").value);


			if (xmlHttp.status == 200) {
				xmlDoc = xmlHttp.responseText;
				//alert(client.responseText);
				//document.getElementById("demo").value = xmlDoc;
				document.getElementById("Response").value = '' + xmlDoc;
				//console.log(client.responseXML.xml);
			} else
				alert("false");
		}

		createXMLDescription();
	</script>


</body>
</html>