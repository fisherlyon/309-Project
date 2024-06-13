package project_csc309_spring_2024;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.auth.credentials.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.io.*;
import java.net.*;
import org.json.*;

/**
 * Controls all leaderboard functoinality,
 * including JSON, URL, and AWS-S3
 * functionalities.
 * 
 * @author Fisher Lyon
 */
public class LeaderBoard {

    private final String BUCKET_NAME = "mathmadness";
    private final String OBJECT_KEY = "leaderboard.json";
    private final Region REGION = Region.US_EAST_1;

    public LeaderBoard() {
        GameData.getInstance().setLeaderBoard(this);
        try {
            getLeaderboard();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getLeaderboard() throws Exception {
        // here is the link to my s3 bucket!
        String objectURL = "https://mathmadness.s3.amazonaws.com/leaderboard.json";
        URL url = new URL(objectURL);
        URLConnection connection = url.openConnection();
        InputStream inputStream = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String response = "";
        String inputLine = "";
        while ((inputLine = reader.readLine()) != null) {
            response += inputLine;
        }
        inputStream.close();
        GameData.getInstance().setlbes(parse(response));
    }

    public void updateLeaderboard(String newContent) {
        // save content to a temp file
        File tempFile = null;
        try {
            tempFile = File.createTempFile("leaderboard", ".json");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
                writer.write(newContent);
            }

            // init s3 client with anonymous credential provider (to avoid using my credentials)
            S3Client s3 = S3Client.builder()
                                  .region(REGION)
                                  .credentialsProvider(AnonymousCredentialsProvider.create())
                                  .build();

            // create a put object request, given the bucket name and object key
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                                                                .bucket(BUCKET_NAME)
                                                                .key(OBJECT_KEY)
                                                                .build();

            // upload the file and display a "success" message to terminal
            s3.putObject(putObjectRequest, RequestBody.fromFile(tempFile));
            System.out.println("Successfully placed " + OBJECT_KEY + " into bucket " + BUCKET_NAME);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (tempFile != null) {
                tempFile.delete(); // remove the temp file
            }
        }
    }

    public ArrayList<LeaderBoardEntry> parse(String response) {
        ArrayList<LeaderBoardEntry> lbes = new ArrayList<>();
        JSONArray jsonResponse = new JSONArray(response);
        for (Object obj : jsonResponse) {
            JSONObject jsonObj = (JSONObject) obj;
            LeaderBoardEntry lbe = 
                new LeaderBoardEntry(Integer.parseInt(jsonObj.getString("pos")), 
                                     jsonObj.getString("name"), 
                                     Integer.parseInt(jsonObj.getString("score")));
            lbes.add(lbe);
        }
        return lbes;
    }

    public String formatlbes(ArrayList<LeaderBoardEntry> lbes) {
        String formattedString = "Position\tName\tScore\n";
        for (LeaderBoardEntry lbe : lbes) {
            formattedString += lbe.toString() + "\n";
        }
        return formattedString;
    }

    public void recalculatePositions(ArrayList<LeaderBoardEntry> lbes) {
        Collections.sort(lbes, new LeaderBoardComparator());
        for (int i = 1; i < lbes.size() + 1; i++) {
            lbes.get(i-1).setPos(i);
        }
    }

    public void add(LeaderBoardEntry entry) {
        ArrayList<LeaderBoardEntry> lbes = GameData.getInstance().getlbes();
        
        Iterator<LeaderBoardEntry> iterator = lbes.iterator();
        while (iterator.hasNext()) {
            LeaderBoardEntry existingEntry = iterator.next();
            if (entry.getName().equals(existingEntry.getName())) {
                if (entry.getScore() > existingEntry.getScore()) {
                    iterator.remove(); // Safe removal using iterator
                } else {
                    return; // No need to proceed further if existing entry has higher score
                }
            }
        }
    
        lbes.add(entry);
        recalculatePositions(lbes);
        updateLeaderboard(convertToJson(lbes));
    }
    

    public String convertToJson(ArrayList<LeaderBoardEntry> lbes) {
        JSONArray jsonArray = new JSONArray();
        for (LeaderBoardEntry lbe : lbes) {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("pos", Integer.toString(lbe.getPos()));
            jsonObj.put("name", lbe.getName());
            jsonObj.put("score", Integer.toString(lbe.getScore()));
            jsonArray.put(jsonObj);
        }
        return jsonArray.toString();
    }
}
