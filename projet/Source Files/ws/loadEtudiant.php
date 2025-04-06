<?php

use service\EtudiantService;

if ($_SERVER["REQUEST_METHOD"] == "GET") {
    include_once '../racine.php';
    include_once RACINE . '/service/EtudiantService.php';
    loadAll();
}
function loadAll() {
    $es = new EtudiantService();
    header('Content-type: application/json');
    echo json_encode($es->findAllApi());
}
