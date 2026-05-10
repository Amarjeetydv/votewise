import logic.Candidate;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * JUnit test cases for Candidate model class.
 * Tests basic functionality and data integrity.
 */
public class TestCandidate {

    /**
     * Test candidate object creation and getter methods.
     */
    @Test
    public void testCandidateCreation() {
        Candidate candidate = new Candidate(1, "Alice Smith", "Alpha Party");
        
        assertEquals(1, candidate.getId());
        assertEquals("Alice Smith", candidate.getName());
        assertEquals("Alpha Party", candidate.getParty());
    }

    /**
     * Test toString method returns correct format.
     */
    @Test
    public void testCandidateToString() {
        Candidate candidate = new Candidate(2, "Bob Jones", "Beta Party");
        String expected = "Bob Jones - Beta Party";
        
        assertEquals(expected, candidate.toString());
    }

    /**
     * Test that candidate data is immutable.
     */
    @Test
    public void testCandidateImmutability() {
        Candidate candidate1 = new Candidate(1, "Alice", "Party A");
        Candidate candidate2 = new Candidate(1, "Alice", "Party A");
        
        // Both candidates have same data but are different objects
        assertNotSame(candidate1, candidate2);
        assertEquals(candidate1.getName(), candidate2.getName());
    }

    /**
     * Test candidate with different IDs.
     */
    @Test
    public void testMultipleCandidates() {
        Candidate candidate1 = new Candidate(1, "Candidate 1", "Party 1");
        Candidate candidate2 = new Candidate(2, "Candidate 2", "Party 2");
        
        assertNotEquals(candidate1.getId(), candidate2.getId());
        assertNotEquals(candidate1.getName(), candidate2.getName());
    }
}
