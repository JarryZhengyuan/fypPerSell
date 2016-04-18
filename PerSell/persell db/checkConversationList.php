<?php
	$con=mysqli_connect("mysql8.000webhost.com","a7898048_persell","persellfyp5127","a7898048_persell");
	
	$user=$_POST["user"];

	$sql = "SELECT u.UserID,u.UserName,c.roomID 
 		FROM User u,RoomChat c
 		WHERE CASE 
 		WHEN c.user_one = '$user'
 		THEN c.user_two = u.UserID
 		WHEN c.user_two = '$user'
 		THEN c.user_one= u.UserID
 		END 
 		AND (
 		c.user_one ='$user'
 		OR c.user_two ='$user'
 		)
 		ORDER BY c.date DESC";

	$res = mysqli_query($con,$sql);

	$result = array();

	while($row = mysqli_fetch_array($res)){
    		array_push($result,
    		array('UserID'=>$row[0],
    		'UserName'=>$row[1],
		'roomID'=>$row[2]
  		));
	}

	echo json_encode(array("result"=>$result));

	mysqli_close($con);
	
?>