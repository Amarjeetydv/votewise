package logic;

/**
 * Model class representing a candidate in the election.
 * Immutable data transfer object (DTO) for candidate information.
 */
public class Candidate {
    private int id;          // Unique identifier from database
    private String name;      // Candidate's full name
    private String symbol;     // Election symbol

    public Candidate(int id, String name, String symbol) {
        this.id = id;
        this.name = name;
        this.symbol = symbol;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getSymbol() { return symbol; }

    @Override
    public String toString() {
        return name + " - " + symbol;
    }
}
