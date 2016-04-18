<?php
	$con=mysqli_connect("mysql8.000webhost.com","a7898048_persell","persellfyp5127","a7898048_persell");
	
	$id=$_POST["id"];
	$accUser=$_POST["accUser"];
	$bankID=$_POST["bankID"];
	$name=$_POST["name"];
	
	$statement=mysqli_prepare($con,"INSERT INTO BankAcc (accUser,bankID,UserID,name) VALUES (?,?,?,?)");
	mysqli_stmt_bind_param($statement, "siss",$accUser,$bankID,$id,$name);
	mysqli_stmt_execute($statement);

	mysqli_stmt_close($statement);

	mysqli_close($con);

?>