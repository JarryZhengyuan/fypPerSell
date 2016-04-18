<?php
	$con=mysqli_connect("mysql8.000webhost.com","a7898048_persell","persellfyp5127","a7898048_persell");
	
	$roomID=$_POST["roomID"];

	$sql = "SELECT * FROM Message WHERE roomID='$roomID' ORDER BY date";
  	$res = mysqli_query($con,$sql);

        $result = array();

	while($row = mysqli_fetch_array($res)){
    		array_push($result,
    		array('messageID'=>$row[0],
    		'roomID'=>$row[1],
		'date'=>$row[2],
    		'message'=>$row[3],
		'UserID'=>$row[4]
  		));
	}

	echo json_encode(array("result"=>$result));

	mysqli_close($con);
	
?>