<?php
	$con=mysqli_connect("mysql8.000webhost.com","a7898048_persell","persellfyp5127","a7898048_persell");
	
	$id=$_POST["id"];
	$statusID=$_POST["statusID"];

	$sql = "SELECT * FROM Item WHERE UserID='$id' AND statusID='$statusID' ORDER BY date DESC";

  	$res = mysqli_query($con,$sql);

	$result = array();

  	while($row = mysqli_fetch_array($res)){
    		array_push($result,
    		array('itemID'=>$row[0],
    		'categoryID'=>$row[1],
		'itemPrice'=>$row[2],
    		'pic1'=>$row[3],
		'pic2'=>$row[4],
    		'pic3'=>$row[5],
		'itemDes'=>$row[6],
    		'statusID'=>$row[7],
		'UserID'=>$row[8],
		'date'=>$row[9],
		'stateID'=>$row[10],
                'itemTitle'=>$row[11]
  		));
	}

	echo json_encode(array("result"=>$result));

	mysqli_close($con);
?>