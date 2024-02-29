Labo Spring: Gestion de tournois d’échecs.

Objectifs : Création d’une Web API pour la gestion de tournois d’échecs.

Contraintes techniques :
● WebAPI MVC (Spring Boot) + Testing
● Architecture 3 tiers (respects des responsabilités inhérentes aux différentes 
couches).

Mise en situation :
Monsieur Checkmate souhaiterait créer une application pour la gestion de tournois de son 
club d’échecs. Il voudrait, entre autres, pouvoir créer des tournois et des comptes pour les 
joueurs de son club. Les joueurs pourraient alors s’inscrire aux différents tournois qui seront 
organisés. Il aimerait aussi que certaines actions soient automatisées (Envoi d’email, 
création des rencontres,…).

Fonctionnalités :
1. Inscrire un joueur (membre du club) (Monsieur Checkmate) :
● Un membre possède :
o 1 pseudo
o 1 email
o 1 mot de passe
o 1 date de naissance
o 1 genre (garçon, fille, autre)
o 1 ELO (classement aux échecs entre 0 - 3000)
o 1 rôle (pour différencier Monsieur Checkmate des autres utilisateurs)
● Règles métiers :
o Le pseudo et l’email devront être unique.
o Les mots de passe devront être hachés dans la db (l’ajout d’un sel est conseillé).
o Si un joueur n’a pas de classement (ELO), il commencera toujours à 1200.
● Bonus :
o Le mot de passe sera généré aléatoirement.
o Lorsque Monsieur Checkmate enregistre un nouveau membre, un email est envoyé au membre avec son mot de passe pour le prévenir (prévoir template Thymeleaf).

2. Créer un tournoi (Monsieur Checkmate) :
● Un tournoi possède :
o 1 nom
o 1 lieu (nullable)
o 1 nombre minimum de joueurs (2-32)
o 1 nombre maximum de joueurs (2-32)
o 1 ELO minimum (0-3000, nullable)
o 1 ELO maximum (0-3000, nullable)
o 1 ou plusieurs catégories (junior, senior, veteran)
o 1 statut (en attente de joueurs, en cours, terminé)
o 1 numéro correspondant à la ronde courante
o 1 booléen (WomenOnly) qui détermine si le tournoi n’est autorisé qu’aux filles
o 1 date de fin des inscriptions
o 1 date de création
o 1 date de mise à jour
● Règles métiers :
o Le nombre minimum de joueurs doit être plus petit ou égal au nombre maximum (pareil pour l’ELO)
o La date de fin des inscriptions devra être supérieure à la date du jour + nombre minimum de joueurs (ex : Si le tournoi est créé le 10/10/2022 et que le nombre min de joueurs est 8 alors la date de fin des inscriptions devra être supérieure au) 18/10/2022.
o La ronde courante est 0
o Un tournoi que vient d’être créé aura le statut « en attente de joueurs »
o La date de création et de mise à jour correspond à la date du jour
● Bonus :
o A la création d’un tournoi un email est envoyé à tous les joueurs qui respectent les contraintes du tournoi (v. inscriptions) pour les prévenir (prévoir template Thymeleaf).


3. Supprimer un tournoi (Monsieur Checkmate) :
● Règles métiers :
o Seuls les tournois qui n’ont pas commencé peuvent être supprimés
● Bonus :
o Prévenir par mail tous les joueurs inscrits que le tournoi a été supprimé


4. Afficher les différents tournois (tout le monde) :
● Afficher les 10 derniers tournois non clôturés par ordre décroissant sur la date de 
mise à jour
● Ils présenteront :
o L’identifiant
o Le nom
o Le lieu
o Le nombre de joueurs inscrits
o Le nombre min
o Le nombre max
o Les catégories
o L’ELO minimum
o L’ELO maximum
o Le statut
o La date de fin des inscriptions
o La ronde courante
● Bonus :
o Un filtre de recherche (nom, statut, page, catégories, …) peut être ajouté pourrechercher les différents tournois
o canRegister (détermine si un joueur peut s’inscrire ou non) (v. inscriptions)
o isRegistered (détermine si un joueur est déjà inscrit ou non)


5. Afficher les détails d’un tournoi (tout le monde) :
● Présenter les mêmes infos que pour tous les tournois
o Y inclure les joueurs inscrits
● Bonus :
o Afficher aussi les rencontres de la ronde courante


6. Se connecter (tout le monde) :
● Retourner un jeton (JWT).
● Règles métiers :
o Un peu se connecter avec son pseudo ou son email


7. S’inscrire à un tournoi (tous les utilisateurs connectés) :
● Règles métiers :
o On peut s’inscrire
▪ Si un tournoi n’a pas encore commencé.
▪ Si la date de fin des inscriptions n’est pas dépassée
▪ Si le joueur n’est pas déjà inscrit
▪ Si un tournoi n’a pas atteint le nombre maximum de participants
▪ Si l’âge du joueur l’y autorise:
Son est calculé par rapport à la date de fin des inscriptions (c-à-d l’âge qu’il aura à la fin des inscriptions)
Selon son âge, il fera partie d’une certaine catégorie Junior (< 18) Senior (>=18 et < 60) Vétéran (>= 60)
▪ Si son ELO, l’y autorise:
L’ELO du joueur doit <= à l’ELO max (si renseigné)
L’ELO du joueur doit >= à l’ELO min (si renseigné)
▪ Si son genre l’y autorise:
Seuls les joueurs (fille et autre) peuvent s’inscrire à un tournoi
« WomenOnly »


8. Se désinscrire d’un tournoi (tous les utilisateurs connectés) :
● Règles métiers :
o On peut se désinscrire
▪ Si le tournoi n’a pas encore commencé
▪ Si le joueur est inscrit


9. Démarrer un tournoi (Monsieur Checkmate) :
● Règles métiers :
o Un tournoi ne peut démarrer que
▪ Si le nombre minimum de participants est atteint
▪ Si la date de fin des inscriptions est dépassée
o La ronde courante du tournoi passe à 1
o La date de mise à jour du tournoi est modifiée
o Lorsqu’un tournoi démarre, toutes les rencontres sont générées
o Tous les joueurs se rencontrent 2 fois (Round Robin – Aller-Retour)
o Une rencontre possède :
▪ 1 id
▪ 1 id de tournoi (pour lequel la rencontre a été jouée)
▪ 1 id du joueur blanc
▪ 1 id du joueur noir
▪ 1 numéro de ronde
▪ 1 résultat (pas encore joué, blanc, noir, égalité)


10. Modifier le résultat d’une rencontre (Monsieur Checkmate) :
● Règles métiers :
o On ne peut modifier le résultat d’une rencontre que si elle fait partie de la 
ronde
Courante


11. Passer au tour suivant (Monsieur Checkmate) :
● Règles Métiers
o On ne peut passer la ronde suivante que si toutes les rencontres de la ronde courante ont été jouées
o On incrémente la ronde courante du tournoi


12. Voir le tableau des scores pour un tournoi et une ronde donnée (tout le monde) :
● Afficher un tableau qui contiendra les joueurs du tournoi (trié par ordre décroissant sur le score)
● Afficher
o Le nom
o Le nombres de rencontres jouées
o Le nombre de victoires
o Le nombre de défaites
o Le nombre d’égalité
o Le score (1pt victoire, 0.5pt égalité)
