<?php
	$con=mysqli_connect("mysql8.000webhost.com","a7898048_persell","persellfyp5127","a7898048_persell");
	

	$itemID=$_POST["itemID"];

	$sql = "SELECT Comment.*, User.UserName FROM Comment JOIN User ON Comment.UserID = User.UserID WHERE Comment.itemID='$itemID' ORDER BY date";

  	$res = mysqli_query($con,$sql);

	$result = array();

  	while($row = mysqli_fetch_array($res)){
    		array_push($result,
    		array('commentID'=>$row[0],
    		'content'=>$row[1],
		'date'=>$row[2],
    		'itemID'=>$row[3],
		'UserID'=>$row[4],
		'UserName'=>$row[5]
  		));
	}

	echo json_encode(array("result"=>$result));

	mysqli_close($con);
?>