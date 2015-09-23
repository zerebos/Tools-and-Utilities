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
package com.zackrauen.types;

import java.util.ArrayList;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * Non-binary "Tree" data type. This class includes a 
 * generic type for storage of any object needed to be
 * stored. This is commonly used with <tt>{@code Map<String, String>}</tt>
 * to create a listing of keys to values in a hierarchy.
 * This is the same premise used for parsing and writing
 * XML files.<br>
 * This class only has a single member variable which
 * represents the root element of the tree. The tree is
 * then able to expand because the root is a {@link TreeNode TreeNode}
 * object. Each of these then has a <tt>List</tt> of TreeNodes
 * which act as the children of the individual node.
 * Therefore it is imperative that you look at TreeNode
 * in order to understand the Tree.
 *
 * @author Zack Rauen
 * @version 1.0
 * @param <T> the generic type
 * @see TreeNode
 */
public class Tree<T> {
 
    private TreeNode<T> rootElement;

    /**
     * Instantiates a new blank tree.
     */
    public Tree(){}
    
    /**
     * Instantiates a new tree with a root element. Updates
     * the depth of the root element which updates the 
     * depths of its children recursively due to the
     * {@link TreeNode#setDepth(Integer) setDepth} function
     * of the TreeNodes.
     *
     * @param root the root element to use
     */
    public Tree(TreeNode<T> root) {
    	this.rootElement=root;
    	this.getRootElement().setDepth(0);
    }
    
    /**
     * Gets the root element.
     *
     * @return the root element
     */
    public TreeNode<T> getRootElement() {
        return this.rootElement;
    }
 
    /**
     * Grab children of a given tree node.
     *
     * @param item the item to get the children of
     * @return the list of children
     */
    public List<List<TreeNode<T>>> grabChildren(TreeNode<T> item) {
		List<List<TreeNode<T>>> foo = new ArrayList<List<TreeNode<T>>>();
		if (item.getChildren().size()>0) {
			foo.add(grabChildren(item).get(0));
		} else {
			foo.add(item.getChildren());
		}
		return foo;
	}

    /**
     * Sets the root element. Updates
     * the depth of the root element which updates the 
     * depths of its children recursively due to the
     * {@link TreeNode#setDepth(Integer) setDepth} function
     * of the TreeNodes.
     *
     * @param rootElement the new root element
     */
    public void setRootElement(TreeNode<T> rootElement) {
        this.rootElement = rootElement;
        this.getRootElement().setDepth(0);
    }
     
    /**
     * returns a list of all the TreeNodes in the tree.
     *
     * @return the list
     */
    public List<TreeNode<T>> toList() {
        List<TreeNode<T>> list = new ArrayList<TreeNode<T>>();
        addAll(rootElement, list);
        return list;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
	public String toString() {
        return toList().toString();
    }
    
	private void addAll(TreeNode<T> element, List<TreeNode<T>> list) {
        list.add(element);
        for (TreeNode<T> data : element.getChildren()) {
        	addAll(data, list);
        }
    }

}
