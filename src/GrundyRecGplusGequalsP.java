
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Grundy game - Version 4. This version optimizes efficiency by applying
 * additional rules for combining winning and losing piles: - Winning + Winning
 * (same type) = Losing - Winning + Winning (different types) = Winning
 *
 * Neutral configurations and redundant pairs are removed during normalization.
 * Winning and losing positions are stored in `posGagnantes` and `posPerdantes`.
 *
 * Builds on previous versions with enhanced type-based simplifications for
 * faster calculations.
 *
 * @author Arthur Le Gall, Alexis Baron
 */
class GrundyRecGplusGequalsP {

    long cpt;
    ArrayList<ArrayList<Integer>> posPerdantes = new ArrayList<ArrayList<Integer>>();
    ArrayList<ArrayList<Integer>> posGagnantes = new ArrayList<ArrayList<Integer>>();
    ArrayList<Integer> statistiques = new ArrayList<Integer>();

    /**
     * List of statistics
     */
    ArrayList<Integer> statistiques_temps = new ArrayList<Integer>();

    int type[] = {0, 0, 0, 1, 0, 2, 1, 0, 2, 1, 0, 2, 1, 3, 2, 1, 3, 2, 4, 3, 0, 4, 3, 0, 4, 3, 0, 4, 1, 2, 3, 1, 2, 4, 1, 2, 4, 1, 2, 4, 1, 5, 4, 1, 5, 4, 1, 5, 4, 1, 0};

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
        testNormaliser();
        testEstConnueGagnante();
        testEstConnuePerdante();
        testAdditionEstGagnante();
        // // leJeu(5);
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

            int ligne = premier(jeu, essai);

            while (ligne != -1 && !gagnant) {

                if (estPerdante(essai)) {
                    jeu.clear();
                    gagnant = true;

                    for (int i = 0; i < essai.size(); i++) {
                        jeu.add(essai.get(i));
                    }
                } else {
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
            } else if (estConnuePerdante(normaliser(jeu))) {
                return true;
            } else if (estConnueGagnante(normaliser(jeu))) {
                return false;
            } else {

                ArrayList<Integer> essai = new ArrayList<Integer>(); // size = 0 !

                int ligne = premier(jeu, essai);

                while ((ligne != -1) && ret) {
                    cpt++;

                    if (estPerdante(essai)) {
                        ret = false;

                    } else {

                        ligne = suivant(jeu, essai, ligne);
                    }
                }
            }
        }

        if (ret) {
            posPerdantes.add(normaliser(jeu));
        } else {
            posGagnantes.add(normaliser(jeu));
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
            posPerdantes.clear();
            posGagnantes.clear();
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
     * Splits a pile of matches into two piles. The new pile is added at the end
     * of the list. The original pile is reduced by the number of matches
     * removed.
     *
     * @param jeu list of piles of matches
     * @param ligne index of the pile to be split
     * @param nb number of matches removed from the pile during the split
     */
    void enlever(ArrayList<Integer> jeu, int ligne, int nb
    ) {

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

            jeuEssai.clear();
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
     * Returns the index of the pile to be split into two for the new
     *
     * @param jeu game board
     * @param jeuEssai test configuration of the game after splitting
     * @param ligne the index of the pile that was last split
     * @return the index of the pile split into two for the new configuration,
     * -1 if no further decomposition is possible
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
                i = ligne + 1;

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

    /**
     * Indicates if a configuration is known to be losing
     *
     * @param jeu game board
     * @return true if the configuration is known to be losing, false otherwise
     */
    boolean estConnuePerdante(ArrayList<Integer> jeu) {
        return posPerdantes.contains(jeu);
    }

    /**
     * Brief tests for the method estConnuePerdante()
     */
    void testEstConnuePerdante() {
        System.out.println("*** testEstConnuePerdante()***");

        ArrayList<Integer> jeu1 = new ArrayList<>();
        jeu1.add(3);
        posPerdantes.add(jeu1);

        testCasEstConnuePerdante(jeu1, true);

        posPerdantes.clear();

        ArrayList<Integer> jeu2 = new ArrayList<>();
        jeu2.add(5);

        testCasEstConnuePerdante(jeu2, false);

        System.out.println("");

    }

    /**
     * Test a case of the method estConnuePerdante()
     *
     * @param jeu the game board
     * @param attendu the expected result from estConnuePerdante
     */
    void testCasEstConnuePerdante(ArrayList<Integer> jeu, boolean attendu) {
        System.out.print("estConnuePerdante(" + jeu + ") = " + attendu + " : ");
        boolean resultat = estConnuePerdante(jeu);
        if (resultat == attendu) {
            System.out.println("OK");
        } else {
            System.err.println("ERREUR");
        }
    }

    /**
     * Indicates if a configuration is known to be winning
     *
     * @param jeu game board
     * @return true if the configuration is known to be winning, false otherwise
     */
    boolean estConnueGagnante(ArrayList<Integer> jeu) {
        return posGagnantes.contains(jeu);
    }

    /**
     * Brief tests for the method estConnueGagnante()
     */
    void testEstConnueGagnante() {
        System.out.println("*** testEstConnueGagnante() ***");

        ArrayList<Integer> jeu1 = new ArrayList<>();
        jeu1.add(4);
        posGagnantes.add(jeu1);

        testCasEstConnueGagnante(jeu1, true);

        posGagnantes.clear();

        ArrayList<Integer> jeu2 = new ArrayList<>();
        jeu2.add(6);

        testCasEstConnueGagnante(jeu2, false);

        System.out.println("");
    }

    /**
     * Test a case of the method estConnueGagnante()
     *
     * @param jeu the game board
     * @param attendu the expected result from estConnueGagnante
     */
    void testCasEstConnueGagnante(ArrayList<Integer> jeu, boolean attendu) {
        System.out.print("estConnueGagnante(" + jeu + ") = " + attendu + " : ");
        boolean resultat = estConnueGagnante(jeu);
        if (resultat == attendu) {
            System.out.println("OK");
        } else {
            System.err.println("ERREUR");
        }
    }

    /**
     * Normalizes the game board by removing losing piles and simplifying
     * winning pairs. Winning pairs of the same type are replaced with a losing
     * configuration, and losing piles are removed entirely.
     *
     * @param jeu game board
     * @return the normalized game board
     */
    ArrayList<Integer> normaliser(ArrayList<Integer> jeu) {
        ArrayList<Integer> jeuNormalise = new ArrayList<Integer>();
        for (int i = 0; i < jeu.size(); i++) {
            if (jeu.get(i) > 2) {
                ArrayList<Integer> jeuNormaliseTemp = new ArrayList<Integer>();

                jeuNormaliseTemp.add(jeu.get(i));

                if (estConnueGagnante(jeuNormaliseTemp) || !estConnuePerdante(jeuNormaliseTemp)) {

                    /*
                     * - If [GT1 GT2 NOTHING] then [GT1 GT2 NOTHING]
                     * - If [GT1 GT1 GT3 NOTHING] then [GT1 GT1 GT3 NOTHING] OR [GT1 GT3 NOTHING]
                     * GT1, GT3; GT1, GT1; GT1, GT3
                     *
                     * GT1 GT1 GT1 X: X OR X GT1
                     * You compare 1 to 1 but you remove, so you can't compare 1 to 1
                     * If you have xy that doesn't work but xz that works, then in our final table we have xz or just z
                     *
                     * List of all possible winners and compare them one by one
                     *
                     * Loser = remove both
                     * Winner = remove nothing
                     *
                     * x loses with y but wins with z =
                     *
                     * AS SOON AS WE FIND LOSING DUO (winner + winner of the same type)
                     */
                    jeuNormalise.add(jeuNormaliseTemp.get(0));
                }
            }
        }

        for (int j = 0; j < jeuNormalise.size(); j++) {
            for (int k = j + 1; k < jeuNormalise.size(); k++) {
                if (j != k) {
                    if (!additionEstGagnante(jeuNormalise.get(j), jeuNormalise.get(k))) {
                        jeuNormalise.remove(jeuNormalise.get(j));
                        jeuNormalise.remove(jeuNormalise.get(k - 1));
                        j = 0;

                    }
                }
            }
        }

        Collections.sort(jeuNormalise);
        return jeuNormalise;
    }

    /**
     * Brief tests for the method normaliser()
     */
    void testNormaliser() {
        System.out.println("*** testNormaliser() ***");

        // Case 1: Normal test with defined winning and losing positions
        ArrayList<Integer> jeu1 = new ArrayList<>();
        jeu1.add(3);
        jeu1.add(5);
        jeu1.add(13);

        ArrayList<Integer> attendu1 = new ArrayList<>();
        attendu1.add(3);
        attendu1.add(5);
        attendu1.add(13);

        testCasNormaliser(jeu1, attendu1);

        // Case 2: No pile is either winning or losing
        ArrayList<Integer> jeu2 = new ArrayList<>();
        jeu2.add(11);
        jeu2.add(9);
        jeu2.add(15);

        ArrayList<Integer> attendu2 = new ArrayList<>();
        attendu2.add(11);

        testCasNormaliser(jeu2, attendu2);

        System.out.println("");
    }

    /**
     * Test a case of the method normaliser()
     *
     * @param jeu the game board
     * @param attendu the expected normalized game board
     */
    void testCasNormaliser(ArrayList<Integer> jeu, ArrayList<Integer> attendu) {
        System.out.print("normaliser(" + jeu + ") = " + attendu + " : ");
        ArrayList<Integer> resultat = normaliser(jeu);
        if (resultat.equals(attendu)) {
            System.out.println("OK");
        } else {
            System.err.println("ERREUR");
        }
    }

    /**
     * Indicates if the addition of two piles is winning
     */
    boolean additionEstGagnante(int tasnum1, int tasnum2) {
        boolean res;
        if (type[tasnum1] == type[tasnum2]) {
            res = false;

        } else {
            res = true;
        }
        return res;
    }

    /**
     * Brief tests for the method additionEstGagnante()
     */
    void testAdditionEstGagnante() {
        System.out.println("*** testAdditionEstGagnante() ***");

        // Case 1: Piles of the same type
        testCasAdditionEstGagnante(9, 15, false);

        // Case 2: Piles of different types
        testCasAdditionEstGagnante(5, 41, true);

        System.out.println("");
    }

    /**
     * Test a case of the method additionEstGagnante()
     *
     * @param tasnum1 the first pile
     * @param tasnum2 the second pile
     * @param attendu the expected result from additionEstGagnante
     */
    void testCasAdditionEstGagnante(int tasnum1, int tasnum2, boolean attendu) {
        System.out.print("additionEstGagnante(" + tasnum1 + ", " + tasnum2 + ") = " + attendu + " : ");
        boolean resultat = additionEstGagnante(tasnum1, tasnum2);
        if (resultat == attendu) {
            System.out.println("OK");
        } else {
            System.err.println("ERREUR");
        }
    }

}
