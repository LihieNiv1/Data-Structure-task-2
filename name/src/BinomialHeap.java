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
		HeapNode[] nodes=new HeapNode[node.rank+1];
		HeapNode cur=node.next;
		nodes[node.rank]=node;
		for (int i =1;i<nodes.length;i++){
			nodes[cur.rank]=cur;
			cur=cur.next;
		}
		size=1<<(1+node.rank);
		size--;
		numTree=node.rank+1;
		first=nodes[0];
		last=first;
		min=first;
		prevMin=nodes[nodes.length-1];
		int minKey=first.item.key;
		first.next=first; first.parent=null;
		for (int i=1;i<nodes.length;i++){
			last.next=nodes[i];
			last.parent=null;
			if (last.next.item.key<minKey){
				minKey=last.next.item.key;
				min=last.next;
				prevMin=last;
			}
			last=last.next;
		}
		last.next=first;
	}
	/* *****Runtime Analysis for BinomialHeap(node)******
	 * goes over all the "brothers" of node exactly two times, so runtime=O(brothers(node))
	 * in practice, nodes will always be part of a binomial tree which is a part of a binomial heap, so brothers(node)=rank(parent)<=log(n)
	 * so runs in O(log(n)).
	 */
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
		return item; 
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
		if (first==min) first=min.next;
		if (last==min) last=prevMin;
		prevMin.next=min.next;
		HeapNode p=min.child;
		BinomialHeap children=new BinomialHeap(p);
		locateNextMin();
		meld(children);
	}
	/* *****Runtime Analysis for deleteMin*****
	 * line 91 runs in O(min.rank)=O(log(n)) (in case min is the root of the biggest tree).
	 * line 92 runs in O(numTree-1)=O(numTree)=O(log(n))
	 * line 93 runs in O(log((n-2^min.rank)+2^min.rank))=O(log(n))
	 * Overall we have runtime=O(log(n))
	 */
	/*
	 * Locates the minimum in heap, sets min to it and prevMin to the node before it. 
	 */
	private void locateNextMin(){
		prevMin=last;
		HeapNode cur=first;
		min=first;
		while (cur.next!=first){ //happens numTrees times - O(log(n))
			HeapNode nxt=cur.next;
			if (nxt.item.key<min.item.key){
				prevMin=cur;
				min=nxt;
			}
			cur=nxt;
		}
	}
	/* *****Runtime Analysis for locateNextMin*****
	 * has a single loop that iterates over all the trees in the heap, so runs in O(numTrees) which is O(log(n)). 
	 */

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
		HeapNode cur=item.node;
		int key=item.key-diff;
		item.key=key; 
		while (cur.parent!=null && cur.parent.item.key>key){ //happens depth(item.node) times, O(log(n)) 
			cur.parent.item.node=cur;
			item.node=cur.parent;
			cur.item=cur.parent.item;
			cur.parent.item=item;
			cur=cur.parent;
		}
		if (cur.item.key<min.item.key) locateNextMin();
		return; 
	}
	/* *****Runtime Anaylsis for decreaseKey*****
	 * in every iteration, cur goes one rank up in the tree, meaning depth(cur)->depth(cur)-1. 
	 * This can only happen depth(item.node) times, which is at max the height/rank of the biggest tree, which is log(n).
	 * Therefore, runs at O(log(n)). (all other operations are O(1))
	 */
	/**
	 * 
	 * Delete the item from the heap.
	 *
	 */
	public void delete(HeapItem item) 
	{    
		int minKey=min.item.key;
		int diff=item.key-minKey+1;
		decreaseKey(item,diff); //O(log(n))
		deleteMin(); //O(log(n))
	}
	// ******Runtime Analysis for delete****** since calls for decreaseKey and deleteMin once, and both cost O(log(n)), runs at O(log(n)) as well (all other operations are O(1)).

	/**
	 * 
	 * Meld the heap with heap2
	 *
	 */
	public void meld(BinomialHeap heap2)
	{
		if (heap2.empty()) return;
		if (empty()){
			size=heap2.size;
			numTree=heap2.numTree;
			min=heap2.min;
			prevMin=heap2.prevMin;
			first=heap2.first;
			last=heap2.last;
			return;
		}
		HeapNode carry=null;
		HeapNode prev1=last;
		HeapNode cur2=heap2.first;
		HeapNode cur1=first;
		int n=size;
		int m=heap2.size;
		int r=cur1.rank>cur2.rank?cur2.rank:cur1.rank;
		while (n>>r!=0 && m>>r!=0){ //while rank is smaller than maximal rank in both trees, happens at most numTrees(self)+numTrees(heap2) times = O(log(n)+log(m))
			if (cur1.rank>r&&cur2.rank>r){ //both dont have a tree of rank r
				if(carry!=null){ //add carry to "empty slot"
					carry.next=cur1;
					prev1.next=carry;
					prev1=carry;
					carry=null;
					numTree++;
				}
				r=cur1.rank>cur2.rank?cur2.rank:cur1.rank;
			} //out of this while means either n or m have a tree of rank r
			if (cur2.rank>r){ //meaning self has a tree of rank r, heap2 doesn't
				if (carry==null){prev1=cur1;
				cur1=cur1.next;}
				else{
					boolean start=(cur1==first);
					boolean end=(cur1==last);
					prev1.next=cur1.next;
					numTree--;
					carry=linkTrees(cur1,carry);
					if (end) {
						if (start){ //self.numTree=1,replace cur1 by carry
							first=carry;
							last=first;
							first.next=first;
							n=1<<(r+1); //rank of carry is r+1
							prev1=last;
						}
						carry.next=first;
						prev1.next=carry;
						last=carry;
						carry=null;
						numTree++;
						n+=1<<(r+1);
					}
					else if (start){
						first=prev1.next;
					}
					cur1=prev1.next;

				}
				n=(n>>(r+1))<<(r+1); //"deleting" the tree from self
			}
			else if (cur1.rank>r){	//meaning heap2 has a tree of rank r, self doesn't
				if (carry==null){
					numTree++;
					HeapNode p =cur2;
					cur2=cur2.next;
					p.next=cur1;
					prev1.next=p;
					prev1=p;
					if (cur1==first){
						first=p;
					}
				}
				else{ //theres carry - link current2 and carry, turn into new carry
					boolean end=(cur2==heap2.last);
					HeapNode p=cur2.next;
					carry=linkTrees(cur2,carry);
					if (end){
						heap2.last=carry;
						p=carry;
						carry=null;
						m+=1<<(r+1); //rank of carry is r+1
					}
					cur2=p;
				}
				m=(m>>(r+1))<<(r+1); //"delete" the tree from heap2
			}
			else{ //meaning both have a tree of rank r, if theres carry - add it to self
				boolean end1=(cur1==last);
				boolean end2=(cur2==heap2.last);
				boolean start=(cur1==first);
				HeapNode p=cur2.next;
				numTree--;
				if (carry!=null){
					carry.next=cur1.next; //replacing cur1 with carry
					prev1.next=carry;
					prev1=carry;
					numTree++;
					if (start){
						first=carry;
						start=false;
					}
				}
				else{
					prev1.next=cur1.next;
				}
				carry=linkTrees(cur1,cur2); //turning cur1 (linked with cur2) into carry
				if (end1){ //add carry to the end of self
					prev1.next=carry;
					carry.next=first;
					last=carry;
					n+=1<<(r+1);//carry is of rank r+1
					carry=null;
					numTree++;
				}
				else if (end2){//add carry to the end of heap2, notice - cannot happen if cur1 is last in self - self has more trees.
					m+=1<<(r+1); //carry is of rank r+1
					heap2.last=carry;
					p=carry;
					carry=null;
				}
				if (start){
					first=prev1.next;
				}
				cur1=prev1.next;
				cur2=p;//cur2.next from before linking, or carry if end2
				n=(n>>(r+1))<<(r+1);
				m=(m>>(r+1))<<(r+1);
			}
			r++;
		}
		if (n>>r==0){//concate the rest of heap2 to heap1
			last.next=cur2;
			last=heap2.last;
		}
		last.next=first;
		size+=heap2.size;
		locateNextMin(); //update min and prevMin for self
		return;  		
	}
	/* ******Runtime Analysis for meld****** 
	* There is one loop - line 201. In each Iteration of the loop, r grows by at least 1, so the maximum total iterations are 
	* the maximum value of r=min(log(n),log(m)) where n=size(self),m=size(heap2). 
	* notice the call for locateNextmin in line 321, which adds O(log(n+m))=O(max(log(n)+log(m))) to the Runtime.
	* Besides, every other line runs at constant time, so runtime is O(r+log(n+m))=O(log(n+m))=O(log(n_new)).
	*/

	private HeapNode linkTrees(HeapNode tree1, HeapNode tree2){
		if (tree1.item.key>tree2.item.key){
			return linkTrees(tree2,tree1);//complexity still O(1) - max recursion depth is 1!
		}
		tree1.rank+=1;
		tree2.parent=tree1;
		if (tree1.child==null){
			tree2.next=tree2;
			tree1.child=tree2;
		}
		else{
			tree2.next=tree1.child.next;
			tree1.child.next=tree2;
			tree1.child=tree2;
		}
		return tree1;

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

	public String easyTester(){
		String str="";
		str="Heap Size="+size+".\n";
		str+="Number Of Trees="+numTree+".\n";
		str+="Minimal key in Heap is "+findMin().key+".\n";
		HeapNode cur=first;
		int i=2;
		str+="Tree 1: rank="+cur.rank+", root key="+cur.item.key+".\n";
		cur=cur.next;
		while (cur!=first){
			str+="Tree "+i+": rank="+cur.rank+", root key="+cur.item.key+".\n";
			i++;
			cur=cur.next;
		}
		return str;
	}
	public static void main(String[] args){
		BinomialHeap heap=new BinomialHeap();
		//System.out.println("Now for the stupid tests!!!\nnumTrees: func="+heap.numTrees()+" expected="+heap.numTree+"\nsize: func="+heap.size()+" expected="+heap.size+"\nempty: func="+heap.empty()+" expected="+(heap.size==0));
		HeapItem item15 =heap.insert(15,"15");
		heap.insert(40,"40");
		heap.insert(20,"20");
		heap.insert(35,"35");
		heap.insert(45,"45");
		heap.insert(31,"31");
		heap.insert(58,"58");
		heap.insert(67,"67");
		System.out.println(heap.easyTester());
		heap.insert(9,"9"); 
		heap.insert(33,"33");
		heap.insert(23,"23");
		heap.delete(item15);
		System.out.println("***************heap**************");
		System.out.println(heap.easyTester());
		BinomialHeap heap2=new BinomialHeap();
		int[] arr = {22,32,14,57,39,31,10,13,42,55,69};
		for (int i:arr) heap2.insert(i,""+i);
		System.out.println("***************heap2**************");
		System.out.println(heap2.easyTester());
		heap.meld(heap2);
		System.out.println("***************meld**************");
		System.out.println(heap.easyTester());
		heap.deleteMin();
		System.out.println(heap.easyTester());
		//System.out.println("Now for the stupid tests!!!\nnumTrees: func="+heap.numTrees()+" expected="+heap.numTree+"\nsize: func="+heap.size()+" expected="+heap.size+"\nempty: func="+heap.empty()+" expected="+(heap.size==0));


	}

}
