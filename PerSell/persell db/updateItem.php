<?php
	$con=mysqli_connect("mysql8.000webhost.com","a7898048_persell","persellfyp5127","a7898048_persell");
	
	$id=$_POST["id"];
	$categoryID=$_POST["categoryID"];
	$itemPrice=$_POST["itemPrice"];
	$pic1=$_POST["pic1"];
	$pic2=$_POST["pic2"];
	$pic3=$_POST["pic3"];
	$itemDes=$_POST["itemDes"];
        $stateID=$_POST["stateID"];
        $itemTitle=$_POST["itemTitle"];

	$statement=mysqli_prepare($con,"UPDATE Item SET categoryID= ?, itemPrice = ?, pic1 = ?, pic2 = ?, pic3 = ?, itemDes = ?, stateID = ?,itemTitle = ? WHERE itemID = ?");
	mysqli_stmt_bind_param($statement, "idssssisi",$categoryID,$itemPrice,$pic1,$pic2,$pic3,$itemDes,$stateID,$itemTitle,$id);
	mysqli_stmt_execute($statement);

	mysqli_stmt_close($statement);

	mysqli_close($con);

?>