TP5 - Métriques orientées objet

Gestion_Bib • Abdellah Kachani • Qualité du logiciel • 5 novembre 2026

Code et résultats : https://github.com/kachaniabdellah86/tp5-gestion-bibNouvelle version : apres/src ; version de départ : avant/src.

## 1. Préparation et méthode

Le projet de départ est la V2 du dépôt de l’énoncé (révision cc04dce). Les six classes ont été compilées avec Temurin 8u482, puis analysées par CKJM 1.8, avant et après modification. Les tests sont exclus des mesures. Le script VERIFIER.cmd reproduit la compilation, les mesures et les tests.

Compilation : javac -source 1.8 -target 1.8 -encoding UTF-8 -d bin &lt;sources&gt;.Analyse : java -cp "bin;ckjm-1.8.jar" gr.spinellis.ckjm.MetricsFilter &lt;classes&gt;.Cette invocation conserve le classpath ; avec -jar, -cp est ignoré. Java 8 évite les erreurs de résolution de Object : DIT vaut 1, et 2 pour DigitalBook, contrairement aux zéros de la capture du TP.

## 2. Signification des métriques (exercice 1)

| Mesure | Nom complet | Interprétation retenue |
| --- | --- | --- |
| WMC | Weighted Methods per Class | Nombre de méthodes, constructeur compris ; poids 1 dans CKJM. |
| DIT | Depth of Inheritance Tree | Profondeur de l’héritage, Object servant de racine. |
| NOC | Number of Children | Nombre de sous-classes directes. |
| CBO | Coupling Between Objects | Classes utilisées ; SDK exclu par défaut. |
| RFC | Response For a Class | Méthodes de la classe et appels directs distincts, SDK inclus. |
| LCOM | Lack of Cohesion of Methods | max(0, paires sans attribut commun - paires avec attribut commun). |
| Ca / Ce | Afferent / Efferent Coupling | Classes clientes / classes dont on dépend. |
| NPM | Number of Public Methods | Nombre de méthodes publiques. |

La sortie CKJM contient Ca puis NPM, sans colonne Ce séparée. Au niveau classe, son CBO mesure les dépendances sortantes. Les Ca/Ce de packages sont calculés séparément.

## 3. Mesures par classe : avant → après

| Classe | WMC | DIT | NOC | CBO | RFC | LCOM | Ca | NPM |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| Book | 7 | 1 | 1 | 1 | 13 → 12 | 5 | 3 → 2 | 7 |
| DigitalBook | 2 | 2 | 0 | 1 | 3 | 0 | 1 | 2 |
| Lendable | 2 → 4 | 1 | 0 | 0 | 2 → 4 | 1 → 6 | 4 | 2 → 4 |
| Customer | 3 | 1 | 0 | 1 | 10 | 0 | 0 | 3 |
| Library | 5 | 1 | 0 | 2 → 1 | 18 → 15 | 0 | 1 | 5 |
| LibraryApp | 2 → 3 | 1 | 0 | 4 | 10 → 18 | 1 → 3 | 0 | 2 |

Une valeur seule est inchangée. Book a le WMC maximal (7) et le LCOM maximal avant modification (5). LibraryApp a le CBO maximal (4), et Library le RFC maximal (18). Ce sont des maxima relatifs : les WMC et CBO restent faibles selon les repères du cours.


---

## 4. Interprétation critique (exercice 2)

Complexité. Book est la classe la plus chargée en méthodes et contient initialement une boucle de démonstration avec plusieurs branches, étrangère à la gestion des livres. Library concentre davantage d’interactions : son RFC de 18 vient notamment des collections et de l’affichage.

Cohésion et réutilisation. DigitalBook est la classe concrète la plus simple et cohésive (LCOM = 0), à égalité avec Library et Customer sur cet indicateur. Le LCOM de Book (5), le couplage de Library vers Book et les impressions console dans le modèle limitent la réutilisation. Le LCOM de Lendable n’est pas un défaut métier : une interface sans attribut donne mécaniquement des paires sans champ partagé.

WMC et RFC. La corrélation de Pearson avant modification vaut r ≈ 0,72 sur six types. La tendance est positive, sans prouver une causalité : Library a moins de méthodes que Book (5 contre 7), mais un RFC supérieur (18 contre 13). Les appels comptent autant que la taille de la classe.

| Package | Ca avant → après | Ce avant → après | I = Ce / (Ca + Ce) |
| --- | --- | --- | --- |
| library.model | 3 → 3 | 0 → 0 | 0 → 0 |
| library.service | 1 → 1 | 2 → 1 | 0,67 → 0,50 |
| library.app | 0 → 0 | 4 → 4 | 1 → 1 |

Comptage des classes externes distinctes dans jdeps, hors SDK et liens internes. Les clients du modèle sont LibraryApp, Library et Customer. Le service dépend initialement de Book et Lendable, puis seulement de Lendable. model est donc le plus stable. Son abstractivité reste A = 1/3 ; ajouter des méthodes à une interface ne change pas cette proportion.

## 5. Refactoring réalisé et effets (exercice 3)

1. Séparer les responsabilités. L’exemple numérique passe de Book à LibraryApp ; l’affichage du catalogue passe de Library à l’application. Book ne fait plus d’entrée-sortie console. Son RFC baisse de 13 à 12, mais son WMC et son LCOM restent à 7 et 5, car getDescription() remplace la méthode déplacée.

2. Dépendre du contrat Lendable. Ajout de isBorrowed() et getDescription(), suppression du test instanceof et de la conversion vers Book dans Library. Son CBO baisse de 2 à 1 et son RFC de 18 à 15. Le filtre des disponibilités exclut désormais les objets empruntés.

3. Protéger l’état et les collections. Le catalogue est exposé par une copie non modifiable et les attributs stables deviennent final. Un emprunt déjà actif est refusé par exception avant son enregistrement chez un autre client. Les valeurs CKJM de Customer restent identiques : ces corrections améliorent le comportement sans réduire le nombre de méthodes.

Limites des règles de refactoring. Extraire une méthode peut augmenter WMC dans CKJM ; encapsuler un attribut ne diminue pas automatiquement CBO. Découper une classe par responsabilité peut réduire LCOM, mais ajoute des dépendances ; la fusion n’est utile que si les responsabilités se recouvrent. Une nouvelle interface peut augmenter A ; I ne baisse que si les dépendances changent. Ici, le contrat existant est élargi, sans ajout de type.

## 6. Vérification et conclusion

20 vérifications réussies : emprunts, retours, conflit entre clients, livre numérique, disponibilité, copie protégée et autre type Lendable. La sortie de démonstration est identique. Un témoin reconstruit sans packages donne les mêmes métriques CKJM que la V2 : le rangement seul ne réduit pas la complexité ; ce témoin n’est pas une V1 historique.

Le service est moins couplé et les prêts sont plus fiables. Il n’y a toutefois pas de baisse globale : les sommes WMC/RFC/LCOM passent de 21/56/7 à 24/62/14. Le code déplacé augmente les valeurs de LibraryApp et le contrat élargi celles de Lendable. Le gain porte sur la séparation des rôles et les règles métier ; Book reste à surveiller pour sa cohésion. L’exemple reste mono-utilisateur, en mémoire.

Sources : énoncé TP_Code_Metrics[POO], p. 1-4 ; cours QL_Metrique_POO, p. 5-8, 17-25 et 29-34 ; dépôt de départ ; documentation CKJM (Spinellis). Mesures et journaux complets : dossier resultats du dépôt lié en page 1.
