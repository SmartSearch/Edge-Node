<!--Code by Telesto Telesto Technologies-->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="eu.smartfp7.SocialNetworkManager.SocialNetworkInterface"%>
<html>
<head>
<%@ page language="java"%>

<%@ page import="java.sql.DriverManager"%>
<%@ page import="java.sql.Connection"%>
<%@ page import="java.sql.PreparedStatement"%>
<%@ page import="java.sql.ResultSet"%>
<%@ page import="java.sql.SQLException"%>
<%@ page import="java.sql.Statement"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.Calendar"%>
<%@ page import="java.util.Date"%>

<title>Social Network Manager Configurator</title>
<meta http-equiv="X-UA-Compatible" content="IE=edge">

<link href="kendoUI/content/shared/styles/examples-offline1.css"
	rel="stylesheet">
<link href="kendoUI/styles/kendo.dataviz.min.css" rel="stylesheet">
<link href="kendoUI/styles/kendo.common.min.css" rel="stylesheet">
<link href="kendoUI/styles/kendo.silver.min.css" rel="stylesheet">

<script src="kendoUI/js/jquery.min.js"></script>
<script src="kendoUI/js/kendo.dataviz.min.js"></script>
<script src="kendoUI/js/kendo.web.min.js"></script>
<script src="kendoUI/js/console.js"></script>

<script src="source/kendo.all.min.js"></script>
<link href="kendoUI/styles/kendo.common.css" rel="stylesheet" />
<link href="kendoUI/styles/kendo.default.css" rel="stylesheet" />

<script type="text/javascript">
	
</script>
</head>
<body>
	<div id="header" class="k-content">
		<table border="0">
			<tr>
				<td><img class="source-image" src="smart-logo.png" alt="" /></td>
				<td>
					<h1>
						<font color="grey"><big>&nbsp;&nbsp;&nbsp;&nbsp;Social
								Network Manager Configurator</big></font>
					</h1>
				</td>
			</tr>
		</table>
	</div>


	<%
		Class[] driverClasses = eu.smartfp7.SocialNetworkManager.GeneralSearch
				.getClasses("eu.smartfp7.SocialNetworkDriver");
		Class[] FilterClasses = eu.smartfp7.SocialNetworkManager.GeneralSearch
				.getClasses("eu.smartfp7.SocialNetworkFilters");
		Class SNInterface = SocialNetworkInterface.class;
	%>
	<div id="content" class="k-content ">

		<ul id="treeview">

			<li data-expanded="true" id="root"><span class="k-sprite folder"></span>
				Components
				<ul>
					<li><span class="k-sprite html"></span> General Search</li>
					<li data-expanded="true"><span class="k-sprite folder"></span>
						Network Specific Search
						<ul>

							<%
								for (int i = 0; i < driverClasses.length; i++) {
							%>
							<li><span class="k-sprite html"></span> <%
 	out.println(driverClasses[i].getName().replace(
 				"eu.smartfp7.SocialNetworkDriver.", ""));
 	}
 %></li>
							<!-- <li><span class="k-sprite html"></span>humidity</li>-->



						</ul>
					<li data-expanded="true"><span class="k-sprite folder"></span>
						Driver Specific Call
						<ul>

							<%
								for (int i = 0; i < driverClasses.length; i++) {
							%>
							<li><span class="k-sprite folder"></span> <%
 	out.println(driverClasses[i].getName().replace(
 				"eu.smartfp7.SocialNetworkDriver.", ""));
 %><ul>
									<%
										for (int j = 0; j < driverClasses[i].getDeclaredMethods().length; j++) {
												boolean found = false;
												for (int k = 0; k < SNInterface.getDeclaredMethods().length; k++) {
													if (SNInterface.getDeclaredMethods()[k]
															.getName()
															.contentEquals(
																	driverClasses[i].getDeclaredMethods()[j]
																			.getName())) {
														found = true;
													}
												}
												if (found == false) {
									%>

									<li><span class="k-sprite html"></span> <%
 	out.println(driverClasses[i].getDeclaredMethods()[j]
 						.getName());
 %></li>
									<%
										}
											}
									%>
								</ul> <%
 	}
 %></li>
							<!-- <li><span class="k-sprite html"></span>humidity</li>-->



						</ul>
					<li data-expanded="true"><span class="k-sprite folder"></span>Filters
						<ul>

							<%
								for (int i = 0; i < FilterClasses.length; i++) {
							%>
							<li><span class="k-sprite html"></span> <%
 	out.println(FilterClasses[i].getName().replace(
 				"eu.smartfp7.SocialNetworkFilters.", ""));
 	}
 %> <!-- <li><span class="k-sprite html"></span>humidity</li>-->
						</ul>
				</ul>
				<ul>

				</ul>
		</ul>
		</li>

		</ul>
	</div>

	<div>
		<script>
			/*	$(document)
						.ready(*/
			var WinID = 0;
			var WinArray = new Array();
			$("#treeview")
					.kendoTreeView(
							{

								select : function onSelect(e) {
								
										//alert(this.text(e.node.parentNode.parentNode.parentNode));
										if (this.text(e.node.parentNode) == 'Network Specific Search') {
											DriversWin(
													WinArray,
													this.text(e.node),
													this
															.text(e.node.parentNode.parentNode.parentNode),
													100, 400);

										} else if (this.text(e.node.parentNode) == 'Filters') {
											FiltersWin(
													WinArray,
													this.text(e.node),
													this
															.text(e.node.parentNode.parentNode.parentNode),
													100, 400);
										} else if (this.text(e.node) == 'General Search') {
											GeneralSearchWin(
													WinArray,
													this.text(e.node),
													this
															.text(e.node.parentNode.parentNode.parentNode),
													100, 400);
										} else if (this
												.text(e.node.parentNode.parentNode.parentNode) == 'Driver Specific Call') {
											DriverCallWin(
													WinArray,
													this.text(e.node),
													this
															.text(e.node.parentNode.parentNode),
													100, 400);
										}
									}
								
							});
			//	function() {

			function DriverCallWin(WinArray, WinTitle, Parent, Top, Left) {
				WinID++;
				WinArray.push(($("<div id="+WinID+" />")
						.appendTo(document.body).kendoWindow({

					draggable : true,
					resizable : false,
					width : "650px",
					height : "600px",
					title : WinTitle,
					scrollable : false,
					close : function() {
						a = false;
						for ( var l in WinArray)

						{

							if ($("#" + WinID).data("WinID") == WinArray[l]
									.data("WinID")) {
								WinArray.splice(l, 1);

							}
						}
						$("#" + WinID).parent().remove();

					},
					open : function() {

						$("#" + WinID).data("WinID", WinID);
						$("#" + WinID).data("WinTitle", WinTitle);
						$("#" + WinID).data("Parent", Parent);
						$("#" + WinID).data("Top", Top);
						$("#" + WinID).data("Left", Left);

						$("#" + WinID).closest(".k-window").css({

							left : Left,
							top : Top

						});

						lastWinUsed = $("#" + WinID);
					},
					iframe : true,
					content : "DriverSpecificCall.jsp?DriverName=" + Parent
							+ "&MethodName=" + WinTitle,
					modal : false,
					actions : [ "Close" ],

					dragend : function onDragEnd(e) {

					}
				}

				)));

			};

			function DriversWin(WinArray, WinTitle, Parent, Top, Left) {
				WinID++;
				WinArray.push(($("<div id="+WinID+" />")
						.appendTo(document.body).kendoWindow({

					draggable : true,
					resizable : false,
					width : "650px",
					height : "600px",
					title : WinTitle,
					scrollable : false,
					close : function() {
						a = false;
						for ( var l in WinArray)

						{

							if ($("#" + WinID).data("WinID") == WinArray[l]
									.data("WinID")) {
								WinArray.splice(l, 1);

							}
						}
						$("#" + WinID).parent().remove();

					},
					open : function() {

						$("#" + WinID).data("WinID", WinID);
						$("#" + WinID).data("WinTitle", WinTitle);
						$("#" + WinID).data("Parent", Parent);
						$("#" + WinID).data("Top", Top);
						$("#" + WinID).data("Left", Left);

						$("#" + WinID).closest(".k-window").css({

							left : Left,
							top : Top

						});

						lastWinUsed = $("#" + WinID);
					},
					iframe : true,
					content : "DriverSearch.jsp?DriverName=" + WinTitle,
					modal : false,
					actions : [ "Close" ],

					dragend : function onDragEnd(e) {

					}
				}

				)));

			};

			function FiltersWin(WinArray, WinTitle, Parent, Top, Left) {
				WinID++;
				WinArray.push(($("<div id="+WinID+" />")
						.appendTo(document.body).kendoWindow({
					draggable : true,
					resizable : false,
					width : "650px",
					height : "600px",
					title : WinTitle,
					scrollable : false,
					close : function() {

						c = false;
						for ( var l in WinArray)

						{

							if ($("#" + WinID).data("WinID") == WinArray[l]
									.data("WinID")) {
								WinArray.splice(l, 1);

							}
						}

						$("#" + WinID).parent().remove();

					},
					open : function() {

						$("#" + WinID).data("WinID", WinID);
						$("#" + WinID).data("WinTitle", WinTitle);
						$("#" + WinID).data("Parent", Parent);
						$("#" + WinID).data("Top", Top);
						$("#" + WinID).data("Left", Left);

						$("#" + WinID).closest(".k-window").css({

							left : Left,
							top : Top

						});
					},
					iframe : true,
					content : "FilterSearch.jsp?FilterName=" + WinTitle,
					modal : false,
					actions : [ "Close" ]
				}

				)));
			};

			function GeneralSearchWin(WinArray, WinTitle, Parent, Top, Left) {
				WinID++;
				WinArray.push(($("<div id="+WinID+" />")
						.appendTo(document.body).kendoWindow({
					draggable : true,
					resizable : false,
					width : "650px",
					height : "600px",
					title : WinTitle,
					scrollable : false,
					close : function() {

						for ( var l in WinArray)

						{

							if ($("#" + WinID).data("WinID") == WinArray[l]
									.data("WinID")) {
								WinArray.splice(l, 1);

							}
						}

						$("#" + WinID).parent().remove();

					},
					open : function() {

						$("#" + WinID).data("WinID", WinID);
						$("#" + WinID).data("WinTitle", WinTitle);
						$("#" + WinID).data("Parent", Parent);
						$("#" + WinID).data("Top", Top);
						$("#" + WinID).data("Left", Left);

						$("#" + WinID).closest(".k-window").css({

							left : Left,
							top : Top

						});
					},
					iframe : true,
					content : "GeneralSearch.jsp",
					modal : false,
					actions : [ "Close" ]
				}

				)));
			};
		</script>
		<style scoped>
.configuration .k-textbox {
	width: 40px;
}

.menu1 .k-menu .k-item {
	
}

#MainMenu {
	align: center /*background: url('social-network.jpg') no-repeat 0 0;*/
}
</style>
		</d
							iv>
		<style scoped>
.chart-wrapper {
	margin: 0 0 0 20px;
	width: 1000px;
	height: 434px;
	/*background: url("kendoUI/content/shared/styles/chart-wrapper-small.png")
		transparent no-repeat 0 0;*/
}

.chart-wrapper .k-chart {
	height: 350px;
	padding: 37px;
	width: 650px;
}

#example .k-datepicker {
	vertical-align: middle;
}

#example h3 {
	clear: both;
}

#example .code-sample {
	width: 60%;
	float: left;
	margin-bottom: 20px;
}

#example .output {
	width: 24%;
	margin-left: 4%;
	float: left;
}

#menu1 .k-menu { /*    float:none;*/
	width: 90%;
}

.k-button { /*    float:none;*/
	width: 200px;
}

#img.source-image {
	width: 100%;
	position: absolute;
	top: 0;
	left: 0;
}

.demo-section {
	width: 200px;
}

#treeview .k-sprite {
	background-image:
		url("kendoUI/content/web/treeview/coloricons-sprite.png");
}

.rootfolder {
	background-position: 0 0;
}

.folder {
	background-position: 0 -16px;
}

.pdf {
	background-position: 0 -32px;
}

.html {
	background-position: 0 -48px;
}

.image {
	background-position: 0 -64px;
}

#vertical {
	height: 380px;
	width: 700px;
	margin: 0 auto;
}

#content {
	width: 250px;
}

#middle-pane {
	background-color: rgba(60, 70, 80, 0.10);
}

#bottom-pane {
	background-color: rgba(60, 70, 80, 0.15);
}

#left-pane {
	background-color: rgba(60, 70, 80, 0.05);
}

#center-pane {
	background-color: rgba(60, 70, 80, 0.05);
}

#right-pane {
	background-color: rgba(60, 70, 80, 0.05);
}

.pane-content {
	padding: 0 10px;
}
</style>
	</div>

</body>
</html>
