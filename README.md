
Matching GPS traces with MapMatcher
===================================

Le but de l’algorithme est de recaler des points d'observation sur un réseau. Un cas classique consiste à recaler des points GPS d'une voiture sur un réseau routier. Il prend en entrée un jeu de traces GPS et un réseau linéaire et fournit en sortie les points GPS recalés sur le réseau.


Installation
--------------

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