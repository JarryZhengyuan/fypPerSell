<?php
	$con=mysqli_connect("mysql8.000webhost.com","a7898048_persell","persellfyp5127","a7898048_persell");
	
	$poskod=$_POST["poskod"];
	$city=$_POST["city"];
	$stateID=$_POST["stateID"];
	$address=$_POST["address"];
	$itemID=$_POST["itemID"];
	$date=$_POST["date"];
	$UserID=$_POST["UserID"];


        $sql = "SELECT * FROM TempAdd WHERE itemID='$itemID'";
  	$res = mysqli_query($con,$sql);

        if(mysqli_num_rows($res)==0) {
	$insertAdd="INSERT INTO TempAdd (poskod,city,stateID,address,itemID) VALUES ('$poskod','$city','$stateID','$address','$itemID')";
	$queryAdd=mysqli_query($con,$insertAdd);
        }
        else{
        $updateAdd="UPDATE TempAdd SET poskod='$poskod', city='$city', stateID='$stateID', address='$address' WHERE itemID='$itemID'";
	$queryUAdd = mysqli_query($con,$updateAdd) or die(mysql_error());
        }
        

        $sql = "SELECT * FROM Invoice WHERE itemID='$itemID'";
  	$res = mysqli_query($con,$sql);

        if(mysqli_num_rows($res)==0) {
	$insertIn="INSERT INTO Invoice (date,itemID,status,UserID) VALUES ('$date','$itemID',2,'$UserID')";
        $queryIn=mysqli_query($con,$insertIn);    
        }
        else{
        $updateIn="UPDATE Invoice SET date='$date', status=2, UserID='$UserID' WHERE itemID='$itemID'";
	$queryUIn = mysqli_query($con,$updateIn) or die(mysql_error());
        }

    
	$updateItem="UPDATE Item SET statusID=2  WHERE itemID='$itemID'";
	$queryItem = mysqli_query($con,$updateItem) or die(mysql_error());

	mysqli_close($con);
	
?>