package logic;

import db.AdminDAO;
import db.CandidateDAO;
import db.VoterDAO;
import db.ElectionDAO;
import java.util.List;
import java.util.Properties;

/**
 * Service layer for admin operations.
 * Provides high-level business logic by orchestrating DAO calls.
 * Separates business rules from database access.
 */
public class AdminService {
    private AdminDAO adminDAO = new AdminDAO();
    private CandidateDAO candidateDAO = new CandidateDAO();
    private VoterDAO voterDAO = new VoterDAO();
    private SmtpConfigStore smtpConfigStore = new SmtpConfigStore();
    private ElectionDAO electionDAO = new ElectionDAO();
    private OtpEmailService otpEmailService = new OtpEmailService();
    private String smtpTestError = "";

    public boolean login(String username, String password) {
        return adminDAO.validateAdmin(username, password);
    }

    public boolean addCandidate(String name, String symbol) {
        return candidateDAO.addCandidate(name, symbol);
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

    public Properties getSmtpSettings() {
        return smtpConfigStore.load();
    }

    public boolean saveSmtpSettings(String host, String port, String user, String pass, String from) {
        return smtpConfigStore.save(host, port, user, pass, from);
    }

    public boolean testSmtpSettings(String host, String port, String user, String pass, String from, String recipient) {
        boolean ok = otpEmailService.sendTestEmailWithConfig(host, port, user, pass, from, recipient);
        smtpTestError = otpEmailService.getLastError();
        return ok;
    }

    public String getSmtpTestError() {
        return smtpTestError;
    }

    public ElectionDAO.ElectionInfo getElectionInfo() {
        return electionDAO.getElectionInfo();
    }

    public boolean setElectionInfo(String name, String post, String description) {
        return electionDAO.setElectionInfo(name, post, description);
    }

    public ElectionDAO.BallotStats getBallotStats() {
        return electionDAO.getBallotStats();
    }

    public boolean exportResultsToCSV(List<Object[]> results) {
        ElectionDAO.ElectionInfo info = getElectionInfo();
        ElectionDAO.BallotStats stats = getBallotStats();
        return CsvExporter.exportResultsToCsv(results, info, stats);
    }

    public boolean exportVotersToCSV(List<String[]> voters) {
        ElectionDAO.ElectionInfo info = getElectionInfo();
        return CsvExporter.exportVotersToCSv(voters, info);
    }
}
