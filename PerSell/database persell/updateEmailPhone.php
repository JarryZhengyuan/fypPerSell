<?php
	$con=mysqli_connect("mysql8.000webhost.com","a7898048_persell","persellfyp5127","a7898048_persell");
	
	$id=$_POST["id"];
	$email=$_POST["email"];
	$phone=$_POST["phone"];
	
	$statement=mysqli_prepare($con,"UPDATE User SET email=?, phone = ? WHERE UserID = ?");
	mysqli_stmt_bind_param($statement, "sss",$email,$phone,$id);
	mysqli_stmt_execute($statement);

	mysqli_stmt_close($statement);

	mysqli_close($con);

?>