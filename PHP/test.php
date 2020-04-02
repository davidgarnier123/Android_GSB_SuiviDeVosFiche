    <?php
    include 'server.php';
   
    $conn = null;
    //Tentative de connexion de l'utilisateur
if (isset($_POST['Auth'])) {
     echo ' POST bien reçu ';
      // Connexion à la base de donnée
      $conn = OpenCon();
    echo " Connected Successfully to Database ";
    
 } if (isset($_POST['Sync'])){
 	$conn = OpenCon();
 	//Tentative de synchronisation des données locales vers la bdd
 	Synchronisation($conn);

 } 
 else {
 	echo "Une erreur est survenue";

 }
    //Fermeture de la connexion établie avec la base de donnée
if(isset($conn)){
	CloseCon($conn);
}

    ?>
