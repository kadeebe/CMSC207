package application;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

class Graph
{
	public List<Vertex> vertices;
	public List<List<Edge>> edge;
	public List<Edge> mst;
	
	public Graph()
	{
	 edge = new ArrayList<List<Edge>>();
	 mst = new ArrayList<Edge>();
	 vertices = new ArrayList<Vertex>();
	}
	
	public void addVertex(Vertex vertices)
	{
		this.vertices.add(vertices);
	}
	 
}

class Edge
{
	Vertex a;
	Vertex b;
	 int beg;
	 int end;
	 double weight;
	 
	 public Edge(Vertex beg, Vertex end)
	 {
		 a = beg;
		 b = end;
		 this.beg = a.getIndex();
		 this.end = b.getIndex();
		 this.weight = a.distance(b);
	 }
}

class Vertex
{
	private static int count = 1;
	private int index = count - 1;
	private double xCoord;
	private double yCoord;
	
	public Vertex(int xCoord, int yCoord)
	{
		this.xCoord = xCoord;
		this.yCoord = yCoord;
		count++;
	}
	
	public Vertex(double xCoord, double yCoord)
	{
		this.xCoord = xCoord;
		this.yCoord = yCoord;
		count++;
	}
	
	public double distance(Vertex argument)
	{
		return Math.sqrt(Math.pow(this.xCoord - argument.xCoord, 2) + Math.pow(this.yCoord - argument.yCoord, 2));
	}
	
	public int getIndex()
	{
		return index;
	}
	
	public int getCount()
	{
		return count;
	}
	
	public double getXCoord()
	{
		return xCoord;
	}
	
	public double getYCoord()
	{
		return yCoord;
	}
}

class UnionFind
{
	private int[] parents;
	private int[] ranks;
	
	public UnionFind(int n)
	{
		parents = new int[n];
		ranks = new int[n];
		
		for (int i = 0; i < n; i++)
		{
			parents[i] = i;
			ranks[i] = 0;
		}
	}
	
	public int find(int i)
	{
		if (parents[i] == i)
		{
			return i;
		}
		
		return find(parents[i]);
	}
	
	public void union(int i, int j)
	{
		int iParent = find(i);
		int jParent = find(j);
		if (iParent == jParent)
		{
			return;
		}
		
		if(ranks[iParent] < ranks[jParent])
		{
			parents[iParent] = jParent;
		}
		else if (ranks[iParent] > ranks[jParent])
		{
			parents[jParent] = iParent;
		}
		else
		{
			parents[jParent] = iParent;
			ranks[iParent]++;
		}
	}
	
	public int treeCount()
	{
		HashSet<Integer> count = new HashSet<Integer>();
		
		for (int i = 0; i < parents.length; i++)
		{
			count.add(this.find(i));
		}
		
		return count.size();
	}
}

public class BoruvkaMST 
{
	private Graph graph;
	private static double totalWeight = 0;
	Edge [] cheapest;
	UnionFind ufind;
	List<List<Edge>> mstEdges = new ArrayList<List<Edge>>();
	int b=0;
	
	public BoruvkaMST(Graph graph)
	{
		this.graph = graph;
		ufind = new UnionFind(graph.vertices.size());
		
		for (int i = 0; i < graph.vertices.size(); i++)
		{
			graph.edge.add(new ArrayList<Edge>());
			for (int j = 0; j < graph.vertices.size(); j++)
			{
				graph.edge.get(i).add(new Edge(graph.vertices.get(i), graph.vertices.get(j)));
			}
			
		}
		
		cheapest = new Edge[graph.edge.size()];
		
		for (int i = 0; i < cheapest.length; i++)
		{
			cheapest[i] = null;
		}
	}
	
	public double getTotalWeight()
	{
		return totalWeight;
	}
	
	public int getNumOfTrees()
	{
		return ufind.treeCount();
	}
	
	public List<List<Edge>> iteration()
	{
		while (ufind.treeCount() > 1)
		{
			
			for (int index = 0; index < graph.vertices.size(); index++)
			{
				for (Edge e : graph.edge.get(index))
				{
					int begParent = ufind.find(e.beg);
					int endParent = ufind.find(e.end);
					
					if(e.weight != 0)
					{
						if (begParent != endParent)
						{
							if (cheapest[begParent] == null)
							{
								cheapest[begParent] = e;
							}
							else
							{
								cheapest[begParent] = cheapest[begParent].weight > e.weight ? e : cheapest[begParent];
							}
							if (cheapest[endParent] == null)
							{
								cheapest[endParent] = e;
							}
							else
							{
								cheapest[endParent] = cheapest[endParent].weight > e.weight ? e : cheapest[endParent];
							}
						}
					}
				}					
			}
			
			for (int i = 0; i < cheapest.length; i++)
			{
				Edge edge = cheapest[i];
				
				if (edge != null)
				{
					int beg = edge.beg;
					int end = edge.end;
					double weight = edge.weight;
					
					if (ufind.find(beg) != ufind.find(end))
					{
						graph.mst.add(edge);
						totalWeight += weight;
						ufind.union(beg, end);
					}
				}
			}
			
			mstEdges.add(new ArrayList<Edge>());
			
			for (int i = 0; i < graph.mst.size(); i++)
			{
				mstEdges.get(b).add(graph.mst.get(i));
			}
			
			b=b+1;
			
			for (int i = 0; i < cheapest.length; i++)
			{
				cheapest[i] = null;
			}
		}
		
		return mstEdges;
	}
	
	public static void main(String[] args)
	{
		Graph graph = new Graph();
		List<List<Edge>> tempGraph;
		Vertex one = new Vertex(1,0);
		Vertex two = new Vertex(0,2);
		Vertex three = new Vertex (0,4);
		Vertex four = new Vertex (0,5);
		Vertex five = new Vertex (0,0);
		
		Vertex [] temp = {one, two, three, four, five};
		
		for (int i = 0; i < temp.length; i++)
		{
			graph.addVertex(temp[i]);
		}
		
		UnionFind ufind = new UnionFind(graph.vertices.size());		
		
		BoruvkaMST bor = new BoruvkaMST(graph);
		
		tempGraph = bor.iteration();
		
		System.out.println(bor.getTotalWeight() + "+" + bor.getNumOfTrees());
		
		for (List<Edge> i : tempGraph)
		{
			System.out.println(i);
			
		}
	}
	
}
