import java.io.*;
import java.util.*;

class Book implements Comparable<Book> {
    int bookId;
    String title;
    String author;
    String category;
    boolean isIssued;

    public Book(int bookId, String title, String author, String category, boolean isIssued) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.category = category;
        this.isIssued = isIssued;
    }

    public void displayBookDetails() {
        System.out.println("Book ID: " + bookId);
        System.out.println("Title: " + title);
        System.out.println("Author: " + author);
        System.out.println("Category: " + category);
        System.out.println("Issued: " + (isIssued ? "Yes" : "No"));
    }

    public void markAsIssued() { isIssued = true; }
    public void markAsReturned() { isIssued = false; }
    public int compareTo(Book b) {
        return this.title.compareToIgnoreCase(b.title);
    }
}

class Member {
    int memberId;
    String name;
    String email;
    List<Integer> issuedBooks = new ArrayList<>();

    public Member(int memberId, String name, String email) {
        this.memberId = memberId;
        this.name = name;
        this.email = email;
    }

    public void displayMemberDetails() {
        System.out.println("Member ID: " + memberId);
        System.out.println("Name: " + name);
        System.out.println("Email: " + email);
        System.out.println("Issued Books: " + issuedBooks);
    }

    public void addIssuedBook(int id) { issuedBooks.add(id); }
    public void returnIssuedBook(int id) { issuedBooks.remove(Integer.valueOf(id)); }
}

public class LibraryManager {

    Map<Integer, Book> books = new HashMap<>();
    Map<Integer, Member> members = new HashMap<>();

    Scanner sc = new Scanner(System.in);
    public void loadFromFile() {
        try {
            File file = new File("books.txt");
            if (!file.exists()) file.createNewFile();

            BufferedReader br = new BufferedReader(new FileReader("books.txt"));
            String line;

            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                int id = Integer.parseInt(p[0]);
                books.put(id, new Book(id, p[1], p[2], p[3], Boolean.parseBoolean(p[4])));
            }
            br.close();
        } catch (Exception e) {
            System.out.println("Error loading books.");
        }

        try {
            File file = new File("members.txt");
            if (!file.exists()) file.createNewFile();

            BufferedReader br = new BufferedReader(new FileReader("members.txt"));
            String line;

            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                int id = Integer.parseInt(p[0]);
                Member m = new Member(id, p[1], p[2]);

                if (p.length > 3 && !p[3].isEmpty()) {
                    String[] arr = p[3].split("-");
                    for (String x : arr) m.issuedBooks.add(Integer.parseInt(x));
                }
                members.put(id, m);
            }
            br.close();
        } catch (Exception e) {
            System.out.println("Error loading members.");
        }
    }

    public void saveToFile() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("books.txt"));
            for (Book b : books.values()) {
                bw.write(b.bookId + "," + b.title + "," + b.author + "," + b.category + "," + b.isIssued);
                bw.newLine();
            }
            bw.close();
        } catch (Exception e) {
            System.out.println("Error saving books.");
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("members.txt"));
            for (Member m : members.values()) {
                StringBuilder sb = new StringBuilder();
                for (Integer id : m.issuedBooks) sb.append(id).append("-");

                bw.write(m.memberId + "," + m.name + "," + m.email + "," + sb.toString());
                bw.newLine();
            }
            bw.close();
        } catch (Exception e) {
            System.out.println("Error saving members.");
        }
    }

    public void addBook() {
        System.out.print("Enter Book ID: ");
        int id = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter Title: ");
        String t = sc.nextLine();
        System.out.print("Enter Author: ");
        String a = sc.nextLine();
        System.out.print("Enter Category: ");
        String c = sc.nextLine();

        books.put(id, new Book(id, t, a, c, false));
        saveToFile();
        System.out.println("Book added successfully.");
    }

    public void addMember() {
        System.out.print("Enter Member ID: ");
        int id = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter Name: ");
        String n = sc.nextLine();
        System.out.print("Enter Email: ");
        String e = sc.nextLine();

        members.put(id, new Member(id, n, e));
        saveToFile();
        System.out.println("Member added successfully.");
    }

    public void issueBook() {
        System.out.print("Enter Book ID: ");
        int bId = sc.nextInt();
        System.out.print("Enter Member ID: ");
        int mId = sc.nextInt();

        if (!books.containsKey(bId)) { System.out.println("Book not found."); return; }
        if (!members.containsKey(mId)) { System.out.println("Member not found."); return; }

        Book b = books.get(bId);
        if (b.isIssued) { System.out.println("Book already issued."); return; }

        b.markAsIssued();
        members.get(mId).addIssuedBook(bId);

        saveToFile();
        System.out.println("Book issued.");
    }

    public void returnBook() {
        System.out.print("Enter Book ID: ");
        int bId = sc.nextInt();
        System.out.print("Enter Member ID: ");
        int mId = sc.nextInt();

        if (!books.containsKey(bId) || !members.containsKey(mId)) {
            System.out.println("Invalid details.");
            return;
        }

        books.get(bId).markAsReturned();
        members.get(mId).returnIssuedBook(bId);

        saveToFile();
        System.out.println("Book returned.");
    }

    public void searchBooks() {
        sc.nextLine();
        System.out.print("Search by title/author/category: ");
        String key = sc.nextLine().toLowerCase();

        for (Book b : books.values()) {
            if (b.title.toLowerCase().contains(key) ||
                b.author.toLowerCase().contains(key) ||
                b.category.toLowerCase().contains(key)) {
                b.displayBookDetails();
                System.out.println("----------");
            }
        }
    }

    public void sortBooks() {
        List<Book> list = new ArrayList<>(books.values());

        System.out.println("1. Sort by Title");
        System.out.println("2. Sort by Author");
        int c = sc.nextInt();

        if (c == 1) {
            Collections.sort(list);  
        } else {
            list.sort((b1, b2) -> b1.author.compareToIgnoreCase(b2.author));
        }

        for (Book b : list) {
            b.displayBookDetails();
            System.out.println("---------");
        }
    }
    public static void main(String[] args) {
        LibraryManager lm = new LibraryManager();
        lm.loadFromFile();

        
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\nWelcome to City Library Digital Management System");
            System.out.println("1. Add Book");
            System.out.println("2. Add Member");
            System.out.println("3. Issue Book");
            System.out.println("4. Return Book");
            System.out.println("5. Search Books");
            System.out.println("6. Sort Books");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");

            int ch = sc.nextInt();

            if (ch == 1) lm.addBook();
            else if (ch == 2) lm.addMember();
            else if (ch == 3) lm.issueBook();
            else if (ch == 4) lm.returnBook();
            else if (ch == 5) lm.searchBooks();
            else if (ch == 6) lm.sortBooks();
            else if (ch == 7) {
                lm.saveToFile();
                System.out.println("Exiting…");
                break;
            }
        }
    }
}
