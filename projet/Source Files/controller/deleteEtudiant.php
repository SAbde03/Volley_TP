<?php
include_once '../racine.php';
include_once RACINE.'/service/EtudiantService.php';

use service\EtudiantService;

$etudiantService = new EtudiantService();

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    if (isset($_POST['id'])) {
        $id = $_POST['id'];


        $result = $etudiantService->delete($id);

        if ($result) {
            echo "Suppression réussie.";
        } else {
            echo "Échec de la suppression.";
        }
    } else {
        echo "ID non fourni.";
    }
} else {
    echo "Méthode non autorisée.";
}
