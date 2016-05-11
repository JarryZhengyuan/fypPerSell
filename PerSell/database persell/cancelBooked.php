<?php
	$con=mysqli_connect("mysql8.000webhost.com","a7898048_persell","persellfyp5127","a7898048_persell");
	
	$id=$_POST["id"];

	$statement=mysqli_prepare($con,"UPDATE Item SET statusID = 1 WHERE itemID = ?");
	mysqli_stmt_bind_param($statement, "i",$id);
	mysqli_stmt_execute($statement);
	
	$statement=mysqli_prepare($con,"DELETE FROM TempAdd WHERE itemID = ?");
	mysqli_stmt_bind_param($statement, "i",$id);
	mysqli_stmt_execute($statement);

	$statement=mysqli_prepare($con,"DELETE FROM Invoice WHERE itemID = ?");
	mysqli_stmt_bind_param($statement, "i",$id);
	mysqli_stmt_execute($statement);

	mysqli_stmt_close($statement);

	mysqli_close($con);

?>