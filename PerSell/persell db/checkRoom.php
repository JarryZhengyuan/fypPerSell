<?php
	$con=mysqli_connect("mysql8.000webhost.com","a7898048_persell","persellfyp5127","a7898048_persell");
	
	$user_one=$_POST["user_one"];
	$user_two=$_POST["user_two"];

        if($user_one!=null && $user_two!=null){

	$sql = "SELECT * FROM RoomChat WHERE (user_one='$user_one' AND user_two='$user_two') OR (user_one='$user_two' AND user_two='$user_one')";
  	$res = mysqli_query($con,$sql);

	if(mysqli_num_rows($res)==0) {
		$insert="INSERT INTO RoomChat (user_one,user_two) VALUES ('$user_one','$user_two')";
		$query = mysqli_query($con,$insert) or die(mysql_error());
	}
    
	$res = mysqli_query($con,$sql);

        $result = array();

	while($row = mysqli_fetch_array($res)){
    		array_push($result,
    		array('roomID'=>$row[0],
    		'user_one'=>$row[1],
		'user_two'=>$row[2],
    		'date'=>$row[3]
  		));
	}

	echo json_encode(array("result"=>$result));}

	mysqli_close($con);
	
?>