import java.io.IOException;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;



/*
 Write a program to create an Address Book in Java. 
 The Address Book accepts a Person's Name, Address, and Phone number. 
 Use a text based menu-driven approach. 
 A user may create a new entry in their address book and input a name, address, and phone number. 
 A user may list all the members of their address book. 
 
1. Add a new contact 
2. Update an existing contact
3. Delete a contact
4. List all added contacts in sorted order
5. Search for a given contact. Search can be done by first name, last name, or phone number. 
   Return all the details upon finding a match.
6. Quit

Persist the contacts in the AddressBook. Save to a MySQL DB instead of a file. 
Use Exception Handling in your code to make your program robust. 
Include a read me file and all your SQL statements to create the table.
 */
public class AddressBook {
	private static ArrayList<Contact> addressBook = new ArrayList<>();
	
	static Scanner sc = new Scanner(System.in);
	
	private static boolean connectedWithoutAHitch = false;
	
	private static Connection connection = null;
	private Statement statement = null;
	private static ResultSet resultSet = null;
	private static PreparedStatement preparedStatement = null;
	private static String driver  = "com.mysql.cj.jdbc.Driver";
	private static String url = "jdbc:mysql://localhost:3306/AddressBook";
	private static String username = null;
	private static String password = null;

	// connects to the database that user inputs
	public static Connection getConnection() throws ClassNotFoundException{
		try {
			
			
			
			System.out.println("This program will NOT save username or password information");
			
			System.out.println("Please Enter your MySQL username. It should be root");
			username = sc.nextLine();
			
			System.out.println("Please Enter your MySQL password");
			password = sc.nextLine();
			Class.forName(driver);
			
			Connection conn = DriverManager.getConnection(url, username, password);
			
			System.out.println("Connected to DB");
			connectedWithoutAHitch = true;
			return conn;
		}catch(SQLException e) {
			System.out.println(e + "\nthrown in getConnection()");
		}catch(InputMismatchException ioe) {
			System.out.println(ioe);
		}
		return null;
	}
	
	// reads data from connected database and copies information into arraylist
	@SuppressWarnings("finally")
	public static ArrayList<Contact> readDB(Connection c) {
		
		ArrayList<Contact> aList= new ArrayList<Contact>();
		try {
			preparedStatement = connection.prepareStatement("select * from addressBook.contacts");
	        resultSet = preparedStatement.executeQuery();
	
	        while(resultSet.next()) {
	            aList.add(new Contact(resultSet.getString("firstName"), resultSet.getString("lastName"), 
	            		resultSet.getString("address"), 
	            		resultSet.getString("phoneNumber")));
	        }
			
		}catch(SQLException sqlEx) {
			System.out.println(sqlEx + "\nthrown in readDb()");
		}
		finally{
			return aList;
			}
		
	}
	// returns index of desired contact
	public int search() throws IOException {
		
		if(addressBook.isEmpty()) throw new IOException("Address Book is EMPTY\n");
		
		System.out.println("This search will return the first instance of the search parameter inputed.\n"
				+ "If this is the wrong contact, please try again.");
		
		String willCheckCurrent;
		int index = -1;
		String number;
		try {
			System.out.println(" To check current list for reference, enter any key. Otherwise, enter 'no'. ");			
			willCheckCurrent = sc.nextLine();
		
			if(willCheckCurrent.equals("no")) {/* do nothing */}
			else {
				System.out.println(willCheckCurrent);
				listAddressBook(); //display the current address book for reference
			}
		}catch(InputMismatchException e) {System.out.println(e);}	
			boolean searchPending = true;
			while(searchPending) {
				try {
					System.out.println("Please enter a number (1-3) to choose how you would like to search:\n"
							+ "1. First Name\n"
							+ "2. Last Name\n"
							+ "3. Phone Number\n");
				
				String searchType = sc.nextLine();
				int convertedToInt = Integer.parseInt(searchType);
				switch(convertedToInt) {
				
				// search by first name
				case 1:
					System.out.println("Please enter the FIRST name of the person you would like to find");
					String fnSearch = sc.nextLine();
					for(Contact c : addressBook) {
						if(c.getFirstName().equalsIgnoreCase(fnSearch)) {
							index = addressBook.indexOf(c);
							System.out.println(index + " is the index");
							c.toString();
							break;
							}
					}
					searchPending= false;
					break;
					
				// search by last name	
				case 2: 
					System.out.println("Please enter the LAST name of the person you would like to find");
					String lnSearch = sc.nextLine();
					for(Contact c : addressBook) {
						if(c.getLastName().equalsIgnoreCase(lnSearch)) {
							index = addressBook.indexOf(c);
							c.toString();
							break;
							}
					};
					searchPending= false;
					break;
					
				// search by phone number	
				case 3: 
					System.out.println("Please enter the 10 digit phone number of the person you would like to search");
					number = sc.nextLine();
					if (isValidPhoneNumber(number)) {
						for(Contact c: addressBook) {
							if (c.getPhoneNumber().equalsIgnoreCase(number)) {
								index = addressBook.indexOf(c);
								c.toString();
								break;
							}
						}
					}
					searchPending= false;
					break;
					
				// invalid input	
				//default:
					//throw new IOException("That was an invalid input");
				}
				
				
				}catch(InputMismatchException ioe){
					System.out.println(ioe);
				}catch(NumberFormatException e) {
					System.out.println(e+"\nThat was an invalid input");
				}
			}
			System.out.println(addressBook.get(index).toString());
			if(searchVerification()==true) return index;
			else {
				System.out.println("You can try searching with another parameter next time");
				return -1;
			}
	}
	
	// checks to see if the search was successful
	public boolean searchVerification() throws IOException{
		boolean verificationPending = true;
		System.out.println("Was this the contact you were searching for? (yes/no)");
		String yesOrNo;
		try {
			while(verificationPending) {	
				yesOrNo = sc.nextLine();
				if(yesOrNo.equals("no") || yesOrNo.equals("No")){
					System.out.println("Incorrect input was chosen");
					return false;
				}else if(yesOrNo.equals("yes") || yesOrNo.equals("Yes")) {
					System.out.println("Correct input was chosen");
					return true;
				}else {
					System.out.println(yesOrNo);
					throw new IOException("That was an invalid responce, try again (yes/no)");
				}
			}
		}catch(IOException e) {System.out.println(e);}
		System.out.println("something happened in searchVerification, returning true");
		return true;
	}
	
	// checks to see if phone number is valid
	public boolean isValidPhoneNumber(String phoneNum) {
		try {
			@SuppressWarnings("unused")
			int convertedToInt;
			convertedToInt = Integer.parseInt(phoneNum);
		}catch(NumberFormatException nfe) {
			System.out.println(nfe + "\nMake sure to only include the 10 digit number ONLY. "
					+ "\nNo dashes or any other extra characters.");
			return false;
		}
		if(phoneNum.length() != 10) {
			System.out.println("Invalid number entered. Please make sure to include only "
					+ "the 10 digit phonenumber");
			return false;
		}
		return true;
	}
	
	// adds a contact to address book
	public void addContact() {
		try {
			String firstName_, lastName_, address_, phonenumber_;
			
			System.out.println("Please enter the Contact's first name");
			firstName_ = sc.nextLine();
			
			System.out.println("Please enter the Contact's last name");
			lastName_ = sc.nextLine();
			
			System.out.println("Please enter the Contact's address");
			address_ = sc.nextLine();
			
			System.out.println("Please enter the Contact's 10 digit phone number");
			phonenumber_ = sc.nextLine();
			if(phonenumber_.length() != 10) {
				throw new IOException("This is not a valid phone number.\n"
						+ "Please make sure to use a 10 digit phone number without any extra characters\n"
						+ "e.g. 1234567890 NOT 123-456-7890");
			}
			addressBook.add(new Contact(firstName_, lastName_, address_, phonenumber_));
		}catch(IOException ioe){
			System.out.println(ioe);
			}
	}
	
	// updates address
	@SuppressWarnings("finally")
	public void updateContact() throws IOException {
		int index = search();
		if (index == -1) {
			System.out.println("Index not found. Exiting update");
			return;
		}
		boolean continueUpdate = true;
		while(continueUpdate) {
			System.out.println("Enter...\n"
				+ "1 for first name\n"
				+ "2 for last name\n"
				+ "3 for address\n"
				+ "4 for phone number\n"
				+ "or any other key to quit update");
			try {
				String updateNumber = sc.nextLine();
				
				
				switch(updateNumber) {
					case("1"):
						System.out.println("Please enter the new first name for this contact");
					addressBook.get(index).setFirstName(sc.nextLine());
						break;
					case("2"):
						System.out.println("Please enter the new last name for this contact");
					addressBook.get(index).setLastName(sc.nextLine());
						break;
					case("3"):
						System.out.println("Please enter the new address for this contact");
					addressBook.get(index).setAddress(sc.nextLine());
						break;
					case("4"):
						System.out.println("Please enter the new phone number for this contact");
					addressBook.get(index).setPhoneNumber(sc.nextLine());
						break;
					default:
						continueUpdate = false;
						break;
					}
				
				//br.close();
			}catch(InputMismatchException ioe) {
				System.out.println(ioe);
				return;
			}
		}
	}	

	
	// delete a specified contact
	public void deleteContact() throws IOException {
		if(addressBook.isEmpty()) {
			System.out.println("What are you doing here? It's already empty!");
			return;
		}
		try {
			int index = search();
			if (index == -1) {
				System.out.println("Index not found. Exiting deletion");
				return;
			}
			addressBook.remove(addressBook.get(index)); // removes contact at the index found by the search method
			System.out.println("Successfully deleted contact");
		} catch (IOException e) {
			System.out.println(e);
		} catch(Exception e) {System.out.println(e);}
	}
	
	public void listAddressBook() {
		if (addressBook.isEmpty()) {
			System.out.println("Address Book Empty");
			return;
		}
		 Collections.sort(addressBook); // TODO 
		for (Contact c : addressBook) {
			System.out.println(c.toString());
		}
	}
	
	
	
	// TODO
	public void mainMenu(AddressBook ab) {
		try {
			boolean continueProgram = true;
			System.out.println("Address Book\n"
					+ "___________\n"
					+ "Type in a number (1-6) corresponding to a command listed below to do things\n");
			while(continueProgram) {	
				try {
					System.out.println(""
							+ "1   Add a New Contact\n"
							+ "2   Update an Existing Contact\n"
							+ "3   Delete a Contact\n"
							+ "4   Search for a contact by last name\n"
							+ "5   Save Address Book\n"
							+ "6   List all Contacts\n"
							+ "7   Quit");
					String commandNumber = sc.nextLine();
					
					int convertedToInt = Integer.parseInt(commandNumber); // checks to see if input is a valid input, otherwise catch exception
					
					switch(convertedToInt) {
					case 1: 
						ab.addContact();
						break;
					case 2:
						ab.updateContact();
						break;
					case 3:
						ab.deleteContact();
						break;
					case 4:
						ab.search();
						System.out.println();
						break;
					case 5: 
						ab.saveToDB();
						break;
					case 6:
						ab.listAddressBook();
						break;
					case 7:
						System.out.println("Quitting program, saving changes");
						ab.saveToDB();
						continueProgram = false;
						break;
					default:
						throw new IOException("Invalid entry. Please ONLY enter a number (1-6)");
					}
					
				}catch(IOException e) {
					System.out.println(e);
					break;
				}catch(NumberFormatException nfe) {
					System.out.println(nfe + "\nPlease ONLY enter a number (1-6)");
					continue;
				}
			}
			ab.close();
		}catch(Exception e) {System.out.println(e);}
	}
	
	//saves address book data to SQL server
	public void saveToDB() {
		 try {
	            Class.forName(driver);
	            connection = DriverManager.getConnection(url, username, password);
	            statement = connection.createStatement();
	            preparedStatement = connection.prepareStatement("TRUNCATE TABLE contacts");
	            preparedStatement.executeUpdate();

	            String query = "insert into contacts(firstName, lastName, address, phoneNumber) values(?, ?, ?, ?)";
	            for(Contact c : addressBook) {
	                preparedStatement = connection.prepareStatement(query);
	                preparedStatement.setString(1, c.getFirstName());
	                preparedStatement.setString(2, c.getLastName());
	                preparedStatement.setString(3, c.getAddress());
	                preparedStatement.setString(4, c.getPhoneNumber());
	                preparedStatement.executeUpdate();
	            }

	           System.out.println("successfully saved address book to server");
	        } catch(SQLException e) {
	            System.out.println(e);
	        } 
		 	catch(ClassNotFoundException e) {
	            System.out.println(e);
	        }finally {
	            close();
	        }
	}
	
	// closes sql connection
	public void close() {
		try {
			if(resultSet != null) {
				resultSet.close();
			}
			if(statement != null) {
				statement.close();
			}
			if(connection != null) {
				connection.close();
			}
		}catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static void main(String[] args) throws IOException{
		boolean proceedWithProgram = true;
		while(proceedWithProgram == true) {
			try {
				connection = getConnection();
				if(connectedWithoutAHitch == true) {
					addressBook = readDB(connection);
					
					// populate initial address book for test purpose
					if(addressBook.isEmpty()) {
						addressBook.add(new Contact("Maaz", "Sheikh", "in the computer", "7894561232"));
						addressBook.add(new Contact("Deepa", "Mahalingam", "College of marin", "7894444232"));
						addressBook.add(new Contact("Joe", "Biden", "white house", "1111111111"));
						addressBook.add(new Contact("My", "Cat", "Her bed", "7845129635"));
					}
					AddressBook ab = new AddressBook();
					ab.mainMenu(ab);
					proceedWithProgram = false;
				}
				// if connection is not established, user can try again or quit
				else {
					System.out.println("Failed to connect to database."); 
						break;
				}
			} catch (ClassNotFoundException e) {
				System.out.println(e);
			} 
		}
		sc.close();
		System.out.println("Goodbye!");
	}
}


