package kdtree;

public abstract class PointI 
{
	protected int v[]; 


	public int get(int i) {
		return v[i];
	}
	
	public int getDim(){
		return v.length;	
	}

	public void add(PointI p) {
		for(int i=0;i<v.length; ++i) {
			v[i] += p.v[i];
		}
	}
	public void div(int d) {
		for(int i=0;i<v.length; ++i) {
			v[i] /= d;
		}
	}
	
	public int sqrDist(PointI p) {
		int res = 0;
		for(int i=0;i<v.length; ++i) {
			res += (v[i]-p.v[i])*(v[i]-p.v[i]);
		}
		return res;
	}

	public boolean equals(PointI p) {
		for(int i=0;i<v.length; ++i) {
			if(v[i]!=p.v[i]) return false;
		}
		return true;
	}
	
	int[] cloneValues() {
		return v.clone();		
	}
	
	// this method should be static but that's not possible in Java...
	public abstract PointI zero();
}
