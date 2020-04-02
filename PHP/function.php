<?php


function authentification($conn, $username, $password){
	
	//DEBUG
	//echo 'Tentative de connexion de : ' . $username . ' avec le mot de passe : ' . $password;

		$sql = "SELECT * FROM user WHERE name = '$username' AND password = '$password'";
		
		// Prepare statement
		$stmt = $conn->prepare($sql);

		// execute the query
		$stmt->execute();

		//renvoi un nombre de resultat trouvé par rapport aux identifiants dans la base de donnée
		// Si 1, authentification reussie, si 0 erreur d'authentification
		$nb_result = $stmt->rowCount();

		
		if($nb_result > 0 ){

			//Récupération de l'id de l'utilisateur
			$req = $stmt->fetchAll();
			$userID = $req[0]['id'];

			$result = "Authentification réussie%" . (string)$userID . "%";
			
		} else {
			$result = 'Erreur d\'authentification';
		}
		
		
		echo $result;


}

 //Fonction qui permet de traiter les informations reçuent en POST afin de les insérer en BDD
function Sync($conn){

	//Recuperation de la date au format AAAAMM pour vérifier si le mois est en cours. Si c'est le mois en cours, la fiche ne doit pas être cloturée
	date_default_timezone_set('UTC');
	$currentDate = date("Y").date("m");

	//Récupération des données passés en POST, decode le JSON
	$datas = json_decode($_POST["Sync"]);
	//Récupération des données dans des variables
	$user = $datas[0]->UserID;
	$date = $datas[0]->date;
	$km = $datas[0]->KM;
	$rep = $datas[0]->REP;
	$etp = $datas[0]->ETP;
	$nuit = $datas[0]->NUI;
   
	//Ajout des frais en base de donnée

	ajoutFraisForfait($conn, $currentDate, $user, $date, $km, $rep, $etp, $nuit );
  
    
    //Variables qui vont me permettre de parcourir les différents frais HF et de m'aretter quand il n'y en a plus
 	$hfRestant = true;
 	$index = 0;
 	//Boucle pour trouver tout les frais HF envoyé
	do {
		
		//variable qui l'iteration et la recherche dans l'array
		$HFindex = "HF".$index;
		//Si le frais HF existe, n'est pas null
    	if ( !empty($datas[0]->$HFindex) ){

    		//récupération de l'objet HF
    		$hf = $datas[0]->$HFindex;

    		//Initialisation des variables contenant les valeurs relatives au HF
    		$hfMontant = $hf->Montant;
    		$hfMotif = $hf->Motif;
    		$hfJour = $hf->Jour;

    		//DEBUG
    		//echo "<br> Hors forfait numero " . $index . " : " . $hfMotif . $hfMontant . $hfJour;

    		ajoutFraisHorsForfait($conn, $user, $date, $currentDate, $hfMotif, $hfMontant, $hfJour);

    		//iteration pour tester si il y a d'autre frais HF
    		$index++;

    	} else {
    		//il n'y a plus de frais HF à traiter : je sors de la boucle
    		$hfRestant = false;
    	}

	} while ($hfRestant);

    //DEBUG
	//echo "Les données :  userID = " . $user . "date = " . $date . " km = " . $km . " rep = " . $rep . " etap = " . $etp . " nuit = " . $nuit;



	

	//Ajout des frais HF en base de donnée
	
}

function ajoutFraisHorsForfait($conn, $user, $date, $currentDate, $hfMotif, $hfMontant, $hfJour){
		
		//Je ne rentre en base de donnée que les fiches concernant des mois terminés
		//Si la fiche de frais n'est pas celle du mois en cours
if( !(strval($currentDate) === strval($date) ) ){
	try{
		$req = $conn->prepare("INSERT INTO lignefraishorsforfait(idvisiteur,mois,libelle,montant) VALUES (:idvisiteur,:mois,:libelle,:montant)");
    			$req->bindParam (':idvisiteur', $user, PDO::PARAM_STR,255);
    			$req->bindParam (':mois', $date, PDO::PARAM_STR,255);
    			$req->bindParam (':libelle', $hfMotif, PDO::PARAM_STR,255);
    			$req->bindParam (':montant', $hfMontant, PDO::PARAM_INT);
    	
    			$req->execute();
	} catch (PDOException $e){
		echo "Probleme d'insertion des frais hors forfait : " . $e;
		// en cas d'erreur, je conserve les données en locale
		echo "%error%";
	}
		
}
		
}
	


function ajoutFraisForfait($conn, $currentDate, $user, $date, $km, $rep, $etp, $nuit ){


		//j'associe dans un tableau les valeurs avec des clés pour les insérer dans la bdd
		$assoc = array
  		(
  			array("KM",$km),
  			array("ETP",$etp),
  			array("NUI",$nuit),
  			array("REP",$rep)
 		);

		

  		//Je ne rentre en base de donnée que les fiches concernant des mois terminés
		//Si la fiche de frais n'est pas celle du mois en cours
		if( !(strval($currentDate) === strval($date) ) ){
			//Je boucle pour rentrer les frais un par un
			for ($i=0; $i < 4; $i++) { 
				$index = $i;
				//j'utilise l'array $assoc afin d'attribuer un IDfrais à une valeur
				$quantite = $assoc[$index][1];
				$idfraisforfait = $assoc[$index][0];

				try{
					$req = $conn->prepare("INSERT INTO lignefraisforfait(idvisiteur,mois,idfraisforfait,quantite) VALUES (:idvisiteur,:mois,:idfraisforfait,:quantite)");
    				$req->bindParam (':idvisiteur', $user, PDO::PARAM_STR,255);
    				$req->bindParam (':mois', $date, PDO::PARAM_STR,255);
    				$req->bindParam (':idfraisforfait', $idfraisforfait, PDO::PARAM_STR,255);
    				$req->bindParam (':quantite', $quantite, PDO::PARAM_INT);
    	
    				$req->execute();
    			} catch (PDOException $e){
					echo "Un problème d'insertion dans la base de donnée est survenue : Duplication -> " . $e;
					// en cas d'erreur, je conserve les données en locale
					echo "%error%";
				}
			}
		
		}
		
	}

?>