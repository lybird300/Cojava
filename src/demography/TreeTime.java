package demography;

import java.util.concurrent.RecursiveTask;

import segment.segWorker;

import nodes.node;

public class TreeTime extends RecursiveTask<Double> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 574697128318752850L;
	node aNode;
	segWorker segFactory;
	double point,parentTime;
	
	TreeTime(double aPoint, double aTime,node someNode,segWorker aSegWorker){
		aNode = someNode;
		segFactory = aSegWorker;
		parentTime = aTime;
		point = aPoint;
	}
	@Override
	protected Double compute() {
		double time;
		
		if(aNode == null)
			return 0.0;
		if(!segFactory.segContains(aNode.getSegment(), point))
			return 0.0;
		
		time = parentTime - aNode.getGen();
		TreeTime timeL = new TreeTime(point,aNode.getGen(),aNode.getDescendents()[0],segFactory);
		timeL.fork();
		TreeTime timeR = new TreeTime(point,aNode.getGen(),aNode.getDescendents()[1],segFactory);
		return time + timeR.compute() + timeL.join();
		
	}

}
