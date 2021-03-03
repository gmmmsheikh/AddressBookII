# AddressBookII
Read me file for address book project

This program stores an address book and saves it via MySql database.

To run this program, you need to make the neccessary database. To do this:

1. Create a DB in the mySQL workbench using the command -> CREATE DATABASE addressBook;
2. Use the following command -> USE addressBook;
3. Create a table -> CREATE TABLE contacts (firstName VARCHAR(20), lastName VARCHAR (20), address VARCHAR(200), phoneNumber VARCHAR(10));

Make sure you have a mySQL connector in your computer to run this program. One can easily be found online.

In AddressBook.java, the driver and url are taylored for my setup. If this does not work for you, make sure to change it tou accomodate your configuration.

Your mySQL username and password have to be inputed in the start of the program when you hit run.

The command are pretty straight forward, but they go like this:

1. add a contact to the address book. The program will ask user for all parameters for a contact
2. update contact. Using the search method, user will find contact to update and user will then update the desired parameters
3. delete contact. Using the search method, user will find contact to delete for addressbook and then contact will be deleted
