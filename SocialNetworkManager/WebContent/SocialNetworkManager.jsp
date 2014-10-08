<!DOCTYPE html>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="eu.smartfp7.SocialNetworkManager.GeneralSearch"%>
<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=ISO-8859-1">
<title>Basic usage</title>

<script src="kendoUI/js/jquery.min.js"></script>
<script src="kendoUI/js/kendo.web.min.js"></script>
<script src="kendoUI/js/console.js"></script>
<link href="kendoUI/styles/kendo.common.min.css" rel="stylesheet" />
<link href="kendoUI/styles/kendo.silver.min.css" rel="stylesheet" />
</head>
<body>
	<% Class[] driverClasses=eu.smartfp7.SocialNetworkManager.GeneralSearch.getClasses("SocialNetworkDriver");%>
	<div id="example" class="k-content">
		<div id="window">


			<div id="MainMenu">
				<div align="center">
					<img src="socialnetworksg2.png" />
				</div>

				<ul class="k-widget k-reset k-header k-menu k-menu-horizontal"
					data-role="menu" id="menu">
					<!--  <li style="z-index: auto;" class="k-item k-state-default k-first"><span
						class="k-link"> Social Network Manager Configuration:&nbsp;
							<span class="k-icon k-arrow-down"></span>
					</span></li>-->
					<li style="z-index: auto;" class="k-item k-state-default k-first"><span
						class="k-link"><img src="./icons/search32.png" /> Rest API
							calls <span class="k-icon k-arrow-down"></span></span>
						<div
							style="width: 99px; height: 130px; margin-left: -2px; padding-left: 2px; padding-right: 2px; padding-bottom: 4px; overflow: hidden; display: none; position: absolute; z-index: 10002; top: 36px; left: 0px;"
							class="k-animation-container">
							<ul
								style="display: none; position: absolute; font-family: serif; font-size: 16px; font-stretch: normal; font-style: normal; font-weight: 400; line-height: 20px; -moz-transition: none 0s ease 0s; -moz-transform: translateY(-130px);"
								data-role="popup" class="k-group k-popup k-reset">
								<li class="k-item k-state-default k-first"><span
									class="k-link"> General Search <span
										class="k-icon k-arrow-next"></span></span></li>
								<li class="k-item k-state-default"><span class="k-link">
										Network Specific Search <span class="k-icon k-arrow-next"></span>
								</span>
									<ul class="k-group">

										<%for(int i=0;i<driverClasses.length;i++){%>
										<li class="k-item k-state-default"><span class="k-link">
												<%out.println(driverClasses[i].getName().replace("SocialNetworkDriver.",""));}%>
										</span></li>
									</ul></li>
								<li class="k-item k-state-default k-first"><span
									class="k-link"> Driver Specific Call <span
										class="k-icon k-arrow-next"></span></span></li>
							</ul>
						</div></li>
					<li style="z-index: auto;" class="k-item k-state-default"><span
						class="k-link"><img src="./icons/gear32.png" /> Attached
							Drivers <span class="k-icon k-arrow-down"></span></span>
						<div
							style="width: 99px; height: 130px; margin-left: -2px; padding-left: 2px; padding-right: 2px; padding-bottom: 4px; overflow: hidden; display: none; position: absolute; z-index: 10002; top: 36px; left: 0px;"
							class="k-animation-container">
							<ul
								style="display: none; position: absolute; font-family: serif; font-size: 16px; font-stretch: normal; font-style: normal; font-weight: 400; line-height: 20px; -moz-transition: none 0s ease 0s; -moz-transform: translateY(-130px);"
								data-role="popup" class="k-group k-popup k-reset">
								<%for(int i=0;i<driverClasses.length;i++){%>
								<li class="k-item k-state-default"><span class="k-link">
										<%out.println(driverClasses[i].getName().replace("SocialNetworkDriver.",""));}%>
								</span></li>
							</ul>
						</div></li>
					<li style="z-index: auto;" class="k-item k-state-default"><span
						class="k-link"><img src="./icons/star32.png" /> Attached
							Filters <span class="k-icon k-arrow-down"></span></span>
						<div
							style="width: 99px; height: 130px; margin-left: -2px; padding-left: 2px; padding-right: 2px; padding-bottom: 4px; overflow: hidden; display: none; position: absolute; z-index: 10002; top: 36px; left: 0px;"
							class="k-animation-container">
							<ul
								style="display: none; position: absolute; font-family: serif; font-size: 16px; font-stretch: normal; font-style: normal; font-weight: 400; line-height: 20px; -moz-transition: none 0s ease 0s; -moz-transform: translateY(-130px);"
								data-role="popup" class="k-group k-popup k-reset">
								<% Class[] FilterClasses=GeneralSearch.getClasses("SocialNetworkFilters");%>
								<%for(int i=0;i<FilterClasses.length;i++){%>
								<li class="k-item k-state-default"><span class="k-link">
										<%out.println(FilterClasses[i].getName().replace("SocialNetworkFilters.",""));}%>
								</span></li>
							</ul>

						</div></li>
				</ul>
			</div>
<div id="cap-view" class="k-header">sdfsfsd</div>
		</div>
		

		<style scoped="">
#MainMenu {
	width: 600px;
	margin: 30px auto; //
	padding-top: 120px;
	/*background: url('social-network.jpg') no-repeat 0 0;*/
}

#menu h2 {
	font-size: 1em;
	text-transform: uppercase;
	padding: 5px 10px;
}

.forms li {
	margin-bottom: 5px;
	list-style: none;
}

.forms li>* {
	width: 450px;
}

#template img {
	margin: 5px 20px 0 0;
	float: left;
}

#template {
	width: 380px;
}

#template ol {
	float: left;
	margin: 0;
	padding: 10px 10px 0 10px;
}

#template:after {
	content: ".";
	display: block;
	height: 0;
	clear: both;
	visibility: hidden;
}

#template .k-button {
	float: left;
	clear: left;
	margin: 5px 0 5px 12px;
}

#ControlPanel {
	border-radius: 10px 10px 10px 10px;
	border-style: solid;
	border-width: 1px;
	overflow: hidden;
	width: 500px;
	margin: 30px auto;
	padding: 20px 20px 0 20px;
	background-position: 0 -255px;
}
</style>
		<script>
			$(document).ready(function() {
				$("#menu").kendoMenu();
			});

			$(document).ready(
					function() {
						var window = $("#window"), undo = $("#undo").bind(
								"click",
								function() {
									var kendowin = window.data("kendoWindow")
											.open();
									undo.hide();

								});

						var onClose = function() {
							undo.show();

						}

						if (!window.data("kendoWindow")) {
							window.kendoWindow({
								width : "600px",
								title : "Social Network Manager Configuration",
								//close : onClose,
								actions : {},
								resizable : false,
								draggable : false

							});
						}

						$("#window").data("kendoWindow").center();
						$("#window").data("kendoWindow").maximize();
						$("#window").data("kendoWindow").resizeable(false);
					});
		</script>
	</div>
</body>
</html>