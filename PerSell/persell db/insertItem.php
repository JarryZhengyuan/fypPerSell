<?php
	$con=mysqli_connect("mysql8.000webhost.com","a7898048_persell","persellfyp5127","a7898048_persell");
	
	$id=$_POST["id"];
	$categoryID=$_POST["categoryID"];
	$itemPrice=$_POST["itemPrice"];
	$pic1=$_POST["pic1"];
	$pic2=$_POST["pic2"];
	$pic3=$_POST["pic3"];
	$itemDes=$_POST["itemDes"];
	$statusID=$_POST["statusID"];
        $date=$_POST["date"];
        $stateID=$_POST["stateID"];
        $itemTitle=$_POST["itemTitle"];

	
	$statement=mysqli_prepare($con,"INSERT INTO  Item (categoryID,itemPrice,pic1,pic2,pic3,itemDes,statusID,UserID,date,stateID,itemTitle) VALUES (?,?,?,?,?,?,?,?,?,?,?)");
	mysqli_stmt_bind_param($statement, "idssssissis",$categoryID,$itemPrice,$pic1,$pic2,$pic3,$itemDes,$statusID,$id,$date,$stateID,$itemTitle);
	mysqli_stmt_execute($statement);

	mysqli_stmt_close($statement);

	mysqli_close($con);

?>