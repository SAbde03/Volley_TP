<?php

namespace service;
include_once RACINE . '/classes/Etudiant.php';
include_once RACINE . '/connexion/Connexion.php';
include_once RACINE . '/dao/IDao.php';


use classes\Etudiant;
use connexion\Connexion;
use dao\IDao;
use PDO;

class EtudiantService implements IDao {
    private $connexion;
    private $pdo;
    function __construct() {
        $this->connexion = new Connexion();
    }
    public function create($o) {
        $query = "INSERT INTO Etudiant (`id`, `nom`, `prenom`, `ville`, `sexe`) "
            . "VALUES (NULL, '" . $o->getNom() . "', '" . $o->getPrenom() . "',  
                '" . $o->getVille() . "', '" . $o->getSexe() ."');";
        $req = $this->connexion->getConnexion()->prepare($query);
        $req->execute() or die('Erreur SQL');
    }

    public function delete($id) {
        $query = "DELETE FROM Etudiant WHERE id = ?";
        $req = $this->connexion->getConnexion()->prepare($query);
        $req->execute(array($id)) or die('Erreur SQL: ' . implode(' ', $req->errorInfo()));
    }

    public function findAll() {
        $etds = array();
        $query = "select * from Etudiant";
        $req = $this->connexion->getConnexion()->prepare($query);
        $req->execute();
        while ($e = $req->fetch(PDO::FETCH_OBJ)) {
            $etds[] = new Etudiant($e->id, $e->nom, $e->prenom, $e->ville, $e->sexe);
        }
        return $etds;
    }

    public function findById($id) {
        $query = "select * from Etudiant where id = " . $id;
        $req = $this->connexion->getConnexion()->prepare($query);
        $req->execute();
        if ($e = $req->fetch(PDO::FETCH_OBJ)) {
            $etd = new Etudiant($e->id, $e->nom, $e->prenom, $e->ville, $e->sexe, $e->dateNaissance, $e->photo);
        }
        return $etd;
    }
    public function findAllApi() {
        $query = "select * from Etudiant";
        $req = $this->connexion->getConnexion()->prepare($query);
        $req->execute();
        return $req->fetchAll(PDO::FETCH_ASSOC);
    }

    public function update($o) {
        $query = "UPDATE `etudiant` SET `nom` = '" . $o->getNom() . "', `prenom` = '"
            . $o->getPrenom() . "', `ville` = '" . $o->getVille() . "', `sexe` = '"
            . $o->getSexe() . "' WHERE `etudiant`.`id` = " . $o->getId();
        $req = $this->connexion->getConnexion()->prepare($query);
        $req->execute() or die('Erreur SQL');
    }
}