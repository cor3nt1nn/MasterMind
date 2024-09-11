package fr.besqueutvilledieu.server.packet;
/*
 * Ensure that all packet have the same structure
 * 
 * 
 */
public interface Deliverable {
	
	// MEANING FOR DEBUG
	public String description();
	
	//FOR GENERICITY OF PACKET 
	public PacketType type();
	
	//DEFINE BEHAVIOR OF THE REQUEST MAYBE DEPRECIATE
//	public Object behavior(Object object);
	
}
