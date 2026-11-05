# TP5 - Analyse des métriques orientées objet avec CKJM

Abdellah Kachani - Qualité du logiciel - 5 novembre 2026

**[Rapport PDF](Rapport_TP5.pdf)** · **[Code modifié](apres/src/library)** · **[Résultats des tests](resultats/tests.txt)**

Projet réalisé à partir de [qazdar/gestion_Bib_V2](https://github.com/qazdar/gestion_Bib_V2), révision `cc04dce820d41a1a167ab50e2402e0ba1c58ba8f`.

## Contenu

- `avant/src` : les six fichiers Java d'origine, sans modification.
- `apres/src` : nouvelle version, avec les mêmes packages `model`, `service` et `app`.
- `apres/tests` : tests autonomes des emprunts, retours, disponibilités et collections.
- `resultats` : mesures CKJM brutes, dépendances jdeps, sorties et résultats des tests.
- `controle_sans_packages` : copie témoin reconstruite à partir de la V2 en retirant seulement les packages et imports internes. Ce n'est pas une V1 historique récupérée.
- `Rapport_TP5.pdf` : compte-rendu de deux pages. `Rapport_TP5.md` : version texte modifiable.
- `outils/ckjm-1.8.jar` : outil fourni par le dépôt du TP.

## Exécuter sur ce laptop

Double-cliquer sur **VERIFIER.cmd** pour recompiler les trois variantes, recalculer les métriques, exécuter les 20 vérifications et comparer les sorties. Double-cliquer sur **LANCER.cmd** pour lancer la version modifiée. La copie locale contient un JDK 8 portable ; aucune modification du Java installé n'est nécessaire.

## Reproduire depuis GitHub ou l'archive de dépôt

Installer ou décompresser un JDK 8, puis exécuter depuis PowerShell :

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\verifier.ps1 -JavaHome 'C:\chemin\vers\jdk8'
```

Le JDK portable n'est pas inclus dans GitHub ni dans l'archive de dépôt. La version exacte utilisée et son empreinte SHA-256 sont indiquées dans `outils/java8-provenance.json`. `analyser.py` permet, avec Python 3, de régénérer `resultats/synthese.json` à partir des sorties brutes.

La compilation utilise `javac -encoding UTF-8 -source 1.8 -target 1.8 -d bin <sources>`. L'analyse utilise :

```powershell
java -cp 'bin;ckjm-1.8.jar' gr.spinellis.ckjm.MetricsFilter <fichiers .class>
```

Les fichiers sont énumérés récursivement par le script. On utilise la classe principale de CKJM avec `-cp`, car `-jar` ignore le classpath fourni par `-cp`. Java 8 permet à l'ancien BCEL de retrouver `java.lang.Object` et de calculer correctement DIT. Les tests sont compilés séparément et exclus des métriques.

## Changements

1. Déplacement de l'exemple numérique hors de `Book`, vers l'application. La démonstration produit exactement la même sortie.
2. Déplacement de l'affichage du catalogue vers `LibraryApp`. `Library` manipule uniquement `Lendable`, sans conversion vers `Book` ; `getDescription()` assure l'affichage polymorphe.
3. Consultation de l'état via `Lendable.isBorrowed()` et correction du filtre des disponibilités.
4. Refus explicite d'un second emprunt par une exception, avant l'enregistrement chez le client. Un autre client ne peut plus rendre ce prêt.
5. Copie non modifiable du catalogue, avec attributs stables déclarés `final`.

L'API évolue : `listAllItems()` devient `getItems()`, `complexMethodExample()` est déplacée dans l'application et `Lendable` comporte deux méthodes supplémentaires. Les implémentations de cette interface doivent donc être adaptées. `borrow()` et `returnItem()` signalent désormais les transitions invalides par `IllegalStateException`, au lieu d'imprimer un message. Le scénario normal reste inchangé.

## Lecture des mesures

Ordre CKJM : `classe WMC DIT NOC CBO RFC LCOM Ca NPM`. Ce n'est pas une colonne Ce : au niveau classe, le CBO de cet outil mesure les dépendances sortantes, hors SDK par défaut. Les Ca/Ce des packages sont recalculés à partir des classes externes distinctes, sans additionner les Ca de classe et sans compter les liens internes.

`Library` passe de CBO 2 à 1 et de RFC 18 à 15. Les totaux WMC, RFC et LCOM ne diminuent pas : le déplacement du code et l'élargissement du contrat augmentent les valeurs de l'application et de l'interface. Le rapport décrit ces limites. Le code reste un exemple pédagogique en mémoire, sans gestion concurrente des prêts, stockage permanent ni validation générale des saisies.

## Sources

- Énoncé : `TP_Code_Metrics[POO].pdf`, fourni pour le TP.
- Cours : `QL_Metrique_POO.pdf`, Pr. Aimad Qazdar, 2 novembre 2026.
- [Documentation CKJM, Diomidis Spinellis](https://www.spinellis.gr/sw/ckjm/doc/indexw.html).
- [Dépôt original](https://github.com/qazdar/gestion_Bib_V2). L'historique du dépôt est conservé par ce fork ; aucune nouvelle licence n'est attribuée au code du cours.
