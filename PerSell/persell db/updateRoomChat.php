<?php
	$con=mysqli_connect("mysql8.000webhost.com","a7898048_persell","persellfyp5127","a7898048_persell");
	
	$roomID=$_POST["roomID"];
	$date=$_POST["date"];


	$update="UPDATE RoomChat SET date='$date' WHERE roomID = '$roomID'";
		$query = mysqli_query($con,$update) or die(mysql_error());

	mysqli_close($con);
?>