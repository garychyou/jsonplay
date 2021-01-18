package com.jq;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import com.jq.model.PlayList; //TODO. can use this for faster look up
import com.jq.model.Song;
import com.jq.model.User; //TODO. can use this for validation and faster look up

public class Driver {
	public static void main(String[] args){

		String baseDir = args[0];
		String changesFile = baseDir + "/data/changes.txt";
		String inputFile = baseDir + "/data/mixtape.json";
		String outputFile = baseDir + "/data/output.json";
		
		List<List<String>> changes = null;
		try {
			changes = readChanges(changesFile);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		//JSON parser object to parse read file
        JSONParser jsonParser = new JSONParser();
        JSONObject obj = null;
        
        try (FileReader reader = new FileReader(inputFile))
        {
            //Read JSON file
            obj = (JSONObject) jsonParser.parse(reader);
            //System.out.println(obj.toString());
 
            /**
            JSONArray userList = (JSONArray)  obj.get("users") ;
            JSONArray songsList = (JSONArray) obj.get("songs") ;
            JSONArray playLists = (JSONArray) obj.get("playlists") ;
            //System.out.println(userList.toJSONString());
            //System.out.println(songsList.toJSONString());
            //System.out.println(playLists.toJSONString());
            // TODO, load users into Hashtable, for qucik lookup
            // TODO, load songs into Hashtable, for qucik lookup
             * 
             */
             
            //Iterate over changes, and apply the changes
            if (changes != null) {
            	for (List<String> change : changes) {
            		applyChange(change, (JSONObject) obj);
            	}
            }
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        
        //Write JSON file
        try (FileWriter file = new FileWriter(outputFile)) {
 
            file.write(obj.toJSONString());
            file.flush();
 
        } catch (IOException e) {
            e.printStackTrace();
        }
        
	}

	private static void applyChange(List<String> change, JSONObject obj) {
		JSONArray playLists = (JSONArray) ((JSONObject) obj).get("playlists");
		
		String operator = change.get(0);
		if ("new".equals(operator)) {
			// auto generate unique key, id
			int largest = -1;
			for (int i = 0; i < playLists.size(); i++) { // iteration, expensive
				JSONObject cur = (JSONObject) playLists.get(i);
				String cur_user_id = (String)cur.get("id");
				Integer primaryKey = Integer.parseInt(cur_user_id);
				largest = primaryKey > largest? primaryKey : largest;
			}
			int nextKey = largest + 1;
			String nextKeyStr = ""+ nextKey; 
			
			// add a new playList
			String userId = change.get(1); // TODO. validate foreign key
			JSONArray song_ids = new JSONArray();
			for (int k = 2; k < change.size(); k++) {
				String curSongID = change.get(k); // TODO. validate foreign key
				song_ids.add(curSongID); 
		    }
			JSONObject newPlayList = new JSONObject();
			newPlayList.put("id", nextKeyStr);
			newPlayList.put("user_id", userId);
			newPlayList.put("song_ids", song_ids);
			
			playLists.add(newPlayList);

		} else if ("remove".equals(operator)) {
			String targetId = change.get(1); 
			int targetIndx = -1;
			for (int i = 0; i < playLists.size(); i++) { // iteration, slow
				JSONObject cur = (JSONObject) playLists.get(i);
				String cur_id = (String)cur.get("id");
				if (cur_id.equals(targetId)) {
					targetIndx = i;
					break;
				}
			}
			
			if (targetIndx != -1) {
				playLists.remove(targetIndx);
			} else {
				System.out.println("no play list of [id=" + targetId + "] to remove");
			}
			
		} else if ("add_song".equals(operator)) { 
			String targetId = change.get(1); 
			JSONObject playListToModify = null; 
			for (int i = 0; i < playLists.size(); i++) { // iteration, slow
				JSONObject cur = (JSONObject) playLists.get(i);
				String cur_id = (String)cur.get("id");
				if (cur_id.equals(targetId)) {
					playListToModify = cur;
					break;
				}
			}
			
			if (playListToModify != null) {
				String newsongIdToAdd = change.get(2);
				JSONArray song_ids =(JSONArray) playListToModify.get("song_ids");
				for (int i = 0; i < song_ids.size(); i++) {
					String curSongID = (String) song_ids.get(i);
					if (curSongID.equals(newsongIdToAdd)) {
						// exist already, this is noOp;
						return;
					}
			    }
				song_ids.add(newsongIdToAdd);  
			}
			
		} else {
			// not supported
			// throw exception? or do nothing
		}
		
	}

	public static List<List<String>> readChanges(String changesFile) throws IOException {
	    Path filePath = Paths.get(changesFile);
	    List<List<String>> filteredLines = null;

	    if (Files.exists(filePath)) {
	    	try (Stream<String> lines = Files.lines(filePath)) {
	    		filteredLines = lines
	                    .filter(s -> (!s.startsWith("#") && !s.trim().isEmpty()))
	                    .map(line -> Arrays.asList(line.split(",")))
	                    .collect(Collectors.toList());
	    	}
	    }

	    return filteredLines;

	}

}
