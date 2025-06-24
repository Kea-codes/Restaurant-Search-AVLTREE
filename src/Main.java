//Correctness (15 marks) ********************************
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Scanner;

public class Main {
	/*
	 * Reads all the current restaurants from the local database (binary file of a collection of restaurants)
	 * and builds an AVL Tree where Entries contain restaurant names as a key (for keyword searching) and 
	 * the restaurant object as the value.
	 * @param The path to the database
	 * 
	 */
	public static AVLTree<String,Restaurant> readRestaurantsFromDB(String path){
		AVLTree<String,Restaurant> restaurants = new AVLTree<>();
		File f = new File(path);
		ObjectInputStream ois = null;		
		
		try {
			ois = new ObjectInputStream(new FileInputStream(f));
			Restaurant r = null;
			while(true){
				try
				{
					r = (Restaurant)ois.readObject();
					System.out.println("Inserting: "+r.getName());
					String[] keywords = r.getName().split(" ");
					for(String keyword: keywords){
						if(!keyword.toLowerCase().equals("the"))
							restaurants.insert(keyword, r);
					}
				}
				catch(EOFException e){
					break;
				}
			}			
		} catch (FileNotFoundException e) {
			System.err.println("File not found, please check path");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("An error occured during reading from database");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.err.println("Restaurant not recognized");
			e.printStackTrace();
		}
		finally{
			try {
				ois.close();
			} catch (IOException e) {			
				e.printStackTrace();
			}
		}
		return restaurants;
	}	
	
	public static void main(String[] args) {		
		System.out.println("Building restaurant index");
		AVLTree<String,Restaurant> restaurantIndex = readRestaurantsFromDB("restaurant.db");
		
		System.out.println("Testing AVL insert");
		Restaurant r1 = new Restaurant("Student Center","Your go to place.", new GPSPoint(27.998614,-26.183000));
		restaurantIndex.insert(r1.getName(), r1);
		
		Restaurant r2 = new Restaurant("Chaf Pozi","A good old shisanyama place.", new GPSPoint(28.002175,-26.181158));
		restaurantIndex.insert(r2.getName(), r2);
		
		System.out.println("Testing AVL tree Search");
		System.out.println("Searching for: grill");
		BTPosition<Entry<String,Restaurant>> foundRestaurant = restaurantIndex.treeSearch("grill", restaurantIndex.root());		
		
		if(foundRestaurant.element()!= null)
			System.out.println("Restauarant found: \n"+foundRestaurant.element());
		else 
			System.out.println("Restaurant search failed");
		
		boolean go = true;
		Scanner sc = new Scanner(System.in);
		while(go){
			System.out.println("User search (q to quit): ");			
			String keyword = sc.next();
			if (!keyword.equalsIgnoreCase("q")){
				foundRestaurant = restaurantIndex.treeSearch(keyword, restaurantIndex.root());
				System.out.println("Restauarant found: \n"+foundRestaurant.element());
			}
			else go = false;			
		}
		System.out.println("Thank you for using restaurant keyword search");
		sc.close();
		
	}
}