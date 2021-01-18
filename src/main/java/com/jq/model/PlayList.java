package com.jq.model;

import java.util.List;

/*
e.g.

      "id" : "2",
      "user_id" : "3",
      "song_ids" : [
        "6",
        "8",
        "11"
      ]
    }
 */
public class PlayList {
	private String id;
	private String userId;
	private List <String> songIds;
	public PlayList(String id, String userId, List<String> songIds) {
		super();
		this.id = id;
		this.userId = userId;
		this.songIds = songIds;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public List<String> getSongIds() {
		return songIds;
	}
	public void setSongIds(List<String> songIds) {
		this.songIds = songIds;
	}
	
	@Override
	public String toString() {
		return "PlayList [id=" + id + ", userId=" + userId + ", songIds=" + songIds + "]";
	}

}
