<?php
	$con=mysqli_connect("mysql8.000webhost.com","a7898048_persell","persellfyp5127","a7898048_persell");
	
	$id=$_POST["id"];
	
	$statement=mysqli_prepare($con,"SELECT * FROM Item WHERE itemID = ?");
	mysqli_stmt_bind_param($statement, "i",$id);
	mysqli_stmt_execute($statement);

	mysqli_stmt_store_result($statement);
	mysqli_stmt_bind_result($statement, $itemID,$categoryID,$itemPrice,$pic1,$pic2,$pic3,$itemDes,$statusID,$UserID,$date,$stateID,$itemTitle);

	$item=array();
	
	while(mysqli_stmt_fetch($statement)){
		
		$item[itemID]=$itemID;
		$item[categoryID]=$categoryID;
		$item[itemPrice]=$itemPrice;

		$item[pic1]=$pic1;
		$item[pic2]=$pic2;
		$item[pic3]=$pic3;

		$item[itemDes]=$itemDes;
		$item[statusID]=$statusID;
		$item[UserID]=$UserID;

		$item[date]=$date;
		$item[stateID]=$stateID;
                $item[itemTitle]=$itemTitle;
	}

	echo json_encode($item);

	mysqli_stmt_close($statement);

	mysqli_close($con);

?>