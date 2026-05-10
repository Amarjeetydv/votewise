package logic;

import db.AdminDAO;
import db.CandidateDAO;
import db.VoterDAO;
import java.util.List;

public class AdminService {
    private AdminDAO adminDAO = new AdminDAO();
    private CandidateDAO candidateDAO = new CandidateDAO();
    private VoterDAO voterDAO = new VoterDAO();

    public boolean login(String username, String password) {
        return adminDAO.validateAdmin(username, password);
    }

    public boolean addCandidate(String name, String party) {
        return candidateDAO.addCandidate(name, party);
    }

    public boolean removeCandidate(int id) {
        return candidateDAO.deleteCandidate(id);
    }

    public List<Object[]> getResults() {
        return candidateDAO.getVoteResults();
    }

    public List<String[]> getVoterList() {
        return voterDAO.getAllVoters();
    }
}
