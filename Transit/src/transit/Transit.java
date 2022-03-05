package transit;

import java.util.ArrayList;
import java.util.Queue;

import org.w3c.dom.Node;

/**
 * This class contains methods which perform various operations on a layered linked
 * list to simulate transit
 * 
 * @author Ishaan Ivaturi
 * @author Prince Rawal
 */
public class Transit {
	private TNode trainZero; // a reference to the zero node in the train layer

	/* 
	 * Default constructor used by the driver and Autolab. 
	 * DO NOT use in your code.
	 * DO NOT remove from this file
	 */ 
	public Transit() { trainZero = null; }

	/* 
	 * Default constructor used by the driver and Autolab. 
	 * DO NOT use in your code.
	 * DO NOT remove from this file
	 */
	public Transit(TNode tz) { trainZero = tz; }
	
	/*
	 * Getter method for trainZero
	 *
	 * DO NOT remove from this file.
	 */
	public TNode getTrainZero () {
		return trainZero;
	}

	/**
	 * Makes a layered linked list representing the given arrays of train stations, bus
	 * stops, and walking locations. Each layer begins with a location of 0, even though
	 * the arrays don't contain the value 0. Store the zero node in the train layer in
	 * the instance variable trainZero.
	 * 
	 * @param trainStations Int array listing all the train stations
	 * @param busStops Int array listing all the bus stops
	 * @param locations Int array listing all the walking locations (always increments by 1)
	 */
	public TNode walkTo(TNode start, int end){
		TNode currentLoc = start;
		for(; currentLoc!=null&&currentLoc.getLocation()<end;currentLoc=currentLoc.getNext());
		if (currentLoc!=null&&currentLoc.getLocation() == end){
			return currentLoc;
		}
		return null;
	}

	public void addStop(TNode start, int loc) {
		TNode current = start;
		TNode dwn;
		
		while(current.getLocation()<loc){
			if (current.getNext().getLocation()>loc){
				//create new stop
				dwn = walkTo(current.getDown(), loc);
				TNode newStop = new TNode(loc, current.getNext(), dwn);
				current.setNext(newStop);
			}
			current=current.getNext();
		}
 
	 }

	/* public TNode copyNode(TNode target){
		
		TNode nextCopy = null;
		TNode belowCopy = null;
		if (target.getNext()!=null){
			 nextCopy = copyNode(target.getNext());
		}
		if (target.getDown()!=null){
			belowCopy = copyNode(target.getDown());
		}
		TNode copy = new TNode(target.getLocation());
		copy.setNext(nextCopy);
		copy.setDown(belowCopy);
		return copy;
	} */

	public ArrayList<TNode> mapTo(TNode start, int end){
		ArrayList<TNode> map = new ArrayList<>();
		TNode currentLoc = start;
		for(; currentLoc!=null&&currentLoc.getLocation()<=end;currentLoc=currentLoc.getNext()){
			map.add(currentLoc);
		}
		return map;
	}

	public void makeList(int[] trainStations, int[] busStops, int[] locations) {
		
		int walking_location;
		int bus_location = 0;
		int train_location = 0;
		
		TNode firstloc = new TNode(0);
		TNode firstBus = new TNode(0,null,firstloc);
		trainZero = new TNode(0,null,firstBus);

		TNode loc_node=null, bus_node=null, train_node=null;
		TNode prev_loc_node = firstloc, prev_bus_node = firstBus, prev_train_node = trainZero;
		
		for (int location_idx = 0, bus_idx = 0, train_idx = 0; location_idx  < locations.length; location_idx++){
			walking_location = locations[location_idx];
			if (bus_idx<busStops.length){
			bus_location = busStops[bus_idx];
			}
			if (train_idx<trainStations.length){
			train_location = trainStations[train_idx];
			}

			//Hook up location
			loc_node = new TNode(walking_location);
			if (prev_loc_node != null)
				prev_loc_node.setNext(loc_node);
			prev_loc_node = loc_node;
			// Hook up bus
			if ( walking_location == bus_location){

				// Creates the bus node, sets loc_node as down
				bus_node = new TNode(bus_location, null, loc_node);
				if (prev_bus_node != null)
					prev_bus_node.setNext(bus_node);
				prev_bus_node = bus_node;
				++bus_idx;


				// Hook up train
				if (bus_location == train_location){
					train_node = new TNode(train_location, null, bus_node);
					if (prev_train_node != null)
						prev_train_node.setNext(train_node);
					prev_train_node = train_node;
					++train_idx;
				}
			}
		}
		System.out.println();
	}
	
	/**
	 * Modifies the layered list to remove the given train station but NOT its associated
	 * bus stop or walking location. Do nothing if the train station doesn't exist
	 * 
	 * @param station The location of the train station to remove
	 */
	public void removeTrainStation(int station) {
		TNode currentStop = trainZero.getNext();
		TNode prev=trainZero;
		while(currentStop!=null){
			if (currentStop.getLocation()==station){
				prev.setNext(currentStop.getNext());
			}
			prev = currentStop;
			currentStop = currentStop.getNext();
		}
	}

	/**
	 * Modifies the layered list to add a new bus stop at the specified location. Do nothing
	 * if there is no corresponding walking location.
	 * 
	 * @param busStop The location of the bus stop to add
	 */
	public void addBusStop(int busStop) {
	   TNode busZero = trainZero.getDown();
	   addStop(busZero, busStop);
	}
	
	/**
	 * Determines the optimal path to get to a given destination in the walking layer, and 
	 * collects all the nodes which are visited in this path into an arraylist. 
	 * 
	 * @param destination An int representing the destination
	 * @return
	 */
	public ArrayList<TNode> bestPath(int destination) {
		ArrayList<TNode> path = new ArrayList<>();
		ArrayList<TNode> trains=mapTo(trainZero, destination);
		ArrayList<TNode> busses=mapTo(trains.get(trains.size()-1).getDown(), destination);
		ArrayList<TNode> locs=mapTo(busses.get(busses.size()-1).getDown(), destination);
		path.addAll(trains);
		path.addAll(busses);
		path.addAll(locs);
	    return path;
	}

	/**
	 * Returns a deep copy of the given layered list, which contains exactly the same
	 * locations and connections, but every node is a NEW node.
	 * 
	 * @return A reference to the train zero node of a deep copy
	 */
	public TNode copy(TNode target){
		if (target!=null){
		TNode copy = new TNode(target.getLocation());
		return copy;
		}
		return null;
	}

	public TNode duplicate(){
		TNode busZero = trainZero.getDown();
		TNode locZero = busZero.getDown();
		TNode cursor;
		TNode lzCopy = copy(locZero);
		TNode bzCopy = copy(busZero);
		TNode tzCopy = copy(trainZero);
		tzCopy.setDown(bzCopy);
		bzCopy.setDown(lzCopy);
		TNode prevCopy = lzCopy;
		//copy locs layer
		cursor = locZero.getNext();
		while (cursor!=null){
			TNode copy = copy(cursor);
			prevCopy.setNext(copy);
			cursor=cursor.getNext();
			prevCopy = copy;
		}
		//copy bus layer
		cursor = busZero.getNext();
		prevCopy = bzCopy;
		while (cursor!=null){
			TNode copy = copy(cursor);
			if (cursor.getDown()!=null){
				copy.setDown(walkTo(lzCopy, cursor.getLocation()));
			}
			prevCopy.setNext(copy);
			cursor=cursor.getNext();
			prevCopy = copy;
		}
		//copy train layer
		cursor = trainZero.getNext();
		prevCopy = tzCopy;
		while (cursor!=null){
			TNode copy = copy(cursor);
			if (cursor.getDown()!=null){
				copy.setDown(walkTo(bzCopy, cursor.getLocation()));
			}
			prevCopy.setNext(copy);
			cursor=cursor.getNext();
			prevCopy = copy;
		}
		return tzCopy;
	}
	/* public TNode duplicate(){

		TNode tz = trainZero;
		TNode bz = tz.getDown();
		TNode wz = bz.getDown();
		TNode copy = null, below = null, prev = null;
		TNode[] original_zeros = {tz, bz, wz};


		TNode[] copied_zeros = {new TNode(0), new TNode(0), new TNode(0)};
		for (int i = 0; i < original_zeros.length; i++){
			// Start from walking layer and work right then go up a layer
			TNode curr = original_zeros[original_zeros.length - i - 1];
			while (curr != null){
				copy = new TNode(curr.getLocation());

				// Set down if applicable
				if (i > 0) {
					below = walkTo(copied_zeros[i-1], curr.getLocation());
					copy.setDown(below);
				}
				
				// Set point the previous copy to the current copy
				if (prev != null)
					prev.setNext(copy);
				prev = copy;

				// Move right
				curr = curr.getNext();
			}
		}
		return copied_zeros[0];
	} */

	/* public TNode duplicate() {
		int walking_location;
		int bus_location = 0;
		int train_location = 0;
		
		TNode firstloc = new TNode(0);
		TNode firstBus = new TNode(0,null,firstloc);
		trainZero = new TNode(0,null,firstBus);

		TNode loc_node=null, bus_node=null, train_node=null;
		TNode prev_loc_node = firstloc, prev_bus_node = firstBus, prev_train_node = trainZero;
		
		for (int location_idx = 0, bus_idx = 0, train_idx = 0; location_idx  < locations.length; location_idx++){
			walking_location = locations[location_idx];
			if (bus_idx<busStops.length){
			bus_location = busStops[bus_idx];
			}
			if (train_idx<trainStations.length){
			train_location = trainStations[train_idx];
			}

			//Hook up location
			loc_node = new TNode(walking_location);
			if (prev_loc_node != null)
				prev_loc_node.setNext(loc_node);
			prev_loc_node = loc_node;
			// Hook up bus
			if ( walking_location == bus_location){

				// Creates the bus node, sets loc_node as down
				bus_node = new TNode(bus_location, null, loc_node);
				if (prev_bus_node != null)
					prev_bus_node.setNext(bus_node);
				prev_bus_node = bus_node;
				++bus_idx;


				// Hook up train
				if (bus_location == train_location){
					train_node = new TNode(train_location, null, bus_node);
					if (prev_train_node != null)
						prev_train_node.setNext(train_node);
					prev_train_node = train_node;
					++train_idx;
				}
			}
		}
		System.out.println();
	} */

	/**
	 * Modifies the given layered list to add a scooter layer in between the bus and
	 * walking layer.
	 * 
	 * @param scooterStops An int array representing where the scooter stops are located
	 */
	public void addScooter(int[] scooterStops) {
		TNode busZero = trainZero.getDown();
		TNode locZero = busZero.getDown();
		TNode scooterZero = new TNode(0,null,locZero);
		busZero.setDown(scooterZero);
		TNode current = scooterZero;
		TNode dwn;
		for(int stop = 0; stop<scooterStops.length; stop++){
			TNode currentBus = walkTo(busZero, scooterStops[stop]);
			dwn = walkTo(locZero, scooterStops[stop]);
			TNode newScoot = new TNode(scooterStops[stop], null, dwn);
			if (currentBus!=null){
			currentBus.setDown(newScoot);
			}
			current.setNext(newScoot);
			current=current.getNext();
		}
	}

	/**
	 * Used by the driver to display the layered linked list. 
	 * DO NOT edit.
	 */
	public void printList() {
		// Traverse the starts of the layers, then the layers within
		for (TNode vertPtr = trainZero; vertPtr != null; vertPtr = vertPtr.getDown()) {
			for (TNode horizPtr = vertPtr; horizPtr != null; horizPtr = horizPtr.getNext()) {
				// Output the location, then prepare for the arrow to the next
				StdOut.print(horizPtr.getLocation());
				if (horizPtr.getNext() == null) break;
				
				// Spacing is determined by the numbers in the walking layer
				for (int i = horizPtr.getLocation()+1; i < horizPtr.getNext().getLocation(); i++) {
					StdOut.print("--");
					int numLen = String.valueOf(i).length();
					for (int j = 0; j < numLen; j++) StdOut.print("-");
				}
				StdOut.print("->");
			}

			// Prepare for vertical lines
			if (vertPtr.getDown() == null) break;
			StdOut.println();
			
			TNode downPtr = vertPtr.getDown();
			// Reset horizPtr, and output a | under each number
			for (TNode horizPtr = vertPtr; horizPtr != null; horizPtr = horizPtr.getNext()) {
				while (downPtr.getLocation() < horizPtr.getLocation()) downPtr = downPtr.getNext();
				if (downPtr.getLocation() == horizPtr.getLocation() && horizPtr.getDown() == downPtr) StdOut.print("|");
				else StdOut.print(" ");
				int numLen = String.valueOf(horizPtr.getLocation()).length();
				for (int j = 0; j < numLen-1; j++) StdOut.print(" ");
				
				if (horizPtr.getNext() == null) break;
				
				for (int i = horizPtr.getLocation()+1; i <= horizPtr.getNext().getLocation(); i++) {
					StdOut.print("  ");

					if (i != horizPtr.getNext().getLocation()) {
						numLen = String.valueOf(i).length();
						for (int j = 0; j < numLen; j++) StdOut.print(" ");
					}
				}
			}
			StdOut.println();
		}
		StdOut.println();
	}
	
	/**
	 * Used by the driver to display best path. 
	 * DO NOT edit.
	 */
	public void printBestPath(int destination) {
		ArrayList<TNode> path = bestPath(destination);
		for (TNode vertPtr = trainZero; vertPtr != null; vertPtr = vertPtr.getDown()) {
			for (TNode horizPtr = vertPtr; horizPtr != null; horizPtr = horizPtr.getNext()) {
				// ONLY print the number if this node is in the path, otherwise spaces
				if (path.contains(horizPtr)) StdOut.print(horizPtr.getLocation());
				else {
					int numLen = String.valueOf(horizPtr.getLocation()).length();
					for (int i = 0; i < numLen; i++) StdOut.print(" ");
				}
				if (horizPtr.getNext() == null) break;
				
				// ONLY print the edge if both ends are in the path, otherwise spaces
				String separator = (path.contains(horizPtr) && path.contains(horizPtr.getNext())) ? ">" : " ";
				for (int i = horizPtr.getLocation()+1; i < horizPtr.getNext().getLocation(); i++) {
					StdOut.print(separator + separator);
					
					int numLen = String.valueOf(i).length();
					for (int j = 0; j < numLen; j++) StdOut.print(separator);
				}

				StdOut.print(separator + separator);
			}
			
			if (vertPtr.getDown() == null) break;
			StdOut.println();

			for (TNode horizPtr = vertPtr; horizPtr != null; horizPtr = horizPtr.getNext()) {
				// ONLY print the vertical edge if both ends are in the path, otherwise space
				StdOut.print((path.contains(horizPtr) && path.contains(horizPtr.getDown())) ? "V" : " ");
				int numLen = String.valueOf(horizPtr.getLocation()).length();
				for (int j = 0; j < numLen-1; j++) StdOut.print(" ");
				
				if (horizPtr.getNext() == null) break;
				
				for (int i = horizPtr.getLocation()+1; i <= horizPtr.getNext().getLocation(); i++) {
					StdOut.print("  ");

					if (i != horizPtr.getNext().getLocation()) {
						numLen = String.valueOf(i).length();
						for (int j = 0; j < numLen; j++) StdOut.print(" ");
					}
				}
			}
			StdOut.println();
		}
		StdOut.println();
	}
}
