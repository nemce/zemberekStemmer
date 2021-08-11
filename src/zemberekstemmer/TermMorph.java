/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zemberekstemmer;

import java.util.List;
import zemberek.morphology.TurkishMorphology;
import zemberek.morphology.analysis.SingleAnalysis;
import zemberek.morphology.analysis.WordAnalysis;
import zemberek.morphology.morphotactics.Morpheme;

/**
 *
 * @author turgut
 */
public class TermMorph {

    String term;
    String lemma;
    String morph_id;
    boolean unable = false;

    public void setTerm(String term) {
        this.term = term;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    public void setMorph_id(String morph_id) {
        this.morph_id = morph_id;
    }

    public void setUnable(boolean unable) {
        this.unable = unable;
    }

    public String getTerm() {
        return term;
    }

    public String getMorph_id() {
        return morph_id;
    }

    public boolean isUnable() {
        return unable;
    }

    public String getLemma() {
        return lemma;
    }

    public TermMorph(String term) {
        this.term = term;
    }

    public void processTerm(TurkishMorphology morphology, String word) {

        //termMorph termMorph = new termMorph(word);
        try {
            WordAnalysis results = morphology.analyze(word);
            for (SingleAnalysis result : results) {

                List<SingleAnalysis.MorphemeData> surfaces = result.getMorphemeDataList();
                this.lemma = result.getStem();

                this.morph_id = surfaces.get(0).morpheme.id;
                this.lemma = result.getDictionaryItem().lemma;

                for (int i = 1; i < surfaces.size(); i++) {
                    SingleAnalysis.MorphemeData s = surfaces.get(i);
                    Morpheme morpheme = s.morpheme;
                    if (s.morpheme.id == "Unable") {
                        this.unable = true;
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("Error:" + word);
            System.out.print(ex.getStackTrace());
        }
    }

}
