# LocalTosaCode4J

Projet pour tester en local les challenges proposés par [Tosa Code](https://www.isograd.com/FR/solutionconcours.php) comme la Battle Dev.

Tester avec : Intellij 2019.2.4 CE

## Utilisation
* créer une branche pour le challenge à tester :
```
git checkout -b batteldev0811-fonction-mystere
```
* télécharger le ZIP d'examples et décompresser son contenu dans `/LocalTosaCode4J/src/test/resources/com/isograd/exercise/IsoContest`
* développer la solution dans la classe `/LocalTosaCode4J/src/main/java/com/isograd/exercise/IsoContest.java`
* pour tester : exécuter la classe de test Junit5 `/LocalTosaCode4J/src/test/java/com/isograd/exercise/IsoContestTest.java`
  un test JUnit5 dynamique est créé pour chaque fichier `inputN.txt` présents dans le répertoire de ressources du test.
  
Après résolution du challenge le plus rapide est de commiter et de revenir sur le master pour créer une nouvelle branche pour le challenge suivant.

Il est également possible de copier les classes et les ressources sous un autre nom, par exemple:
```
/src/main/java/com/isograd/exercise/IsoContest.java     -> /src/main/java/com/isograd/exercise/battledev0811/FonctionMystere.java
/src/test/java/com/isograd/exercise/IsoContestTest.java -> /src/test/java/com/isograd/exercise/battledev0811/FonctionMystereTest.java
/src/test/resources/com/isograd/exercise/IsoContest     -> /src/test/resources/com/isograd/exercise/battledev0811/FonctionMystere
```
en respectant les conventions la classe de test restera exécutable sous ce nouveau nom.

## Workaround Intellij
Pour contourner le bug https://github.com/gradle/gradle/issues/5975 il est nécessaire de sélectionner pour l'exécution des tests le "Platform Test Runner"
sinon les tests dynamiques JUnit5 n'apparaisent pas dans le panel Run > Test Results.
```
File > Settings... > Build, Execution, Deployment > Build Tools > Gradle : Run tests using: IntelliJ IDEA
```
