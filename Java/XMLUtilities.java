/*
 * Copyright (c) 2015 Zachary Rauen
 * Website: www.ZackRauen.com
 *
 * All rights reserved. Use is subject to license terms.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * If a copy of the License is not provided with the work, you may
 * obtain a copy at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zackrauen.utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.zackrauen.types.Tree;
import com.zackrauen.types.TreeNode;

// TODO: Auto-generated Javadoc
/**
 * This class has static functions to read and write XML files.
 * It utilizes the W3C Doc library but is an easy-to-use 
 * wrapper for anyone looking for an easier method.
 * Reading and writing are both dependent on the
 * {@link Tree Tree} class.<br>
 * <h2>Reading</h2>
 * <p>When reading an XML file in,
 * it parses it into a <tt>Tree</tt> where all the 
 * {@link TreeNode TreeNodes} hold <tt>Maps</tt> of 
 * String to String. Or, more succinctly, 
 * <tt>{@code Tree<Map<String, String>>}</tt>. These Maps are 
 * used to hold all the data the parser can find in the 
 * XML document. This means that any attribute, such as 
 * <tt>{@code <tag attr="val">} </tt> or data-child 
 * (e.g. <tt>{@code <tag><data>text<data></tag>}</tt>)
 * will have the <i>attr</i> or <i>{@code <data>}</i>, 
 * respectively, as the key where as the value for each
 * will be the value in the map.</p>
 * <p>Also, another value in the map will be the actual tag
 * name for each tag found. The key for this value will be
 * "tagname" so as to *hopefully* not interfere with anything
 * found in the document.</p>
 * <h2>Writing</h2>
 * <p>As for writing, if the data is formatted the same as
 * (or very similarly to) how the data is read in then
 * writing will progress just fine. If you haven't read
 * the section on reading or just aren't clear heres a
 * quick overview.</p>
 * <p>In order to write you should pass a tree where each
 * node has a Map from string to string. The only required
 * value in the map is the name of the actual tag to put
 * in the XML. The key it uses to grab this is "tagname".
 * The hierarchy of the tree will be duplicated in the
 * XML and any mapped value (aside from the tag name)
 * will appear either as a child data element or as
 * an attribute. Generally speaking, if the TreeNode has
 * children then the map data will be put in attributes,
 * otherwise it will be child elements. </p>
 * <p><strong>NOTE: </strong> This will not affect the actual
 * data being read or written. Attributes and child 
 * elements, with actual textual data, will always be treated
 * the same; all your data will be maintained just in a
 * slightly different format than (possibly) desired.</p>
 *
 * @author Zack Rauen
 * @version 1.0
 * @see Tree
 * @see TreeNode
 */
public class XMLUtilities {

	private static Integer depth = 0;
	
	/**
	 * This is a wrapper for the other {@link #readFile(String, Boolean) readFile}
	 * function and this just submits verbose to be false by default.
	 *
	 * @param fileIn the file in
	 * @return the tree
	 */
	public static Tree<Map<String,String>> readFile(String fileIn) {
		return XMLUtilities.readFile(fileIn, false);
	}
	
	/**
	 * Reads/parses the XML file passed to this function
	 * and return it as a tree of maps relating to all
	 * the information found in the XML file.
	 *
	 * @param fileIn the file to be read.
	 * @param verbose whether to print everything out.
	 * @return the tree of the data.
	 */
	public static Tree<Map<String,String>> readFile(String fileIn, Boolean verbose) {
		XMLUtilities.depth = 0; // reset depth
		Tree<Map<String,String>> elementData = new Tree<Map<String,String>>();
		try {
			// create a new document maker (w3c)
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

		    if (verbose)
		    	System.out.println("Preparing to read: "+fileIn);
		    
		    Document document = builder.parse(fileIn); // make the document from the file
		    document.normalizeDocument(); // trim any nasty stuff
		    HashMap<String,String> rootData = new HashMap<String,String>();
		    // copy xml root into tree root
		    rootData.put("tagname",document.getDocumentElement().getNodeName());
		    // set the root node
		    elementData.setRootElement(new TreeNode<Map<String,String>>(rootData));

		    // if theres more than the root (needs to for useful xml data)
		    if (document.hasChildNodes()) {
		    	// recursively grab the data
	    		elementData.getRootElement().setChildren(parseNodeList(document.getDocumentElement().getChildNodes()));
		    }


		    if (verbose) {
		    	XMLUtilities.printElementData(elementData);
		    }
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return elementData;
	}
	
	/**
	 * This is a wrapper for the other 
	 * {@link #writeFile(Tree, String, Boolean) writeFile}
	 * function and this just submits verbose to be false by default.
	 *
	 * @param elementData the element data
	 * @param filenameWithPath the filename with path
	 * @return true, if successful
	 */
	public static boolean writeFile(Tree<Map<String,String>> elementData, String filenameWithPath) {
		return XMLUtilities.writeFile(elementData, filenameWithPath, false);
	}

	/**
	 * This writes the file dependent on the element data
	 * provided. The data MUST have have the key "tagname"
	 * to be used as the tag name in the XML document.
	 *
	 * @param elementData the element data
	 * @param filenameWithPath the filename with path
	 * @param verbose the verbose
	 * @return true, if successful
	 */
	public static boolean writeFile(Tree<Map<String,String>> elementData, String filenameWithPath, Boolean verbose) {
		// either add ".xml" or leave it
		String file = "";
		if (filenameWithPath.endsWith(".xml"))
			file = filenameWithPath;
		else
			file = filenameWithPath+".xml";
		
		try {
			// create the builder.
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

		    Document document = builder.newDocument(); // new document for writing.
		    // put the tree root in the document root.
		    Element root = document.createElement(elementData.getRootElement().getData().get("tagname"));
		    document.appendChild(root);

		    // recursively add children to the document according to the tree setup.
		    for (TreeNode<Map<String,String>> node : elementData.getRootElement().getChildren()) {
		    	XMLUtilities.appendWithChildren(document,root,node);
		    }

		    //essentially translates the DOM source into real XML
			TransformerFactory.newInstance().newTransformer().transform(new DOMSource(document),
					new StreamResult(new File(file)));

			// print things out if desired
			if (verbose) {
				XMLUtilities.printElementData(elementData);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private static void appendWithChildren(Document doc, Element root, TreeNode<Map<String,String>> node) {
		if (node.hasChildren()) {
			List<TreeNode<Map<String,String>>> children = node.getChildren();
			Element enode = doc.createElement(node.getData().get("tagname"));
			for (String key : node.getData().keySet()) {
				if (!key.equals("tagname"))
				enode.setAttribute(key, node.getData().get(key));
			}
			root.appendChild(enode);
			for (TreeNode<Map<String,String>> n : children) {
				XMLUtilities.appendWithChildren(doc, enode, n);
			}
		}
		else {
			Element enode = doc.createElement(node.getData().get("tagname"));
			for (String key : node.getData().keySet()) {
				if (key.equals("id")) {
					enode.setAttribute(key, node.getData().get(key));
				} else if (!key.equals("tagname")) {
					Element subnode = doc.createElement(key);
					subnode.appendChild(doc.createTextNode(node.getData().get(key)));
					enode.appendChild(subnode);
				}
			}
			root.appendChild(enode);
		}

	}

	private static List<TreeNode<Map<String,String>>> parseNodeList(NodeList nodes) {
		XMLUtilities.depth++; // add one to depth each time this is called.
		List<TreeNode<Map<String,String>>> elements = new ArrayList<TreeNode<Map<String,String>>>();
		for (int i=0;i<nodes.getLength();i++) {
			Node currentNode = nodes.item(i); // grab each node
			if (currentNode instanceof Element) {
				//Assume no children
				Boolean hasChildren=false;
				HashMap<String,String> data = new HashMap<String,String>();
				// go through all attributes
				for (int z=0;z<currentNode.getAttributes().getLength();z++) {
					// add all attributes => values to map
        			data.put(currentNode.getAttributes().item(z).getNodeName(),currentNode.getAttributes().item(z).getNodeValue());
        		}
				// get children of current node (can be data)
				// text nodes are junk
				NodeList currentNodeList = currentNode.getChildNodes();
				for (int z=0;z<currentNodeList.getLength();z++) {
					// if the child has children
					if (currentNodeList.item(z).hasChildNodes()) {
						// then look at those children
						NodeList currentNodeSubList = currentNodeList.item(z).getChildNodes();
						// go through them all
						for (int k=0;k<currentNodeSubList.getLength();k++) {
							// if one of these isn't a text node, then it truly has children
							if (currentNodeSubList.item(k).getNodeType()!=Node.TEXT_NODE)
								hasChildren=true;
						}
					}
					// if the node we originally grabbed wasnt a text node (and lacks children)
					// then we add it to the list of data
					if (currentNodeList.item(z).getNodeType()!=Node.TEXT_NODE && !hasChildren)
						data.put(currentNodeList.item(z).getNodeName(),currentNodeList.item(z).getTextContent());
        		}
				// if we didnt already add the tagname, add it now
				// tagname is the tag from xml
				if (!data.containsKey("tagname"))
					data.put("tagname", currentNode.getNodeName());
				TreeNode<Map<String,String>> nodeToAdd = new TreeNode<Map<String,String>>(data);
				nodeToAdd.setDepth(XMLUtilities.depth);
				// before we add the node, add all children to this node
				// by going through the recursion
				if (hasChildren)
					nodeToAdd.setChildren(parseNodeList(currentNodeList));
			elements.add(nodeToAdd); // finally add this node to the list.
			}
		}
	return elements; // return everything recursively found
	}
	
	private static void printElementData(Tree<Map<String, String>> elementData) {
	    for (String key : elementData.getRootElement().getData().keySet()) {
	    	System.out.println("Key: "+key+" Value: "+elementData.getRootElement().getData().get(key));
	    }
	    System.out.println("");System.out.println("");System.out.println("");
	    for (TreeNode<Map<String, String>> subRoot : elementData.getRootElement().getChildren()) {
	    	System.out.println("Num of elements: " + subRoot.getNumberOfChildren());
    		for (String key : subRoot.getData().keySet()) {
    			System.out.println("Key: "+key+" Value: "+subRoot.getData().get(key));
    		}
    		System.out.println("================================");
	    	for (TreeNode<Map<String, String>> leaf : subRoot.getChildren()) {
	    		for (String key : leaf.getData().keySet()) {
	    			System.out.println("Key: "+key+" Value: "+leaf.getData().get(key));
	    		}
	    		System.out.println("");
	    	}
	    	System.out.println("");
	    	System.out.println("");
	    	System.out.println("");
	    }
	}
}
