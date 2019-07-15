/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 *
 ******************************************************************************/
package fr.ign.cogit.mapmatcher.util;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * XML Tools.
 * 
 * @author Marie-Dominique Van Damme
 */
public class Xml {

    /** TAG PATH. */
    private static final String XML_PATH_TAG = "paths";
    private static final String XML_PATH_ATT_ID = "id";
    
    /** TAG EDGE. */
    private static final String XML_EDGE_TAG = "edge";

    /**
     * Method to merge index blocs.
     * 
     * Structure of xml file : 
     * <paths>
     *     <track id="1" path="trace1.gps"/> 
     *     ...
     * </paths> 
     * <edge id="39" count="5">
     *     <track id="2">
     *         <point id="639"/> 
     *         ...
     *     </track>
     *     <track id="5">
     *         <point id="197"/> 
     *         ... 
     *     </track>
     * </edge>
     * 
     * 
     * @param fileMerged merged file with fileToInsert and itself
     * @param fileToInsert file will merged in fileMerged
     */
//    public static void mergeXml(String fileToMerge, String fileToInsert) {
//        System.out.println("MERGE " + fileToMerge + "," + fileToInsert);
//        try {
//
//            // Parsing fileMerge
//            DocumentBuilderFactory documentBuilderFactory0 = DocumentBuilderFactory.newInstance();
//            DocumentBuilder documentBuilder0 = documentBuilderFactory0.newDocumentBuilder();
//            Document document0 = documentBuilder0.parse(fileToMerge);
//            Element root0 = document0.getDocumentElement();
//
//            // Parsing fileToInsert
//            DocumentBuilderFactory documentBuilderFactory1 = DocumentBuilderFactory.newInstance();
//            DocumentBuilder documentBuilder1 = documentBuilderFactory1.newDocumentBuilder();
//            Document document1 = documentBuilder1.parse(fileToInsert);
//            Element root1 = document1.getDocumentElement();
//      
//      
//            // =============================================================================================
//            // 1. merge PATHS
//            
//            // Noeud parent dans lequel on va ajouter les enfants
//            Node noeudPath0 = root0.getElementsByTagName(XML_PATH_TAG).item(0);
//
//            // Liste des identifiants déjà présents dans le fichier
//            TreeMap<Integer, Node> listExistingId = new TreeMap<Integer, Node>();
//
//            for (int j = 0; j < noeudPath0.getChildNodes().getLength(); j++) {
//                Node node = noeudPath0.getChildNodes().item(j);
//                if (node instanceof Element) {
//                    Element trackNode = (Element) node;
//                    listExistingId.put(Integer.parseInt(trackNode.getAttribute(XML_PATH_ATT_ID)), trackNode);
//                }
//            }
//
//            Node nodePath1 = root1.getElementsByTagName(XML_PATH_TAG).item(0);
//
//            // On ajoute les tracks du path 1 qui n'y sont pas encore
//            for (int j = 0; j < nodePath1.getChildNodes().getLength(); j++) {
//                Node node = nodePath1.getChildNodes().item(j);
//                if (node instanceof Element) {
//                    Element trackNode = (Element) node;
//                    int id = Integer.parseInt(trackNode.getAttribute(XML_PATH_ATT_ID));
//
//                    // Est-ce que c'est un nouveau premier élément ?
//                    if (listExistingId.get(id) == null) {
//
//                        // On récupère le noeud juste inférieur car c'est trié
//                        if (listExistingId.headMap(id).size() > 0) {
//
//                            // Dernier noeud dont l'identifiant est inférieur à celui qu'on veut fusionner
//                            Node n = listExistingId.get(listExistingId.headMap(id).lastKey());
//
//                            // On ajoute le noeud
//                            Node firstDocImportedNode = document0.importNode(trackNode, true);
//                            noeudPath0.insertBefore(firstDocImportedNode, n.getNextSibling());
//
//                            // On met a jour la liste des identifiants
//                            listExistingId.put(id, firstDocImportedNode);
//
//                        } else {
//
//                            // C'est un nouveau premier élément
//                            Node firstDocImportedNode = document0.importNode(trackNode, true);
//                            noeudPath0.insertBefore(firstDocImportedNode, noeudPath0.getFirstChild());
//
//                            // Mise à jour la liste des identifiants ?
//                            listExistingId.put(id, firstDocImportedNode);
//
//                        }
//
//                    }
//
//                }
//
//            }
//      
//            // =============================================================================================
//            //   Fusion des EDGES
//      
//            // System.out.println("Fichier 0 - total edge : " + root0.getElementsByTagName("edge").getLength());
//            NodeList listExistingEdge = root0.getElementsByTagName(XML_EDGE_TAG);
//      
//            // Liste des identifiants EDGE déjà présents dans le fichier
//            TreeMap<Integer, Object[]> treeExistingEdgeTrack = new TreeMap<Integer, Object[]>(); 
//            for (int j = 0; j < listExistingEdge.getLength(); j++) {
//                Node node = listExistingEdge.item(j);
//                if (node instanceof Element) {
//                    Element edgeNode = (Element)node;
//                    int idEdge = Integer.parseInt(edgeNode.getAttribute("id"));
//          
//                    // Liste des tracks
//                    List<Integer> listTrack = new ArrayList<Integer>();
//                    for (int k = 0; k < edgeNode.getChildNodes().getLength(); k++) {
//                        Node nodeEnfant = edgeNode.getChildNodes().item(k);
//                        if (nodeEnfant instanceof Element) {
//                            Element trackNode = (Element)nodeEnfant;
//                            int idTrack = Integer.parseInt(trackNode.getAttribute("id"));
//                            listTrack.add(idTrack);
//                        }
//                    }
//          
//                    Object[] treeEdgeTrack = new Object[2];
//                    treeEdgeTrack[0] = edgeNode;
//                    treeEdgeTrack[1] = listTrack;
//                    treeExistingEdgeTrack.put(idEdge, treeEdgeTrack);
//                }
//            }
//      
//      
//            // On boucle sur les edges à insérer
//            NodeList listEdgeToInsert = root1.getElementsByTagName(XML_EDGE_TAG);
//            for (int j = 0; j < listEdgeToInsert.getLength(); j++) {
//                Node nodeE = listEdgeToInsert.item(j);
//                if (nodeE instanceof Element) {
//                    Element edgeNode = (Element)nodeE;
//                    int idEdge = Integer.parseInt(edgeNode.getAttribute("id"));
//                    // System.out.println(idEdge + " edge en cours");
//          
//                    // Est-ce que le edge existe ?
//                    if (treeExistingEdgeTrack.get(idEdge) == null) {
//                        // System.out.println(idEdge + " on ajoute tout le edge");
//            
//                        // Est-ce que c'est un nouveau premier élément ?
//                        if (treeExistingEdgeTrack.headMap(idEdge).size() <= 0) {
//                          
//                          // System.out.println(idEdge + " tout devant ... ");
//                          Entry<Integer, Object[]> ent = treeExistingEdgeTrack.firstEntry();
//                          Element elt = (Element)ent.getValue()[0];
//                          Node firstDocImportedNode = document0.importNode(edgeNode, true);
//                          elt.getParentNode().insertBefore(firstDocImportedNode, elt);
//                        
//                        } else {
//                        
//                          // On cherche le plus grand id edge inférieur à idEdge
//                          int lastKey = treeExistingEdgeTrack.headMap(idEdge).lastKey();
//                          // System.out.println("  last key = " + lastKey);
//                          Element lastElt = (Element)treeExistingEdgeTrack.get(lastKey)[0];
//                          // System.out.println("  last key = " + lastElt.getTagName() + ", " + lastElt.getAttribute("count"));
//                          // System.out.println("  " + lastElt.getOwnerDocument() + " - " + edgeNode.getOwnerDocument());
//                          
//                          // On ajoute le edge
//                          Node firstDocImportedNode = document0.importNode(edgeNode, true);
//                          lastElt.getParentNode().insertBefore(firstDocImportedNode, lastElt.getNextSibling());
//                          
//                          // On met a jour la liste des identifiants
//                          // listExistingId.put(id, firstDocImportedNode);
//                          Object[] treeEdgeTrack = new Object[2];
//                          treeEdgeTrack[0] = firstDocImportedNode;
//                          treeEdgeTrack[1] = new ArrayList<Integer>(); // pas nécessaire de mettre à jour ?
//                          treeExistingEdgeTrack.put(idEdge, treeEdgeTrack);
//                          
//                          // Le count n'a pas besoin d'être mis à jour
//                          
//                        }
//            
//                    } else {
//            
//                        // Le EDGE existe deja, on ajoute uniquement les tracks
//                        // System.out.println(idEdge + " on ajoute uniquement les tracks ... ");
//            
//            
//                        // On recupere le COUNT
//                        Element e = (Element) treeExistingEdgeTrack.get(idEdge)[0];
//                        int count = Integer.parseInt(e.getAttribute("count"));
//                        int add = Integer.parseInt(edgeNode.getAttribute("count"));
//                        // On met a jour le COUNT
//                        ((Element) treeExistingEdgeTrack.get(idEdge)[0]).setAttribute("count", Integer.toString(count + add));
//             
//            
//                        // On récupère d'abord les tracks à insérer
//                        for (int k = 0; k < edgeNode.getChildNodes().getLength(); k++) {
//                            Node nodeEnfant = edgeNode.getChildNodes().item(k);
//                            if (nodeEnfant instanceof Element) {
//                                Element trackAInserer = (Element)nodeEnfant;
//                                int idTrack = Integer.parseInt(trackAInserer.getAttribute("id"));
//                                // System.out.println("    num track a inserer : " + idTrack);
//                
//                                // On recupere le edge qui a le même numero
//                                // System.out.println("    Edge a fusionner : " + idEdge);
//                                Object[] tabEdgeToMerged = treeExistingEdgeTrack.get(idEdge);
//                                Element edgeToMerged = (Element) tabEdgeToMerged[0];
//
//                                // On cherche le track juste apres
//                                // par defaut c'est le premier
//                                Element t0 = null; // (Element) edgeToMerged.getElementsByTagName("track").item(0);
//                                Element t = null;
//                                // System.out.println("   0");
//                                for (int cpt = 0; cpt < edgeToMerged.getElementsByTagName("track").getLength(); cpt++) {
//                                  t = (Element)edgeToMerged.getElementsByTagName("track").item(cpt);
//                                  int idAComp = Integer.parseInt(t.getAttribute("id"));
//                                  if (idTrack < idAComp) {
//                                    t0 = t;
//                                    break;
//                                  }
//                                }
//                                if (t0 == null) {
//                                    // On ajoute à la fin s'il y a déjà un edge
//                                    if (t != null) {
//                                        Node firstDocImportedNode = document0.importNode(trackAInserer, true);
//                                        t.getParentNode().appendChild(firstDocImportedNode);
//                                    } else {
//                                        Node firstDocImportedNode = document0.importNode(trackAInserer, true);
//                                        edgeToMerged.appendChild(firstDocImportedNode);
//                                    }
//                                } else {
//                                    // On ajoute
//                                    Node firstDocImportedNode = document0.importNode(trackAInserer, true);
//                                    t0.getParentNode().insertBefore(firstDocImportedNode, t0);
//                                }
//                
//                            }
//                        }
//            
//                    }
//                }
//            }
//      
//
//            // =============================================================================================
//            //   On rééecrit en streaming
//            DOMSource source = new DOMSource(document0);
//      
//            TransformerFactory transformerFactory = TransformerFactory.newInstance();
//            Transformer transformer = transformerFactory.newTransformer();
//            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
//              
//            StreamResult result = new StreamResult(fileToMerge);
//            transformer.transform(source, result);
//      
//      
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.exit(0);
//        }
//        System.out.println("The end.");
//    }
    
    // Sans ordre
    public static void mergeXml(String fileToMerge, String fileToInsert) {
        System.out.println("MERGE " + fileToMerge + "," + fileToInsert);
        try {

            // Parsing fileMerge
            DocumentBuilderFactory documentBuilderFactory0 = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder0 = documentBuilderFactory0.newDocumentBuilder();
            Document document0 = documentBuilder0.parse(fileToMerge);
            Element root0 = document0.getDocumentElement();

            // Parsing fileToInsert
            DocumentBuilderFactory documentBuilderFactory1 = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder1 = documentBuilderFactory1.newDocumentBuilder();
            Document document1 = documentBuilder1.parse(fileToInsert);
            Element root1 = document1.getDocumentElement();
            
            
            
            // =============================================================================================
            // 1. merge PATHS
                
            // Noeud parent dans lequel on va ajouter les enfants
            Node noeudPath0 = root0.getElementsByTagName(XML_PATH_TAG).item(0);
   
            // Liste des identifiants déjà présents dans le fichier
            TreeMap<Integer, Node> listExistingId = new TreeMap<Integer, Node>();
    
            for (int j = 0; j < noeudPath0.getChildNodes().getLength(); j++) {
                Node node = noeudPath0.getChildNodes().item(j);
                if (node instanceof Element) {
                    Element trackNode = (Element) node;
                    listExistingId.put(Integer.parseInt(trackNode.getAttribute(XML_PATH_ATT_ID)), trackNode);
                }
            }
    
            Node nodePath1 = root1.getElementsByTagName(XML_PATH_TAG).item(0);
    
            // On ajoute les tracks du path 1 qui n'y sont pas encore
            for (int j = 0; j < nodePath1.getChildNodes().getLength(); j++) {
                Node node = nodePath1.getChildNodes().item(j);
                if (node instanceof Element) {
                    Element trackNode = (Element) node;
                    int id = Integer.parseInt(trackNode.getAttribute(XML_PATH_ATT_ID));
    
                    // Est-ce que c'est un nouveau premier élément ?
                    if (listExistingId.get(id) == null) {
                        
                        // On ajoute le noeud
                        Node firstDocImportedNode = document0.importNode(trackNode, true);
                        noeudPath0.appendChild(firstDocImportedNode);
    
                        // On met a jour la liste des identifiants
                        listExistingId.put(id, firstDocImportedNode);

                    }
    
                }
    
            }
          
          
            // =============================================================================================
            //   Fusion des EDGES
    
            // System.out.println("Fichier 0 - total edge : " + root0.getElementsByTagName("edge").getLength());
            NodeList listExistingEdge = root0.getElementsByTagName(XML_EDGE_TAG);
    
            // Liste des identifiants EDGE déjà présents dans le fichier
            TreeMap<Integer, Object[]> treeExistingEdgeTrack = new TreeMap<Integer, Object[]>(); 
            for (int j = 0; j < listExistingEdge.getLength(); j++) {
                Node node = listExistingEdge.item(j);
                if (node instanceof Element) {
                    Element edgeNode = (Element)node;
                    int idEdge = Integer.parseInt(edgeNode.getAttribute("id"));
        
                    // Liste des tracks
                    List<Integer> listTrack = new ArrayList<Integer>();
                    for (int k = 0; k < edgeNode.getChildNodes().getLength(); k++) {
                        Node nodeEnfant = edgeNode.getChildNodes().item(k);
                        if (nodeEnfant instanceof Element) {
                            Element trackNode = (Element)nodeEnfant;
                            int idTrack = Integer.parseInt(trackNode.getAttribute("id"));
                            listTrack.add(idTrack);
                        }
                    }
        
                    Object[] treeEdgeTrack = new Object[2];
                    treeEdgeTrack[0] = edgeNode;
                    treeEdgeTrack[1] = listTrack;
                    treeExistingEdgeTrack.put(idEdge, treeEdgeTrack);
                }
            }
    
    
            // On boucle sur les edges à insérer
            NodeList listEdgeToInsert = root1.getElementsByTagName(XML_EDGE_TAG);
            for (int j = 0; j < listEdgeToInsert.getLength(); j++) {
                Node nodeE = listEdgeToInsert.item(j);
                if (nodeE instanceof Element) {
                    Element edgeNode = (Element)nodeE;
                    int idEdge = Integer.parseInt(edgeNode.getAttribute("id"));
                    // System.out.println(idEdge + " edge en cours");
        
                    // Est-ce que le edge existe ?
                    if (treeExistingEdgeTrack.get(idEdge) == null) {
          
                        Entry<Integer, Object[]> ent = treeExistingEdgeTrack.firstEntry();
                        Element elt = (Element)ent.getValue()[0];
                        Node firstDocImportedNode = document0.importNode(edgeNode, true);
                        elt.getParentNode().insertBefore(firstDocImportedNode, elt);
                      
                      
                        
                        // On met a jour la liste des identifiants
                        // listExistingId.put(id, firstDocImportedNode);
                        Object[] treeEdgeTrack = new Object[2];
                        treeEdgeTrack[0] = firstDocImportedNode;
                        treeEdgeTrack[1] = new ArrayList<Integer>(); // pas nécessaire de mettre à jour ?
                        treeExistingEdgeTrack.put(idEdge, treeEdgeTrack);
                        
                        // Le count n'a pas besoin d'être mis à jour
                        
                      
          
                    } else {
          
                      // Le EDGE existe deja, on ajoute uniquement les tracks
                      // System.out.println(idEdge + " on ajoute uniquement les tracks ... ");
          
                      // On recupere le COUNT
                      Element e = (Element) treeExistingEdgeTrack.get(idEdge)[0];
                      int count = Integer.parseInt(e.getAttribute("count"));
                      int add = Integer.parseInt(edgeNode.getAttribute("count"));
                      // On met a jour le COUNT
                      ((Element) treeExistingEdgeTrack.get(idEdge)[0]).setAttribute("count", Integer.toString(count + add));
           
          
                      // On récupère d'abord les tracks à insérer
                      for (int k = 0; k < edgeNode.getChildNodes().getLength(); k++) {
                          Node nodeEnfant = edgeNode.getChildNodes().item(k);
                          if (nodeEnfant instanceof Element) {
                              Element trackAInserer = (Element)nodeEnfant;
                              // int idTrack = Integer.parseInt(trackAInserer.getAttribute("id"));
                              // System.out.println("    num track a inserer : " + idTrack);
              
                              // On recupere le edge qui a le même numero
                              // System.out.println("    Edge a fusionner : " + idEdge);
                              Object[] tabEdgeToMerged = treeExistingEdgeTrack.get(idEdge);
                              Element edgeToMerged = (Element) tabEdgeToMerged[0];

                              Node firstDocImportedNode = document0.importNode(trackAInserer, true);
                              edgeToMerged.appendChild(firstDocImportedNode);
              
                          }
                      }
          
                  }
              }
          }
    
            
            

            // =============================================================================================
            //   On rééecrit en streaming
            DOMSource source = new DOMSource(document0);
                  
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
                          
            StreamResult result = new StreamResult(fileToMerge);
            transformer.transform(source, result);
                  
            
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
        System.out.println("The end.");
    }

}
