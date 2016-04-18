<?php
	$con=mysqli_connect("mysql8.000webhost.com","a7898048_persell","persellfyp5127","a7898048_persell");
	
	$id=$_POST["id"];
        $UserName=$_POST["UserName"];
	
	$statement=mysqli_prepare($con,"INSERT INTO User (UserID,UserName) VALUES (?,?)");
	mysqli_stmt_bind_param($statement, "ss",$id,$UserName);
	mysqli_stmt_execute($statement);

	mysqli_stmt_close($statement);

	mysqli_close($con);

?>