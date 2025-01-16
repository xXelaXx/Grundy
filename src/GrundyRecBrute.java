
import java.util.ArrayList;

/**
 * Grundy game, this program only contains methods to test jouerGagnant(). This
 * version is raw without any improvements.
 *
 * @author Arthur Le Gall, Alexis Baron
 */
class GrundyRecBrute {

    /**
     * Counter for the number of recursive calls
     */
    long cpt;

    /**
     * List of statistics
     */
    ArrayList<Integer> statistiques = new ArrayList<Integer>();

    /**
     * List of statistics
     */
    ArrayList<Integer> statistiques_temps = new ArrayList<Integer>();

    /**
     * Main method of the program, starts the game
     */
    void principal() {
        testSuivant();
        testEstPerdante();
        testAfficher();
        testPremier();
        testJouerGagnant();
        testLeJeu();
        testEstGagnante();
        testEnlever();
        testEstPossible();

        testEstGagnanteEfficacite();
        // leJeu(5);
    }

    /**
     * Plays the Grundy game
     *
     * @param n number of matches in the game
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

        while (estPossible(jeu)) {

            if (player % 2 == 0) {
                // Player
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
                // Computer
                System.out.println("La machine est en train de jouer...");
                if (!jouerGagnant(jeu)) {
                    boolean found = false;
                    // If the machine cannot win, it plays a random move
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
     * Brief tests for the method leJeu()
     */
    void testLeJeu() {
        System.out.println("*** testLeJeu() ***");

        ArrayList<Integer> jeu1 = new ArrayList<Integer>();

        testCasLeJeu(1);

        testCasLeJeu(2);

        //! testCasLeJeu(5);
        System.out.println("");
    }

    /**
     * Test a case of the method leJeu()
     *
     * @param nb number of matches in the game
     */
    void testCasLeJeu(int nb) {
        System.out.print("leJeu(" + nb + ") \t= Test visuel de la méthode leJeu \t: ");
        leJeu(nb);
    }

    /**
     * Plays the winning move if it exists
     *
     * @param jeu game board
     * @return true if there is a winning move, false otherwise
     */
    boolean jouerGagnant(ArrayList<Integer> jeu
    ) {

        boolean gagnant = false;

        if (jeu == null) {
            System.err.println("jouerGagnant(): le paramètre jeu est null");
        } else {
            ArrayList<Integer> essai = new ArrayList<Integer>();
            // Perform an initial decomposition from the game.
            // This first decomposition of the game is recorded in essai.
            // ligne is the index of the ArrayList (starting from zero) that
            // stores the pile (number of matches) that has been decomposed.
            int ligne = premier(jeu, essai);

            // Implement rule number 2
            // A situation (or position) is considered winning for the machine if there is AT LEAST ONE decomposition
            // (i.e., ONE action that consists of decomposing a pile into 2 unequal piles) that is losing for the opponent. This
            // losing decomposition will obviously be chosen by the machine.
            while (ligne != -1 && !gagnant) {
                // estPerdante is recursive
                if (estPerdante(essai)) {
                    // estPerdante (for the opponent) is true ===> Bingo essai is the decomposition chosen by the machine, which is then
                    // certain to win!!
                    jeu.clear();
                    gagnant = true;
                    // essai is copied into jeu because essai is the new game situation after the machine has played (winning).
                    for (int i = 0; i < essai.size(); i++) {
                        jeu.add(essai.get(i));
                    }
                } else {
                    // estPerdante is false ===> the machine tries another decomposition by calling "suivant".
                    // If, after executing suivant, ligne is (-1), then there are no more possible decompositions from the game (and we exit the while loop).
                    // In other words: the machine has NOT found a winning decomposition from the game.
                    ligne = suivant(jeu, essai, ligne);
                }
            }
        }

        return gagnant;
    }

    /**
     * Brief tests for the method jouerGagnant()
     */
    void testJouerGagnant() {
        System.out.println();
        System.out.println("*** testJouerGagnant() ***");

        ArrayList<Integer> jeu1 = new ArrayList<Integer>();
        jeu1.add(6);
        ArrayList<Integer> resJeu1 = new ArrayList<Integer>();
        resJeu1.add(4);
        resJeu1.add(2);

        testCasJouerGagnant(jeu1, resJeu1, true);

    }

    /**
     * Test a case of the method jouerGagnant()
     *
     * @param jeu the game board
     * @param resJeu the game board after playing the winning move
     * @param res the expected result from jouerGagnant
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
     * RECURSIVE method that indicates if the configuration (of the current game
     * or test game) is losing. This method is used by the machine to know if
     * the opponent can lose (100%).
     *
     * @param jeu current game board (the state of the game at a certain moment
     * during the game)
     * @return true if the configuration (of the game) is losing, false
     * otherwise
     */
    boolean estPerdante(ArrayList<Integer> jeu
    ) {

        boolean ret = true;

        if (jeu == null) {
            System.err.println("estPerdante(): le paramètre jeu est null");
        } else {

            if (!estPossible(jeu)) {
                ret = true;
                // creation of a test game that will examine all possible decompositions
                // from the game
            } else {
                ArrayList<Integer> essai = new ArrayList<Integer>(); // size = 0 !  ArrayList<Integer> essai = new ArrayList<Integer>(); // size = 0 !
                // first decomposition: remove 1 matchstick from the first pile that has
                // at least 3 matchsticks, ligne = -1 means there are no more piles with at least 3 matchsticks  
                int ligne = premier(jeu, essai);

                while ((ligne != -1) && ret) {
                    cpt++;
                    // Implement rule number 1
                    // A situation (or position) is considered losing if and only if ALL its possible decompositions
                    // (i.e., ALL actions that consist of decomposing a pile into 2 unequal piles) are ALL winning
                    // (for the opponent).
                    // The call to "estPerdante" is RECURSIVE.
                    // If "estPerdante(essai)" is true, it is equivalent to "estGagnante" being false, so the decomposition
                    // essai is not winning, we exit the while loop and return false.
                    if (estPerdante(essai) == true) {
                        // If ONE SINGLE decomposition (from the game) is losing (for the opponent), then the game is NOT losing.
                        // Therefore, we will return false: the situation (game) is NOT losing.  
                        ret = false;
                    } else {
                        // generates the next trial configuration (i.e., ONE possible decomposition)
                        // from the game, if ligne = -1 there are no more possible decompositions 
                        ligne = suivant(jeu, essai, ligne);
                    }
                }
            }
        }

        return ret;
    }

    /**
     * Brief tests for the method estPerdante()
     */
    void testEstPerdante() {
        System.out.println("*** testEstPerdante() ***");

        ArrayList<Integer> jeu1 = new ArrayList<Integer>();
        jeu1.add(6);
        testCasEstPerdante(jeu1, false);

        jeu1.clear();
        jeu1.add(7);
        testCasEstPerdante(jeu1, true);

        System.out.println("");
    }

    /**
     * Test a case of the method estPerdante()
     *
     * @param jeu the game board
     * @param res the expected result from estPerdante
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
     * Indicates if the configuration is winning. Method that simply calls
     * "estPerdante".
     *
     * @param jeu game board
     * @return true if the configuration is winning, false otherwise
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
     * Brief tests for the method estGagnante()
     */
    void testEstGagnante() {
        System.out.println("*** testEstGagnante() ***");

        ArrayList<Integer> jeu1 = new ArrayList<Integer>();
        jeu1.add(6);
        testCasEstGagnante(jeu1, true);

        jeu1.clear();
        jeu1.add(7);
        testCasEstGagnante(jeu1, false);
        System.out.println("");
    }

    /**
     * Test a case of the method estGagnante()
     *
     * @param jeu the game board
     * @param res the expected result from estGagnante
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
     * Efficiency test for the method estGagnante
     */
    void testEstGagnanteEfficacite() {

        int[] tab;
        int n, indice;
        long t1, t2, diffT;
        double n2;

        n = 3;

        System.out.println("*** Test d'efficacité de la méthode estGagnante ***");

        for (int i = 1; i <= 20; i++) {
            tab = new int[n];
            cpt = 0;
            t1 = System.nanoTime();
            ArrayList<Integer> jeu = new ArrayList<Integer>();
            jeu.add(n);
            boolean valeur = estGagnante(jeu);
            System.out.println("La partie est-elle gagnante ? " + valeur + " pour n = " + n);
            t2 = System.nanoTime();
            diffT = (t2 - t1);

            Integer c = (int) cpt;

            statistiques.add(c);
            statistiques_temps.add((int) diffT);

            System.out.println("Tps = " + diffT + " ns");
            System.out.println("cpt = " + cpt);
            System.out.println("");

            n = n + 1;
        }
        System.out.println("Fin du test d'efficacité");
        System.out.println("");

        System.out.println("Statistiques");
        System.out.println("Liste des CPT");
        System.out.print("[");
        for (int i = 0; i < statistiques.size(); i++) {
            System.out.print(statistiques.get(i) + (i < statistiques.size() - 1 ? "," : ""));
        }
        System.out.println("]");

        System.out.println("Liste des temps");
        System.out.print("[");
        for (int i = 0; i < statistiques_temps.size(); i++) {
            System.out.print(statistiques_temps.get(i) + (i < statistiques_temps.size() - 1 ? "," : ""));
        }
        System.out.println("]");

        System.out.println("");

    }

    /**
     * Splits a pile of matches into two piles. The new pile is always added at
     * the end of the list. The pile that is split decreases by the number of
     * matches removed.
     *
     * @param jeu list of piles of matches
     * @param ligne index of the pile to be split
     * @param nb number of matches removed from the pile during the split
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
        } else if (nb > 2) {
            System.err.println("enlever() : le nb d'allumettes à retirer est trop grand");
        } else if (nb <= 0) {
            System.err.println("enlever() : le nb d'allumettes à retirer est trop petit");
        } else if (2 * nb == jeu.get(ligne)) {
            System.err.println("enlever() : le nb d'allumettes à retirer est la moitié");
        } else {

            jeu.add(nb);

            jeu.set(ligne, (jeu.get(ligne) - nb));
        }
    }

    /**
     * Brief tests for the method enlever()
     */
    void testEnlever() {
        System.out.println("*** testEnlever() ***");

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

    /**
     * Test a case of the method enlever()
     *
     * @param jeu the game board
     * @param ligne the index of the pile to be split
     * @param nb the number of matches removed from the pile during the split
     * @param res the expected game board after the split
     * @param casErr true if an error is expected, false otherwise
     */
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
     * Tests if it is possible to split one of the piles
     *
     * @param jeu game board
     * @return true if there is at least one pile with 3 or more matches, false
     * otherwise
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

    /**
     * Brief tests for the method estPossible()
     */
    void testEstPossible() {
        System.out.println("*** testEstPossible() ***");

        ArrayList<Integer> jeu1 = new ArrayList<Integer>();
        jeu1.add(10);
        testCasEstPossible(jeu1, true);

        jeu1.clear();
        jeu1.add(2);
        testCasEstPossible(jeu1, false);

        System.out.println("");
    }

    /**
     * Test a case of the method estPossible()
     *
     * @param jeu the game board
     * @param res the expected result from estPossible
     */
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
     * Creates an initial test configuration from the game
     *
     * @param jeu game board
     * @param jeuEssai new game configuration
     * @return the index of the pile split into two or (-1) if there is no pile
     * with at least 3 matches
     */
    int premier(ArrayList<Integer> jeu, ArrayList<Integer> jeuEssai
    ) {

        int numTas = -1;
        int i;

        if (jeu == null) {
            System.err.println("premier(): le paramètre jeu est null");
        } else if (!estPossible((jeu))) {
            System.err.println("premier(): aucun tas n'est divisible");
        } else if (jeuEssai == null) {
            System.err.println("premier(): le paramètre jeuEssai est null");
        } else {

            jeuEssai.clear(); // size = 0
            i = 0;

            while (i < jeu.size()) {
                jeuEssai.add(jeu.get(i));
                i = i + 1;
            }

            i = 0;

            boolean trouve = false;
            while ((i < jeu.size()) && !trouve) {

                if (jeuEssai.get(i) >= 3) {
                    trouve = true;
                    numTas = i;
                }

                i = i + 1;
            }
            // Split the pile (at index numTas) into a new pile with ONE matchstick, which is added at the end of the list
            // The pile at index numTas is decreased by one matchstick (removal of one matchstick)
            // jeuEssai is the game board that reflects this split  
            if (numTas != -1) {
                enlever(jeuEssai, numTas, 1);
            }
        }

        return numTas;
    }

    /**
     * Brief tests for the method premier()
     */
    void testPremier() {
        System.out.println();
        System.out.println("*** testPremier() ***");

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
     * Test a case of the method premier
     *
     * @param jeu the game board
     * @param ligne the index of the pile split first
     * @param res the game board after the first split
     */
    void testCasPremier(ArrayList<Integer> jeu, int ligne, ArrayList<Integer> res
    ) {

        System.out.print("premier (" + jeu.toString() + ") \t= ");
        ArrayList<Integer> jeuEssai = new ArrayList<Integer>();

        int noLigne = premier(jeu, jeuEssai);

        System.out.print(noLigne + " \t: ");
        boolean egaliteJeux = jeuEssai.equals(res);
        if (egaliteJeux && noLigne == ligne) {
            System.out.println("OK");
        } else {
            System.err.println("ERREUR");
        }
    }

    /**
     * Generates the next trial configuration (i.e., ONE possible decomposition)
     *
     * @param jeu game board
     * @param jeuEssai trial configuration of the game after splitting
     * @param ligne the index of the pile that was last split
     * @return the index of the pile split into two for the new configuration,
     * -1 if no more decompositions are possible
     */
    int suivant(ArrayList<Integer> jeu, ArrayList<Integer> jeuEssai,
            int ligne
    ) {

        int numTas = -1;

        int i = 0;

        if (jeu == null) {
            System.err.println("suivant(): le paramètre jeu est null");
        } else if (jeuEssai == null) {
            System.err.println("suivant() : le paramètre jeuEssai est null");
        } else if (ligne >= jeu.size()) {
            System.err.println("suivant(): le paramètre ligne est trop grand");
        } else {

            int nbAllumEnLigne = jeuEssai.get(ligne);
            int nbAllDernCase = jeuEssai.get(jeuEssai.size() - 1);
            // if on the same line (passed as a parameter) we can still remove matches,
            // i.e., if the difference between the number of matches on this line and
            // the number of matches at the end of the list is > 2, then we remove
            // 1 match from this line and add 1 match to the last position  
            if ((nbAllumEnLigne - nbAllDernCase) > 2) {
                jeuEssai.set(ligne, (nbAllumEnLigne - 1));
                jeuEssai.set(jeuEssai.size() - 1, (nbAllDernCase + 1));
                numTas = ligne;
            } else {

                jeuEssai.clear();
                for (i = 0; i < jeu.size(); i++) {
                    jeuEssai.add(jeu.get(i));
                }

                boolean separation = false;
                i = ligne + 1; // next pile
                // if there is still a pile and it contains at least 3 matches
                // then we perform a first split by removing 1 match
                while (i < jeuEssai.size() && !separation) {
                    if (jeu.get(i) > 2) {
                        separation = true;
                        enlever(jeuEssai, i, 1);
                        numTas = i;
                    } else {
                        i = i + 1;
                    }
                }
                while (i < jeuEssai.size() && !separation) {

                    if (jeu.get(i) > 2) {
                        separation = true;

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
     * Brief tests for the method suivant()
     */
    void testSuivant() {
        System.out.println();
        System.out.println("*** testSuivant() ***");

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

        System.out.println("");
    }

    /**
     * Test a case of the method suivant
     *
     * @param jeu the game board
     * @param jeuEssai the game board obtained after splitting a pile
     * @param ligne the index of the pile that was last split
     * @param resJeu the expected game board after splitting
     * @param resLigne the expected index of the pile that is split
     */
    void testCasSuivant(ArrayList<Integer> jeu, ArrayList<Integer> jeuEssai,
            int ligne, ArrayList<Integer> resJeu,
            int resLigne
    ) {

        System.out.print("suivant(" + jeu.toString() + ", " + jeuEssai.toString() + ", " + ligne + ") \t= ");

        int noLigne = suivant(jeu, jeuEssai, ligne);

        System.out.print(noLigne + "\t: ");
        boolean egaliteJeux = jeuEssai.equals(resJeu);
        if (egaliteJeux && noLigne == resLigne) {
            System.out.println("OK");
        } else {
            System.err.println("ERREUR");
        }
    }

    /**
     * Displays the game board
     *
     * @param jeu game board
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

    /**
     * Brief tests for the method afficher()
     */
    void testAfficher() {
        System.out.println("*** testAfficher() ***");

        ArrayList<Integer> jeu1 = new ArrayList<Integer>();
        jeu1.add(10);
        jeu1.add(5);
        testCasAfficher(jeu1);

        jeu1.clear();
        jeu1.add(5);
        testCasAfficher(jeu1);

    }

    /**
     * Test a case of the method afficher()
     *
     * @param jeu the game board
     */
    void testCasAfficher(ArrayList<Integer> jeu) {
        System.out.print("afficher(" + jeu.toString() + ") : ");
        System.out.print("Test visuel de la méthode afficher : ");
        afficher(jeu);

    }

}
