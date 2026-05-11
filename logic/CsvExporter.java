package logic;

import db.ElectionDAO;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Utility for exporting voting results to comprehensive CSV format with metadata and statistics.
 * Implements multiple layers of read-only protection to prevent unauthorized editing.
 */
public class CsvExporter {
    private static void setFileReadOnly(String fileName) {
        try {
            File file = new File(fileName);
            // Set read-only at Java level.
            file.setReadOnly();
            
            // Additional protection: keep the file marked read-only at the OS level.
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                try {
                    ProcessBuilder pb1 = new ProcessBuilder("attrib", "+r", fileName);
                    pb1.start().waitFor();
                    file.setWritable(false, false);
                    
                    // Confirm file is not writable by this process.
                    file.setWritable(false, false);
                } catch (Exception e) {
                    System.err.println("Note: Could not set Windows read-only attribute: " + e.getMessage());
                }
            } else {
                // Unix/Linux: Use chmod 444 for read-only (no write for anyone).
                ProcessBuilder pb = new ProcessBuilder("chmod", "444", fileName);
                pb.start().waitFor();
            }

            System.out.println("CSV file \"" + fileName + "\" is now read-only and openable for viewing.");
        } catch (Exception e) {
            System.err.println("Warning: Could not apply full file protection: " + e.getMessage());
        }
    }
    public static boolean exportResultsToCsv(List<Object[]> results, ElectionDAO.ElectionInfo electionInfo, ElectionDAO.BallotStats ballotStats) {
        String fileName = "votewise_results_" + System.currentTimeMillis() + ".csv";
        File file = new File(fileName);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            // Header section
            writer.write("VoteWise - Election Results Report\n");
            writer.write("\n");
            
            // Election metadata
            writer.write("Election Name,\"" + escapeQuotes(electionInfo.name) + "\"\n");
            writer.write("Position/Post,\"" + escapeQuotes(electionInfo.post) + "\"\n");
            writer.write("Description,\"" + escapeQuotes(electionInfo.description) + "\"\n");
            writer.write("Export Date,\"" + dateFormat.format(new Date()) + "\"\n");
            writer.write("\n");
            
            // Ballot Statistics
            writer.write("=== BALLOT STATISTICS ===\n");
            writer.write("Total Registered Voters," + ballotStats.totalRegistered + "\n");
            writer.write("Total Votes Cast," + ballotStats.totalVoted + "\n");
            writer.write("Remaining to Vote," + ballotStats.getRemaining() + "\n");
            writer.write("Voting Percentage," + String.format("%.2f%%", ballotStats.getVotingPercentage()) + "\n");
            writer.write("\n");
            
            // Determine and display winner
            writer.write("=== ELECTION WINNER ===\n");
            if (results.isEmpty() || ballotStats.totalVoted == 0) {
                writer.write("Status,No winner yet - No votes cast\n");
                writer.write("Election," + escapeQuotes(electionInfo.name) + "\n");
                writer.write("Position," + escapeQuotes(electionInfo.post) + "\n");
            } else {
                // Find winner (candidate with maximum votes)
                Object[] winnerRow = results.get(0);
                int maxVotes = (int) winnerRow[3];
                String winnerName = (String) winnerRow[1];
                String winnerSymbol = (String) winnerRow[2];
                double winnerPercentage = (double) maxVotes / ballotStats.totalVoted * 100;
                
                // Check for tie
                boolean hasTie = false;
                for (int i = 1; i < results.size(); i++) {
                    if ((int) results.get(i)[3] == maxVotes) {
                        hasTie = true;
                        break;
                    }
                }
                
                if (hasTie) {
                    writer.write("Status,TIE - Multiple candidates with equal votes\n");
                    writer.write("Election," + escapeQuotes(electionInfo.name) + "\n");
                    writer.write("Position," + escapeQuotes(electionInfo.post) + "\n");
                    writer.write("Tied Candidates and Votes," + maxVotes + "\n");
                } else {
                    writer.write("Status,WINNER DECLARED\n");
                    writer.write("Election," + escapeQuotes(electionInfo.name) + "\n");
                    writer.write("Position," + escapeQuotes(electionInfo.post) + "\n");
                    writer.write("Winner Name,\"" + escapeQuotes(winnerName) + "\"\n");
                    writer.write("Winner Symbol,\"" + escapeQuotes(winnerSymbol) + "\"\n");
                    writer.write("Total Votes Received," + maxVotes + "\n");
                    writer.write("Vote Percentage," + String.format("%.2f%%", winnerPercentage) + "\n");
                }
            }
            
            writer.write("\n");
            
            // Results table
            writer.write("=== ELECTION RESULTS (DETAILED) ===\n");
            writer.write("Rank,Candidate ID,Candidate Name,Election Symbol,Vote Count,Vote Percentage\n");
            
            int totalVotes = ballotStats.totalVoted;
            int rank = 1;
            int previousVotes = -1;
            int previousRank = 0;
            
            for (Object[] row : results) {
                int id = (int) row[0];
                String name = (String) row[1];
                String symbol = (String) row[2];
                int votes = (int) row[3];
                
                // Handle tied rankings
                if (votes != previousVotes) {
                    previousRank = rank;
                }
                
                double votePercentage = totalVotes > 0 ? (double) votes / totalVotes * 100 : 0;
                writer.write(previousRank + "," + id + ",\"" + escapeQuotes(name) + "\",\"" + escapeQuotes(symbol) + "\"," + 
                             votes + "," + String.format("%.2f%%", votePercentage) + "\n");
                
                previousVotes = votes;
                rank++;
            }
            
            writer.write("\n");
            writer.write("Report Generated: " + dateFormat.format(new Date()) + "\n");
            
            System.out.println("Detailed CSV exported successfully to: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error exporting to CSV: " + e.getMessage());
            return false;
        }
        
        // Apply read-only file protection
        setFileReadOnly(fileName);
        return true;
    }

    public static boolean exportVotersToCSv(List<String[]> voters, ElectionDAO.ElectionInfo electionInfo) {
        String fileName = "votewise_voters_" + System.currentTimeMillis() + ".csv";
        File file = new File(fileName);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            // Header section
            writer.write("VoteWise - Registered Voters Report\n");
            writer.write("\n");
            
            // Election metadata
            writer.write("Election Name,\"" + escapeQuotes(electionInfo.name) + "\"\n");
            writer.write("Position/Post,\"" + escapeQuotes(electionInfo.post) + "\"\n");
            writer.write("Export Date,\"" + dateFormat.format(new Date()) + "\"\n");
            writer.write("\n");
            
            // Voter statistics
            writer.write("=== VOTER STATISTICS ===\n");
            int totalVoters = voters.size();
            long votedCount = voters.stream().filter(v -> "true".equalsIgnoreCase(v[4])).count();
            long notVotedCount = totalVoters - votedCount;
            writer.write("Total Registered Voters," + totalVoters + "\n");
            writer.write("Votes Cast," + votedCount + "\n");
            writer.write("Not Yet Voted," + notVotedCount + "\n");
            writer.write("\n");
            
            // Voters table
            writer.write("=== REGISTERED VOTERS ===\n");
            writer.write("S.No,Voter Name,Email,Course,Section,Has Voted\n");
            
            int sNo = 1;
            for (String[] voter : voters) {
                String name = voter[0];
                String email = voter[1];
                String course = voter[2];
                String section = voter[3];
                String hasVoted = voter[4];
                writer.write(sNo + ",\"" + escapeQuotes(name) + "\",\"" + escapeQuotes(email) + "\"," +
                        "\"" + escapeQuotes(course) + "\",\"" + escapeQuotes(section) + "\"," + hasVoted + "\n");
                sNo++;
            }
            
            writer.write("\n");
            writer.write("Report Generated: " + dateFormat.format(new Date()) + "\n");
            
            System.out.println("Voter CSV exported successfully to: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error exporting voters to CSV: " + e.getMessage());
            return false;
        }
        
        // Apply read-only file protection
        setFileReadOnly(fileName);
        return true;
    }

    private static String escapeQuotes(String value) {
        return value == null ? "" : value.replace("\"", "\"\"");
    }

    public static String getLastExportPath() {
        return new File(".").getAbsolutePath();
    }
}
