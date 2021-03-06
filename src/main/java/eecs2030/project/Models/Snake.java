package eecs2030.project.Models;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;

import com.google.common.hash.HashCode;
import eecs2030.project.Enums.Difficulty;
import eecs2030.project.Enums.Directions;
import eecs2030.project.Utilities.Constants;

/**
 * A simple snake class
 * 
 * @author jianxiongwang
 *
 */
public class Snake {
	// Snake locations
	private final LinkedList<Tile> tiles = new LinkedList<Tile>();
	// Snake moving direction
	private Directions direction = Directions.EAST;
	// Snake starterLength
	private int starterLength = Constants.START_SNAKE_LENGTH;
	// Snake remaining gains (if greater than 0, every movement, snake starterLength will increase by 1)
	private int remainGains = 0;
	
	private int score = 0;
	private boolean isAlive = true;

    /**
	 * Constructor
	 */
	public Snake() {
		this(0);
	}

    /**
     * Constructor with providing score
     *
     * @param score initial snake score
     */
	public Snake(int score) {
	    this.score = score;
        direction = Directions.EAST;
        starterLength = 3;
        int xOffsetUnits = 2;
        int yOffset = Constants.DOT_SIZE * 3;
        for (int i = 0; i< starterLength; i++)
            tiles.add(new Tile(Constants.DOT_SIZE*(starterLength -i-1 +xOffsetUnits), yOffset));
    }
    
    /**
     * Perform snake move, update snake's tiles to new locations
     */
    public void move() {
    	Tile newHead = tiles.getFirst();
    	// remove last only if snake doesn't have remained gains
    	if (this.remainGains == 0) {
    		tiles.removeLast();
    	} else {
    		this.remainGains--;
    	}
    	switch (direction) {
    	case WEST: newHead = new Tile(newHead.getX()-Constants.DOT_SIZE, newHead.getY()); break;
    	case EAST: newHead = new Tile(newHead.getX()+Constants.DOT_SIZE, newHead.getY()); break;
    	case NORTH: newHead = new Tile(newHead.getX(), newHead.getY()-Constants.DOT_SIZE); break;
    	case SOUTH: newHead = new Tile(newHead.getX(), newHead.getY()+Constants.DOT_SIZE); break;
    	}
    	tiles.addFirst(newHead);
    }
    
    /**
     * Check if the snake contains a Tile
     * @param t the checking Tile
     * @return true if contains false not.
     */
    public boolean containsTile(Tile t) {
    	return this.tiles.contains(t);
    }
    
    //*********************************//
    //*********** Accessors ***********//
    //*********************************//

    /**
     * Check if snake hit itself or it's already died.
     *
     * @return true if snake still alive, and false otherwise.
     */
    public boolean isAlive() {
        if (tiles.lastIndexOf(tiles.getFirst()) > 0)
            this.isAlive = false;
        return this.isAlive;
    }
    
    /**
     * Get snake direction
     * 
     * @return snake direction
     */
    public Directions getDirection() {
    	return this.direction;
    }
    
    /**
     * Get snake head position
     * 
     * @return snake head position
     */
    public Tile getHead() {
    	return this.tiles.getFirst();
    }
    
    /**
     * Get snake score (starterLength + remaining gains)
     * 
     * @return snake score
     */
    public int getScore() {
    	return this.score;
    }

    /**
     * Get snake starterLength
     *
     * @return  starterLength of the snake
     */
    public int getStarterLength() { return this.tiles.size(); }
    /**
     * Get snake body tiles iterator
     * 
     * @return snake body tiles iterator
     */
    public Iterator<Tile> getBodyIterator() {
    	Iterator<Tile> iter = this.tiles.iterator();
    	iter.next(); // skip head;
    	return iter;
    }
    
    //********************************//
    //*********** Mutators ***********//
    //********************************//

    /**
     * Set snake alive status
     *
     * @param a new alive status
     */
    public void setAlive(boolean a) {
        this.isAlive = a;
    }

    /**
     * Set snake direction
     * 
     * @param d direction
     */
    public void setDirection(Directions d) {
    	this.direction = d;
    }
    
    /**
     * Add n gains to the snake (snake will increase its body by 1 unit each move until all gains are used)
     * 
     * @param n number of gains
     */
    public void gains(int n) {
    	this.remainGains += n;
    }
    
    /**
     * Add n scores to snake
     * 
     * @param n scores
     */
    public void addScore(int n) {
    	this.score += n;
    }

    /**
     * Add a buffer to snake
     *
     * @param b the buffer
     * @param difficulty current difficulty
     */
    public void addBuffer(Buffer b, Difficulty difficulty) {
        b.addTo(this, difficulty);
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) return false;
        Snake other = (Snake) obj;
        if (this.tiles.size() != other.tiles.size()
                || this.direction != other.direction
                || this.isAlive != other.isAlive
                || this.remainGains != other.remainGains
                || this.score != other.score) {
            return false;
        }
        for (int i=0; i<this.tiles.size(); i++) {
            if (!this.tiles.get(i).equals(other.tiles.get(i))) return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.direction.getValue(), this.isAlive, this.remainGains, this.score, this.tiles);
    }
}
