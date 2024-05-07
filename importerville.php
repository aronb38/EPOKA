<?php
// Connexion à la base de données
$serveur = "localhost";
$utilisateur = "root";
$motDePasse = "";
$baseDeDonnees = "epoka";

$connection = new mysqli($serveur, $utilisateur, $motDePasse, $baseDeDonnees);

// Vérifier la connexion
if ($connection->connect_error) {
    $donnees = array(
        "message" => "Échec de la connexion à la base de données : " . $connection->connect_error
    );
    echo json_encode($donnees);
    exit();
}

// Requête SQL pour récupérer tous les noms des villes et leurs codes postaux
$sql = "SELECT nom, code_postal, idVille FROM villes ORDER BY categorie ASC, nom ASC";
$resultat = $connection->query($sql);

// Vérifier si la requête a retourné des résultats
if ($resultat->num_rows > 0) {
    // Créer un tableau associatif pour stocker les données
    $villes = array();

    // Parcourir les résultats et ajouter les villes au tableau
    while ($row = $resultat->fetch_assoc()) {
        $villes[] = array(
            "nom" => $row["nom"],
            "code_postal" => $row["code_postal"],
            "idVille" => $row["idVille"]
        );
    }

    // Convertir le tableau en format JSON et afficher
    echo json_encode($villes);
} else {
    // Renvoyer un message d'erreur si aucune ville n'a été trouvée
    $donnees = array(
        "message" => "Aucune ville trouvée"
    );
    echo json_encode($donnees);
}

// Fermer la connexion
$connection->close();
?>
