<?php
	$con=mysqli_connect("mysql8.000webhost.com","a7898048_persell","persellfyp5127","a7898048_persell");
	
	$id=$_POST["id"];
	$poskod=$_POST["poskod"];
	$city=$_POST["city"];
	$stateID=$_POST["stateID"];
	$address=$_POST["address"];
	
	$statement=mysqli_prepare($con,"UPDATE Address SET poskod=?, city = ?, stateID = ?, address = ? WHERE UserID = ?");
	mysqli_stmt_bind_param($statement, "ssiss",$poskod,$city,$stateID,$address,$id);
	mysqli_stmt_execute($statement);

	mysqli_stmt_close($statement);

	mysqli_close($con);

?>