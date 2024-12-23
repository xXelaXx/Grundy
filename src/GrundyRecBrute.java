
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Jeu de Grundy avec IA pour la machine Ce programme ne contient que les
 * méthodes permettant de tester jouerGagnant() Cette version est brute sans
 * aucune amélioration
 *
 * @author J-F. Kamp et C. Tibermacine
 */
class GrundyRecBrute {

    long cpt;

    /**
     * Méthode principal du programme, lancement du jeu
     */
    void principal() {
        // testSuivant();
        // testEstPerdante();
        // testAfficher();
        // testPremier();
        // testJouerGagnant();
        // testLeJeu();
        // testEstGagnante();
        // testEnlever();
        // testEstPossible();

        testEstGagnanteEfficacite();
        // leJeu(5);
    }

    /**
     * Joue le jeu de Grundy
     *
     * @param n nombre d'allumettes dans le jeu
     */
    void leJeu(int n) {
        ArrayList<Integer> jeu = new ArrayList<>();
        jeu.add(n);
        int index;
        int answer;
        int player = 0;
        boolean valid;

        System.out.print("Jeu inital : ");
        afficher(jeu);
        System.out.println("");
        // System.out.println(jeu);

        while (estPossible(jeu)) {

            if (player % 2 == 0) {
                // Joueur
                do {
                    valid = true;
                    index = SimpleInput.getInt("Quel est l'indice du tas choisi ? ");
                    answer = SimpleInput.getInt("Combien d'allumettes enlevez vous ? ");
                    ArrayList<Integer> essai = new ArrayList<>(jeu);
                    enlever(jeu, index, answer);

                    if (essai.equals(jeu)) {
                        System.out.println("Mouvement invalide, veuillez réessayer");
                        valid = false;
                    }

                } while (!valid);

            } else {
                // Machine
                System.out.println("La machine est en train de jouer...");
                if (!jouerGagnant(jeu)) {
                    boolean found = false;
                    // Si la machine ne peut pas gagner, elle joue un coup aléatoire
                    do {
                        int index_random = (int) (Math.random() * jeu.size());
                        if (jeu.get(index_random) > 2) {
                            int answer_random = (int) (Math.random() * (jeu.get(index_random) - 1)) + 1;
                            if (jeu.get(index_random) / answer_random != 2) {

                                enlever(jeu, index_random, answer_random);
                                found = true;
                            }
                        }
                    } while (!found);
                }
            }
            player++;
            afficher(jeu);
            System.out.println("");

        }
        if (player % 2 == 0) {
            System.out.println("La machine a gagné !");
        } else {
            System.out.println("Le joueur a gagné !");
        }
    }

    /**
     * Tests succinct de la méthode leJeu()
     */
    void testLeJeu() {
        System.out.println("*** testLeJeu() ***");
        System.out.println("Test des cas normaux");

        ArrayList<Integer> jeu1 = new ArrayList<Integer>();

        testCasLeJeu(1);

        testCasLeJeu(2);

        testCasLeJeu(5);

        System.out.println("");
    }

    /**
     * Test d'un cas de la méthode leJeu()
     *
     * @param nb nombre d'allumettes dans le jeu
     */
    void testCasLeJeu(int nb) {
        System.out.println("leJeu(" + nb + ") \t= Test visuel de la méthode leJeu \t: ");
        leJeu(nb);
    }

    /**
     * Joue le coup gagnant s'il existe
     *
     * @param jeu plateau de jeu
     * @return vrai s'il y a un coup gagnant, faux sinon
     */
    boolean jouerGagnant(ArrayList<Integer> jeu
    ) {

        boolean gagnant = false;

        if (jeu == null) {
            System.err.println("jouerGagnant(): le paramètre jeu est null");
        } else {
            ArrayList<Integer> essai = new ArrayList<Integer>();

            // Une toute première décomposition est effectuée à partir de jeu.
            // Cette première décomposition du jeu est enregistrée dans essai.
            // ligne est le numéro de la case du tableau ArrayList (qui commence à zéro) qui
            // mémorise le tas (nbre d'allumettes) qui a été décomposé
            int ligne = premier(jeu, essai);

            // mise en oeuvre de la règle numéro2
            // Une situation (ou position) est dite gagnante pour la machine, s’il existe AU MOINS UNE décomposition
            // (c-à-d UNE action qui consiste à décomposer un tas en 2 tas inégaux) perdante pour l’adversaire. C'est
            // évidemment cette décomposition perdante qui sera choisie par la machine.
            while (ligne != -1 && !gagnant) {
                // estPerdante est récursif
                if (estPerdante(essai)) {
                    // estPerdante (pour l'adversaire) à true ===> Bingo essai est la décomposition choisie par la machine qui est alors
                    // certaine de gagner !!
                    jeu.clear();
                    gagnant = true;
                    // essai est recopié dans jeu car essai est la nouvelle situation de jeu après que la machine ait joué (gagnant)
                    for (int i = 0; i < essai.size(); i++) {
                        jeu.add(essai.get(i));
                    }
                } else {
                    // estPerdante à false ===> la machine essaye une autre décomposition en faisant appel à "suivant".
                    // Si, après exécution de suivant, ligne est à (-1) alors il n'y a plus de décomposition possible à partir de jeu (et on sort du while).
                    // En d'autres mots : la machine n'a PAS trouvé à partir de jeu UNE décomposition gagnante.
                    ligne = suivant(jeu, essai, ligne);
                }
            }
        }

        return gagnant;
    }

    /**
     * Tests succincts de la méthode joueurGagnant()
     */
    void testJouerGagnant() {
        System.out.println();
        System.out.println("*** testJouerGagnant() ***");

        System.out.println("Test des cas normaux");
        ArrayList<Integer> jeu1 = new ArrayList<Integer>();
        jeu1.add(6);
        ArrayList<Integer> resJeu1 = new ArrayList<Integer>();
        resJeu1.add(4);
        resJeu1.add(2);

        testCasJouerGagnant(jeu1, resJeu1, true);

    }

    /**
     * Test d'un cas de la méthode jouerGagnant()
     *
     * @param jeu le plateau de jeu
     * @param resJeu le plateau de jeu après avoir joué gagnant
     * @param res le résultat attendu par jouerGagnant
     */
    void testCasJouerGagnant(ArrayList<Integer> jeu, ArrayList<Integer> resJeu,
            boolean res
    ) {
        // Arrange
        System.out.print("jouerGagnant (" + jeu.toString() + ") : ");

        // Act
        boolean resExec = jouerGagnant(jeu);

        // Assert
        System.out.print(jeu.toString() + " " + resExec + " : ");
        boolean egaliteJeux = jeu.equals(resJeu);
        if (egaliteJeux && (res == resExec)) {
            System.out.println("OK\n");
        } else {
            System.err.println("ERREUR\n");
        }
    }

    /**
     * Méthode RECURSIVE qui indique si la configuration (du jeu actuel ou jeu
     * d'essai) est perdante. Cette méthode est utilisée par la machine pour
     * savoir si l'adversaire peut perdre (à 100%).
     *
     * @param jeu plateau de jeu actuel (l'état du jeu à un certain moment au
     * cours de la partie)
     * @return vrai si la configuration (du jeu) est perdante, faux sinon
     */
    boolean estPerdante(ArrayList<Integer> jeu
    ) {

        boolean ret = true; // par défaut la configuration est perdante

        if (jeu == null) {
            System.err.println("estPerdante(): le paramètre jeu est null");
        } else {
            // si il n'y a plus que des tas de 1 ou 2 allumettes dans le plateau de jeu
            // alors la situation est forcément perdante (ret=true) = FIN de la récursivité
            cpt++;
            if (!estPossible(jeu)) {
                ret = true;
            } else {
                // création d'un jeu d'essais qui va examiner toutes les décompositions
                // possibles à partir de jeu
                ArrayList<Integer> essai = new ArrayList<Integer>(); // size = 0 !

                // toute première décomposition : enlever 1 allumette au premier tas qui possède
                // au moins 3 allumettes, ligne = -1 signifie qu'il n'y a plus de tas d'au moins 3 allumettes
                int ligne = premier(jeu, essai);

                while ((ligne != -1) && ret) {

                    // mise en oeuvre de la règle numéro1
                    // Une situation (ou position) est dite perdante si et seulement si TOUTES ses décompositions possibles
                    // (c-à-d TOUTES les actions qui consistent à décomposer un tas en 2 tas inégaux) sont TOUTES gagnantes 
                    // (pour l’adversaire).
                    // L'appel à "estPerdante" est RECURSIF.
                    // Si "estPerdante(essai)" est true c'est équivalent à "estGagnante" est false, la décomposition
                    // essai n'est donc pas gagnante, on sort du while et on renvoie false.
                    if (estPerdante(essai) == true) {

                        // Si UNE SEULE décomposition (à partir du jeu) est perdante (pour l'adversaire) alors le jeu n'EST PAS perdant.
                        // On renverra donc false : la situation (jeu) n'est PAS perdante.
                        ret = false;

                    } else {
                        // génère la configuration d'essai suivante (c'est-à-dire UNE décomposition possible)
                        // à partir du jeu, si ligne = -1 il n'y a plus de décomposition possible
                        ligne = suivant(jeu, essai, ligne);
                    }
                }
            }
        }

        return ret;
    }

    /**
     * Tests succincts de la méthode estPerdante()
     */
    void testEstPerdante() {
        System.out.println("*** testEstPerdante() ***");
        System.out.println("Test des cas normaux");

        ArrayList<Integer> jeu1 = new ArrayList<Integer>();
        jeu1.add(6);
        testCasEstPerdante(jeu1, false);

        jeu1.clear();
        jeu1.add(7);
        testCasEstPerdante(jeu1, true);

        System.out.println("");
    }

    /**
     * Test d'un cas de la méthode estPerdante()
     *
     * @param jeu le plateau de jeu
     * @param res le résultat attendu par estPerdante
     */
    void testCasEstPerdante(ArrayList<Integer> jeu, boolean res) {
        System.out.print("estPerdante(" + jeu.toString() + ") \t= " + res + " \t: ");

        boolean resExec = estPerdante(jeu);
        if (res == resExec) {
            System.out.println("OK");
        } else {
            System.err.println("ERREUR");
        }
    }

    /**
     * Indique si la configuration est gagnante. Méthode qui appelle simplement
     * "estPerdante".
     *
     * @param jeu plateau de jeu
     * @return vrai si la configuration est gagnante, faux sinon
     */
    boolean estGagnante(ArrayList<Integer> jeu) {
        boolean ret = false;
        if (jeu == null) {
            System.err.println("estGagnante(): le paramètre jeu est null");
        } else {
            ret = !estPerdante(jeu);
        }
        return ret;
    }

    /**
     * Tests succincts de la méthode estGagnante()
     */
    void testEstGagnante() {
        System.out.println("*** testEstGagnante() ***");
        System.out.println("Test des cas normaux");

        ArrayList<Integer> jeu1 = new ArrayList<Integer>();
        jeu1.add(6);
        testCasEstGagnante(jeu1, true);

        jeu1.clear();
        jeu1.add(7);
        testCasEstGagnante(jeu1, false);
        System.out.println("");
    }

    /**
     * Test d'un cas de la méthode estGagnante()
     *
     * @param jeu le plateau de jeu
     * @param res le résultat attendu par estGagnante
     */
    void testCasEstGagnante(ArrayList<Integer> jeu, boolean res) {
        System.out.print("estGagnante(" + jeu.toString() + ") \t= " + res + " \t: ");

        boolean resExec = estGagnante(jeu);
        if (res == resExec) {
            System.out.println("OK");
        } else {
            System.err.println("ERREUR");
        }
    }

    /**
     * Test d'efficacité de la méthode est Gagnante
     */
    void testEstGagnanteEfficacite() {
        // variables locales
        int[] tab;
        int n, indice;
        long t1, t2, diffT;
        double n2;
        // initialisation
        n = 10;
        // multiplication de n par « 2 » à chaque tour
        // 6 expériences
        for (int i = 1; i <= 6; i++) {
            tab = new int[n];
            cpt = 0; // variable globale « long »
            t1 = System.nanoTime();
            ArrayList<Integer> jeu = new ArrayList<Integer>();
            jeu.add(n);
            boolean valeur = estGagnante(jeu);
            System.out.println("La partie est-elle gagnante ? " + valeur);
            t2 = System.nanoTime();
            diffT = (t2 - t1); // en nanosecondes
            System.out.println("Tps = " + diffT + " ns");
            System.out.println("cpt = " + (double) cpt);
            // multiplication de n par « 2 » à chaque tour
            n = n + 1;
            System.out.println("n = " + n);
        }
        System.out.println("Fin du test d'efficacité");
        System.out.println("");
    }

    /**
     * Divise en deux tas les allumettes d'une ligne de jeu (1 ligne = 1 tas).
     * Le nouveau tas se place nécessairement en fin de tableau. Le tas qui est
     * divisé diminue du nombre d'allumettes enlevées.
     *
     * @param jeu tableau des allumettes par ligne
     * @param ligne tas pour lequel les allumettes doivent être séparées
     * @param nb nombre d'allumettes RETIREE du tas (ligne) lors de la
     * séparation
     */
    void enlever(ArrayList<Integer> jeu, int ligne, int nb
    ) {
        // traitement des erreurs
        if (jeu == null) {
            System.err.println("enlever() : le paramètre jeu est null");
        } else if (ligne >= jeu.size()) {
            System.err.println("enlever() : le numéro de ligne est trop grand");
        } else if (nb >= jeu.get(ligne)) {
            System.err.println("enlever() : le nb d'allumettes à retirer est trop grand");
        } else if (nb <= 0) {
            System.err.println("enlever() : le nb d'allumettes à retirer est trop petit");
        } else if (2 * nb == jeu.get(ligne)) {
            System.err.println("enlever() : le nb d'allumettes à retirer est la moitié");
        } else {
            // nouveau tas ajouté au jeu (nécessairement en fin de tableau)
            // ce nouveau tas contient le nbre d'allumettes retirées (nb) du tas à séparer			
            jeu.add(nb);
            // le tas restant possède "nb" allumettes en moins
            jeu.set(ligne, (jeu.get(ligne) - nb));
        }
    }

    void testEnlever() {
        System.out.println("*** testEnlever() ***");
        System.out.println("Test des cas normaux");

        ArrayList<Integer> jeu1 = new ArrayList<Integer>();
        ArrayList<Integer> jeu2 = new ArrayList<Integer>();
        jeu1.add(10);
        jeu2.add(5);
        jeu2.add(5);
        int ligne1 = 0;
        int nb1 = 5;
        testCasEnlever(jeu1, ligne1, nb1, jeu2, true);

        jeu1.clear();
        jeu2.clear();
        jeu1.add(10);
        jeu2.add(9);
        jeu2.add(1);
        ligne1 = 0;
        nb1 = 1;
        testCasEnlever(jeu1, ligne1, nb1, jeu2, false);

        jeu1.clear();
        jeu2.clear();
        jeu1.add(10);
        jeu2.add(0);
        ligne1 = 0;
        nb1 = 11;
        testCasEnlever(jeu1, ligne1, nb1, jeu2, true);

        System.out.println("");

    }

    void testCasEnlever(ArrayList<Integer> jeu, int ligne, int nb, ArrayList<Integer> res, boolean casErr) {
        System.out.print("enlever(" + jeu.toString() + ", " + ligne + ", " + nb + ") \t= " + res.toString() + " \t: ");
        if (!casErr) {
            enlever(jeu, ligne, nb);
            if (jeu.equals(res)) {
                System.out.println("OK");
            } else {
                System.err.println("ERREUR");
            }
        } else {
            System.out.print("Message d'erreur attendu : ");
            enlever(jeu, ligne, nb);
        }
    }

    /**
     * Teste s'il est possible de séparer un des tas
     *
     * @param jeu plateau de jeu
     * @return vrai s'il existe au moins un tas de 3 allumettes ou plus, faux
     * sinon
     */
    boolean estPossible(ArrayList<Integer> jeu
    ) {
        boolean ret = false;
        if (jeu == null) {
            System.err.println("estPossible(): le paramètre jeu est null");
        } else {
            int i = 0;
            while (i < jeu.size() && !ret) {
                if (jeu.get(i) > 2) {
                    ret = true;
                }
                i = i + 1;
            }
        }
        return ret;
    }

    void testEstPossible() {
        System.out.println("*** testEstPossible() ***");
        System.out.println("Test des cas normaux");

        ArrayList<Integer> jeu1 = new ArrayList<Integer>();
        jeu1.add(10);
        testCasEstPossible(jeu1, true);

        jeu1.clear();
        jeu1.add(2);
        testCasEstPossible(jeu1, false);

        System.out.println("");
    }

    void testCasEstPossible(ArrayList<Integer> jeu, boolean res) {
        System.out.print("estPossible(" + jeu.toString() + ") \t= " + res + " \t: ");
        boolean resExec = estPossible(jeu);
        if (res == resExec) {
            System.out.println("OK");
        } else {
            System.err.println("ERREUR");
        }
    }

    /**
     * Crée une toute première configuration d'essai à partir du jeu
     *
     * @param jeu plateau de jeu
     * @param jeuEssai nouvelle configuration du jeu
     * @return le numéro du tas divisé en deux ou (-1) si il n'y a pas de tas
     * d'au moins 3 allumettes
     */
    int premier(ArrayList<Integer> jeu, ArrayList<Integer> jeuEssai
    ) {

        int numTas = -1; // pas de tas à séparer par défaut
        int i;

        if (jeu == null) {
            System.err.println("premier(): le paramètre jeu est null");
        } else if (!estPossible((jeu))) {
            System.err.println("premier(): aucun tas n'est divisible");
        } else if (jeuEssai == null) {
            System.err.println("premier(): le paramètre jeuEssai est null");
        } else {
            // avant la copie du jeu dans jeuEssai il y a un reset de jeuEssai 
            jeuEssai.clear(); // size = 0
            i = 0;

            // recopie case par case de jeu dans jeuEssai
            // jeuEssai est le même que le jeu avant la première configuration d'essai
            while (i < jeu.size()) {
                jeuEssai.add(jeu.get(i));
                i = i + 1;
            }

            i = 0;
            // rechercher un tas d'allumettes d'au moins 3 allumettes dans le jeu
            // sinon numTas = -1
            boolean trouve = false;
            while ((i < jeu.size()) && !trouve) {

                // si on trouve un tas d'au moins 3 allumettes
                if (jeuEssai.get(i) >= 3) {
                    trouve = true;
                    numTas = i;
                }

                i = i + 1;
            }

            // sépare le tas (case numTas) en un nouveau tas d'UNE SEULE allumette qui vient se placer en fin du tableau 
            // le tas en case numTas a diminué d'une allumette (retrait d'une allumette)
            // jeuEssai est le plateau de jeu qui fait apparaître cette séparation
            if (numTas != -1) {
                enlever(jeuEssai, numTas, 1);
            }
        }

        return numTas;
    }

    /**
     * Tests succincts de la méthode premier()
     */
    void testPremier() {
        System.out.println();
        System.out.println("*** testPremier()");

        ArrayList<Integer> jeu1 = new ArrayList<Integer>();
        jeu1.add(10);
        jeu1.add(11);
        int ligne1 = 0;
        ArrayList<Integer> res1 = new ArrayList<Integer>();
        res1.add(9);
        res1.add(11);
        res1.add(1);
        testCasPremier(jeu1, ligne1, res1);
    }

    /**
     * Test un cas de la méthode testPremier
     *
     * @param jeu le plateau de jeu
     * @param ligne le numéro du tas séparé en premier
     * @param res le plateau de jeu après une première séparation
     */
    void testCasPremier(ArrayList<Integer> jeu, int ligne, ArrayList<Integer> res
    ) {
        // Arrange
        System.out.print("premier (" + jeu.toString() + ") : ");
        ArrayList<Integer> jeuEssai = new ArrayList<Integer>();
        // Act
        int noLigne = premier(jeu, jeuEssai);
        // Assert
        System.out.println("\nnoLigne = " + noLigne + " jeuEssai = " + jeuEssai.toString());
        boolean egaliteJeux = jeuEssai.equals(res);
        if (egaliteJeux && noLigne == ligne) {
            System.out.println("OK\n");
        } else {
            System.err.println("ERREUR\n");
        }
    }

    /**
     * Génère la configuration d'essai suivante (c'est-à-dire UNE décomposition
     * possible)
     *
     * @param jeu plateau de jeu
     * @param jeuEssai configuration d'essai du jeu après séparation
     * @param ligne le numéro du tas qui est le dernier à avoir été séparé
     * @return le numéro du tas divisé en deux pour la nouvelle configuration,
     * -1 si plus aucune décomposition n'est possible
     */
    int suivant(ArrayList<Integer> jeu, ArrayList<Integer> jeuEssai,
            int ligne
    ) {

        // System.out.println("suivant(" + jeu.toString() + ", " +jeuEssai.toString() +
        // ", " + ligne + ") = ");
        int numTas = -1; // par défaut il n'y a plus de décomposition possible

        int i = 0;
        // traitement des erreurs
        if (jeu == null) {
            System.err.println("suivant(): le paramètre jeu est null");
        } else if (jeuEssai == null) {
            System.err.println("suivant() : le paramètre jeuEssai est null");
        } else if (ligne >= jeu.size()) {
            System.err.println("suivant(): le paramètre ligne est trop grand");
        } else {

            int nbAllumEnLigne = jeuEssai.get(ligne);
            int nbAllDernCase = jeuEssai.get(jeuEssai.size() - 1);

            // si sur la même ligne (passée en paramètre) on peut encore retirer des allumettes,
            // c-à-d si l'écart entre le nombre d'allumettes sur cette ligne et
            // le nombre d'allumettes en fin de tableau est > 2, alors on retire encore
            // 1 allumette sur cette ligne et on ajoute 1 allumette en dernière case		
            if ((nbAllumEnLigne - nbAllDernCase) > 2) {
                jeuEssai.set(ligne, (nbAllumEnLigne - 1));
                jeuEssai.set(jeuEssai.size() - 1, (nbAllDernCase + 1));
                numTas = ligne;
            } // sinon il faut examiner le tas (ligne) suivant du jeu pour éventuellement le décomposer
            // on recrée une nouvelle configuration d'essai identique au plateau de jeu
            else {
                // copie du jeu dans JeuEssai
                jeuEssai.clear();
                for (i = 0; i < jeu.size(); i++) {
                    jeuEssai.add(jeu.get(i));
                }

                boolean separation = false;
                i = ligne + 1; // tas suivant
                // si il y a encore un tas et qu'il contient au moins 3 allumettes
                // alors on effectue une première séparation en enlevant 1 allumette
                while (i < jeuEssai.size() && !separation) {
                    // le tas doit faire minimum 3 allumettes
                    if (jeu.get(i) > 2) {
                        separation = true;
                        // on commence par enlever 1 allumette à ce tas
                        enlever(jeuEssai, i, 1);
                        numTas = i;
                    } else {
                        i = i + 1;
                    }
                }
            }
        }

        return numTas;
    }

    /**
     * Tests succincts de la méthode suivant()
     */
    void testSuivant() {
        System.out.println();
        System.out.println("*** testSuivant() ****");

        int ligne1 = 0;
        int resLigne1 = 0;
        ArrayList<Integer> jeu1 = new ArrayList<Integer>();
        jeu1.add(10);
        ArrayList<Integer> jeuEssai1 = new ArrayList<Integer>();
        jeuEssai1.add(9);
        jeuEssai1.add(1);
        ArrayList<Integer> res1 = new ArrayList<Integer>();
        res1.add(8);
        res1.add(2);
        testCasSuivant(jeu1, jeuEssai1, ligne1, res1, resLigne1);

        int ligne2 = 0;
        int resLigne2 = -1;
        ArrayList<Integer> jeu2 = new ArrayList<Integer>();
        jeu2.add(10);
        ArrayList<Integer> jeuEssai2 = new ArrayList<Integer>();
        jeuEssai2.add(6);
        jeuEssai2.add(4);
        ArrayList<Integer> res2 = new ArrayList<Integer>();
        res2.add(10);
        testCasSuivant(jeu2, jeuEssai2, ligne2, res2, resLigne2);

        int ligne3 = 1;
        int resLigne3 = 1;
        ArrayList<Integer> jeu3 = new ArrayList<Integer>();
        jeu3.add(4);
        jeu3.add(6);
        jeu3.add(3);
        ArrayList<Integer> jeuEssai3 = new ArrayList<Integer>();
        jeuEssai3.add(4);
        jeuEssai3.add(5);
        jeuEssai3.add(3);
        jeuEssai3.add(1);
        ArrayList<Integer> res3 = new ArrayList<Integer>();
        res3.add(4);
        res3.add(4);
        res3.add(3);
        res3.add(2);
        testCasSuivant(jeu3, jeuEssai3, ligne3, res3, resLigne3);

    }

    /**
     * Test un cas de la méthode suivant
     *
     * @param jeu le plateau de jeu
     * @param jeuEssai le plateau de jeu obtenu après avoir séparé un tas
     * @param ligne le numéro du tas qui est le dernier à avoir été séparé
     * @param resJeu est le jeuEssai attendu après séparation
     * @param resLigne est le numéro attendu du tas qui est séparé
     */
    void testCasSuivant(ArrayList<Integer> jeu, ArrayList<Integer> jeuEssai,
            int ligne, ArrayList<Integer> resJeu,
            int resLigne
    ) {
        // Arrange
        System.out.print("suivant (" + jeu.toString() + ", " + jeuEssai.toString() + ", " + ligne + ") : ");
        // Act
        int noLigne = suivant(jeu, jeuEssai, ligne);
        // Assert
        System.out.println("\nnoLigne = " + noLigne + " jeuEssai = " + jeuEssai.toString());
        boolean egaliteJeux = jeuEssai.equals(resJeu);
        if (egaliteJeux && noLigne == resLigne) {
            System.out.println("OK\n");
        } else {
            System.err.println("ERREUR\n");
        }
    }

    /**
     * Affiche le plateau de jeu
     *
     * @param jeu plateau de jeu
     */
    void afficher(ArrayList<Integer> jeu) {
        if (jeu == null) {
            System.err.println("afficher(): le paramètre jeu est null");
        } else {
            for (int i = 0; i < jeu.size(); i++) {
                for (int j = 0; j < jeu.get(i); j++) {
                    System.out.print("| ");
                }
                System.out.print("    ");
            }
        }
        System.out.println("");
    }

    void testAfficher() {
        System.out.println("*** testAfficher() ***");
        System.out.println("Test des cas normaux");

        ArrayList<Integer> jeu1 = new ArrayList<Integer>();
        jeu1.add(10);
        jeu1.add(5);
        testCasAfficher(jeu1);

        jeu1.clear();
        jeu1.add(5);
        testCasAfficher(jeu1);

        System.out.println("");
    }

    void testCasAfficher(ArrayList<Integer> jeu) {
        System.out.print("afficher(" + jeu.toString() + ") : ");
        System.out.println("Test visuel de la méthode afficher : ");
        afficher(jeu);

    }
}
