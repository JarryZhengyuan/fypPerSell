<?php
	$con=mysqli_connect("mysql8.000webhost.com","a7898048_persell","persellfyp5127","a7898048_persell");
	
	$id=$_POST["id"];
	
	$statement=mysqli_prepare($con,"SELECT * FROM User WHERE UserID = ?");
	mysqli_stmt_bind_param($statement, "s",$id);
	mysqli_stmt_execute($statement);

	mysqli_stmt_store_result($statement);
	mysqli_stmt_bind_result($statement, $UserID,$phone,$email,$UserName);

	$user=array();
	
	while(mysqli_stmt_fetch($statement)){
		
		$user[UserID]=$UserID;
		$user[phone]=$phone;
		$user[email]=$email;
                $user[UserName]=$UserName;
	}

	echo json_encode($user);

	mysqli_stmt_close($statement);

	mysqli_close($con);

?>