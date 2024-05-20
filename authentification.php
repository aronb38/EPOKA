<?php
// Connexion à la base de données
$serveur = "localhost";
$utilisateur = "root";
$motDePasse = "";
$baseDeDonnees = "epoka";

// Connexion
$connection = new mysqli($serveur, $utilisateur, $motDePasse, $baseDeDonnees);

// Vérifier la connexion
if ($connection->connect_error) {
    $donnees = array(
        "message" => "Échec de la connexion à la base de données : " . $connection->connect_error
    );
    echo json_encode($donnees);
    exit();
}

// Vérifier si les données POST existent
if (isset($_POST['id'], $_POST['mot_de_passe'])) {
    // Récupérer les données du formulaire et les sécuriser contre les injections SQL
    $id = $connection->real_escape_string($_POST['id']);
    $motDePasse = $connection->real_escape_string($_POST['mot_de_passe']);

    // Requête SQL pour vérifier les identifiants et récupérer l'ID, le nom et le prénom
    $sql = "SELECT id, nom, prenom FROM salaries WHERE id=? AND mot_de_passe=?";
    $statement = $connection->prepare($sql);
    $statement->bind_param("ss", $id, $motDePasse);
    $statement->execute();
    $resultat = $statement->get_result();

    // Vérifier si l'utilisateur existe
    if ($resultat->num_rows > 0) {
        // Récupérer l'ID, le nom et le prénom de l'utilisateur
        $row = $resultat->fetch_assoc();
        $idUtilisateur = $row["id"];
        $nomUtilisateur = $row["nom"];
        $prenomUtilisateur = $row["prenom"];

        // Créer un tableau associatif pour renvoyer les données
        $donnees = array(
            "message" => "Connexion réussie",
            "id" => $idUtilisateur,
            "nom" => $nomUtilisateur,
            "prenom" => $prenomUtilisateur
        );

        // Convertir le tableau associatif en format JSON et afficher
        echo json_encode($donnees);
    } else {
        // Renvoyer un message d'erreur si l'utilisateur n'existe pas
        $donnees = array(
            "message" => "Connexion refusée"
        );
        echo json_encode($donnees);
    }
} else {
    // Paramètre absent
    $donnees = array(
        "message" => "Paramètre absent"
    );
    echo json_encode($donnees);
}

// Fermer la connexion
$statement->close();
$connection->close();
?>
