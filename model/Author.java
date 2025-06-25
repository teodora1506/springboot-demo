import java.awt.print.Book;
import java.util.List;

public class Author {


    private Long id;

    private String name;


    private List<Book> books;

    // Getteri i setteri
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }
}