# FunProg

Ce projet est un logiciel pour automatiser le contrôle de tondeuses à gazon. Il permet d'entrer un fichier de séquence et de générer le résultat dans un fichier de sortie au format JSON, CSV ou YAML.

## Utilisation

Pour utiliser ce logiciel, exécutez le fichier Main.scala. Vous serez accueilli par un menu vous demandant de choisir entre entrer un fichier de séquence ou quitter le programme.

Si vous choisissez d'entrer un fichier de séquence, vous devrez entrer le chemin du fichier. Le logiciel utilisera alors le fichier pour exécuter les instructions des tondeuses et générer un fichier de sortie.

Vous devrez également entrer le chemin du fichier de sortie et le format de sortie souhaité (JSON, CSV, YAML ou les trois formats).

## Structure du code

La classe principale du logiciel est la classe ConsoleUI. Cette classe gère l'interaction avec l'utilisateur via la console. Elle appelle les autres classes nécessaires pour exécuter les instructions des tondeuses et générer le fichier de sortie.

La classe InputParser est responsable de parser le fichier d'entrée pour obtenir les informations nécessaires pour exécuter les instructions des tondeuses.

La classe Export est responsable de générer le fichier de sortie. Elle utilise la bibliothèque better-files pour écrire les données dans le fichier de sortie au format souhaité (JSON, CSV ou YAML).

La classe Pelouse représente la pelouse sur laquelle les tondeuses se déplacent. Elle a une limite en x et y.

La classe Tondeuse représente une tondeuse. Elle a des informations sur sa position (x, y) et sa direction (N, S, E, W). Elle peut exécuter des instructions pour se déplacer sur la pelouse.
