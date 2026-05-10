package logic;

/**
 * Model class representing a candidate in the election.
 * Immutable data transfer object (DTO) for candidate information.
 */
public class Candidate {
    private int id;          // Unique identifier from database
    private String name;      // Candidate's full name
    private String party;     // Political party affiliation

    public Candidate(int id, String name, String party) {
        this.id = id;
        this.name = name;
        this.party = party;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getParty() { return party; }

    @Override
    public String toString() {
        return name + " - " + party;
    }
}
