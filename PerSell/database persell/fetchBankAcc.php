<?php
	$con=mysqli_connect("mysql8.000webhost.com","a7898048_persell","persellfyp5127","a7898048_persell");
	

	$id=$_POST["id"];

	$sql = "SELECT * FROM BankAcc WHERE UserID='$id'";

  	$res = mysqli_query($con,$sql);

	$result = array();

  	while($row = mysqli_fetch_array($res)){

    		array_push($result,
    		array('accUser'=>$row[0],
    		'bankID'=>$row[1],
		'UserID'=>$row[2],
		'name'=>$row[3]
  		));
	}

	echo json_encode(array("result"=>$result));

	mysqli_close($con);
?>