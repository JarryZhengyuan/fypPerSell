<?php
	$con=mysqli_connect("mysql8.000webhost.com","a7898048_persell","persellfyp5127","a7898048_persell");
	
	$search=$_POST["search"];

	$sql="SELECT Item.*,User.UserName FROM Item JOIN User ON Item.UserID = User.UserID  ".
                "WHERE (User.UserName LIKE '%$search%' OR Item.itemTitle LIKE '%$search%' OR ".
                "Item.categoryID=( SELECT categoryID FROM Category WHERE categoryDes LIKE '%$search%'))".
                " AND Item.statusID=1 ORDER BY date DESC"; 

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
                'itemTitle'=>$row[11],
                'UserName'=>$row[12]
  		));
	}

	echo json_encode(array("result"=>$result));

	mysqli_close($con);


?>