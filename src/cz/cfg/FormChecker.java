/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cfg;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author NICKT
 */
public class FormChecker {

    public Set<String> buildNe(ContextFreeGrammar cfg) {
        Set<String> ne = new HashSet<String>();
        RegExpMaker regexp = new RegExpMaker();
        for (String n : cfg.getRules().keySet()) { //pro kaĹľdĂ˝ neterminĂˇl n z N
            for (String r : cfg.getRules().get(n)) { //pro kaĹľdĂ© pravidlo neterminĂˇlu n
                if (r.matches(regexp.regexOnlyTerminals(cfg.getTerminals()))) { //pokud r obsahuje jen terminĂˇly, pĹ™idej n do ne
                    ne.add(n);
                    break; //StaÄŤĂ­ nĂˇm najĂ­t 1 pravidlo danĂ©ho neterminĂˇlu n, kterĂ© podmĂ­nku splĹ�uje
                }
            }
        }

        if (!ne.isEmpty()) { //pokud nenĂ­ Ne prĂˇzdnĂˇ, pokraÄŤuj
            boolean insertedIntoNe = true;
            while (insertedIntoNe) { //dokud bylo vklĂˇdĂˇno do ne, tak opakuj:
                insertedIntoNe = false;
                Set<String> toVisit = new HashSet<String>();
                toVisit.addAll(cfg.getRules().keySet());
                toVisit.removeAll(ne);
                for (String n : toVisit) { //pro kaĹľdĂ˝ neterminĂˇl n z N
                    for (String r : cfg.getRules().get(n)) { //pro kaĹľdĂ© pravidlo neterminĂˇlu n
                        if (r.matches(regexp.regexNeUnionAlphabeth(ne, cfg.getTerminals()))) { //pokud pravidlo r obsahuje aspoĹ� 1 neterinĂˇl z ne
                            insertedIntoNe = ne.add(n);
                            break;
                        }
                    }
                }
            }
        }
        return ne;
    }

    public boolean languageIsNotEmpty(ContextFreeGrammar cfg) {
        return buildNe(cfg).contains(cfg.getInitialNonTerminal());
    }

    public boolean isNAG(ContextFreeGrammar cfg) {
        return cfg.getRules().isEmpty();
    }

    public boolean hasUnusefullSymbols(ContextFreeGrammar cfg) {
        Transformations transform = new Transformations();
        ContextFreeGrammar other = null;
        try {
            other = transform.removeUnusefullSymbols(cfg);
        } catch (TransformationException ex) {
            return true;
        }
        return !cfg.equals(other);
    }

    public boolean hasUnreachableSymbols(ContextFreeGrammar cfg) {
        Transformations transform = new Transformations();
        ContextFreeGrammar other = transform.removeUnreachableSymbols(cfg);
        return !cfg.equals(other);
    }

    public boolean isReduced(ContextFreeGrammar cfg) {
        return (!hasUnusefullSymbols(cfg) && !hasUnreachableSymbols(cfg));
    }

    public Set<String> buildNeps(ContextFreeGrammar cfg) {
        Set<String> nEps = new HashSet<String>();
        boolean addedToNEps = true;
        while (addedToNEps) { //vytvoĹ™enĂ­ NEps
            addedToNEps = false;
            Set<String> toVisit = new HashSet<String>();
            toVisit.addAll(cfg.getRules().keySet());
            toVisit.removeAll(nEps);
            for (String nonTerminal : toVisit) { //pro kaĹľdĂ˝ neterminĂˇl kterĂ˝ jsme jeĹˇtÄ› nenavĹˇtĂ­vili
                for (String rule : cfg.getRules().get(nonTerminal)) { //pro kaĹľdĂ© jeho pravidlo
                    RegExpMaker regexp = new RegExpMaker();
                    if (rule.matches(regexp.regexPositiveIterateThese(nEps))) { //pokud je pravidlo tvaru NEps* pĹ™idĂˇme neterminĂˇl co ho generuje do NEps
                        addedToNEps = nEps.add(nonTerminal);
                        break;
                    }
                }
            }
        }
        return Collections.unmodifiableSet(nEps);
    }

    public boolean hasInitialSymbolOnRightSide(ContextFreeGrammar cfg) {
        for (String nonTerminal : cfg.getRules().keySet()) {
            for (String rule : cfg.getRules().get(nonTerminal)) {
                if (rule.contains(cfg.getInitialNonTerminal())) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasEpsilonRules(ContextFreeGrammar cfg) {
        Set<String> Neps = new HashSet<String>();
        Neps.addAll(buildNeps(cfg));
        return ((Neps.remove(cfg.getInitialNonTerminal()) && hasInitialSymbolOnRightSide(cfg)) || !Neps.isEmpty());
    }

    public boolean hasSimpleRules(ContextFreeGrammar cfg) {
        boolean hasSimpleRules = false;
        for (String nonTerminal : cfg.getRules().keySet()) {
            for (String rule : cfg.getRules().get(nonTerminal)) {
                for (String allNonterminals : cfg.getNonTerminals()) {
                    if (rule.equals(allNonterminals)) {
                        hasSimpleRules = true;
                        break;
                    }
                }
                if (hasSimpleRules) {
                    break;
                }
            }
            if (hasSimpleRules) {
                break;
            }
        }
        return hasSimpleRules;
    }

    public boolean isProper(ContextFreeGrammar cfg) {
        return (!hasEpsilonRules(cfg) && !hasSimpleRules(cfg) && isReduced(cfg));
    }

    public boolean isInCNF(ContextFreeGrammar cfg) {
        Transformations transform = new Transformations();
        ContextFreeGrammar other = null;
        try {
            other = transform.transformToCNF(cfg);
        } catch (TransformationException ex) {
            return false;
        }
        return cfg.equals(other);
    }

    public boolean isLeftRecursive(ContextFreeGrammar cfg, List<String> ordering) {
        if (!isProper(cfg)) {
            return false;
        }
        Transformations transform = new Transformations();
        Map<ContextFreeGrammar, List[]> returnMap;
        try {
            returnMap = transform.removeLeftRecursion(cfg, ordering);
        } catch (TransformationException ex) {
            return false;
        }
        ContextFreeGrammar other = null;
        for (Map.Entry<ContextFreeGrammar, List[]> entry : returnMap.entrySet()) {
            other = entry.getKey();
        }
        return cfg.equals(other);
    }

    public boolean isInGNF(ContextFreeGrammar cfg) {
        if (!isProper(cfg)) {
            return false;
        }
        for (Map.Entry<String, Set<String>> entry : cfg.getRules().entrySet()) {
            for (String rule : entry.getValue()) {
                for (String nonTerminal : cfg.getNonTerminals()) {
                    if (rule.startsWith(nonTerminal)) {
                        return false;
                    }
                }
                for (String terminal : cfg.getTerminals()) {
                    if (rule.length() > 1 && rule.contains(terminal)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
