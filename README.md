
Matching GPS traces with MapMatcher
===================================

Le but de l’algorithme est de recaler des points d'observation sur un réseau. Un cas classique consiste à recaler des points GPS d'une voiture sur un réseau routier. L'algorithme prend en entrée un jeu de traces GPS et un réseau linéaire et fournit en sortie les points GPS recalés sur le réseau.


[![Project Status: Active – The project has reached a stable, usable state and is being actively developed.](https://www.repostatus.org/badges/latest/active.svg)](https://www.repostatus.org/#active)
[![CircleCI](https://dl.circleci.com/status-badge/img/gh/umrlastig/mapmatcher/tree/master.svg?style=shield)](https://dl.circleci.com/status-badge/redirect/gh/umrlastig/mapmatcher/tree/master)
[![Maven Central](https://img.shields.io/maven-central/v/fr.umr-lastig/mapmatcher.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22fr.umr-lastig%22%20AND%20a:%22mapmatcher%22)
[![Software License](https://img.shields.io/badge/Licence-Cecill--C-blue.svg?style=flat)](https://github.com/umrlastig/mapmatcher/blob/master/LICENSE)




Installation
--------------

<!--

#### Option 1: 

Download mapmatcher jar file at:

https://forge-cogit.ign.fr/nexus/content/repositories/snapshots/fr/ign/cogit/mapmatcher/1.0-SNAPSHOT/mapmatcher-1.0-20180621.110309-1.jar


#### Option 2: 

Insert the following lines in your Maven pom.xml:

```xml
<dependency>
	<groupId>fr.ign.cogit</groupId>
	<artifactId>mapmatcher-core</artifactId>
	<version>1.0-SNAPSHOT</version>
</dependency>
```

```xml
<repository>
	<id>cogit-snapshots</id>
	<name>Cogit Snapshots Repository</name>
	<url>https://forge-cogit.ign.fr/nexus/content/repositories/snapshots/</url>
</repository>
```

-->