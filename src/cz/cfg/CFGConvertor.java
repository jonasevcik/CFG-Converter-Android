/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cfg;

import java.util.List;
import java.util.Map;

/**
 *
 * @author NICKT
 */
public class CFGConvertor {

    public String writeOrdering(List<String> order) {
        StringBuilder outputString = new StringBuilder();
        for (int i = 0; i < order.size(); i++) {
            if (i != order.size() - 1) {
                outputString.append(order.get(i) + " < ");
            } else {
                outputString.append(order.get(i));
            }
        }
        return outputString.toString();
    }

    public String[] convert(ContextFreeGrammar cfg, TransformationTypes type, List<String> ordering) {
        Transformations transform = new Transformations();
        String outputCFG = null;
        String title = null;
               switch (type) {
                    case NE1:
                        title = "Odstran�ny nenormovan� symboly:";
                        try {
                            outputCFG = transform.removeUnusefullSymbols(cfg).toString();
                        } catch (TransformationException ex) {
                            outputCFG = "NAG";
                        }
                        break;
                    case NE2:
                        title = "Odstran�ny nedosa�iteln� symboly:";
                        outputCFG = transform.removeUnreachableSymbols(cfg).toString();
                        break;
                    case RED:
                        title = "Gramatika byla zredukov�na:";
                        try {
                            outputCFG = transform.makeReducedCFG(cfg).toString();
                        } catch (TransformationException ex) {
                            outputCFG = "NAG";
                        }
                        break;
                    case EPS:
                        title = "Odstran�ny epsilon kroky:";
                        outputCFG = transform.removeEps(cfg).toString();
                        break;
                    case SRF:
                        title = "Odstran�na jednoduch� pravidla:";
                        outputCFG = transform.removeSimpleRules(cfg).toString();
                        break;
                    case PRO:
                        title = "P�evedeno na vlastn� CFG:";
                        try {
                            outputCFG = transform.makeProperCFG(cfg).toString();
                        } catch (TransformationException ex) {
                            outputCFG = "NAG";
                        }
                        break;
                    case CNF:
                        title = "P�evedeno do CNF:";
                        try {
                            outputCFG = transform.transformToCNF(cfg).toString();
                        } catch (TransformationException ex) {
                            outputCFG = "NAG";
                        }
                        break;
                    case RLR:
                        try {
                            Map<ContextFreeGrammar, List[]> returnMap = transform.removeLeftRecursion(cfg, ordering);
                            for (Map.Entry<ContextFreeGrammar, List[]> entry : returnMap.entrySet()) {
                                outputCFG = entry.getKey().toString();
                                title = "Odstran�na lev� rekurze (" + writeOrdering(entry.getValue()[0]) + "):";
                            }
                        } catch (TransformationException ex) {
                            outputCFG = "NAG";
                        }
                        break;
                    case GNF:
                        title = "P�evedeno do GNF:";
                        try {
                            outputCFG = transform.transformToGNF(cfg, ordering).toString();
                        } catch (TransformationException ex) {
                            outputCFG = "NAG";
                        }
                        break;
                }
                outputCFG = outputCFG.toString();
                String[] output = {title, outputCFG};
        return output;
    }
}
