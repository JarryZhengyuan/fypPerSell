<?php

        $con=mysqli_connect("mysql8.000webhost.com","a7898048_persell","persellfyp5127","a7898048_persell");
	
	$image=$_POST["image"];
        $payPic=$_POST["payPic"];
	$itemID=$_POST["itemID"];
        $date=$_POST["date"];
	
	$decodedImage=base64_decode("$image");
	file_put_contents("invoice/".$payPic.".JPG",$decodedImage);

        $updateItem="UPDATE Item SET statusID=0  WHERE itemID='$itemID'";
	$queryItem = mysqli_query($con,$updateItem) or die(mysql_error());

        $updateIn="UPDATE Invoice SET status=1, payPic='$payPic', date='$date' WHERE itemID='$itemID'";
	$queryIn = mysqli_query($con,$updateIn) or die(mysql_error());


        mysqli_close($con);
?>