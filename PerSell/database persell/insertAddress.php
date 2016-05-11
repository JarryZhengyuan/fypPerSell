<?php
	$con=mysqli_connect("mysql8.000webhost.com","a7898048_persell","persellfyp5127","a7898048_persell");
	
	$id=$_POST["id"];
	$stateID=$_POST["stateID"];

	$statement=mysqli_prepare($con,"INSERT INTO Address (UserID,stateID) VALUES (?,?)");
	mysqli_stmt_bind_param($statement, "si",$id,$stateID);
	mysqli_stmt_execute($statement);

	mysqli_stmt_close($statement);

	mysqli_close($con);

?>