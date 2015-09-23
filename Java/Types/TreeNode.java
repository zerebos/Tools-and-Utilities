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
 * The Class TreeNode.
 *
 * @author Zack Rauen
 * @version 1.0
 * @param <T> the generic type
 */
public class TreeNode<T> {
 
	private T data;
    private List<TreeNode<T>> children;
    private Integer depth;
 
    /**
     * Instantiates a new tree node.
     */
    public TreeNode() {}
    
    /**
     * Instantiates a new tree node while setting the depth.
     *
     * @param depth the depth of the node.
     */
    public TreeNode(Integer depth) {
    	this.depth=depth;
    }
    
    /**
     * Instantiates a new tree node with the data to hold.
     *
     * @param data the data to be held in the node.
     */
    public TreeNode(T data) {
        setData(data);
    }
     
    /**
     * Adds a child node to this node.
     *
     * @param child the child to append.
     */
    public void addChild(TreeNode<T> child) {
        if (children == null) {
            children = new ArrayList<TreeNode<T>>();
        }
        children.add(child);
        resetChildDepths();
    }

    /**
     * Gets all the children of this node.
     *
     * @return the children of the node.
     */
    public List<TreeNode<T>> getChildren() {
        if (this.children == null) {
            return new ArrayList<TreeNode<T>>();
        }
        return this.children;
    }

    /**
     * Gets the data held in this node.
     *
     * @return the data held here.
     */
    public T getData() {
        return this.data;
    }
    
    /**
     * Gets the depth of this node.
     *
     * @return the depth of the node.
     */
    public Integer getDepth() {
		return depth;
	}
     
    /**
     * Gets the number of direct children to this node.
     *
     * @return the number of children.
     */
    public int getNumberOfChildren() {
        if (children == null) {
            return 0;
        }
        return children.size();
    }
    
    /**
     * Gets the total number of nodes descending from
     * this node, not just direct children.
     *
     * @return the number of descendants.
     */
    public int getNumberOfDescendants() {
        if (children == null) {
            return 0;
        }
        int childCount = 0;
        for (TreeNode<T> child : this.getChildren()) {
        	childCount++;
        	childCount += child.getNumberOfChildren();
        }
        return childCount;
    }
     
    /**
     * Checks for children.
     *
     * @return if the node has direct children.
     */
    public Boolean hasChildren() {
    	return this.getNumberOfChildren() > 0;
    }
     
    /**
     * Insert child at a specific index.
     *
     * @param index the index
     * @param child the child
     * @throws IndexOutOfBoundsException the index out of bounds exception
     */
    public void insertChildAt(int index, TreeNode<T> child) throws IndexOutOfBoundsException {
        if (index == getNumberOfChildren()) {
            addChild(child);
        } else {
            children.get(index);
            children.add(index, child);
            resetChildDepths();
        }
    }
 
    /**
     * Removes the child at.
     *
     * @param index the index
     * @throws IndexOutOfBoundsException the index out of bounds exception
     */
    public void removeChildAt(int index) throws IndexOutOfBoundsException {
        children.remove(index);
    }
 
    /**
     * Sets the children.
     *
     * @param children the new children
     */
    public void setChildren(List<TreeNode<T>> children) {
        this.children = children;
        resetChildDepths();
    }
	
	/**
	 * Sets the data.
	 *
	 * @param data the new data
	 */
	public void setData(T data) {
        this.data = data;
    }
	
	/**
	 * Sets the depth.
	 *
	 * @param depth the new depth
	 */
	public void setDepth(Integer depth) {
		this.depth = depth;
		resetChildDepths();
	}
	
	private void resetChildDepths() {
		for (TreeNode<T> child : this.getChildren()) {
			child.setDepth(this.getDepth()+1);
		}
	}
}

