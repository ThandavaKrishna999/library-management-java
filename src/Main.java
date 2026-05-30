import java.io.*;
import java.util.*;
abstract class Items{
    protected String title;
    protected String author;
    protected double price;
    public Items(String title, String author, double price){
        this.title = title;
        this.author = author;
        this.price = price;
    }
    public abstract void displayinfo();
    public double getPrice(){
        return price;
    }
}
class Books extends Items{
    private String ISBN;
    private boolean available = true;
    public Books(String title, String author, double price, String ISBN){
        super(title, author, price);
        this.ISBN = ISBN;
    }
    public boolean isAvailable(){
        return available;
    }
    public void setAvailable(boolean available){
        this.available = available;
    }
    @Override
    public void displayinfo(){
        System.out.println("Title: " + title + "\nAuthor: " + author + "\nISBN: " + ISBN);
    }
    public String getISBN(){
        return ISBN;
    }
}
abstract class Members{
    protected String name;
    protected int id;
    public Members(String name, int id){
        this.name = name;
        this.id = id;
    }
    public abstract void borrowItem(Items item);
    public void introduce(){
        System.out.println(name + " ID: " + id);
    }
}
class Faculty extends Members{
    private String depart;
    public Faculty(String name, int id, String depart){
        super(name, id);
        this.depart = depart;
    }
    @Override
    public void borrowItem(Items item){
        System.out.println(name + " (faculty) borrowed " + item.title);
    }
    public String getDepart(){
        return depart;
    }
}
class Students extends Members{
    private String grade;
    public Students(String name, int id, String grade){
        super(name, id);
        this.grade = grade;
    }
    @Override
    public void borrowItem(Items item){
        System.out.println(name + " (student) borrowed " + item.title);
    }
    public String getGrade(){
        return grade;
    }
}
class MemberNotFoundException extends Exception{
    public MemberNotFoundException(String message){
        super(message);
    }
}
class BookNotAvailableException extends Exception{
    public BookNotAvailableException(String message){
        super(message);
    }
}
class Library{
    private ArrayList<Items> itemList;
    private HashMap<Integer, Members> memberMap;
    public Library(){
        itemList = new ArrayList<>();
        memberMap = new HashMap<>();
    }
    public void saveBooks(){
        try {
            FileWriter fw = new FileWriter("books.csv");
            for (Items item : itemList){
                if (item instanceof Books){
                    Books book = (Books) item;
                    fw.write(book.title + "," + book.author + "," + book.getPrice() + "," + book.getISBN() + "," + book.isAvailable() + "\n");
                }
            }
            fw.close();
        } catch (IOException e) {
            System.out.println("Error saving books: " + e.getMessage());
        }
    }
    public void saveMembers() {
        try {
            FileWriter fw = new FileWriter("members.csv");
            for (Members member : memberMap.values()) {
                if (member instanceof Students) {
                    Students s = (Students) member;
                    fw.write("student," + s.name + "," + s.id + "," + s.getGrade() + "\n");
                } else if (member instanceof Faculty) {
                    Faculty f = (Faculty) member;
                    fw.write("faculty," + f.name + "," + f.id + "," + f.getDepart() + "\n");
                }
            }
            fw.close();
        } catch (IOException e) {
            System.out.println("Error saving members: " + e.getMessage());
        }
    }
    public void registerMember(Members member){
        memberMap.put(member.id, member);
        System.out.println("New member registered:");
        System.out.println(member.name);
        saveMembers();
    }
    public void addItem(Items item){
        itemList.add(item);
        System.out.println("Item added:");
        item.displayinfo();
        saveBooks();
    }
    public void showAllItems(){
        for (Items item : itemList){
            item.displayinfo();
        }
    }
    public void showAllMembers(){
        for (Members member : memberMap.values()){
            member.introduce();
        }
    }
    public void lendItem(int memberId, Items item) throws MemberNotFoundException, BookNotAvailableException {
        Members member = memberMap.get(memberId);
        if(member == null){
            throw new MemberNotFoundException("Member not found");
        }
        if(item instanceof Books){
            Books book = (Books) item;
            if(book.isAvailable()){
                member.borrowItem(book);
                book.setAvailable(false);
                saveBooks();
            }
            else{
                throw new BookNotAvailableException("Book already borrowed");
            }
        }
    }
    public Items searchByTitle(String searchTitle){
        for (Items item : itemList){
            if(item.title.equalsIgnoreCase(searchTitle)){
                return item;
            }
        }
        return null;
    }
    public Books searchByIsbn(String searchIsbn){
        for (Items item : itemList){
            if(item instanceof Books){
                Books book = (Books) item;
                if(book.getISBN().equalsIgnoreCase(searchIsbn)){
                    return book;
                }
            }
        }
        return null;
    }
    public void returnItem(Items item){
        if(item instanceof Books){
            Books book = (Books) item;
            if(!book.isAvailable()){
                book.setAvailable(true);
                System.out.println(book.title + " returned");
                saveBooks();
            }
            else{
                System.out.println("Book was not borrowed");
            }
        }
    }
    public void loadBooks() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("books.csv"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                String title = parts[0];
                String author = parts[1];
                double price = Double.parseDouble(parts[2]);
                String isbn = parts[3];
                boolean available = Boolean.parseBoolean(parts[4]);
                Books book = new Books(title, author, price, isbn);
                book.setAvailable(available);
                itemList.add(book);
            }
            br.close();
        } catch (FileNotFoundException e) {
            System.out.println("No books file found, starting fresh.");
        } catch (IOException e) {
            System.out.println("Error loading books: " + e.getMessage());
        }
    }
    public void loadMembers() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("members.csv"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                String type = parts[0];
                String name = parts[1];
                int id = Integer.parseInt(parts[2]);
                String extra = parts[3];
                if (type.equals("student")) {
                    memberMap.put(id, new Students(name, id, extra));
                } else if (type.equals("faculty")) {
                    memberMap.put(id, new Faculty(name, id, extra));
                }
            }
            br.close();
        } catch (FileNotFoundException e) {
            System.out.println("No members file found, starting fresh.");
        } catch (IOException e) {
            System.out.println("Error loading members: " + e.getMessage());
        }
    }
}
public class Main{
    public static void main(String[] args) {
        Library lib = new Library();
        lib.loadBooks();
        lib.loadMembers();
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n1. Add Book");
            System.out.println("2. Register Member");
            System.out.println("3. Lend Book");
            System.out.println("4. Return Book");
            System.out.println("5. Search by Title");
            System.out.println("6. Search by ISBN");
            System.out.println("7. Show All Items");
            System.out.println("8. Show All Members");
            System.out.println("0. Exit");
            System.out.print("Choice: ");

            int choice;
            try{
                choice = sc.nextInt();
                sc.nextLine();
            }
            catch (InputMismatchException e){
                sc.nextLine();
                System.out.println("Enter a number ");
                continue;
            }
            switch (choice) {
                case 1:
                    System.out.print("Title: ");
                    String title = sc.nextLine();
                    if (title.trim().isEmpty()){
                        System.out.println("Title cant be empty");
                        break;
                    }
                    System.out.print("Author: ");
                    String author = sc.nextLine();
                    if (author.trim().isEmpty()){
                        System.out.println("author cant be empty");
                        break;
                    }
                    System.out.print("Price: ");
                    double price;
                    try{
                        price = sc.nextDouble();
                        sc.nextLine();
                    }
                    catch(InputMismatchException e){
                        sc.nextLine();
                        System.out.println("Invalid price, enter a number.");
                        break;
                    }
                    System.out.print("ISBN: ");
                    String isbn = sc.nextLine();
                    if (isbn.trim().isEmpty()) {
                        System.out.println("ISBN cannot be empty");
                        break;
                    }
                    lib.addItem(new Books(title, author, price, isbn));
                    break;
                case 2:
                    System.out.print("Name: ");
                    String name = sc.nextLine();
                    if (name.trim().isEmpty()) {
                        System.out.println("Name cannot be empty.");
                        break;
                    }
                    System.out.print("ID: ");
                    int id;
                    try {
                        id = sc.nextInt();
                        sc.nextLine();
                    } catch (InputMismatchException e) {
                        sc.nextLine();
                        System.out.println("Invalid ID, enter a number.");
                        break;
                    }
                    System.out.print("Type (student/faculty): ");
                    String type = sc.nextLine();
                    if (!type.equalsIgnoreCase("student") && !type.equalsIgnoreCase("faculty")) {
                        System.out.println("Invalid type. Enter student or faculty.");
                        break;
                    }
                    if (type.equalsIgnoreCase("student")) {
                        System.out.print("Grade: ");
                        String grade = sc.nextLine();
                        if (grade.trim().isEmpty()) {
                            System.out.println("Grade cannot be empty.");
                            break;
                        }
                        lib.registerMember(new Students(name, id, grade));
                    } else {
                        System.out.print("Department: ");
                        String dept = sc.nextLine();
                        if (dept.trim().isEmpty()) {
                            System.out.println("Department cannot be empty.");
                            break;
                        }
                        lib.registerMember(new Faculty(name, id, dept));
                    }
                    break;
                case 3:
                    System.out.print("Member ID: ");
                    int memberId;
                    try {
                        memberId = sc.nextInt();
                        sc.nextLine();
                    } catch (InputMismatchException e) {
                        sc.nextLine();
                        System.out.println("Invalid ID, enter a number.");
                        break;
                    }
                    System.out.print("Book ISBN: ");
                    String lendIsbn = sc.nextLine();
                    if (lendIsbn.trim().isEmpty()) {
                        System.out.println("ISBN cannot be empty.");
                        break;
                    }
                    Books lendBook = lib.searchByIsbn(lendIsbn);
                    if (lendBook == null) {
                        System.out.println("Book not found.");
                    } else {
                        try {
                            lib.lendItem(memberId, lendBook);
                        } catch (MemberNotFoundException | BookNotAvailableException e) {
                            System.out.println("Error: " + e.getMessage());
                        }
                    }
                    break;
                case 4:
                    System.out.print("Book ISBN to return: ");
                    String returnIsbn = sc.nextLine();
                    if (returnIsbn.trim().isEmpty()) {
                        System.out.println("ISBN cannot be empty.");
                        break;
                    }
                    Books returnBook = lib.searchByIsbn(returnIsbn);
                    if (returnBook == null) {
                        System.out.println("Book not found.");
                    } else {
                        lib.returnItem(returnBook);
                    }
                    break;
                case 5:
                    System.out.print("Title to search: ");
                    String searchTitle = sc.nextLine();
                    if (searchTitle.trim().isEmpty()) {
                        System.out.println("Title cannot be empty.");
                        break;
                    }
                    Items found = lib.searchByTitle(searchTitle);
                    if (found != null) found.displayinfo();
                    else System.out.println("Not found.");
                    break;
                case 6:
                    System.out.print("ISBN to search: ");
                    String searchIsbn = sc.nextLine();
                    if (searchIsbn.trim().isEmpty()) {
                        System.out.println("ISBN cannot be empty.");
                        break;
                    }
                    Books foundBook = lib.searchByIsbn(searchIsbn);
                    if (foundBook != null) foundBook.displayinfo();
                    else System.out.println("Not found.");
                    break;
                case 7:
                    lib.showAllItems();
                    break;
                case 8:
                    lib.showAllMembers();
                    break;
                case 0:
                    System.out.println("Exiting.");
                    sc.close();
                    System.exit(0);
                default:
                    System.out.println("Invalid option.");
                    break;
            }
        }

    }
}