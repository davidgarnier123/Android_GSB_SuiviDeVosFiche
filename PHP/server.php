    <?php
    include 'function.php';
    //Fonction qui permet de se connecter à la base de donnée
    function OpenCon()
     {
     $dbhost = "localhost";
     $dbuser = "root";
     $dbpass = "";
     $db = "gsb";
     

     $conn = new PDO("mysql:host=$dbhost;dbname=$db", $dbuser, $dbpass);
      // set the PDO error mode to exception
     $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

     // Récuperation de l'username et du mot de passe envoyer lors de l'authentification
     $credentials = json_decode($_POST['Auth']);
     $username = (string)$credentials[0];
     $password = (string)$credentials[1];

     //demande d'authentification avec en parametres les données reçus de l'utilisateur
     authentification($conn, $username, $password);
  
     return $conn;
     }

     //Fonction qui lance la Synchronisation des données dans la bdd
    function Synchronisation($conn){
      echo " Fonction de synchro en cours ";

      Sync($conn);
     }

     //Fonction qui permet de fermer la connexion à la base de donnée
    function CloseCon($conn)
     {
     $conn -> close();
     }
       
    ?>