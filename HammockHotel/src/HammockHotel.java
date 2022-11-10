/**
 * @version 20221104.2130
 */
public class HammockHotel {

    /** A simple nested class for linkable Nodes */
    class Node {
        String data;
        Node next;
        Node(String data, Node next) {
            this.data = data;
            this.next = next;
        }
    }  // nested class Node

    /** The underlying array of The Hammock Hotel and the default array size */
    private Node[] rooms;
    private static final int DEFAULT_ROOMS_SIZE = 4;

    /** The number of guests currently hanging for dear life in The Hammock Hotel */
    private int usage;

    /** The desired utilization (load factor) threshold for rehashing and its default */
    private double loadFactor;
    private static final double DEFAULT_LOAD_FACTOR = 2.0;

    /** Resizing factor for underlying array and its default */
    private int resizeFactor;
    private static final int DEFAULT_RESIZE_FACTOR = 2;


    /** Full constructor */
    public HammockHotel(int roomsSize, double loadFactor, int resizeFactor) {
        // The following three ternaries ensure that the passed arguments are legal;
        // If not, the default values will be used.
        roomsSize = (roomsSize > 0) ? roomsSize : DEFAULT_ROOMS_SIZE;
        loadFactor = (loadFactor > 1.0) ? loadFactor : DEFAULT_LOAD_FACTOR;
        resizeFactor = (resizeFactor > 1) ? resizeFactor : DEFAULT_RESIZE_FACTOR;
        this.rooms = new Node[roomsSize];
        this.usage = 0;
        this.loadFactor = loadFactor;
        this.resizeFactor = resizeFactor;
    }  // Full constructor


    /** Default constructor */
    public HammockHotel() {
        this(DEFAULT_ROOMS_SIZE, DEFAULT_LOAD_FACTOR, DEFAULT_RESIZE_FACTOR);
    }  // default constructor


    private int hashCode(String string) {
        // int hash = (int) (string.toUpperCase().charAt(0));
        int hashValue = 0;
        for (int i = 0; i < string.length(); i++) {
            hashValue = hashValue + (int) string.charAt(i);
        }
        return hashValue % this.rooms.length;
    }  // method hashCode


    /**
     * Adds a guest to the Hammock hotel by placing the guest to a hammock (or a Node) dangling
     * from a room specified by the hash value of the guest's name.
     *
     * @param guestName String with guest name.
     */
    public void add(String guestName) {
        int roomNumber = this.hashCode(guestName);
        /*
        rooms[i] is essentially the head of the linked list at this position. We
        are adding a new node to the linked list an making that node the new head.
        That's why the linked list at this position, represented by its head (rooms[i])
        is appended to the new node. The new node's next pointer is to node
        rooms[i], the present head of the linked list. And by placing that new
        node at rooms[i], we make the new node the head of the list.
         */
        this.rooms[roomNumber] = new Node(guestName, this.rooms[roomNumber]);
        // Update count of nodes
        this.usage++;
        // Check if utilization is over the threshold for rehashing
        if (this.utilization() > this.loadFactor) {
            // and if it is, rehash.
            this.rehash();
        }
    }  // method add


    /**
     * Rehashes the structure by replacing the underlying array with a larger one.
     */
    private void rehash() {
        // Move guests to a temporary array similar to underlying array
        Node[] oldRooms = this.rooms;
        // Resize the underlying array to something larger
        this.rooms = new Node[this.resizeFactor*oldRooms.length];
        // Very important to reset usage, so that we don't double count guests.
        this.usage = 0;
        // Go room by room in the temporary array
        for (int i = 0; i < oldRooms.length; i++) {
            // start with the first guest
            Node cursor = oldRooms[i];
            // For every guest in the room, add them to the new array
            while (cursor != null) {
                // this.add operates on this.room that just got upsized!
                this.add(cursor.data);
                cursor = cursor.next;
            }
        }
    }  // method rehash


    /**
     * Compute the current utilization of the structure.
     *
     * @return ratio of nodes to number of hotel rooms.
     */
    public double utilization() {
        return (double) this.usage / (double) this.rooms.length;
    }  // method utilization


    /**
     * String representation of the data structure
     * @return String for printing the contents of the data structure
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("\n\nYour weird hotel has %d rooms and currently %d guests.",
                this.rooms.length, this.usage));
        sb.append(String.format("\nThe utilization rate is %.2f and the rehash threshold is %.2f.\n",
                this.utilization(), this.loadFactor));
        for (int i = 0; i < this.rooms.length; i++) {
            sb.append(String.format("\nRoom[%02d]:", i));
            if (this.rooms[i] == null) {
                sb.append(String.format(" -- the room is empty --"));
            } else {
                Node cursor = this.rooms[i];
                while (cursor != null) {
                    sb.append(String.format("  --> %s", cursor.data));
                    cursor = cursor.next;
                }
            }
        }
        return sb.toString();
    }  // method toString


    /*

                        METHODS FOR THE ASSIGNMENT DUE 11/11/22

     */


    /**
     * Accessor for this.usage
     * @return int this.usage
     */
    public int getUsage() {
        return this.usage;
    }  // method getUsage


    /**
     * Checks if the specified guest is found in one of the rooms.
     *
     * @param guestName String with name of guest to search for
     *
     * @return true if guest is present on one of the hammocks, false otherwise.
     */
    public boolean contains(String guestName) {
        // Initialize return variable
        boolean found = false;
        // Room where guest should be found
        int assignedRoom = this.hashCode(guestName);
        // Explore the hammocks at this room.
        Node hammock = this.rooms[assignedRoom];
        // If the room is empty, the loop won't even run.
        while (!found && hammock != null) {
            // Is the guest in this hammock? If yes, the !found above will break the loop.
            found = hammock.data.equals(guestName);
            // Move to the next hammock, in case we need to try; if null, the loop will end.
            hammock = hammock.next;
        }
        return found;
    }  // method contains


    /**
     * Adds a guest to the hotel if no other guest with the same name is present.
     * The method returns true if the guest has been successfully added; false otherwise.
     *
     * @param guestName String with name of guest to try adding to the hotel
     *
     * @return true if guest was successfully added.
     */
    public boolean addUnique(String guestName) {
        // boolean return var
        boolean successfullyAdded = false;
        // if the guest is not present already
        if (!this.contains(guestName)) {
            // add the guest
            add(guestName);
            // change return condition
            successfullyAdded = true;
        }
        // return
        return successfullyAdded;
    }  // method addUnique

    /**
     * Are there any guests in the hotel?
     *
     * @return true if there are absolutely no guests in the hotel and false otherwise.
     */
    public boolean isEmpty() {
        // return variable initialized to false
        boolean isEmpty = false;
        // for loop to iterate through the rooms
        for (int i = 0; i < rooms.length-1; i++) {
            // if statement to check if current room is empty
            if (this.rooms[i] != null) {
                // change boolean to true
                isEmpty = true;
            }
        }
        // return
        return isEmpty;
    }  // method isEmpty


    /**
     * Create a String array with the guest names as its elements.
     *
     * @return String array with the names of all the guest.
     */
    public String[] toArray() {
        // index of the current room
        int index = 0;
        // array to return ( make as many spots as there are people (a.k.a. usage) )
        String[] guestNames = new String[usage];
        // first available position for guestNames
        int counter = 0;
        // while loop to iterate through all rooms
        while (index < rooms.length) {
            // Node of hammocks in the current room
            Node hammock = this.rooms[index];
            // while hammock is not empty
            while (hammock != null) {
                // put guest name in guestNames first available index
                guestNames[counter] = hammock.data;
                // increase first available index
                counter++;
                // make next person up next
                hammock = hammock.next;
            }
            // increase index to the next room
            index++;
        }
        // return the array
        return guestNames;
    }  // method toArray


    /**
     * Empty the hotel of all guests.
     */
    public void clear() {
        // call stats() to get number of people in each room
        int[] array = this.stats();
        // for loop to iterate through rooms
        for (int i = 0; i < rooms.length-1; i++) {
            // Node of hammocks in the room
            Node hammocks = this.rooms[i];
            // number of people in current room
            int peopleInRoom = array[i];
            // while there are people in the room and hammocks is not null
            while (peopleInRoom > 0 && hammocks != null) {
                // change the current hammock to null
                hammocks.data = null;
                // change current hammock to the next hammock
                hammocks = hammocks.next;
                // subtract usage by one person
                this.usage--;
            }
        }
    }  //  method clear


    /**
     * Create an array with the number of guests in each hotel room
     * @return int array with how many guests are in each room
     */
    public int[] stats() {
        // index of the current room
        int index = 0;
        // array to return
        int[] stats = new int[rooms.length];
        // for loop to iterate through rooms
        while (index < rooms.length) {
            // a node of hammocks within the room
            Node hammocks = this.rooms[index];
            // if the room is completely empty
            if (hammocks == null) {
                // put 0 people in current array spot
                stats[index] = 0;
            } else { // if room is not empty
                // counter
                int counter = 0;
                // while there are people in the room
                while (hammocks != null) {
                    // add counter
                    counter++;
                    // next hammock
                    hammocks = hammocks.next;
                }
                // put number of guests in array
                stats[index] = counter;
                // next room
            }
            // increase index to the next room
            index++;
        }
        // return array
        return stats;
    }  // method stats


}  // class HammockHotel
