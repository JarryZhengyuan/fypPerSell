<?php
	$con=mysqli_connect("mysql8.000webhost.com","a7898048_persell","persellfyp5127","a7898048_persell");
	
	$id=$_POST["id"];
	
	$statement=mysqli_prepare($con,"SELECT * FROM Address WHERE UserID = ?");
	mysqli_stmt_bind_param($statement, "s",$id);
	mysqli_stmt_execute($statement);

	mysqli_stmt_store_result($statement);
	mysqli_stmt_bind_result($statement, $poskod,$city,$stateID,$address,$UserID);

	$Add=array();
	
	while(mysqli_stmt_fetch($statement)){
		
		$Add[poskod]=$poskod;
		$Add[city]=$city;
		$Add[stateID]=$stateID;
		$Add[address]=$address;
		$Add[UserID]=$UserID;
	}

	echo json_encode($Add);

	mysqli_stmt_close($statement);

	mysqli_close($con);

?>