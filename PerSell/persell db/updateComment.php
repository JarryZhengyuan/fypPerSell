<?php
	$con=mysqli_connect("mysql8.000webhost.com","a7898048_persell","persellfyp5127","a7898048_persell");
	
	$content=$_POST["content"];
	$commentID=$_POST["commentID"];

	$statement=mysqli_prepare($con,"UPDATE Comment SET content= ? WHERE commentID = ?");
	mysqli_stmt_bind_param($statement, "si",$content,$commentID);
	mysqli_stmt_execute($statement);

	mysqli_stmt_close($statement);

	mysqli_close($con);

?>