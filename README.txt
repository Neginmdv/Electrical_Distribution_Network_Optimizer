Unité d'Enseignement : 
    Programmation Avancée et Application

Université : 
    Université Paris Cité

Auteurs :
    Negin Mahdavi
    Tala Nasarah

-------------------------------------------------------------------------------------------------------------------------------------
DESCRIPTION DU PROJET

Ce projet a pour objectif de modéliser et d'optimiser un réseau de 
distribution électrique composé de générateurs et de maisons (consommateurs).

Partie 1 : Mode Manuel (Console) 
- Construction manuelle du réseau (ajout de générateurs/maisons).
- Gestion des connexions (ajout/suppression).
- Calcul du coût en temps réel.
- Validation des contraintes (ex: une maison doit avoir un unique générateur).

Partie 2 : Mode Automatique & Fichiers 
- Importation : Lecture d'un réseau depuis un fichier texte respectant la syntaxe 
  generateur, maison, connexion.
- Validation : Vérification syntaxique et sémantique du fichier d'entrée.
- Sauvegarde : Exportation de la solution courante dans un fichier.
- Optimisation : Recherche automatique d'une meilleure solution via des algorithmes.

Bonus :
- Tests Unitaires : Suite complète de tests (TestRunner) validant la logique métier
  et les calculs mathématiques.
- Interface Graphique (JavaFX) : Visualisation du réseau, modification dynamique et
  lancement des algorithmes via une interface utilisateur (GraphInterface).

-------------------------------------------------------------------------------------------------------------------------------------
ÉTAT DU PROJET ET FONCTIONNALITÉS

Fonctionnalités Implémentées :
L'intégralité des fonctionnalités demandées pour la Partie 1 et la Partie 2, 
ainsi que les deux bonus optionnels (Tests Unitaires et Interface Graphique),
ont été implémentées avec succès.

Fonctionnalités Manquantes ou Problèmes Connus :
- Aucune fonctionnalité manquante.
- Aucun bug critique identifié.
(Note : L'algorithme de Recuit Simulé donne des résultats variables par nature, 
ceci est un comportement attendu et non un bug).

-------------------------------------------------------------------------------------------------------------------------------------
ALGORITHMES DE RÉSOLUTION

Pour la résolution automatique (Partie 2), nous avons implémenté deux approches
dans la classe Solver:

1. Algorithme Naïf (Aléatoire) :
    * Effectue des changements de connexions au hasard.
    * N'accepte le changement que si le coût total diminue strictement.
    * Limite : Peut rester bloqué dans un minimum local.
    
2. Algorithme Optimisé (Recuit Simulé) :
    * Il s'agit d'une méta-heuristique inspirée de la thermodynamique.
    * Contrairement à l'approche naïve, cet algorithme accepte parfois une solution
      "moins bonne" (dégradant le coût) avec une certaine probabilité dépendante
      d'une "température" T.
    * Logique : Au début (T élevée), l'algorithme explore largement l'espace des 
      solutions (accepte les dégradations). À mesure que T diminue, il se stabilise
      vers une solution optimale.
    * Cela permet d'échapper aux minimums locaux pour trouver un optimum global plus
      performant.

      Note sur la variabilité des résultats : 
      L'algorithme de Recuit Simulé étant de nature stochastique (probabiliste), 
      il ne garantit pas l'atteinte de l'optimum global absolu à chaque exécution. 
      Bien qu'il soit conçu pour éviter les minimums locaux grâce au paramètre de 
      "température", le résultat final peut légèrement varier d'une exécution à l'autre.
      
      Conseil d'utilisation : 
      Pour des réseaux très complexes, il peut être pertinent de lancer l'algorithme 
      plusieurs fois afin de retenir la meilleure solution trouvée.

-------------------------------------------------------------------------------------------------------------------------------------
INSTALLATION ET EXÉCUTION

-- Prérequis --
* Java JDK (version 17 ou supérieure recommandée).
* Bibliothèque JavaFX (pour l'interface graphique).

1. Compilation
Placez-vous à la racine du projet et créez un dossier bin pour les fichiers compilés :
mkdir bin
javac -d bin src/PAA2/*.java

2. Exécution du Mode Console (Standard)
La classe principale est PAA2.ProjectApp.

Mode Manuel (Partie 1) :
java -cp bin PAA2.ProjectApp

Mode Automatique (Partie 2) :
Prend en argument le fichier d'entrée et le paramètre lambda :
java -cp bin PAA2.ProjectApp data/network.txt 10

3. Exécution de l'Interface Graphique (Bonus)
La classe principale pour l'interface est PAA2.GraphInterface.
java --module-path /chemin/vers/javafx/lib --add-modules javafx.controls -cp bin PAA2.GraphInterface
(Remplacez /chemin/vers/javafx/lib par le chemin réel sur votre machine)

4. Lancement des Tests
Pour vérifier le bon fonctionnement du projet :
java -cp bin PAA2.TestRunner

-------------------------------------------------------------------------------------------------------------------------------------
ARCHITECTURE DU CODE

Network : 
    Modèle principal contenant les HashMaps des générateurs et maisons. 
    Contient la logique de calcul du coût (calculateCost).

NetworkParser : 
    Gère la lecture, le parsing ligne par ligne et la gestion des erreurs des fichiers d'entrée.

Solver : 
    Contient les algorithmes d'optimisation (Random et Recuit Simulé).

ProjectApp : 
    Point d'entrée pour l'application en ligne de commande.

GraphInterface : 
    Point d'entrée pour l'application JavaFX.

Utils : 
    Fonctions utilitaires pour la sauvegarde des fichiers.