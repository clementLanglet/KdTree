package kdtree;

import java.util.ArrayList;

public class KdTree<Point extends PointI> //KdTree de points (qui descendent de PointI)
{
	/** A node in the KdTree (un noeud)
	 */
	//Underscore en C veut dire private
	public class KdNode 
	{
		KdNode child_left_, child_right_;
		Point pos_;
		int d_; 	/// dimension in which the cut occurs
		
		KdNode(Point p, int d){
			this.pos_ = p;
			this.d_ = d;
			this.child_left_ = null;
			this.child_right_ = null;
		}

		KdNode(Point p, int d, KdNode l_child, KdNode r_child){
			this.pos_ = p;
			this.d_ = d;
			this.child_left_ = l_child;
			this.child_right_ = r_child;
		}
		
		/** 
		 * if strictly negative the query point is in the left tree
		 * TODO: equality is problematic if we want a truly balanced tree
		 */
		int dist1D(Point p) { 
			return p.get(d_) - pos_.get(d_);
		}
	}
	
	/////////////////
    /// Attributs ///
    /////////////////

	private final int dim_; /// dimension of space
	private int n_points_; /// number of points in the KdTree
	
	private KdNode root_; /// root node of the KdTree

    //////////////////
    /// Constructor///
    //////////////////

	/** Initialize an empty kd-tree
	 */
	KdTree(int dim) {
		this.dim_ = dim;
		this.root_ = null;
		this.n_points_ = 0;
	}

	/** Initialize the kd-tree from the input point set
	 *  The input dimension should match the one of the points
	 */
	KdTree(int dim, ArrayList<Point> points, int max_depth) {
		this.dim_ = dim;
		// this.n_points_ = points.size(); inutile
		
		//TODO: replace by a balanced initialization
		this.n_points_=0;
		for(Point p : points) {
			insert(p);
		}
	
	}
	  
	/////////////////
	/// Accessors ///
	/////////////////

	int dimension() { return dim_; } //Rien devant int veut dire package protected

	int nb_points() { return n_points_; }

	void getPointsFromLeaf(ArrayList<Point> points) {
		getPointsFromLeaf(root_, points);
	}

	 
	///////////////
	/// Mutator ///
	///////////////

	/** Insert a new point in the KdTree.
	 */
	void insert(Point p) {
		n_points_ += 1;
		
		if(root_==null) 
			root_ = new KdNode(p, 0);
		
		KdNode node = getParent(p);
		if(node.dist1D(p)<0) {
			assert(node.child_left_==null);
			node.child_left_ = new KdNode(p, (node.d_+1)%dim_);
		} else {
			assert(node.child_right_==null);
			node.child_right_ = new KdNode(p, (node.d_+1)%dim_);
		}
	}
	void delete(Point p) {
		assert(false);
	}

	///////////////////////
	/// Query Functions ///
	///////////////////////

	/** Return the node that would be the parent of p if it has to be inserted in the tree
	 */
	KdNode getParent(Point p) {
		assert(p!=null);
		
		KdNode next = root_, node = null;

		while (next != null) {
			node = next;
			if ( node.dist1D(p) < 0 ){
				next = node.child_left_;
			} else {
				next = node.child_right_;
			}
		}
		
		return node;
	}
	
	/** Check if p is a point registered in the tree
	 */
	boolean contains(Point p) {
        return contains(root_, p);
	}

	/** Get the nearest neighbor of point p
	 */
    public Point getNN(Point p)
    {
    	assert(root_!=null);
        return getNN(root_, p, root_.pos_);
    }

	///////////////////////
	/// Helper Function ///
	///////////////////////

    /** Add the points in the leaf nodes of the subtree defined by root 'node'
     * to the array 'point'
     */
	private void getPointsFromLeaf(KdNode node, ArrayList<Point> points)
	{
		if(node.child_left_==null && node.child_right_==null) {
			points.add(node.pos_);
		} else {
		    if(node.child_left_!=null)
		    	getPointsFromLeaf(node.child_left_, points);
		    if(node.child_right_!=null)
		    	getPointsFromLeaf(node.child_right_, points);
		}
	 }
	
	/** Search for a better solution than the candidate in the subtree with root 'node'
	 *  if no better solution is found, return candidate
	 */
	 private Point getNN(KdNode node, Point point, Point candidate)
	 {
	    if ( point.sqrDist(node.pos_) <  point.sqrDist(candidate)) 
	    	candidate = node.pos_;

	    int dist_1D = node.dist1D(point);
	    KdNode n1, n2;
	    if( dist_1D < 0 ) {
	    	n1 = node.child_left_;
	    	n2 = node.child_right_;
	    } else {
	    	// start by the right node
	    	n1 = node.child_right_;
	    	n2 = node.child_left_;
	    }

	    if(n1!=null)
	    	candidate = getNN(n1, point, candidate);

	    if(n2!=null && dist_1D*dist_1D < point.sqrDist(candidate)) 
	    	candidate = getNN(n2, point, candidate);
		 
		 return candidate;
	 }
	 
	private boolean contains(KdNode node, Point p) {
        if (node == null) return false;
        if (p.equals(node.pos_)) return true;

        //TODO : assume the "property" is strictly verified
        if (node.dist1D(p)<0)
            return contains(node.child_left_, p);
        else
            return contains(node.child_right_, p);
	}
	
	public KdNode buildTree(ArrayList<Point> points, int depth, int max_depth) {
// TERMINAISON : 
// si points.size()==0 retourner null (sous-arbre vide)
		if(points.size() == 0){
			return null;
		};

// TRAITEMENT SPECIAL pour le probl�me de la quantization
// if depth == max_depth cr�er un noeud feuille comportant le barycentre des points restant
		if(depth == max_depth){
			int dim = dimension();
			int[] s = new int[dim]; //le barycentre
			for(int i = depth; i < points.size(); i++){ //le point actuel
				points.get(i).div(dim);
				for(int d = 0; d < dim; d++){ //d la dimension dans laquelle on r�cup�re valeur
					s[d] += points.get(i).get(d);
				}
			}
			Point p;
			p.v = s;
			KdNode bar = new KdNode(p, depth % dimension());
			return bar;
		}

// Calcul de la dimension de la coupe (il est possible de commencer par
// d=depth%3)
		int d = depth % dimension();
		
// Trier le tableau de point en fonction de la dimension choisi
// (cela permet d�obtenir la m�diane et son indice)
		points.sort((p1, p2) -> p1.get(d) - p2.get(d));
		int l = points.size();
		int i_med = l/2;
		Point med = points.get(i_med);
		

// Partager le tableau en deux tableaux (indice inf�rieur et sup�rieur � m�diane)
// left_points, right_points
		ArrayList<Point> left_points;
		ArrayList<Point> right_points;
		for(int i = 0; i < i_med; i++){
			left_points.add(points.get(i));
		}
		for(int i = i_med+1; i < l; i++){
			right_points.add(points.get(i));
		}

// Cr�er r�cursivement deux sous arbres
		KdNode left_child = buildTree(left_points,depth+1,max_depth);
		KdNode right_child = buildTree(right_points,depth+1,max_depth);

// Cr�er le nouveau noeud de profondeur depth et le retourner
		KdNode n = new KdNode(med, d);
		return n;
}
	
}


