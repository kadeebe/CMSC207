package application;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class BoruvkaGraph extends JPanel {

    private int width = 800;
    private int heigth = 400;
    private int padding = 25;
    private int labelPadding = 25;
    private Color lineColor = new Color(44, 102, 230, 180);
    private Color pointColor = new Color(50, 50, 50, 180);
    private Color gridColor = new Color(200, 200, 200, 200);
    private static final Stroke GRAPH_STROKE = new BasicStroke(2f);
    private int pointWidth = 4;
    private int numberYDivisions = 10;
    private int numberXDivisions = 10;
    private List<Double> scores;
    private List<Double> scores2;
    private List<List<Edge>> edges;
    private int index = 0;
    private Timer timer;

    public BoruvkaGraph(List<Double> scores, List<Double> scores2, List<List<Edge>> edges) {
        this.scores = scores;
        this.scores2 = scores2;
        this.edges = edges;
        ActionListener taskPerformer=new ActionListener() {
        	   public void actionPerformed(ActionEvent ae) {
        		   if (index == edges.size() - 1)
        		   {
        			   timer.stop();
        		   }
        	      index++;
        	      repaint();
        	   }
        	};
        	timer = new Timer(3000,taskPerformer);
        	timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        double xScale = ((double) getWidth() - 2 * padding - labelPadding) / (getMaxScore(scores) - getMinScore(scores));
        double yScale = ((double) getHeight() - 2 * padding - labelPadding) / (getMaxScore(scores2) - getMinScore(scores2));

        List<Point2D.Double> graphPoints = new ArrayList<>();
        for (int i = 0; i < scores.size(); i++) {
            double x1 = getWidth() -((getMaxScore(scores) - scores.get(i)) * xScale + padding);
            double y1 = ((getMaxScore(scores2) - scores2.get(i)) * yScale + padding);
            graphPoints.add(new Point2D.Double(x1, y1));
        }

        // draw white background
        g2.setColor(Color.WHITE);
        g2.fillRect(padding + labelPadding, padding, getWidth() - (2 * padding) - labelPadding, getHeight() - 2 * padding - labelPadding);
        g2.setColor(Color.BLACK);

        // create hatch marks and grid lines for y axis.
        for (int i = 0; i < numberYDivisions + 1; i++) {
            int x0 = padding + labelPadding;
            int x1 = pointWidth + padding + labelPadding;
            int y0 = getHeight() - ((i * (getHeight() - padding * 2 - labelPadding)) / numberYDivisions + padding + labelPadding);
            int y1 = y0;
            if (scores2.size() > 0) {
                g2.setColor(gridColor);
                g2.drawLine(padding + labelPadding + 1 + pointWidth, y0, getWidth() - padding, y1);
                g2.setColor(Color.BLACK);
                String yLabel = ((int) ((getMinScore(scores2) + (getMaxScore(scores2) - getMinScore(scores2)) * ((i * 1.0) / numberYDivisions)) * 100)) / 100.0 + "";
                FontMetrics metrics = g2.getFontMetrics();
                int labelWidth = metrics.stringWidth(yLabel);
                g2.drawString(yLabel, x0 - labelWidth - 5, y0 + (metrics.getHeight() / 2) - 3);
            }
            g2.drawLine(x0, y0, x1, y1);
        }

        // and for x axis
        for (int i = 0; i < numberXDivisions + 1; i++) {
            if (scores.size() > 1) {
                int x0 = ((i * (getWidth() - padding * 2 - labelPadding)) / numberXDivisions + padding + labelPadding); 
                int x1 = x0;
                int y0 = getHeight() - padding - labelPadding;
                int y1 = y0 - pointWidth;
                if ((i % ((int) ((scores.size() / 20.0)) + 1)) == 0) {
                    g2.setColor(gridColor);
                    g2.drawLine(x0, getHeight() - padding - labelPadding - 1 - pointWidth, x1, padding);
                    g2.setColor(Color.BLACK);
                    String xLabel = ((int) ((getMinScore(scores) + (getMaxScore(scores) - getMinScore(scores)) * ((i * 1.0) / numberXDivisions)) * 100)) / 100.0 + "";
                    FontMetrics metrics = g2.getFontMetrics();
                    int labelWidth = metrics.stringWidth(xLabel);
                    g2.drawString(xLabel, x0 - labelWidth / 2, y0 + metrics.getHeight() + 3);
                }
                g2.drawLine(x0, y0, x1, y1);
            }
        }

        // create x and y axes 
        g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, padding + labelPadding, padding);
        g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, getWidth() - padding, getHeight() - padding - labelPadding);

        Stroke oldStroke = g2.getStroke();
        
        g2.setStroke(oldStroke);
        g2.setColor(pointColor);
        for (int i = 0; i < graphPoints.size(); i++) {
            double x = graphPoints.get(i).x - pointWidth;
            double y = graphPoints.get(i).y - pointWidth;
            int ovalW = pointWidth * 2;
            int ovalH = pointWidth * 2;
            g2.draw(new Ellipse2D.Double(x, y, ovalW, ovalH));
        
        
	        g2.setColor(lineColor);
	        g2.setStroke(GRAPH_STROKE);
	        	        
	        if (index > 0)
	        {
	        	for (Edge e2 : edges.get(index -1))
		    	{
		        	int k = e2.beg;
		        	int j = e2.end;
		        	double x1 = graphPoints.get(k).x;
		            double y1 = graphPoints.get(k).y;
		            double x2 = graphPoints.get(j).x;
		            double y2 = graphPoints.get(j).y;
		    		g2.draw(new Line2D.Double(x1, y1, x2, y2));
		    	}
	        }       
        }
    }

    
//    @Override
//    public Dimension getPreferredSize() {
//        return new Dimension(width, heigth);
//    }
    private double getMinScore(List<Double> s) {
        double minScore = Double.MAX_VALUE;
        for (Double score : s) {
            minScore = Math.min(minScore, score);
        }
        return minScore;
    }

    private double getMaxScore(List<Double> s) {
        double maxScore = Double.MIN_VALUE;
        for (Double score : s) {
            maxScore = Math.max(maxScore, score);
        }
        return maxScore;
    }

    public void setScores(List<Double> scores) {
        this.scores = scores;
        invalidate();
        this.repaint();
    }

    public List<Double> getScores() {
        return scores;
    }
    
    public List<Double> getScores2() {
    	return scores2;
    }

    private static void createAndShowGui(Vertex [] v, List<List<Edge>> e) {
        List<Double> scores = new ArrayList<>();
        List<Double> scores2 = new ArrayList<>();
       for (int i = 0; i < v.length; i++) {
            scores.add((double) v[i].getXCoord());
            scores2.add((double) v[i].getYCoord());
            
           //System.out.println(scores.get(i));
            //System.out.println(scores2.get(i));
            //System.out.println();
        }
        BoruvkaGraph mainPanel = new BoruvkaGraph(scores, scores2, e);
        BoruvkaGraph mainPanel2 = new BoruvkaGraph(scores, scores2, e);
        mainPanel.setPreferredSize(new Dimension(800, 600));
        JFrame frame = new JFrame("DrawGraph");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(mainPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
        frame.setContentPane(mainPanel2);
        frame.revalidate();
        frame.repaint();
        
        frame.setContentPane(mainPanel);
        frame.revalidate();
        frame.repaint();
    }

    public static void main(String[] args) {
    	Graph graph = new Graph();
		List<List<Edge>> tempGraph;
		Vertex [] temp = new Vertex[20];
		Random rand = new Random();
		for (int i = 0; i < temp.length; i++)
		{
			temp[i] = new Vertex(5 * rand.nextDouble(), 5 * rand.nextDouble());
		}
		/*Vertex one = new Vertex(.1,1);
		Vertex two = new Vertex(.2,1);
		Vertex three = new Vertex (.1,4);
		Vertex four = new Vertex (.2,5);
		Vertex five = new Vertex (.1,1);
		Vertex six = new Vertex (.1, 3);*/
		
		//Vertex [] temp = {one, two, three, four, five, six};
		
		for (int i = 0; i < temp.length; i++)
		{
			graph.addVertex(temp[i]);
		}
		
		UnionFind ufind = new UnionFind(graph.vertices.size());
		
		BoruvkaMST bor = new BoruvkaMST(graph);
		
		tempGraph = bor.iteration();
		
		System.out.println(bor.getTotalWeight() + "+" + bor.getNumOfTrees());
		
		
		System.out.println(tempGraph.size());
		
			SwingUtilities.invokeLater(new Runnable() 
		{
         public void run() 
         {
        		 createAndShowGui(temp, tempGraph);
        		 
        	
         }
		}
			);
		
      
   }
}