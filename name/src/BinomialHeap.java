/**
 * BinomialHeap
 *
 * An implementation of binomial heap over non-negative integers.
 * Based on exercise from previous semester.
 */
public class BinomialHeap
{
	public int size;
	public HeapNode first;
	public HeapNode last;
	public HeapNode min;
	public HeapNode prevMin;
	public int numTree;

	/*Initialises an empty heap*/
	public BinomialHeap(){
		size=0; first=null; last=null; min=null; prevMin=null; numTree=0;
	}
	// for meld purposes only, take a part of previous heap and turn into new heap
	private BinomialHeap(HeapNode node){
		size=1<<node.rank; numTree=1; first=node; last=node; min=node;
		int minkey=node.item.key;
		node.parent=null;
		while(last.next!=first){
			last=last.next;
			last.parent=null;
			numTree++; size+=1<<last.rank;
			if (last.item.key<minkey){
				minkey=last.item.key;
				min=last;
			}
		}
	}
	/**
	 * 
	 * pre: key > 0
	 *
	 * Insert (key,info) into the heap and return the newly generated HeapItem.
	 *
	 */
	public HeapItem insert(int key, String info) 
	{    
		if (size==0){
			HeapItem item=new HeapItem(null,key,info);
			HeapNode node=new HeapNode(item,null,null,null,0);
			item.node=node;
			node.next=node;
			numTree=1; size=1; last=node; min=node;first=node;prevMin=node;
			return item;
		}
		BinomialHeap heap1=new BinomialHeap();
		HeapItem item=heap1.insert(key,info);
		meld(heap1);
		return item; // should be replaced by student code
	}


	/**
	 * 
	 * Delete the minimal item
	 * 
	 */
	public void deleteMin()
	{
		size-=1<<min.rank;
		numTree-=1;
		if (size==0){
			first=null; min=null; prevMin=null;last=null;
			return;
		}
		prevMin.next=min.next;
		locateNextMin();
		HeapNode p=min.child;
		BinomialHeap children=new BinomialHeap(p);
		meld(children);
	}
	/*
	 * Locates the minimum in heap, sets min to it and prevMin to the node before it. 
	 * runs in O(numTree)=O(log(n)) time
	 */
	private void locateNextMin(){
		prevMin=last;
		HeapNode cur=first;
		min=first;
		while (cur.next!=first){
			HeapNode nxt=cur.next;
			if (nxt.item.key<min.item.key){
				prevMin=cur;
				min=nxt;
			}
			cur=nxt;
		}
	}

	/**
	 * 
	 * Return the minimal HeapItem
	 *
	 */
	public HeapItem findMin()
	{
		return min.item;
	} 

	/**
	 * 
	 * pre: 0 < diff < item.key
	 * 
	 * Decrease the key of item by diff and fix the heap. 
	 * 
	 */
	public void decreaseKey(HeapItem item, int diff) 
	{    
		return; // should be replaced by student code
	}

	/**
	 * 
	 * Delete the item from the heap.
	 *
	 */
	public void delete(HeapItem item) 
	{    
		int minKey=min.item.key;
		int diff=item.key-minKey;
		decreaseKey(item,diff);
		deleteMin();
	}

	/**
	 * 
	 * Meld the heap with heap2
	 *
	 */
	public void meld(BinomialHeap heap2)
	{
		return; // should be replaced by student code   		
	}

	/**
	 * 
	 * Return the number of elements in the heap
	 *   
	 */
	public int size()
	{
		return size;
	}

	/**
	 * 
	 * The method returns true if and only if the heap
	 * is empty.
	 *   
	 */
	public boolean empty()
	{
		return size==0;
	}

	/**
	 * 
	 * Return the number of trees in the heap.
	 * 
	 */
	public int numTrees()
	{
		return numTree;
	}

	/**
	 * Class implementing a node in a Binomial Heap.
	 *  
	 */
	public class HeapNode{
		public HeapItem item;
		public HeapNode child;
		public HeapNode next;
		public HeapNode parent;
		public int rank;
		public HeapNode(HeapItem it, HeapNode chld, HeapNode nxt, HeapNode prnt, int rk){
			item=it; child=chld; next=nxt; parent=prnt; rank=rk;
		}
	}

	/**
	 * Class implementing an item in a Binomial Heap.
	 *  
	 */
	public class HeapItem{
		public HeapNode node;
		public int key;
		public String info;
		public HeapItem(HeapNode nd, int ke, String inf){
			node=nd; key=ke; info=inf;
		}
	}

	public static void main(String[] args){
		System.out.println("hello world");
	}

}
