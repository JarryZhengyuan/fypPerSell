<?php
	$con=mysqli_connect("mysql8.000webhost.com","a7898048_persell","persellfyp5127","a7898048_persell");
	
	$id=$_POST["id"];

	$statement=mysqli_prepare($con,"SELECT TempAdd.*,Invoice.date,Invoice.payPic,Invoice.status,Invoice.UserID FROM TempAdd JOIN Invoice ON TempAdd.itemID=Invoice.itemID WHERE Invoice.itemID=?");
	mysqli_stmt_bind_param($statement, "i",$id);
	mysqli_stmt_execute($statement);

	mysqli_stmt_store_result($statement);
	mysqli_stmt_bind_result($statement, $poskod,$city,$stateID,$address,$itemID,$date,$payPic,$status,$UserID);

	$result=array();
	
	while(mysqli_stmt_fetch($statement)){
		
		$result[poskod]=$poskod;
		$result[city]=$city;
		$result[stateID]=$stateID;
		$result[address]=$address;
		$result[itemID]=$itemID;
                $result[date]=$date;
		$result[payPic]=$payPic;
		$result[status]=$status;
		$result[UserID]=$UserID;
	}

	echo json_encode($result);

	mysqli_stmt_close($statement);
	mysqli_close($con);
?>