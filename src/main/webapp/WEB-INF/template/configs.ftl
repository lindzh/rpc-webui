<!DOCTYPE html>
<html>
<head>
   <title>rpc admin</title>
   <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
   <script src="/jquery/jquery-2.1.3.min.js"></script>
   <link rel="stylesheet" href="/bootstrap/css/bootstrap.min.css"/>
   <script src="/bootstrap/js/bootstrap.min.js"></script>
</head>
<body>
	<div class="container">
		<#assign page='configs'/>
		<#include 'navbar.ftl'/>

		<div class="panel panel-primary">
		    <div class="panel-body">
		    	<table class="table">
					<thead>
						<tr>
							<th>md5</th>
							<th>namespace</th>
							<th>protocol</th>
							<th>info</th>
						</tr>
					</thead>
					<tbody>
					<#list configs as config>
						<tr>
							<td>${config.md5}</td>
							<td>${config.namespace}</td>
							<td>${config.protocol}</td>
							<td>${config.info}</td>
					</#list>
					</tbody>
				</table>
			</div>
		</div>
	</div>
</body>
</html>