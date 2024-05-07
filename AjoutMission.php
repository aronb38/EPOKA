    <?php
// Connexion à la base de données
$serveur = "localhost";
$utilisateur = "root";
$motDePasse = ""; // Mettez votre mot de passe ici s'il est défini
$baseDeDonnees = "epoka";

try {
    // Connexion à la base de données
    $connexion = new PDO("mysql:host=$serveur;dbname=$baseDeDonnees", $utilisateur, $motDePasse);
    // Définir le mode d'erreur PDO à Exception
    $connexion->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

    // Vérification des paramètres GET
    $parametresRequis = ['debut', 'fin', 'idSalarie']; // 'nom' n'est pas utilisé dans la requête SQL, donc nous le retirons de la liste
    foreach ($parametresRequis as $parametre) {
        if (!isset($_GET[$parametre])) {
            throw new Exception("Paramètre absent : $parametre");
        }
    }

    // Récupération des paramètres GET
    $debut = $_GET['debut'];
    $fin = $_GET['fin'];
    $idSalarie = $_GET['idSalarie'];

    // Requête SQL pour insérer les données dans la table des missions
    // Nous supposons ici que l'id de la ville est déjà connu et fixe à 1
    $idVille = 1; // Remplacez 1 par l'ID de la ville appropriée
    $sql = "INSERT INTO missions (debut, fin, idSalarie, idVille) VALUES (?, ?, ?, ?)";
    $statement = $connexion->prepare($sql);
    $resultat_insertion = $statement->execute([$debut, $fin, $idSalarie, $idVille]);

    // Vérification du résultat de l'insertion
    if ($resultat_insertion) {
        $donnees = array(
            "message" => "Mission insérée avec succès"
        );
        echo json_encode($donnees);
    } else {
        throw new Exception("Échec de l'insertion de la mission");
    }
} catch (PDOException $e) {
    $donnees = array(
        "erreur" => "Erreur PDO : " . $e->getMessage()
    );
    echo json_encode($donnees);
} catch (Exception $e) {
    $donnees = array(
        "erreur" => $e->getMessage()
    );
    echo json_encode($donnees);
}
?>
