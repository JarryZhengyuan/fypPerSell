<?php
	$con=mysqli_connect("mysql8.000webhost.com","a7898048_persell","persellfyp5127","a7898048_persell");
	
	$content=$_POST["content"];
	$date=$_POST["date"];
	$itemID=$_POST["itemID"];
	$UserID=$_POST["UserID"];

	$statement=mysqli_prepare($con,"INSERT INTO Comment (content,date,itemID,UserID) VALUES (?,?,?,?)");
	mysqli_stmt_bind_param($statement, "ssis",$content,$date,$itemID,$UserID);
	mysqli_stmt_execute($statement);

	mysqli_stmt_close($statement);

	mysqli_close($con);

?>